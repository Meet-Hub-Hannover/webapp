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
        final Calendar mergedCalendar = CalendarUtil.loadMergedCalendar(this.getServletContext());
        final Period nextPeriod = new Period(new DateTime(), new Dur(1));
        final String content =
            "<!doctype html>\r\n" +
            "<html lang=\"de\">\r\n" +
            "<head>\r\n" +
            "    <meta charset=\"utf-8\" />\r\n" +
            "    <title>Meet-Hub Hannover</title>\r\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\" />\r\n" +
            "    <link rel=\"shortcut icon\" href=\"favicon.ico\" />" +
            "    <link rel=\"stylesheet\" href=\"tuktuk/tuktuk.css\" />\r\n" +
            "    <link rel=\"stylesheet\" href=\"tuktuk/tuktuk.theme.default.css\" />\r\n" +
            "    <!-- TUKTUK.WIDGETS -->\r\n" +
            "    <link rel=\"stylesheet\" href=\"tuktuk/tuktuk.icons.css\" />\r\n" +
            "    <link href=\"//cdn-images.mailchimp.com/embedcode/slim-081711.css\" rel=\"stylesheet\" type=\"text/css\" />\r\n" +
            "    <style type=\"text/css\">\r\n" +
            "      #mc_embed_signup{background:#fff; clear:left; font:14px Helvetica,Arial,sans-serif; }\r\n" +
            "      /* Add your own MailChimp form style overrides in your site stylesheet or in this style block.\r\n" +
            "         We recommend moving this block and the preceding CSS link to the HEAD of your HTML file. */\r\n" +
            "    </style>\r\n" +
            "</head>\r\n" +
            "<body>\r\n" +
            "    <section class=\"bck theme landing text center\">\r\n" +
            "        <div class=\"row\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <img src=\"meethub2.svg\" width=\"70%\" alt=\"Meet-Hub Hannover\" />\r\n" +
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
            "                Die Nutzung soll für alle Beteiligten so einfach wie möglich sein: Für Besucher ist keine Registrierung erforderlich, und für die Veranstalter keine Pflege eines weiteren Kalenders.\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <section class=\"bck dark padding\">\r\n" +
            "        <div class=\"row text thin center\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <h1 class=\"text book color theme\">Veranstaltungs-Newsletter</h1>\r\n" +
            "                Wöchentlicher Newsletter mit den in der nächsten Zeit anliegenden Terminen." +
            "<!-- Begin MailChimp Signup Form -->\r\n" +
            "<form action=\"//meet-hub-hannover.us9.list-manage.com/subscribe/post?u=00b6e9d7af18838b8580a03ee&amp;id=a76317b2eb\" method=\"post\" id=\"mc-embedded-subscribe-form\" name=\"mc-embedded-subscribe-form\" class=\"validate\" target=\"_blank\" novalidate>\r\n" +
            "    <div id=\"mc_embed_signup_scroll\">\r\n" +
            "    \r\n" +
            "    <input type=\"email\" value=\"\" name=\"EMAIL\" class=\"email\" id=\"mce-EMAIL\" placeholder=\"email address\" required>\r\n" +
            "    <!-- real people should not fill this in and expect good things - do not remove this or risk form bot signups-->\r\n" +
            "    <div style=\"position: absolute; left: -5000px;\"><input type=\"text\" name=\"b_00b6e9d7af18838b8580a03ee_a76317b2eb\" tabindex=\"-1\" value=\"\"></div>\r\n" +
            "    <div class=\"clear\"><input type=\"submit\" value=\"Für Newsletter registrieren\" name=\"subscribe\" id=\"mc-embedded-subscribe\" class=\"button\"></div>\r\n" +
            "    </div>\r\n" +
            "</form>\r\n" +
            "\r\n" +
            "<!--End mc_embed_signup-->" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <section class=\"bck light padding\">\r\n" +
            "        <div class=\"row text thin center\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <h1 class=\"text book color theme\">Beteiligte Gruppen</h1>\r\n" +
            this.formatGroupLinks() +
            "                <br/>\r\n" +
            "                Wenn wir euch auch aufnehmen sollen, sendet eine E-Mail an \"webmaster at meet-hub-hannover.de\"." +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <section class=\"bck dark padding\">\r\n" +
            "        <div class=\"row text thin center\">\r\n" +
            "            <div class=\"column_12 color dark\">\r\n" +
            "                <h1 class=\"text book color theme\">Weitere Links</h1>\r\n" +
            "                <a href=\"http://ugrm.coderbyheart.de\" class=\"text bold color theme\">ugrm</a>: eine vergleichbare Initiative für die Region Rhein-Main<br/>\r\n" +
            "                <a href=\"https://github.com/Meet-Hub-Hannover\" class=\"text bold color theme\">github.com/Meet-Hub-Hannover</a>: Repository für diese Seite und die zugrundeliegende Web-Anwendung<br/>\r\n" +
            "                <a href=\"https://www.softwerkskammer.org/groups/hannover-meetups\" class=\"text bold color theme\">www.softwerkskammer.org/groups/hannover-meetups</a>: Die Themengruppe bei der Softwerkskammer<br/>\r\n" +
            "            </div>\r\n" +
            "        </div>\r\n" +
            "    </section>\r\n" +
            "\r\n" +
            "    <footer class=\"padding align center text small bck light\">\r\n" +
            "        <div class=\"row\">\r\n" +
            "            <div class=\"column_12\">\r\n" +
            "                <p>Meet-Hub Hannover ist eine Initiative von <a href=\"https://www.softwerkskammer.org/groups/hannover\" class=\"text bold color theme\">Softwerkskammer Hannover</a> und <a href=\"http://www.jug-h.de\" class=\"text bold color theme\">JUG Hannover</a></p>\r\n" +
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
