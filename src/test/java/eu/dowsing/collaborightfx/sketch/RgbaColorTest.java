package eu.dowsing.collaborightfx.sketch;

import javafx.scene.paint.Color;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.dowsing.collaborightfx.sketch.misc.RgbaColor;
import eu.dowsing.collaborightfx.view.painting.SketchView;

public class RgbaColorTest {
    @Test
    public void simpleRgbaColorConversionTest() {
        RgbaColor input = RgbaColor.BLUE;
        Color intermediate = SketchView.toFx(input);
        Assert.assertEquals(intermediate, Color.BLUE);
        RgbaColor output = SketchView.toModel(intermediate);
        // do not use assertEquals here?
        Assert.assertTrue(output.equals(input));
    }

    @Test
    public void simpleFxColorConversionTest() {
        Color input = Color.BLUE;
        RgbaColor intermediate = SketchView.toModel(input);
        Assert.assertTrue(intermediate.equals(RgbaColor.BLUE));
        Color output = SketchView.toFx(intermediate);
        Assert.assertTrue(output.equals(input));
    }
}
