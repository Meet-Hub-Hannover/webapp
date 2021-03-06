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

package de.meethub.ical2json;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import de.meethub.util.CalendarUtil;
import de.meethub.util.Event;

public class JsonEventsServlet extends HttpServlet {

    private static final long serialVersionUID = 7514090375502252915L;

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        System.out.println("json request " + request.getParameterMap());
        try {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            final Period period = new Period(
                    new DateTime(df.parse(request.getParameter("start"))),
                    new DateTime(df.parse(request.getParameter("end"))));
            final Calendar c = CalendarUtil.loadMergedCalendar(this.getServletContext());

            final PrintWriter out = response.getWriter();
            boolean first = true;
            out.write('[');
            for (final Event e : Event.getEventsInPeriod(c, period)) {
                if (first) {
                    first = false;
                } else {
                    out.write(',');
                }
                out.write('{');
                writeJsonValue(out, "title", e.getTitle());
                out.write(',');
                writeJsonValue(out, "start", e.getStart());
                if (e.getURL() != null) {
                    out.write(',');
                    writeJsonValue(out, "url", e.getURL());
                }
                out.write('}');
            }
            out.write(']');
        } catch (final ParseException e) {
            throw new ServletException(e);
        }
    }

    private static void writeJsonValue(final Writer out, final String key, final Date value) throws IOException {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        writeJsonValue(out, key, f.format(value));
    }

    private static void writeJsonValue(final Writer out, final String key, final String value) throws IOException {
        out.write('"');
        out.write(key);
        out.write("\":\"");
        out.write(value.replace("\\", "\\\\").replace("\"", "\\\""));
        out.write('"');
    }
}
