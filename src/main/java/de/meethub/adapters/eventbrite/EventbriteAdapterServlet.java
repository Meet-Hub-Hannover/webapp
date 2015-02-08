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

package de.meethub.adapters.eventbrite;

import java.io.IOException;
import java.text.ParseException;
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
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EventbriteAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = -279955077272627865L;

    private String baseUrl;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        this.baseUrl = config.getInitParameter("base-url");
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        final WebDriver d = new HtmlUnitDriver();
        try {
            d.get(this.baseUrl);
            new WebDriverWait(d, 10).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("js-event-list-container")));
            final List<WebElement> eventElements = d.findElements(By.className("event-card__description"));
            final Calendar calendar = this.convertToCalendar(eventElements);
            final CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar;charset=UTF-8");
            outputter.output(calendar, response.getWriter());
        } catch (final ValidationException | ParseException e) {
            throw new ServletException(e);
        } finally {
            d.close();
        }

    }

    private Calendar convertToCalendar(final List<WebElement> eventElements) throws ParseException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//hjs//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        for (final WebElement eventElement : eventElements) {
            final VEvent event = this.convertEvent(eventElement);
            ret.getComponents().add(event);
        }

        return ret;
    }

    private VEvent convertEvent(final WebElement eventElement) throws ParseException {
        final String title = eventElement.findElement(By.className("event-card__header")).getText();
        final String dateText = eventElement.findElement(By.className("event-card__details")).getText();
        final Date parsedDate = parseDate(dateText);
        final VEvent event = new VEvent(
                new net.fortuna.ical4j.model.DateTime(parsedDate),
                title);
        event.getProperties().add(new Uid("mhe" + Integer.toHexString(this.baseUrl.hashCode()) + parsedDate.getTime()));
        return event;
    }

    private static Date parseDate(final String dateElementContent) throws ParseException {
        final Pattern p = Pattern.compile("([A-Za-z]+) ([0-9]+), ([0-9]+) ([0-9]+):([0-9]+) (PM|AM).*");
        final Matcher m = p.matcher(dateElementContent);
        if (!m.matches()) {
            throw new ParseException("date not in expected format: " + dateElementContent, 0);
        }
        final int month = convertMonth(m.group(1).toLowerCase());
        final int day = Integer.parseInt(m.group(2));
        final int year = Integer.parseInt(m.group(3));
        final int hour = Integer.parseInt(m.group(4));
        final int minute = Integer.parseInt(m.group(5));
        final boolean pm = m.group(6).equalsIgnoreCase("PM");
        final GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        currentDate.set(year, month - 1, day, hour + (pm ? 12 : 0), minute, 0);
        return currentDate.getTime();
    }

    private static int convertMonth(final String monthName) throws ParseException {
        if (monthName.equals("jan")) {
            return 1;
        } else if (monthName.equals("feb")) {
            return 2;
        } else if (monthName.equals("mar")) {
            return 3;
        } else if (monthName.equals("apr")) {
            return 4;
        } else if (monthName.equals("may")) {
            return 5;
        } else if (monthName.equals("jun")) {
            return 6;
        } else if (monthName.equals("jul")) {
            return 7;
        } else if (monthName.equals("aug")) {
            return 8;
        } else if (monthName.equals("sep")) {
            return 9;
        } else if (monthName.equals("oct")) {
            return 10;
        } else if (monthName.equals("nov")) {
            return 11;
        } else if (monthName.equals("dec")) {
            return 12;
        } else {
            throw new ParseException("invalid month name: " + monthName, 0);
        }
    }

}
