package de.meethub.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

public class CalendarUtil {

    public static Calendar loadMergedCalendar() throws IOException, ServletException {
        final CalendarBuilder b = new CalendarBuilder();
        final URLConnection conn = new URL("http://localhost:8080/Meethub-Hannover/mergedCalendar.ics").openConnection();
        try (InputStream in = conn.getInputStream()) {
            return b.build(in);
        } catch (final ParserException e) {
            throw new ServletException(e);
        }
    }

}
