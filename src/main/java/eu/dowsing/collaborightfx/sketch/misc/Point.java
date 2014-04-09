package eu.dowsing.collaborightfx.sketch.misc;

import org.simpleframework.xml.Attribute;

public class Point {

    @Attribute
    private double x;

    @Attribute
    private double y;

    public Point(@Attribute(name = "x") double x, @Attribute(name = "y") double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Point (x:" + x + " y:" + y + ")";
    }

}
