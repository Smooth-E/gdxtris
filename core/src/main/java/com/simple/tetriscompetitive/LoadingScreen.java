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

public class LoadingScreen implements Screen {

    ArrayList<GameObject2D> objects = new ArrayList<>();
    Stage stage = new Stage();
    Label captionLabel;
    int screenWidth, screenHeight;
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    protected boolean status = false;

    @Override
    public void show() {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = GameSuper.palette.secondary;
        parameter.size = (screenWidth) / "Loading assets...".length();
        BitmapFont font = GameSuper.mainFontGenerator.generateFont(parameter);
        captionLabel = new Label("Loading assets...", new Label.LabelStyle(font, Color.WHITE));
        Pixmap pixmap = Drawing.createRoundedRectangle(screenWidth - 200 + 40, (int)font.getLineHeight() + 100, 50, GameSuper.palette.onSecondary);
        objects.add(new GameObject2D(pixmap, (screenWidth - pixmap.getWidth()) / 2f, 50));
        captionLabel.setSize(objects.get(0).getWidth() - 40, objects.get(0).getHeight() - 40);
        captionLabel.setAlignment(Align.center);
        captionLabel.setPosition(objects.get(0).getX() + 40, objects.get(0).getY() + 20, Align.bottomLeft);
        stage.addActor(captionLabel);
        pixmap.dispose();
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        for (GameObject2D o : objects) spriteBatch.draw(o);
        spriteBatch.end();

        stage.act();
        stage.draw();

        GameSuper.instance.menuScreen = new MenuScreen();
        GameSuper.instance.menuScreen.init();
        GameSuper.instance.settingsScreen = new SettingsScreen();
        GameSuper.instance.settingsScreen.init();
        GameSuper.instance.playScreen = new PlayScreen();
        status = true;

        if (status) GameSuper.instance.setScreen(GameSuper.instance.menuScreen);
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
