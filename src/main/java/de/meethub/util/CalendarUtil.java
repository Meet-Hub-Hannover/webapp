package de.meethub.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
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

public class CalendarUtil {

    public static Calendar loadMergedCalendar(final ServletContext ctx) throws IOException, ServletException {
        return loadCalendar(new URL(ctx.getInitParameter("root.url") + "mergedCalendar.ics"));
    }

    public static Calendar loadCalendar(final URL url) throws IOException, ServletException {
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
