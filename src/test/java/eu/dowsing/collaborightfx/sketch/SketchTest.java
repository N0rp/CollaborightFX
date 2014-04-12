package eu.dowsing.collaborightfx.sketch;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.testng.Assert;
import org.testng.annotations.Test;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

public class SketchTest {

    private final String simpleSketchPath = "src/test/resources/sketch/SimpleSketch.skml";

    @Test
    public void testSimpleSerialization() {
        Shape shape1 = new Shape(5, 5, 12, null);
        shape1.addPoint(10, 20, true, null);
        Shape shape2 = new Shape(1, 1, 1, null);
        shape2.addPoint(4, 4, false, null);
        shape2.addPoint(4, 5, true, null);

        List<Shape> shapes = new LinkedList<>();
        shapes.add(shape1);
        shapes.add(shape2);
        Sketch sketch = new Sketch(shapes);

        Serializer serializer = new Persister();
        Writer w = new StringWriter();
        try {
            serializer.write(sketch, w);
        } catch (Exception e) {
            System.out.println("Could not write " + this + " as xml");
            e.printStackTrace();
            Assert.fail("Serialization was not possible");
        }

        System.out.println("Simple serialization result is:\n" + w.toString());
    }

    @Test
    public void testSimpleDeSerialization() {
        File simpleSketch = new File(simpleSketchPath);
        if (!simpleSketch.exists()) {
            Assert.fail("Simple Sketch file does not exist");
        }

        Serializer serializer = new Persister();
        try {
            Sketch simple = serializer.read(Sketch.class, simpleSketch);

            Assert.assertTrue(simple.getStrokeColor().equals(Sketch.DEFAULT_STROKE));
            Assert.assertTrue(simple.getFillColor().equals(Sketch.DEFAULT_FILL));
            Assert.assertTrue(simple.getBackground().equals(Sketch.DEFAULT_BACKGROUND));
            Assert.assertEquals(Sketch.DEFAULT_LINE_WIDTH, simple.getLineWidth());
            Assert.assertEquals(2, simple.getShapes().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not parse simple sketch");
        }

    }
}
