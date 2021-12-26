package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.ArrayList;

public class PlayScreen implements Screen {

    public boolean isAdmin = false;

    public PlayScreen(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    static final int STATE_PLAYING = 0, STATE_PLAYERS = 1, STATE_FIELDS = 2;
    int state = STATE_PLAYING;

    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    Stage stage = new Stage();
    int screenHeight, screenWidth, margin;
    float ratioWidth, ratioHeight;

    ArrayList<GameObject2D> playSateObjects = new ArrayList<>();
    GameObject2D playStateInfoButton, playSatePlayersButton, playStateOverviewButton;
    GameObject2D stepLeftButton, stepRightButton, stepDownButton, rotateClockwiseButton, rotateAntiClockwiseButton, rotate180Button;
    GameObject2D instantPlaceButton, exchangeButton;
    GameObject2D playField;
    GameObject2D holdBG, nextPieceBG;
    Pixmap playFieldPixmap;
    Label statsLabel;

    @Override
    public void show() {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        ratioWidth = screenWidth / 1080f;
        ratioHeight = screenHeight / 1920f;

        margin = (int)(20 * ratioWidth);

        int h = (int)(172 * ratioWidth) * 2 + margin * 3, w = (int)(980 * ratioWidth);
        Pixmap pixmap = Drawing.createRoundedRectangle(w, h, (int)(172 * ratioHeight) / 2, GameSuper.palette.primary);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f, 50 * ratioHeight));

        h = (int)(172 * ratioWidth);

        Pixmap circle = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        circle.setColor(GameSuper.palette.onSecondary);
        circle.fillCircle(h / 2, h /2, h / 2);

        float y = 50 * ratioHeight + margin;

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("left.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin, y));
        stepLeftButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("right.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h + margin, y));
        stepRightButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("down.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 2 + margin * 2, y));
        stepDownButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("clockwise.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 3 + margin * 3, y));
        rotateClockwiseButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("anticlockwise.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 4 + margin * 4, y));
        rotateAntiClockwiseButton = playSateObjects.get(playSateObjects.size() - 1);

        y += h + margin;

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("rotate180.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 2 + margin * 2, y));
        rotate180Button = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("exchange.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 3 + margin * 3, y));
        exchangeButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("instant.png")),
                0, 0, 1000, 1000, 0, 0, h, h);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 4 + margin * 4, y));
        instantPlaceButton = playSateObjects.get(playSateObjects.size() - 1);

        int cellDimension = (int)(63.3 * ratioHeight);
        int playFieldWidth = 10 * cellDimension, playFieldHeight = 20 * cellDimension;
        playFieldPixmap = new Pixmap(playFieldWidth, playFieldHeight, Pixmap.Format.RGB888);
        playFieldPixmap.setColor(GameSuper.palette.secondary);
        playFieldPixmap.fill();
        playFieldPixmap.setColor(GameSuper.palette.onPrimary);
        playFieldPixmap.drawRectangle(0, 0, playFieldWidth, playFieldHeight);
        for (int px = 0; px < 10; px++) for (int py = 0; py < 20; py++)
            playFieldPixmap.drawRectangle(px * cellDimension, py * cellDimension, cellDimension, cellDimension);

        pixmap = new Pixmap(screenWidth, playFieldHeight + (int)(100 * ratioHeight), Pixmap.Format.RGB888);
        pixmap.setColor(GameSuper.palette.onSecondary);
        pixmap.fill();
        playSateObjects.add(new GameObject2D(pixmap, 0, screenHeight - 50 * ratioHeight - pixmap.getHeight()));

        w = (screenWidth - playFieldWidth) / 2 - 20 - 20;
        pixmap = Drawing.createRoundedRectangle(w * 2, w, Math.min(margin, w / 2), GameSuper.palette.onPrimary);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() - 2,
                pixmap.getHeight() - 2, Math.min(margin, w), GameSuper.palette.secondary), 0, 0);
        float x = (screenWidth - playFieldWidth) / 2f;
        y = (screenHeight - playFieldHeight - 100 * ratioHeight);
        playSateObjects.add(new GameObject2D(pixmap, x - pixmap.getWidth() / 2f - 20, y + playFieldHeight - pixmap.getHeight() - 1));

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        GameObject2D field = null, stack = null;

        spriteBatch.begin();
        if (state == STATE_PLAYING) {
            for (GameObject2D o : playSateObjects) spriteBatch.draw(o);

            field = new GameObject2D(playFieldPixmap, 0, 0);
            field.setX((screenWidth - field.getWidth()) / 2f);
            field.setY(screenHeight - field.getHeight() - 100 * ratioHeight);
            spriteBatch.draw(field);

            Pixmap pixmap = new Pixmap(20, field.getHeight(), Pixmap.Format.RGB888);
            pixmap.setColor(GameSuper.palette.secondary);
            pixmap.fill();
            pixmap.setColor(GameSuper.palette.onPrimary);
            pixmap.drawRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
            int s = 0;
            if (NetworkingManager.playerInfo != null) s = NetworkingManager.playerInfo.stack;
            pixmap.fillRectangle(0, pixmap.getHeight() - pixmap.getHeight() / 20 * s, pixmap.getWidth(), pixmap.getHeight() / 20 * s);
            stack = new GameObject2D(pixmap, field.getX() - 20, field.getY());
            spriteBatch.draw(stack);
            pixmap.dispose();
        }
        spriteBatch.end();

        if (field != null) field.dispose();
        if (stack != null) stack.dispose();
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
