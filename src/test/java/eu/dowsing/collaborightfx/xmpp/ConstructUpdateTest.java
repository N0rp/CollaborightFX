package eu.dowsing.collaborightfx.xmpp;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.dowsing.collaborightfx.sketch.structure.Shape;
import eu.dowsing.collaborightfx.sketch.transaction.StructureUpdate;

public class ConstructUpdateTest {

    @Test
    public void testSimpleSerialization() {
        Shape shape = new Shape(5, 5, 12.5, null);

        // tests that you can serialize/deserialize updates without exception
        StructureUpdate beforeXml = new StructureUpdate(5, shape);
        String before = beforeXml.toXML();

        StructureUpdate afterXml = null;
        String after = null;
        try {
            afterXml = StructureUpdate.fromExtension(beforeXml);
            after = afterXml.toXML();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not deserialize update");
        }

        Assert.assertEquals(beforeXml.getStructureId(), afterXml.getStructureId());
        // assertTrue(beforeXml.getConstruct().equals(afterXml.getConstruct()));
        Assert.assertTrue(beforeXml.equals(afterXml), "Before and after serialization were not equal. " + "\nBefore:\n"
                + before + "\nAfter:\n" + after);
        System.out.println(afterXml);
    }

}
