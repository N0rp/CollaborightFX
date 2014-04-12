package eu.dowsing.collaborightfx;

import java.io.File;
import java.io.FileReader;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import eu.dowsing.collaborightfx.app.xmpp.XmlPullUnparser;

/**
 * Tests that the xml pull unparser works as intended.
 * 
 * @author richardg
 * 
 */
public class XmlPullUnparserTest {

    @Test
    public void pullToStructuretest() throws Exception {
        File testFile = new File("src/test/resources/sketch/DifferentSketch.skml");

        Assert.assertTrue(testFile.exists());

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new FileReader(testFile));

        XmlPullUnparser unparser = XmlPullUnparser.unparse(xpp, 1, false);
        System.out.println(unparser.getXml());
        System.out.println("First Tag: " + unparser.getFirstTag());
    }
}
