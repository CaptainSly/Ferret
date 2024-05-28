package io.azraein.ferret.system.gfx;
import org.lwjgl.opengl.GL11;

public class BasicShapes {

    // Draw a point at (x, y) with the given size
    public static void drawPoint(float x, float y, float size, int lineWidth, float[] color) {
    	GL11.glLineWidth(lineWidth);
    	GL11.glColor3f(color[0], color[1], color[2]);
        GL11.glPointSize(size);
        GL11.glBegin(GL11.GL_POINTS);
        GL11.glVertex2f(x, y);
        GL11.glEnd();
    }

    // Draw a line from (x1, y1) to (x2, y2)
    public static void drawLine(float x1, float y1, float x2, float y2, int lineWidth, float[] color) {
    	GL11.glLineWidth(lineWidth);
    	GL11.glColor3f(color[0], color[1], color[2]);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
    }

    // Draw an unfilled box with bottom-left corner at (x, y) and specified width and height
    public static void drawBox(float x, float y, float width, float height, int lineWidth, float[] color) {
    	GL11.glLineWidth(lineWidth);
    	GL11.glColor3f(color[0], color[1], color[2]);
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();
    }

    // Draw a filled box with bottom-left corner at (x, y) and specified width and height
    public static void drawFilledBox(float x, float y, float width, float height, int lineWidth, float[] color) {
    	GL11.glLineWidth(lineWidth);
    	GL11.glColor3f(color[0], color[1], color[2]);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(x, y);
        GL11.glVertex2f(x + width, y);
        GL11.glVertex2f(x + width, y + height);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();
    }
}
