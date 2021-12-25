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
    protected static boolean status = false;
    GameObject2D bounce;
    float minBounceX, maxBounceX, bounceX, bounceSpeed = 15;
    int direction = 1;

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
        pixmap = new Pixmap(40, 40, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameSuper.palette.onSecondary);
        pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, pixmap.getHeight() / 2);
        minBounceX = objects.get(0).getX() + 20;
        maxBounceX = minBounceX + objects.get(0).getWidth() - 20 - 20 - 40;
        bounceX = minBounceX;
        bounce = new GameObject2D(pixmap, minBounceX, objects.get(0).getY() + objects.get(0).getHeight() - 20);
        new LoadAssetsThread().start();
    }

    static class LoadAssetsThread extends Thread {
        @Override
        public void run() {
            MenuScreen.load();
            LoadingScreen.status = true;
        }
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        bounce.setX(bounce.getX() + bounceSpeed * direction);
        if (bounce.getX() <= minBounceX) direction = 1;
        else if (bounce.getX() >= maxBounceX) direction = -1;

        spriteBatch.begin();
        spriteBatch.draw(bounce);
        for (GameObject2D o : objects) spriteBatch.draw(o);
        spriteBatch.end();

        stage.act();
        stage.draw();

        if (status) GameSuper.instance.setScreen(new MenuScreen());
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
