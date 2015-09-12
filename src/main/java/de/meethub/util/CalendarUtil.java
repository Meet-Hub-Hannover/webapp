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

package de.meethub.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.CompatibilityHints;

public class CalendarUtil {

    public static Calendar loadMergedCalendar(final ServletContext ctx) throws IOException, ServletException {
        return loadCalendar(getMergedCalendarUtl(ctx));
    }

    public static URL getMergedCalendarUtl(final ServletContext ctx) throws MalformedURLException {
        return new URL(ctx.getInitParameter("root.url") + "mergedCalendar.ics");
    }

    public static Calendar loadCalendar(final URL url) throws IOException, ServletException {
        //noetig wegen ical4j-Bug #167
        CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_RELAXED_PARSING, true);
        final CalendarBuilder b = new CalendarBuilder();
        final URLConnection conn = url.openConnection();
        try (InputStream in = conn.getInputStream()) {
            return b.build(in);
        } catch (final ParserException e) {
            throw new ServletException(e);
        }
    }

    public static void writeCalendar(final Calendar c, final HttpServletResponse response)
        throws IOException, ServletException {

        response.setContentType("text/calendar;charset=UTF-8");
        final PrintWriter out = response.getWriter();
        try {
            final CalendarOutputter outputter = new CalendarOutputter(false);
            outputter.output(c, out);
        } catch (final ValidationException e) {
            throw new ServletException(e);
        }
    }

}
