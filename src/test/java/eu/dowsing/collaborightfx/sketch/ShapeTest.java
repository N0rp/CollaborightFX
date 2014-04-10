package eu.dowsing.collaborightfx.sketch;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.testng.Assert;

import eu.dowsing.collaborightfx.sketch.structure.Shape;

public class ShapeTest {

    private final String simpleShapePath = "src/test/resources/sketch/SimpleShape.xml";

    public void testSimpleSerialization() {
        Shape shape = new Shape(5, 5, 12, null);
        shape.addPoint(10, 10, true, null);

        Serializer serializer = new Persister();
        Writer w = new StringWriter();
        try {
            serializer.write(shape, w);
        } catch (Exception e) {
            System.out.println("Could not write " + this + " as xml");
            e.printStackTrace();
            Assert.fail("Serialization was not possible");
        }

        System.out.println("Simple serialization result is:\n" + w.toString());
    }

    public void testSimpleDeSerialization() {
        File simpleShape = new File(simpleShapePath);
        if (!simpleShape.exists()) {
            Assert.fail("Simple Shape file does not exist");
        }

        Serializer serializer = new Persister();
        try {
            Shape simple = serializer.read(Shape.class, simpleShape);
            simple.getStroke().equals(Shape.DEFAULT_STROKE);
            simple.getFill().equals(Shape.DEFAULT_FILL);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Could not parse simple shape");
        }

    }
}
