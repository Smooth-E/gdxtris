package com.simple.tetriscompetitive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class GameSuper extends Game {

    static class Palette {
        Color secondary, onSecondary, primary, onPrimary;

        public Palette (String secondary, String onSecondary, String primary, String onPrimary) {
            this.secondary   = Color.valueOf(secondary);
            this.onSecondary = Color.valueOf(onSecondary);
            this.primary     = Color.valueOf(primary);
            this.onPrimary   = Color.valueOf(onPrimary);
        }
    }

    public static Palette[] palettes = {
            new Palette("E6E6E6", "5C7AEA", "3D56B2", "14279B")
    };

    public static FreeTypeFontGenerator mainFontGenerator;

    @Override
    public void create() {
        mainFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Product Sans Regular.ttf"));
        DataManagement.loadData();
        MenuScreen menuScreen = new MenuScreen(this);
        setScreen(menuScreen);
    }
}