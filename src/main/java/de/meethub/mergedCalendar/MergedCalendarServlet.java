package de.meethub.mergedCalendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.XProperty;

import org.xml.sax.SAXException;

import de.meethub.groups.Group;
import de.meethub.util.Pair;

public class MergedCalendarServlet extends HttpServlet {

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final long serialVersionUID = -9179228552541564015L;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/calendar;charset=UTF-8");
        final PrintWriter out = response.getWriter();
        try {
            final List<Pair<URL, Group>> urls = this.getCalendarUrls();
            final Calendar c = this.mergeCalendars(urls);
            final CalendarOutputter outputter = new CalendarOutputter(false);
            outputter.output(c, out);
        } catch (final ValidationException | ParserConfigurationException | SAXException | URISyntaxException e) {
            throw new ServletException(e);
        }
    }

    private List<Pair<URL, Group>> getCalendarUrls() throws IOException, ParserConfigurationException, SAXException {
        final List<Pair<URL, Group>> urls = new ArrayList<>();
        for (final Group g : Group.getGroups(this.getServletContext())) {
            if (g.getIcal() != null) {
                urls.add(Pair.create(new URL(g.getIcal()), g));
            }
        }
        return urls;
    }

    private void dumpUrl(final URL url, final PrintWriter out) throws IOException {
        final URLConnection conn = url.openConnection();
        try (InputStream in = conn.getInputStream()) {
            final String encoding = conn.getContentEncoding();
            final Reader r = new InputStreamReader(in, encoding == null ? DEFAULT_CHARSET : encoding);
            int b;
            while ((b = r.read()) >= 0) {
                out.write(b);
            }
        }
    }

    private Calendar mergeCalendars(final List<Pair<URL, Group>> urls) throws URISyntaxException {
        Calendar merged = null;
        for (final Pair<URL, Group> url : urls) {
            try {
                final Calendar cur = this.readCalendar(url.getFirst());
                this.adjustEvents(cur, url.getSecond());
                if (merged == null) {
                    merged = cur;
                } else {
                    this.mergeCalendars(merged, cur);
                }
            } catch (final IOException | ParserException e) {
                this.log("skipping calendar " + url.getFirst() + " due to exception", e);
            }
        }
        if (merged == null) {
            merged = new Calendar();
        }
        this.replaceXProperty(merged, "X-WR-CALNAME", "Meet-Hub Hannover");
        this.replaceXProperty(merged, "X-WR-CALDESC", "Zusammenfassung diverser Termine von UserGroups etc im Umfeld Hannovers");
        return merged != null ? merged : new Calendar();
    }

    private void adjustEvents(final Calendar calendar, final Group group) throws URISyntaxException {
        for (final Object o : calendar.getComponents()) {
            final Component c = (Component) o;
            final Property old = c.getProperty(Summary.SUMMARY);
            if (old != null) {
                c.getProperties().remove(old);
                c.getProperties().add(new Summary(group.getNick() + ": " + old.getValue()));
            }
            if (c instanceof VEvent && c.getProperty(Property.URL)  == null && group.getWebsite() != null) {
                c.getProperties().add(new Url(new URI(group.getWebsite())));
            }
        }
    }

    private void replaceXProperty(final Calendar merged, final String key, final String newValue) {
        merged.getProperties().removeAll(merged.getProperties(key));
        merged.getProperties().add(new XProperty(key, newValue));
    }

    private void mergeCalendars(final Calendar first, final Calendar second) {
        for (final Object c : second.getComponents()) {
            first.getComponents().add((Component) c);
        }
    }

    private Calendar readCalendar(final URL url) throws IOException, ParserException {
        final CalendarBuilder b = new CalendarBuilder();
        final URLConnection conn = url.openConnection();
        try (InputStream in = conn.getInputStream()) {
            final String encoding = conn.getContentEncoding();
            return b.build(new InputStreamReader(in, encoding == null ? DEFAULT_CHARSET : encoding));
        }
    }

}
