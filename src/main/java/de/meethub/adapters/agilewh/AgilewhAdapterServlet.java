package de.meethub.adapters.agilewh;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AgilewhAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = -2799550772726246838L;

    private URL baseUrl;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            this.baseUrl  = new URL(config.getInitParameter("base-url"));
        } catch (final MalformedURLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        try {
            final Document table = this.loadPage();
            this.printEventCalendar(table.getDocumentElement(), response);
        } catch (SAXException | ParserConfigurationException | ParseException | ValidationException e1) {
            throw new ServletException(e1);
        }
    }

    private void printEventCalendar(final Element table, final HttpServletResponse response)
        throws IOException, ParseException, ValidationException {

        final Calendar calendar = this.convertToCalendar(table);
        final CalendarOutputter outputter = new CalendarOutputter();
        response.setContentType("text/calendar;charset=UTF-8");
        outputter.output(calendar, response.getWriter());
    }

    private Calendar convertToCalendar(final Element table) throws SocketException, ParseException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//agileWH//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        final NodeList trs = table.getElementsByTagName("tr");
        for (int i = 1; i < trs.getLength(); i++) {
            final Element row = (Element) trs.item(i);
            final NodeList tds = row.getElementsByTagName("td");
            final String date = tds.item(1).getTextContent();
            final String title = tds.item(2).getTextContent();
            if (!date.trim().isEmpty()) {
                final VEvent event = new VEvent(new Date(this.toMiddleOfDay(df.parse(date))), title);
                final UidGenerator ug = new UidGenerator(ManagementFactory.getRuntimeMXBean().getName());
                event.getProperties().add(ug.generateUid());
                ret.getComponents().add(event);
            }
        }
        return ret;
    }

    private java.util.Date toMiddleOfDay(final java.util.Date parsed) {
        //ical4j verkackt das mit der Zeitzone für die Datumswerte, deshalb in die Tagesmitte begeben, dann passt's
        return new java.util.Date(parsed.getTime() + 12 * 60 * 60 * 1000L);
    }

    private Document loadPage() throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final URLConnection conn = this.baseUrl.openConnection();
        //ohne den User-Agent zu setzen kommt ein 403
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        String page = this.readAsString(conn);
        page = page.replace("&hellip;", "...");
        page = page.replace("&raquo;", "»");
        page = page.replace("&laquo;", "«");
        page = page.replace("&bdquo;", "„");
        page = page.replace("&ldquo;", "“");
        //Hacks, weil die Datei kein gültiges XHTML ist und uns sowieso nur der Teil im tbody interessiert
        page = page.replaceAll("(?s)^.*<tbody", "<tbody");
        page = page.replaceAll("(?s)</tbody>.*$", "</tbody>");
        return b.parse(new InputSource(new StringReader(page)));
    }

    private String readAsString(final URLConnection conn) throws IOException {
        final StringWriter buffer = new StringWriter();
        try (InputStream in = conn.getInputStream()) {
            final InputStreamReader r = new InputStreamReader(in,
                    conn.getContentEncoding() == null ? "UTF-8" : conn.getContentEncoding());
            int ch;
            while ((ch = r.read()) >= 0) {
                buffer.write(ch);
            }
        }
        return buffer.toString();
    }

}
