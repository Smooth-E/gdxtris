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

    private final GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    private final ArrayList<GameObject2D> objects = new ArrayList<>();
    private final Stage stage = new Stage();
    private boolean assetsLoaded = false;
    private GameObject2D bouncingIndicator;
    private float minBounceX;
    private float maxBounceX;
    private int direction = 1;

    @Override
    public void show() {
        int screenWidth = Gdx.graphics.getWidth();
        int cornerRadius = 50;

        String loadingLabelMessage = "Loading assets...";

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = GameSuper.palette.secondary;
        parameter.size = (screenWidth) / loadingLabelMessage.length();
        BitmapFont font = GameSuper.mainFontGenerator.generateFont(parameter);

        int loadingBoxWidth = screenWidth - 200 + 40;
        int loadingBoxHeight = (int)font.getLineHeight() + 100;
        float loadingBoxX = (screenWidth - loadingBoxWidth) / 2f;
        float loadingBoxY = 50;

        minBounceX = loadingBoxX + 20;
        maxBounceX = minBounceX + loadingBoxWidth - 20 - 20 - 40;

        Pixmap loadingBoxPixmap = Drawing.createRoundedRectangle(
            loadingBoxWidth,
            loadingBoxHeight,
            cornerRadius,
            GameSuper.palette.onSecondary
        );
        objects.add(new GameObject2D(loadingBoxPixmap, loadingBoxX, loadingBoxY));
        loadingBoxPixmap.dispose();

        Label.LabelStyle loadingLabelStyle = new Label.LabelStyle(font, Color.WHITE);
        Label loadingLabel = new Label(loadingLabelMessage, loadingLabelStyle);
        loadingLabel.setSize(loadingBoxWidth - 40, loadingBoxHeight - 40);
        loadingLabel.setAlignment(Align.center);
        loadingLabel.setPosition(loadingBoxX + 40, loadingBoxY + 20, Align.bottomLeft);
        stage.addActor(loadingLabel);

        int indicatorSize = 40;
        int circleSize = indicatorSize / 2;
        float indicatorY = loadingBoxY + loadingBoxHeight - 20;

        Pixmap indicatorCirclePixmap =
                new Pixmap(indicatorSize, indicatorSize, Pixmap.Format.RGBA8888);
        indicatorCirclePixmap.setColor(GameSuper.palette.onSecondary);
        indicatorCirclePixmap.fillCircle(circleSize, circleSize, circleSize);
        bouncingIndicator = new GameObject2D(indicatorCirclePixmap, minBounceX, indicatorY);

        new AssetsLoadingThread(this).start();
    }

    @Override
    public void render(float delta) {
        Color color = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(color.r,color.g,color.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        float indicatorSpeed = 15;
        bouncingIndicator.setX(bouncingIndicator.getX() + indicatorSpeed * direction);

        float indicatorX = bouncingIndicator.getX();
        if (indicatorX <= minBounceX)
            direction = 1;
        else if (indicatorX >= maxBounceX)
            direction = -1;

        spriteBatch.begin();
        spriteBatch.draw(bouncingIndicator);

        for (GameObject2D gameObject : objects)
            spriteBatch.draw(gameObject);

        spriteBatch.end();

        stage.act();
        stage.draw();

        if (assetsLoaded) {
            GameSuper.instance.setScreen(new MenuScreen());
            assetsLoaded = false;
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() { }

    static class AssetsLoadingThread extends Thread {

        private final LoadingScreen loadingScreen;

        public AssetsLoadingThread(LoadingScreen loadingScreen) {
            this.loadingScreen = loadingScreen;
        }

        @Override
        public void run() {

            // TODO: Load assets in the background

            loadingScreen.assetsLoaded = true;
        }

    }

}
