package de.meethub.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

public class UrlUtil {

    public static String readAsString(final URL url) throws IOException {
        final URLConnection conn = url.openConnection();
        //ohne den User-Agent zu setzen kommt meistens ein 403
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        final StringWriter buffer = new StringWriter();
        try (InputStream in = conn.getInputStream()) {
            final InputStreamReader r = new InputStreamReader(in,
                    conn.getContentEncoding() == null ? "UTF-8" : conn.getContentEncoding());
            int ch;
            while ((ch = r.read()) >= 0) {
                buffer.write(ch);
            }
        }
        return buffer.toString();
    }

}
