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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

public class HjsJsonAdapterServlet extends HttpServlet {

    private static final long serialVersionUID = -279955077272627865L;

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

        final URLConnection c = this.baseUrl.openConnection();
        final InputStream s = c.getInputStream();
        try {
            final JsonValue data = Json.parse(new InputStreamReader(s, c.getContentEncoding()));
            final Calendar calendar = convertToCalendar(data);
            final CalendarOutputter outputter = new CalendarOutputter();
            response.setContentType("text/calendar;charset=UTF-8");
            outputter.output(calendar, response.getWriter());
        } catch (final ValidationException | ParseException | URISyntaxException e) {
            throw new ServletException(e);
        } finally {
            s.close();
        }

    }

    static Calendar convertToCalendar(final JsonValue data) throws ParseException, URISyntaxException {
        final Calendar ret = new Calendar();
        ret.getProperties().add(new ProdId("-//Meet-Hub Hannover//hjs//DE"));
        ret.getProperties().add(Version.VERSION_2_0);
        ret.getProperties().add(CalScale.GREGORIAN);

        final JsonArray array = data.asArray();
        for (final JsonValue v : array) {
            final JsonObject obj = v.asObject();
            final Date parsedDate = Date.from(Instant.parse(obj.get("date").asString()));
            final VEvent event = new VEvent(
                    new net.fortuna.ical4j.model.Date(parsedDate),
                    obj.get("venue").asObject().get("name").asString());
            event.getProperties().add(new Uid("hjs" + parsedDate));
            final String link = obj.get("meetup_url").asString();
            event.getProperties().add(new net.fortuna.ical4j.model.property.Url(new URI(link)));
            ret.getComponents().add(event);
        }

        return ret;
    }

}
