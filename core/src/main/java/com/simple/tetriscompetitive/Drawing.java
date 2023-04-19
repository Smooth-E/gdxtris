package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Drawing {

    public static final int CORNER_RADIUS = 50;

    // Stolen from an old project =)
    public static Pixmap createRoundedRectangle(
        int width,
        int height,
        int cornerRadius,
        Color color
    ) {
        // TODO: Investigate, what the returnable Pixmap is used for
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Pixmap returnable = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        pixmap.setColor(color);

        pixmap.fillCircle(cornerRadius, cornerRadius, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, cornerRadius, cornerRadius);
        pixmap.fillCircle(cornerRadius, height - cornerRadius - 1, cornerRadius);
        pixmap.fillCircle(width - cornerRadius - 1, height - cornerRadius - 1, cornerRadius);

        pixmap.fillRectangle(cornerRadius, 0, width - cornerRadius * 2, height);
        pixmap.fillRectangle(0, cornerRadius, width, height - cornerRadius * 2);

        returnable.setColor(color);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixmap.getPixel(x, y) != 0)
                    returnable.drawPixel(x, y);
            }
        }

        pixmap.dispose();

        return returnable;
    }

    public static Pixmap createButtonPixmap(int width, int height, int shadowX, int shadowY) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        int segmentHeight = height - shadowX, segmentWidth = width - shadowY;
        int colorSchemeIndex = DataManagement.data.colorSchemeIndex;
        GameSuper.Palette palette = GameSuper.palettes[colorSchemeIndex];

        Pixmap backgroundPixmap = createRoundedRectangle(
            segmentWidth,
            segmentHeight,
            CORNER_RADIUS,
            palette.onSecondary
        );

        Pixmap foreground = createRoundedRectangle(
            segmentWidth,
            segmentHeight,
            CORNER_RADIUS,
            palette.primary
        );

        for(int x = 0; x < segmentWidth; x++) {
            for(int y = 0; y < segmentHeight; y++){
                pixmap.drawPixel(x, y, backgroundPixmap.getPixel(x, y));
                pixmap.drawPixel(x + shadowX, y + shadowX, foreground.getPixel(x, y));
            }
        }

        return pixmap;
    }

    public static Pixmap createButtonPixmap(int width, int height, int shadowXY){
        return createButtonPixmap(width, height, shadowXY, shadowXY);
    }

    public static Pixmap createButtonPixmap(
        int width, int height,
        float shadowPercentX,
        float shadowPercentY
    ){
        return createButtonPixmap(
            width,
            height,
            width * shadowPercentX,
            height * shadowPercentY
        );
    }

    public static Pixmap createButtonPixmap(int width, int height, float shadowPercentXY){
        return createButtonPixmap(
            width,
            height,
            width * shadowPercentXY,
            height * shadowPercentXY
        );
    }

    public static Pixmap getIcon(String path, int width, int height, Color color) {
        Pixmap initialPixmap = new Pixmap(Gdx.files.internal(path));
        Pixmap resizedPixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        resizedPixmap.drawPixmap(
            initialPixmap,
            0,
            0,
            initialPixmap.getWidth(),
            initialPixmap.getHeight(),
            0,
            0,
            width,
            height
        );

        resizedPixmap.setColor(color);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (resizedPixmap.getPixel(x, y) != Color.CLEAR.toIntBits())
                    resizedPixmap.drawPixel(x, y);
            }
        }

        return resizedPixmap;
    }
}
