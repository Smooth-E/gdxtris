package com.simple.tetriscompetitive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Drawing {

    //Stolen from old project =)
    public static Pixmap createRoundedRectangle(int width, int height, int cornerRadius, Color color) {

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap ret = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(color);

        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);

        pixmap.fillRectangle(cornerRadius, 0, width - cornerRadius * 2, height);
        pixmap.fillRectangle(0, cornerRadius, width, height - cornerRadius * 2);

        ret.setColor(color);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixmap.getPixel(x, y) != 0) ret.drawPixel(x, y);
            }
        }
        pixmap.dispose();

        return ret;
    }

    public static final float shadowSize = 0.03f;
    public static final int cornerRadius = 50;

    public static Pixmap createButtonPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int segmentHeight = height - (int)(height * shadowSize), segmentWidth = width - (int)(width * shadowSize);
        Pixmap background = createRoundedRectangle(segmentWidth, segmentHeight, cornerRadius,
                GameSuper.palettes[DataManagement.data.colorSchemeIndex].onSecondary);
        Pixmap foreground = createRoundedRectangle(segmentWidth, segmentHeight, cornerRadius,
                GameSuper.palettes[DataManagement.data.colorSchemeIndex].primary);
        for(int x = 0; x < segmentWidth; x++) {
            for(int y = 0; y < segmentHeight; y++){
                pixmap.drawPixel(x, y, background.getPixel(x, y));
                pixmap.drawPixel(x + (int)(width * shadowSize), y + (int)(height * shadowSize), foreground.getPixel(x, y));
            }
        }
        return pixmap;
    }
}
