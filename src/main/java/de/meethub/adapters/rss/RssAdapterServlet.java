/**
    This file is part of Meet-Hub-Hannover.

    Meet-Hub-Hannover is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Meet-Hub-Hannover is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Meet-Hub-Hannover. If not, see <http://www.gnu.org/licenses/>.
 */

package de.meethub.adapters.rss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

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
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class RssAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = -2799550772726246838L;

    private URL baseUrl;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
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
            final Calendar calendar = convertToCalendar(table);
            final CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar;charset=UTF-8");
            outputter.output(calendar, response.getWriter());
        } catch (SAXException | ParserConfigurationException | ParseException | ValidationException | URISyntaxException e1) {
            throw new ServletException(e1);
        }
    }

    static Calendar convertToCalendar(final Document rss) throws ParseException, URISyntaxException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//rss//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        final NodeList items = rss.getElementsByTagName("item");
        for (int i = 0; i < items.getLength(); i++) {
            final Element item = (Element) items.item(i);
            final String guid = getElementContent(item, "guid");
            final String date = getElementContent(item, "ev:startdate");
            final String title = getElementContent(item, "title");
            final String link = getElementContent(item, "link");

            final VEvent event = new VEvent(new DateTime(df.parse(date)), stripDate(title));
            event.getProperties().add(new Uid(guid));
            if (!link.isEmpty()) {
                event.getProperties().add(new net.fortuna.ical4j.model.property.Url(new URI(link)));
            }
            ret.getComponents().add(event);
        }
        return ret;
    }

    private static String stripDate(final String title) {
        final int colonIndex = title.indexOf(':');
        if (colonIndex >= 0) {
            return title.substring(colonIndex + 1).trim();
        } else {
            return title;
        }
    }

    private static String getElementContent(final Element item, final String tagName) {
        final NodeList subitems = item.getElementsByTagName(tagName);
        if (subitems.getLength() == 0) {
            return "";
        }
        return subitems.item(0).getTextContent().trim();
    }

    private Document loadPage() throws SAXException, IOException, ParserConfigurationException {
        final DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(new InputSource(this.baseUrl.openStream()));
    }

}
