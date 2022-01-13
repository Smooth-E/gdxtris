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
            new Palette("E6E6E6", "5C7AEA", "3D56B2", "14279B"),
            new Palette("FFCDDD", "EA99D5", "B762C1", "8946A6"),
            new Palette("E9A6A6", "864879", "3F3351", "1F1D36"),
            new Palette("181D31", "678983", "E6DDC4", "F0E9D2"),
            new Palette("C996CC", "916BBF", "3D2C8D", "1C0C5B"),
    };

    public static FreeTypeFontGenerator mainFontGenerator;
    public static Palette palette = palettes[0];

    public static GameSuper instance;

    public static MenuScreen menuScreen;

    @Override
    public void create() {
        instance = this;

        System.setProperty("java.net.preferIPv6Addresses", "true");

        Gdx.input.setCatchBackKey(true);

        mainFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Product Sans Regular.ttf"));
        DataManagement.loadData();
        palette = palettes[DataManagement.data.colorSchemeIndex];
        menuScreen = new MenuScreen();
        LoadingScreen loadingScreen = new LoadingScreen();
        setScreen(loadingScreen);
        //setScreen(new PlayScreen(true));
    }
}