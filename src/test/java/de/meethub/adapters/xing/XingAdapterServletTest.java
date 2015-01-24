package de.meethub.adapters.xing;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.junit.Test;

import de.meethub.util.Pair;

public class XingAdapterServletTest {

    private static Date date(final int day, final int month, final int year, final int hour, final int minute) {
        final GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        cal.clear();
        cal.set(year, month - 1, day, hour, minute);
        return cal.getTime();
    }

    @Test
    public void testExtractSingle() {
        final String content =
            "    <ul>\r\n" +
            "        <li>\r\n" +
            "          <a class=\"\" href=\"/events/cocoaheads-hannover-februar-2015-1492348\"><img alt=\"CocoaHeads Hannover Februar 2015\" itemprop=\"image\" src=\"https://www.xing.com/img/custom/events/events_files/2/2/0/557600/ad_big/XING-Gruppenlogo.jpg?1420487028\" title=\"\" /></a><br>\r\n" +
            "          <strong><a data-masked-link=\"e670244745d67e21.l4uLj4zF0NCIiIjRh5aRmNGckJLQmomakYuM0JyQnJCel5qem4zSl56RkZCJmo3SmZqdjYqejdLNz87K0s7Lxs3My8c\">CocoaHeads Hannover Februar 2015</a></strong>\r\n" +
            "          <div>Mo, 02.02.2015, 19:00</div><meta itemprop=\"startDate\" content=\"2015-02-02T18:00:00Z\">\r\n" +
            "          <address>Hannover</address>\r\n" +
            "        </li>\r\n" +
            "    </ul>\r\n";
        final List<Pair<String, Date>> result = XingAdapterServlet.extractEvents(content);
        assertEquals(1, result.size());
        assertEquals(Pair.create("CocoaHeads Hannover Februar 2015", date(2, 2, 2015, 19, 0)), result.get(0));
    }

    @Test
    public void testExtractMultiple() {
        final String content =
            "    <ul>\r\n" +
            "        <li>\r\n" +
            "          <a class=\"\" href=\"/events/cocoaheads-hannover-februar-2015-1492348\"><img alt=\"CocoaHeads Hannover Februar 2015\" itemprop=\"image\" src=\"https://www.xing.com/img/custom/events/events_files/2/2/0/557600/ad_big/XING-Gruppenlogo.jpg?1420487028\" title=\"\" /></a><br>\r\n" +
            "          <strong><a data-masked-link=\"e670244745d67e21.l4uLj4zF0NCIiIjRh5aRmNGckJLQmomakYuM0JyQnJCel5qem4zSl56RkZCJmo3SmZqdjYqejdLNz87K0s7Lxs3My8c\">CocoaHeads Hannover Februar 2015</a></strong>\r\n" +
            "          <div>Mo, 02.02.2015, 19:00</div><meta itemprop=\"startDate\" content=\"2015-02-02T18:00:00Z\">\r\n" +
            "          <address>Hannover</address>\r\n" +
            "        </li>\r\n" +
            "    </ul>\r\n" +
            "    <ul>\r\n" +
            "        <li>\r\n" +
            "          <a class=\"\" href=\"/events/cocoaheads-hannover-februar-2015-1492348\"><img alt=\"zweiter Termin\" itemprop=\"image\" src=\"https://www.xing.com/img/custom/events/events_files/2/2/0/557600/ad_big/XING-Gruppenlogo.jpg?1420487028\" title=\"\" /></a><br>\r\n" +
            "          <strong><a data-masked-link=\"e670244745d67e21.l4uLj4zF0NCIiIjRh5aRmNGckJLQmomakYuM0JyQnJCel5qem4zSl56RkZCJmo3SmZqdjYqejdLNz87K0s7Lxs3My8c\">zweiter Termin</a></strong>\r\n" +
            "          <div>Di, 03.02.2015, 18:00</div><meta itemprop=\"startDate\" content=\"2015-02-03T17:00:00Z\">\r\n" +
            "          <address>Hannover</address>\r\n" +
            "        </li>\r\n" +
            "    </ul>\r\n" +
            "    <ul>\r\n" +
            "        <li>\r\n" +
            "          <a class=\"\" href=\"/events/cocoaheads-hannover-februar-2015-1492348\"><img alt=\"dritter Termin\" itemprop=\"image\" src=\"https://www.xing.com/img/custom/events/events_files/2/2/0/557600/ad_big/XING-Gruppenlogo.jpg?1420487028\" title=\"\" /></a><br>\r\n" +
            "          <strong><a data-masked-link=\"e670244745d67e21.l4uLj4zF0NCIiIjRh5aRmNGckJLQmomakYuM0JyQnJCel5qem4zSl56RkZCJmo3SmZqdjYqejdLNz87K0s7Lxs3My8c\">dritter Termin</a></strong>\r\n" +
            "          <div>Mo, 02.03.2015, 19:00</div><meta itemprop=\"startDate\" content=\"2015-03-02T18:00:00Z\">\r\n" +
            "          <address>Hannover</address>\r\n" +
            "        </li>\r\n" +
            "    </ul>\r\n";
        final List<Pair<String, Date>> result = XingAdapterServlet.extractEvents(content);
        assertEquals(3, result.size());
        assertEquals(Pair.create("CocoaHeads Hannover Februar 2015", date(2, 2, 2015, 19, 0)), result.get(0));
        assertEquals(Pair.create("zweiter Termin", date(3, 2, 2015, 18, 0)), result.get(1));
        assertEquals(Pair.create("dritter Termin", date(2, 3, 2015, 19, 0)), result.get(2));
    }

}
