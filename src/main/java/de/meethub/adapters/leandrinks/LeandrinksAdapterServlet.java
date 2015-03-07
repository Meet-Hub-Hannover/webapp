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

package de.meethub.adapters.leandrinks;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;
import de.meethub.util.Pair;
import de.meethub.util.UrlUtil;

public class LeandrinksAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = -2799550772726246839L;

    private URL baseUrl;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        try {
            this.baseUrl  = new URL(config.getInitParameter("base-url"));
        } catch (final MalformedURLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        try {
            final Calendar calendar = this.createCalendar();
            final CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar;charset=UTF-8");
            outputter.output(calendar, response.getWriter());
        } catch (final ValidationException e1) {
            throw new ServletException(e1);
        }
    }

    private Calendar createCalendar() throws IOException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//leandrinks//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        for (final Pair<String, java.util.Date> eventData : extractEvents(UrlUtil.readAsString(this.baseUrl))) {
            final VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(eventData.getSecond()), eventData.getFirst());
            final UidGenerator ug = new UidGenerator(ManagementFactory.getRuntimeMXBean().getName());
            event.getProperties().add(ug.generateUid());
            ret.getComponents().add(event);
        }

        return ret;
    }

    public static List<Pair<String, Date>> extractEvents(final String content) {
        final Pattern pattern = Pattern.compile(
                "(\\d\\d)\\.(\\d\\d)\\.(\\d\\d\\d\\d) - (\\d\\d):(\\d\\d).{1,300}>[^<]{3,}</.{1,300}>([^<]{3,})</",
                Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(content);
        final List<Pair<String, Date>> ret = new ArrayList<>();
        while (matcher.find()) {
            final GregorianCalendar date = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
            date.clear();
            date.set(GregorianCalendar.DAY_OF_MONTH, Integer.parseInt(matcher.group(1)));
            date.set(GregorianCalendar.MONTH, Integer.parseInt(matcher.group(2)) - 1);
            date.set(GregorianCalendar.YEAR, Integer.parseInt(matcher.group(3)));
            date.set(GregorianCalendar.HOUR, Integer.parseInt(matcher.group(4)));
            date.set(GregorianCalendar.MINUTE, Integer.parseInt(matcher.group(5)));
            final String text = matcher.group(6);
            ret.add(Pair.create(
                    text,
                    date.getTime()));
        }
        return ret;
    }

}
