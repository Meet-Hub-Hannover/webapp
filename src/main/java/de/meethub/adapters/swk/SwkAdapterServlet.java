package de.meethub.adapters.swk;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Summary;
import de.meethub.util.CalendarUtil;

public class SwkAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = 4520397332083399423L;

    private URL baseUrl;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        try {
            this.baseUrl  = new URL(config.getInitParameter("base-url"));
        } catch (final MalformedURLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final Calendar c = CalendarUtil.loadCalendar(this.baseUrl);

        final Iterator<?> iter = c.getComponents().iterator();
        while (iter.hasNext()) {
            final Object o = iter.next();
            if ((o instanceof VEvent) && !this.isHannoverEvent((VEvent) o)) {
                iter.remove();
            }
        }

        CalendarUtil.writeCalendar(c, response);
    }

    private boolean isHannoverEvent(final VEvent o) {
        final Summary s = o.getSummary();
        return s != null && s.getValue().toLowerCase().contains("hannover");
    }

}
