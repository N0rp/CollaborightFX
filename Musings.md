Musings / Ideas
==============

About Canvas
--------------

Why do we even use canvas? We could just use Groups and a StackPane or something?

Because of performance. The group approach is fine for most cases, probably (not tests done or anything). But if someone decides to use the programm to create a really complicated drawing the performance would probably be horrible. See the [bubble example](http://www.canoo.com/blog/2012/09/21/take-care-of-the-javafx-scene-graph/) which does not include canvas animations.

Better JavaFX performance
--------------------------
Use node.setCache(true) to switch on node caching.
Use node.setCacheHint(CacheHint.SPEED) to enable high speed node transforms.

Draw shapes while the user is drawing into the overlay and move it to the canvas first after confirm.

Scale the canvas no through gc.scale(2,2) will scale the next things you draw on the canvas by a factor of 2, not existing stuff you have already drawn; but through:
canvas.setScaleX(2);
canvas.setScaleY(2);

Use relocate(x, y) or setLayoutX(x)/Y(y) to move the canvas AND its contents.

Use multiple canvas to display content.

Canvas is basically a 2D image that cannot be resized???

Canvas save() and restore() methods work similar to opengl push() and pop() and help with complex drawing.

add a stylesheet for the whole scene: scene.getStylesheets().add("/javafxapplication/main.css");

Look at [Maps in JavaFX](http://fxexperience.com/2011/05/maps-in-javafx-2-0/)

Implement a scroll handler:
node.setOnScroll(new EventHandler() {
        @Override public void handle(ScrollEvent event) {
            node.setTranslateX(node.getTranslateX() + event.getDeltaX());
            node.setTranslateY(node.getTranslateY() + event.getDeltaY());
        }
    });