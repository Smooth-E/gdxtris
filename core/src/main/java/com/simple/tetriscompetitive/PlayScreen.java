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

import javax.swing.GroupLayout;

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
    GameObject2D gameStatusOverlay, startGameButton;
    Pixmap playFieldPixmap;
    Label statsLabel, startGameLabel;
    Stage playStateStage = new Stage();

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

        float x = 50;
        y = screenHeight - playFieldHeight - 100 * ratioHeight;

        w = screenWidth - (int)x * 2 - playFieldWidth - 20;
        pixmap = Drawing.createRoundedRectangle(w * 2, w, margin, GameSuper.palette.onPrimary);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() - 1,
                pixmap.getHeight() - 1, margin, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap,
                x + 20 + playFieldWidth - pixmap.getWidth() / 2f, y + playFieldHeight - pixmap.getHeight() - 1));
        holdBG = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = Drawing.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight() * 3, margin, GameSuper.palette.onPrimary);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() - 1, pixmap.getHeight() - 1,
                10, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, holdBG.getX(), holdBG.getY() - holdBG.getHeight() * 3 - margin));
        nextPieceBG = playSateObjects.get(playSateObjects.size() - 1);

        w = Math.min(playFieldHeight - 2 * margin - holdBG.getHeight() - nextPieceBG.getHeight() - margin,
                screenWidth - playFieldWidth - (int)x - margin * 4);
        pixmap = new Pixmap(w, w, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameSuper.palette.primary);
        pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, w / 2);
        pixmap.drawPixmap(new Pixmap(Gdx.files.internal("help.png")),
                0, 0, 1000, 1000, 0, 0, w, w);
        playSateObjects.add(new GameObject2D(pixmap, holdBG.getX() + holdBG.getWidth() / 2f + margin * 2, y));

        x = 50 * ratioWidth + 20;

        pixmap = new Pixmap(playFieldWidth, playFieldHeight / 3, Pixmap.Format.RGBA8888);
        Color color = new Color(GameSuper.palette.onPrimary);
        color.a = .5f;
        pixmap.setColor(color);
        pixmap.fill();
        gameStatusOverlay = new GameObject2D(pixmap, x, y + playFieldHeight / 3f);

        w = gameStatusOverlay.getWidth() / 4 * 3;

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = w / "START GAME".length();
        parameter.color = GameSuper.palette.secondary;

        BitmapFont font = GameSuper.mainFontGenerator.generateFont(parameter);

        pixmap = Drawing.createRoundedRectangle(w, (int)font.getLineHeight() + margin, margin, GameSuper.palette.onPrimary);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() - 6, pixmap.getHeight() - 6,
                margin - 3, GameSuper.palette.primary), 3, 3);
        startGameButton = new GameObject2D(pixmap,
                gameStatusOverlay.getX() + (gameStatusOverlay.getWidth() - pixmap.getWidth()) / 2f,
                gameStatusOverlay.getY() + margin);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        startGameLabel = new Label("START GAME!", labelStyle);
        startGameLabel.setSize(startGameButton.getWidth(), startGameButton.getHeight());
        startGameLabel.setAlignment(Align.center);
        startGameLabel.setPosition(startGameButton.getX(), startGameButton.getY(), Align.bottomLeft);
        playStateStage.addActor(startGameLabel);

        h = gameStatusOverlay.getHeight() - startGameButton.getHeight() - 3 * margin;;

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = h - 2 * margin;
        parameter.color = GameSuper.palette.primary;
        parameter.borderColor = GameSuper.palette.onPrimary;
        parameter.borderWidth = 3;
        parameter.characters = "0123456789snthd";

        font = GameSuper.mainFontGenerator.generateFont(parameter);

        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        statsLabel = new Label("4th", labelStyle);
        statsLabel.setPosition(startGameButton.getX(),
                startGameButton.getY() + margin * 2, Align.bottomLeft);
        statsLabel.setAlignment(Align.center);
        statsLabel.setSize(startGameButton.getWidth(), h);
        statsLabel.setAlignment(Align.center);
        playStateStage.addActor(statsLabel);

        statsLabel.setText(getNumberWithSuffix(NetworkingManager.roomInfo.players.size()));

        Gdx.input.setInputProcessor(stage);
    }

    private String getNumberWithSuffix(int number){
        switch (number % 10){
            case 1:
                return Integer.toString(number) + "st";
            case 2:
                return Integer.toString(number) + "nd";
            default:
                return Integer.toString(number) + "th";
        }
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        startGameButton.setActive(NetworkingManager.roomInfo.status == Networking.Room.STATUS_IDLE && isAdmin);
        startGameLabel.setVisible(startGameButton.isActive());

        switch (NetworkingManager.roomInfo.status) {
            case Networking.Room.STATUS_CD1:
            case Networking.Room.STATUS_CD2:
            case Networking.Room.STATUS_CD3:
                statsLabel.setText(NetworkingManager.roomInfo.status);
                break;
            case Networking.Room.STATUS_PLAYING:
                startGameLabel.setVisible(false);
                startGameButton.setActive(false);
                gameStatusOverlay.setActive(NetworkingManager.playerInfo.canPlay);
                statsLabel.setVisible(NetworkingManager.playerInfo.canPlay);
        }

        //Logic
        if (Gdx.input.justTouched()){
            int x = Gdx.input.getX(), y = screenHeight - Gdx.input.getY();
            if (state == STATE_PLAYING) {
                if (startGameButton.contains()) NetworkingManager.client.sendTCP(new Networking.StartGameRequest());
            }
        }

        spriteBatch.begin();
        if (state == STATE_PLAYING) {
            for (GameObject2D o : playSateObjects) spriteBatch.draw(o);

            GameObject2D field, stack;

            field = new GameObject2D(playFieldPixmap, 0, 0);
            field.setX(50 * ratioWidth + 20);
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

            spriteBatch.draw(gameStatusOverlay);

            spriteBatch.draw(startGameButton);

            spriteBatch.end();

            field.dispose();
            stack.dispose();

            playStateStage.act();
            playStateStage.draw();

            NetworkingManager.client.sendTCP(new Networking.UpdatedGameStateRequest());
        }
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
