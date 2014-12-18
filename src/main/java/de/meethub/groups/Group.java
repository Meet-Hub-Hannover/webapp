package de.meethub.groups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Group {

    private static final String USERGROUP_DIR_PARAM = "usergroup.directory";

    private final String name;
    private final String nick;
    private final String website;
    private final String ical;

    private Group(final String name, final String nick, final String website, final String ical) {
        this.name = name;
        this.nick = nick;
        this.website = website;
        this.ical = ical;
    }

    public static List<Group> getGroups(final ServletContext servletContext)
        throws ParserConfigurationException, SAXException, IOException {

        return getGroups(new File(servletContext.getInitParameter(USERGROUP_DIR_PARAM)));
    }

    public static List<Group> getGroups(final File groupDirectory)
        throws ParserConfigurationException, SAXException, IOException {

        if (!groupDirectory.exists()) {
            throw new IllegalArgumentException("Gruppenverzeichnis " + groupDirectory.getAbsolutePath() + " existiert nicht");
        }
        final List<Group> ret = new ArrayList<>();
        final File[] files = groupDirectory.listFiles();
        Arrays.sort(files);
        for (final File groupFile : files) {
            if (groupFile.getName().endsWith(".xml")) {
                ret.add(parseGroup(groupFile));
            }
        }
        return ret;
    }

    private static Group parseGroup(final File file) throws ParserConfigurationException, SAXException, IOException {
        final DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document doc = b.parse(file);
        return new Group(
                getFirstChildText(doc, "name"),
                getFirstChildText(doc, "nickname"),
                getFirstChildText(doc, "url"),
                getFirstChildText(doc, "ical"));
    }

    private static String getFirstChildText(final Document doc, final String tagname) {
        final NodeList elements = doc.getElementsByTagName(tagname);
        return elements.getLength() == 0 ? null : elements.item(0).getTextContent();
    }

    public String getName() {
        return this.name;
    }

    public String getNick() {
        return this.nick;
    }

    public String getIcal() {
        return this.ical;
    }

    public String getWebsite() {
        return this.website;
    }

}
