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

package de.meethub.adapters.hannoverjs;

import java.io.IOException;
import java.text.ParseException;
import java.util.GregorianCalendar;
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

public class HjsAdapterServlet extends HttpServlet {

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

        final WebDriver d = new HtmlUnitDriver(true);
        try {
            d.get(this.baseUrl);
            final WebElement dateElement = new WebDriverWait(d, 10).until(
                    ExpectedConditions.presenceOfElementLocated(By.className("ng-binding")));
            final Calendar calendar = convertToCalendar(dateElement);
            final CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar;charset=UTF-8");
            outputter.output(calendar, response.getWriter());
        } catch (final ValidationException | ParseException e) {
            throw new ServletException(e);
        } finally {
            d.close();
        }

    }

    /**
     * Konvertiert ins Zielformat yyyyMMdd.
     */
    private static String parseDate(final String dateElementContent) throws ParseException {

        final Pattern p = Pattern.compile("([0-9]+)[a-z]* of ([A-Za-z]+)");
        final Matcher m = p.matcher(dateElementContent);
        if (!m.matches()) {
            throw new ParseException("date not in expected format: " + dateElementContent, 0);
        }
        final int day = Integer.parseInt(m.group(1));
        final int month = convertMonth(m.group(2).toLowerCase());
        final GregorianCalendar currentDate = new GregorianCalendar();
        final int year = currentDate.get(GregorianCalendar.YEAR) + (month < currentDate.get(GregorianCalendar.MONTH) + 1 ? 1 : 0);
        return String.format("%04d%02d%02d", year, month, day);
    }

    private static int convertMonth(final String monthName) throws ParseException {
        if (monthName.equals("january")) {
            return 1;
        } else if (monthName.equals("february")) {
            return 2;
        } else if (monthName.equals("march")) {
            return 3;
        } else if (monthName.equals("april")) {
            return 4;
        } else if (monthName.equals("may")) {
            return 5;
        } else if (monthName.equals("june")) {
            return 6;
        } else if (monthName.equals("july")) {
            return 7;
        } else if (monthName.equals("august")) {
            return 8;
        } else if (monthName.equals("september")) {
            return 9;
        } else if (monthName.equals("october")) {
            return 10;
        } else if (monthName.equals("november")) {
            return 11;
        } else if (monthName.equals("december")) {
            return 12;
        } else {
            throw new ParseException("invalid month name: " + monthName, 0);
        }
    }

    static Calendar convertToCalendar(final WebElement dateElement) throws ParseException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//hjs//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        final String parsedDate = parseDate(dateElement.getText());
        final VEvent event = new VEvent(
                new net.fortuna.ical4j.model.Date(parsedDate),
                "Hannover.js meetup");
        event.getProperties().add(new Uid("hjs" + parsedDate));
        ret.getComponents().add(event);

        return ret;
    }

}
