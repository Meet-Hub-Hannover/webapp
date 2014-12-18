package de.meethub.site;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Period;

import org.xml.sax.SAXException;

import de.meethub.groups.Group;
import de.meethub.util.CalendarUtil;
import de.meethub.util.Event;

public class IndexServlet extends HttpServlet {

    private static final long serialVersionUID = 8073715990536713582L;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        final Calendar mergedCalendar = CalendarUtil.loadMergedCalendar();
        final Period nextPeriod = new Period(new DateTime(), new Dur(1));
        final String content =
            "<!doctype html>\r\n" +
            "<html lang=\"de\">\r\n" +
            "<head>\r\n" +
            "    <meta charset=\"utf-8\" />\r\n" +
            "    <title>Meet-Hub Hannover</title>\r\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\" />\r\n" +
            "    <link rel=\"stylesheet\" href=\"tuktuk/tuktuk.css\">\r\n" +
            "    <link rel=\"stylesheet\" href=\"tuktuk/tuktuk.theme.default.css\">\r\n" +
            "    <!-- TUKTUK.WIDGETS -->\r\n" +
            "    <link rel=\"stylesheet\" href=\"tuktuk/tuktuk.icons.css\">\r\n" +
            "</head>\r\n" +
            "<body>\r\n" +
            "    <section class=\"bck theme landing text center\">\r\n" +
            "        <div class=\"row\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <h1>Meet-Hub Hannover</h1>\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <section class=\"bck dark padding text center\">\r\n" +
            "        <div class=\"row padding-bottom\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <h1 class=\"text book color theme\">Nächste Termine</h1>\r\n" +
            this.formatNextEvents(mergedCalendar, nextPeriod) +
            "                <h2>Danach</h2>\r\n" +
            this.formatLaterEvents(mergedCalendar, nextPeriod) +
            "                <p><a href=\"calendarView.html\" class=\"text bold color theme\">Kalenderansicht</a><br/>\r\n" +
            "                <a href=\"mergedCalendar.ics\" class=\"text bold color theme\">Termine als iCal</a></p>\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "    <section class=\"bck light padding\">\r\n" +
            "        <div class=\"row text thin center\">\r\n" +
            "            <div class=\"column_12 color dark\">\r\n" +
            "                <h1 class=\"text book color theme\">Was ist Meet-Hub?</h1>\r\n" +
            "                Ziel von \"Meet-Hub Hannover\" ist, einen Überblick über alle community-getriebenen Veranstaltungen aus dem (informations-)technischen Bereich in und um Hannover zu bieten." +
            "                Community-getrieben umfasst dabei sowohl \"User Groups\" im engeren Sinne, aber auch andere Veranstaltungen mit vorwiegend nicht-kommerziellem Hintergrund." +
            "                Die Nutzung soll für alle Beteiligten so einfach wie möglich sein: Für Besucher ist keine Anmeldung erforderlich, und für die Veranstalter keine Pflege eines weiteren Kalenders.\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <section class=\"bck dark padding\">\r\n" +
            "        <div class=\"row text thin center\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <h1 class=\"text book color theme\">Beteiligte Gruppen</h1>\r\n" +
            this.formatGroupLinks() +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <section class=\"bck light padding\">\r\n" +
            "        <div class=\"row text thin center\">\r\n" +
            "            <div class=\"column_12 color dark\">\r\n" +
            "                <h1 class=\"text book color theme\">Weitere Links</h1>\r\n" +
            "                <a href=\"http://ugrm.coderbyheart.de\" class=\"text bold color theme\">ugrm</a>: eine vergleichbare Initiative für die Region Rhein-Main<br/>\r\n" +
            "                <a href=\"https://github.com/tobiasbaum\" class=\"text bold color theme\">github.com/tobiasbaum</a>: Repository für diese Seite und das zugrundeliegende Framework<br/>\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <footer class=\"padding align center text small bck dark\">\r\n" +
            "        <div class=\"row\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <p>Meet-Hub Hannover ist eine Initiative von <a href=\"http://www.jug-h.de\" class=\"text bold color theme\">JUG Hannover</a> und <a href=\"http://https://www.softwerkskammer.org/groups/hannover\" class=\"text bold color theme\">Softwerkskammer Hannover</a></p>\r\n" +
            "                <a href=\"impressum.html\" class=\"text bold color theme\">Impressum</a>\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </footer>\r\n" +
            "\r\n" +
            "    <script src=\"http://code.jquery.com/jquery-1.9.1.min.js\"></script>\r\n" +
            "    <script src=\"tuktuk/tuktuk.js\"></script>\r\n" +
            "</body>\r\n" +
            "</html>\r\n" +
            "";
        response.getWriter().write(content);
    }

    private String formatNextEvents(final Calendar c, final Period nextPeriod) {
        final List<Event> filtered = Event.getEventsInPeriod(c, nextPeriod);
        return filtered.isEmpty() ? "Keine Termine in der nächsten Woche" : this.formatEvents(filtered);
    }

    private String formatLaterEvents(final Calendar c, final Period nextPeriod) {
        final List<Event> filtered = Event.getEventsInPeriod(c, new Period(nextPeriod.getEnd(), new Dur(7)));
        return filtered.isEmpty() ? "Keine Termine in der nächsten Zeit danach" : this.formatEvents(filtered);
    }

    private String formatEvents(final List<Event> filteredAndSortedEvents) {
        final StringBuilder b = new StringBuilder();
        b.append("<table><tbody>");
        for (final Event e : filteredAndSortedEvents) {
            b.append("<tr>");
            b.append("<td class=\"color light bck dark\">").append(this.formatDow(e.getStart())).append("</td>");
            b.append("<td class=\"color light bck dark\">").append(this.formatDate(e.getStart())).append("</td>");
            b.append("<td class=\"color light bck dark\">");
            if (e.getURL() != null) {
                b.append("<a class=\"color theme\" href=\"");
                b.append(escapeHtml(e.getURL()));
                b.append("\">");
                b.append(escapeHtml(e.getTitle()));
                b.append("</a>");
            } else {
                b.append(e.getTitle());
            }
            b.append("</td>");
            b.append("</tr>\r\n");
        }
        b.append("</tbody></table>");
        return b.toString();
    }

    private static String escapeHtml(final String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    private String formatDow(final Date date) {
        switch (date.getDay()) {
        case 0:
            return "Sonntag";
        case 1:
            return "Montag";
        case 2:
            return "Dienstag";
        case 3:
            return "Mittwoch";
        case 4:
            return "Donnerstag";
        case 5:
            return "Freitag";
        case 6:
            return "Samstag";
        default:
            throw new AssertionError(date.getDay());
        }
    }

    private String formatDate(final Date date) {
        final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }

    private String formatGroupLinks() throws ServletException, IOException {
        try {
            final StringBuilder ret = new StringBuilder();
            for (final Group g : Group.getGroups(this.getServletContext())) {
                if (g.getWebsite() != null) {
                    ret.append("<a href=\"")
                        .append(escapeHtml(g.getWebsite()))
                        .append("\" class=\"text bold color theme\">")
                        .append(escapeHtml(g.getName()))
                        .append("</a>");
                } else {
                    ret.append(g.getName());
                }
                ret.append("<br/>\r\n");
            }
            return ret.toString();
        } catch (ParserConfigurationException | SAXException e) {
            throw new ServletException(e);
        }
    }
}