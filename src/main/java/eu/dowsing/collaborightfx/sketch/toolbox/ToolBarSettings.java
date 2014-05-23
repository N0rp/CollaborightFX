package eu.dowsing.collaborightfx.sketch.toolbox;

/**
 * Settings of the Tool Bar.
 * 
 * @author richardg
 *
 */
public class ToolBarSettings {

    /** The tool choices available to the user. **/
    public enum ToolChoice {
        DRAW, TEXT, SELECT
    }

    private ToolChoice tool;

    /**
     * Create default tool bar settings
     */
    public ToolBarSettings() {
        this.tool = ToolChoice.DRAW;
    }

    /**
     * Set the currently selected tool.
     * 
     * @param tool
     *            the selected tool
     */
    public void setTool(ToolChoice tool) {
        this.tool = tool;
    }

    /**
     * Get the currently selected tool.
     * 
     * @return
     */
    public ToolChoice getTool() {
        return this.tool;
    }

}
