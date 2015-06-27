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

package de.meethub.adapters.xing;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class XingAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = -2799550772726246839L;

    private String baseUrl;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        this.baseUrl  = config.getInitParameter("base-url");
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {

        final WebDriver d = new HtmlUnitDriver(true);
        try {
            d.get(this.baseUrl);
            new WebDriverWait(d, 10).until(ExpectedConditions.presenceOfElementLocated(By.className("event-preview")));
            final Calendar calendar = this.convertToCalendar(d);
            final CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar;charset=UTF-8");
            outputter.output(calendar, response.getWriter());
        } catch (final ValidationException | ParseException e) {
            throw new ServletException(e);
        } finally {
            d.close();
        }
    }

    private Calendar convertToCalendar(final WebDriver d) throws IOException, ParseException, ServletException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//xing//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        for (final WebElement eventElement : d.findElements(By.className("event-preview"))) {
            final VEvent event = new VEvent(new net.fortuna.ical4j.model.DateTime(
                    this.extractStartTime(eventElement)), this.extractTitle(eventElement));
            final UidGenerator ug = new UidGenerator(ManagementFactory.getRuntimeMXBean().getName());
            event.getProperties().add(ug.generateUid());
            ret.getComponents().add(event);
        }

        return ret;
    }

    private String extractTitle(final WebElement eventElement) {
        return eventElement.findElement(By.className("media-preview-link")).getText().trim();
    }

    private Date extractStartTime(final WebElement eventElement) throws ParseException, ServletException {
        for (final WebElement meta : eventElement.findElements(By.tagName("meta"))) {
            if ("startDate".equals(meta.getAttribute("itemprop"))) {
                final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
                return f.parse(meta.getAttribute("content"));
            }
        }
        throw new ServletException("could not extract date from " + eventElement);
    }

}
