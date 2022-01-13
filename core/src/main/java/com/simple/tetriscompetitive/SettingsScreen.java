package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class SettingsScreen implements Screen {

    Stage stage = new Stage();
    GameObject2D backButton;
    ArrayList<GameObject2D> objects = new ArrayList<>();
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    ArrayList<GameObject2D> themeButtons = new ArrayList<>();

    int screenHeight, screenWidth;

    @Override
    public void show() {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        int h = screenHeight / 7;
        Pixmap pixmap = Drawing.createRoundedRectangle((screenWidth - 40), h - 20, h / 2 - 10, GameSuper.palette.onSecondary);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("left.png")), 0, 0, 1000, 1000, 0, 0, pixmap.getHeight(), pixmap.getHeight());
        backButton = new GameObject2D(pixmap, 20, 20);

        for (int i = 0; i < GameSuper.palettes.length; i++){
            pixmap = Drawing.createRoundedRectangle(screenWidth - 40, h - 20, h / 2 - 10, Color.BLACK);
            pixmap.drawPixmap(Drawing.createRoundedRectangle(screenWidth - 40 - 4, h - 20 - 4, h / 2 - 10 - 2, GameSuper.palette.onSecondary), 2, 2);
            int segmentWidth = pixmap.getWidth() / 4;
            Pixmap overlay = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Pixmap.Format.RGB888);
            GameSuper.Palette p = GameSuper.palettes[i];
            overlay.setColor(p.secondary);
            overlay.fillRectangle(0, 0, segmentWidth, overlay.getHeight());
            overlay.setColor(p.onSecondary);
            overlay.fillRectangle(segmentWidth, 0, segmentWidth, overlay.getHeight());
            overlay.setColor(p.primary);
            overlay.fillRectangle(segmentWidth * 2, 0, segmentWidth, overlay.getHeight());
            overlay.setColor(p.onPrimary);
            overlay.fillRectangle(segmentWidth * 3, 0, segmentWidth, overlay.getHeight());

            for (int x = 0; x < pixmap.getWidth(); x++) {
                for (int y = 0; y < pixmap.getHeight(); y++) {
                    if (pixmap.getPixel(x, y) != Color.BLACK.toIntBits() && pixmap.getPixel(x, y) != Color.CLEAR.toIntBits()) pixmap.drawPixel(x, y, overlay.getPixel(x, y));
                }
            }

            themeButtons.add(new GameObject2D(pixmap, 20, h + h * i + 10));

        }

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Math.min(h - 20, screenWidth / "themes".length());
        parameter.color = GameSuper.palette.onSecondary;

        BitmapFont font = GameSuper.mainFontGenerator.generateFont(parameter);

        Label headerLabel = new Label("Themes", new Label.LabelStyle(font, Color.WHITE));
        headerLabel.setSize(screenWidth, h);
        headerLabel.setAlignment(Align.center);
        headerLabel.setPosition(0, screenHeight - h, Align.bottomLeft);
        stage.addActor(headerLabel);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (Gdx.input.justTouched()) {
            if (backButton.contains()) GameSuper.instance.setScreen(new MenuScreen());
            else {
                for (int i = 0; i < themeButtons.size(); i++) {
                    if (themeButtons.get(i).contains()) {
                        DataManagement.data.colorSchemeIndex = i;
                        DataManagement.saveData();
                        GameSuper.palette = GameSuper.palettes[i];
                        GameSuper.instance.setScreen(new SettingsScreen());
                    }
                }
            }
        }

        spriteBatch.begin();
        spriteBatch.draw(backButton);
        for (GameObject2D o : objects) spriteBatch.draw(o);
        for (GameObject2D o : themeButtons) spriteBatch.draw(o);
        spriteBatch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
