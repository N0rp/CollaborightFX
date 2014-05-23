package eu.dowsing.collaborightfx.xmpp.app;

import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

/**
 * Takes xml pull parser data and creates a regular xml structure again.
 * 
 * @author richardg
 * 
 */
public class XmlPullUnparser {

    private String xml = "";
    private String firstTag = "";

    private XmlPullUnparser() {
    }

    public String getXml() {
        return xml;
    }

    public String getFirstTag() {
        return firstTag;
    }

    /**
     * Takes the parser and creates an xml string again.
     * 
     * @param xpp
     *            a parser
     * @oaram minimal if <tt>true</tt> non-requirement spaces and new lines are omitted
     * @return an xml string in a format like <root><childNode attribute1="value1" ...>...</childNode></root>
     * @throws Exception
     */
    public static XmlPullUnparser unparse(XmlPullParser xpp, int spacesPerDepthLevel, boolean minimal) throws Exception {

        XmlPullUnparser unparser = new XmlPullUnparser();

        final boolean trimContent = true;
        boolean foundFirstTag = false;

        int eventType = xpp.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            int depth = xpp.getDepth();

            if (eventType == XmlPullParser.START_DOCUMENT) {
                // System.out.println("Start document");
            } else if (eventType == XmlPullParser.END_DOCUMENT) {
                // System.out.println("End document");
            } else if (eventType == XmlPullParser.START_TAG) {
                if (!foundFirstTag) {
                    foundFirstTag = true;
                    unparser.firstTag = xpp.getName();
                }
                unparser.xml += createString("<" + xpp.getName() + "" + attributesToString(xpp) + ">", depth,
                        spacesPerDepthLevel, minimal);
            } else if (eventType == XmlPullParser.END_TAG) {
                unparser.xml += createString("</" + xpp.getName() + ">", depth, spacesPerDepthLevel, minimal);
                if (xpp.getName().equalsIgnoreCase(unparser.getFirstTag())) {
                    System.out.println("Unparser: Breaking because end tag for first tag was found: "
                            + unparser.getFirstTag());
                    break;
                }
            } else if (eventType == XmlPullParser.TEXT) {
                String content = xpp.getText();
                if (trimContent) {
                    content = content.trim();
                }
                if (!content.isEmpty()) {
                    unparser.xml += createString(xpp.getText(), depth, spacesPerDepthLevel, minimal);
                }
            }
            eventType = xpp.nextToken();
        }

        return unparser;
    }

    /**
     * Returns attributes in the parser on the current level as a string. The node tag name is not included.
     * 
     * @param parser
     *            a parser
     * @return attributes in a form similar to this: attribute1="value1" attribute2="value2"
     * @throws Exception
     */
    private static String attributesToString(XmlPullParser parser) throws Exception {
        String result = "";
        Map<String, String> attrs = null;
        int acount = parser.getAttributeCount();
        if (acount != -1) {
            attrs = new HashMap<String, String>(acount);
            for (int x = 0; x < acount; x++) {
                result += " " + parser.getAttributeName(x) + "=" + "\"" + parser.getAttributeValue(x) + "\"";
                attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
            }
        } else {
            throw new Exception("Required entity attributes missing");
        }

        return result;
    }

    private static String createString(String content, int depth, int spacesPerDepthLevel, boolean minimal) {
        String result = "";

        // create depth string
        String depthStr = "";
        depth *= spacesPerDepthLevel;
        while (depth-- >= 0) {
            depthStr += " ";
        }

        // add depth
        if (!minimal) {
            result += depthStr;
        }

        // add content
        result += content;

        // add new line
        if (!minimal) {
            content += "\n'";
        }

        return result;
    }
}
