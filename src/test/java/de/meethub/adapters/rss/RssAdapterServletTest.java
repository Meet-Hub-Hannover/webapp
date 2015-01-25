package de.meethub.adapters.rss;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class RssAdapterServletTest {

    private static Document loadDocument(final String doc) throws Exception {
        final DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return b.parse(new InputSource(new StringReader(doc)));
    }

    @Test
    public void testToCalendar() throws Exception {
        final String doc =
                "<rss xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">\r\n" +
                "<channel>\r\n" +
                "<title>LeineLab - Jan 2015</title>\r\n" +
                "<link>http://cal.leinelab.de:80/rss/rss2.0.php/</link>\r\n" +
                "<description>LeineLab Kalender - Jan 2015</description>\r\n" +
                "<language>de</language>\r\n" +
                "<item>\r\n" +
                "<guid isPermaLink=\"false\">\r\n" +
                "http://cal.leinelab.de:80/day.php?getdate=20150101&amp;cal=3f0391bdd49de56f96c90b3e66f46b09&amp;uid=3c28a732-4cc2-435d-90cc-002cc465e3ed\r\n" +
                "</guid>\r\n" +
                "<title>Do Jan 1 2015: Un-Cloud yourself</title>\r\n" +
                "<ev:startdate>2015-01-01T19:00:00</ev:startdate>\r\n" +
                "<ev:enddate>2015-01-01T22:30:00</ev:enddate>\r\n" +
                "<link>\r\n" +
                "http://cal.leinelab.de:80/day.php?getdate=20150101&amp;cal=3f0391bdd49de56f96c90b3e66f46b09\r\n" +
                "</link>\r\n" +
                "<description>Do Jan 1 2015 19:00:</description>\r\n" +
                "<ev:location>LeineLab%2C+Glocksee</ev:location>\r\n" +
                "</item>\r\n" +
                "<item>\r\n" +
                "<guid isPermaLink=\"false\">\r\n" +
                "http://cal.leinelab.de:80/day.php?getdate=20150102&amp;cal=3f0391bdd49de56f96c90b3e66f46b09&amp;uid=62761fcb-7145-49df-9cce-3fb7521b254b\r\n" +
                "</guid>\r\n" +
                "<title>Fr Jan 2 2015: Freitagstreff</title>\r\n" +
                "<ev:startdate>2015-01-02T18:00:00</ev:startdate>\r\n" +
                "<ev:enddate>2015-01-02T23:59:00</ev:enddate>\r\n" +
                "<link>\r\n" +
                "http://cal.leinelab.de:80/day.php?getdate=20150102&amp;cal=3f0391bdd49de56f96c90b3e66f46b09\r\n" +
                "</link>\r\n" +
                "<description>Fr Jan 2 2015 18:00:</description>\r\n" +
                "<ev:location>LeineLab%2C+Glocksee</ev:location>\r\n" +
                "</item>\r\n" +
                "</channel>\r\n" +
                "</rss>";
        final Calendar c = RssAdapterServlet.convertToCalendar(loadDocument(doc));
        assertEquals(2, c.getComponents().size());

        final VEvent ev1 = (VEvent) c.getComponents().get(0);
        assertEquals("Un-Cloud yourself", ev1.getSummary().getValue());
        assertEquals("20150101T190000", ev1.getStartDate().getValue());
        assertEquals("http://cal.leinelab.de:80/day.php?getdate=20150101&cal=3f0391bdd49de56f96c90b3e66f46b09", ev1.getUrl().getValue());

        final VEvent ev2 = (VEvent) c.getComponents().get(1);
        assertEquals("Freitagstreff", ev2.getSummary().getValue());
        assertEquals("20150102T180000", ev2.getStartDate().getValue());
        assertEquals("http://cal.leinelab.de:80/day.php?getdate=20150102&cal=3f0391bdd49de56f96c90b3e66f46b09", ev2.getUrl().getValue());
    }

}
