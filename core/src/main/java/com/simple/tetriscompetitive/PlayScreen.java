package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.sun.source.doctree.TextTree;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.Key;
import java.util.ArrayList;
import java.util.Random;

public class PlayScreen implements Screen {

    public boolean isAdmin = false;

    public PlayScreen(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    static final int STATE_PLAYING = 0, STATE_INFO = 1;
    int state = STATE_PLAYING;

    int previousRoomStatus = Networking.Room.STATUS_IDLE;

    float timePassedFromTick = 0;

    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    Stage stage = new Stage();
    int screenHeight, screenWidth, margin;
    float ratioWidth, ratioHeight;

    String networkAddress = "NO ADDRESS";

    ArrayList<GameObject2D> playSateObjects = new ArrayList<>();
    GameObject2D playStateInfoButton, playSatePlayersButton, playStateOverviewButton;
    GameObject2D stepLeftButton, stepRightButton, stepDownButton, rotateClockwiseButton, rotateAntiClockwiseButton, rotate180Button;
    GameObject2D instantPlaceButton, exchangeButton;
    GameObject2D playField;
    GameObject2D holdBG, nextPieceBG;
    GameObject2D gameStatusOverlay, startGameButton;
    Pixmap playFieldPixmap;
    Label statsLabel, startGameLabel, scoreLabel;
    Stage playStateStage = new Stage();

    ArrayList<GameObject2D> infoStateObjects = new ArrayList<>();
    ArrayList<Label> playerInfoLabels = new ArrayList<>(10);
    ArrayList<GameObject2D[]> playerInfoCosmeticElements = new ArrayList<GameObject2D[]>(10);
    Label headerLabel;
    GameObject2D backToPlayButton, exitGameButton;
    Stage infoStage = new Stage();

    float fadeOutAnimationProgress = 1;
    Screen nextScreen = null;

    @Override
    public void show() {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80));
            Gdx.app.log("INET", "Hosted / connected: " + socket.getLocalAddress());
            networkAddress = socket.getLocalAddress().toString();
        } catch (java.lang.Exception e) {
            Gdx.app.log("INET", "Unable to get host name / address: " + e.toString());
        }

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
        pixmap.drawPixmap(Drawing.getIcon("left.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin, y));
        stepLeftButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("right.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h + margin, y));
        stepRightButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("down.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 2 + margin * 2, y));
        stepDownButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("clockwise.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 3 + margin * 3, y));
        rotateClockwiseButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("anticlockwise.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 4 + margin * 4, y));
        rotateAntiClockwiseButton = playSateObjects.get(playSateObjects.size() - 1);

        y += h + margin;

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("rotate180.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 2 + margin * 2, y));
        rotate180Button = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("exchange.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 3 + margin * 3, y));
        exchangeButton = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(circle, 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("instant.png", h, h, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, (screenWidth - w) / 2f + margin + h * 4 + margin * 4, y));
        instantPlaceButton = playSateObjects.get(playSateObjects.size() - 1);

        int cellDimension = (int)(63.3 * ratioHeight);
        int playFieldWidth = 10 * cellDimension, playFieldHeight = 20 * cellDimension;
        playFieldPixmap = new Pixmap(playFieldWidth, playFieldHeight, Pixmap.Format.RGB888);
        playFieldPixmap.setColor(Color.BLACK);
        playFieldPixmap.fill();
        playFieldPixmap.setColor(GameSuper.palette.secondary);
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
        pixmap = Drawing.createRoundedRectangle(w * 2, w, margin, GameSuper.palette.secondary);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() - 10,
                pixmap.getHeight() - 10, margin, Color.BLACK), 5, 5);
        playSateObjects.add(new GameObject2D(pixmap,
                x + 20 + playFieldWidth - pixmap.getWidth() / 2f, y + playFieldHeight - pixmap.getHeight() - 1));
        holdBG = playSateObjects.get(playSateObjects.size() - 1);

        pixmap = Drawing.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight() * 3, margin, GameSuper.palette.secondary);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() - 10, pixmap.getHeight() - 10,
                10, Color.BLACK), 5, 5);
        playSateObjects.add(new GameObject2D(pixmap, holdBG.getX(), holdBG.getY() - holdBG.getHeight() * 3 - margin));
        nextPieceBG = playSateObjects.get(playSateObjects.size() - 1);

        w = Math.min(playFieldHeight - 2 * margin - holdBG.getHeight() - nextPieceBG.getHeight() - margin,
                screenWidth - playFieldWidth - (int)x - margin * 4);
        pixmap = new Pixmap(w, w, Pixmap.Format.RGBA8888);
        pixmap.setColor(GameSuper.palette.primary);
        pixmap.fillCircle(pixmap.getWidth() / 2, pixmap.getHeight() / 2, w / 2);
        pixmap.drawPixmap(Drawing.getIcon("help.png", w, w, GameSuper.palette.secondary), 0, 0);
        playSateObjects.add(new GameObject2D(pixmap, holdBG.getX() + holdBG.getWidth() / 2f + margin * 2, y));
        playStateInfoButton = playSateObjects.get(playSateObjects.size() - 1);

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

        statsLabel.setText(getNumberWithSuffix(NetworkingManager.clientSideRoom.players.size()));

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = GameSuper.palette.primary;
        parameter.size = (int)(50 * ratioHeight);

        font = GameSuper.mainFontGenerator.generateFont(parameter);

        Label.LabelStyle scoreLabelStyle = new Label.LabelStyle(font, Color.WHITE);

        scoreLabel = new Label("SCORE: ", scoreLabelStyle);
        scoreLabel.setPosition(0, 454 * ratioHeight, Align.bottomLeft);
        scoreLabel.setSize(screenWidth, 50 * ratioHeight);
        scoreLabel.setAlignment(Align.center);
        playStateStage.addActor(scoreLabel);

        Tetris.generateField();

        Gdx.input.setInputProcessor(stage);

        //Info Scene
        int unitHeight = (screenHeight - 4 * margin) / 11;

        pixmap = Drawing.createRoundedRectangle(screenWidth - margin * 2, unitHeight, unitHeight / 2, GameSuper.palette.onSecondary);
        infoStateObjects.add(new GameObject2D(pixmap, margin, screenHeight - pixmap.getHeight() - 2 * margin));

        pixmap = Drawing.getIcon("left.png", unitHeight, unitHeight, GameSuper.palette.secondary);
        backToPlayButton = new GameObject2D(pixmap, margin, infoStateObjects.get(0).getY());

        pixmap = Drawing.getIcon("exit.png", unitHeight, unitHeight, GameSuper.palette.secondary);
        exitGameButton = new GameObject2D(pixmap, screenWidth - margin - pixmap.getWidth(), backToPlayButton.getY());

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (screenWidth - 2 * margin) / (10 + " [000.000.000.000]".length()) * 2;
        parameter.color = GameSuper.palette.secondary;

        font = GameSuper.mainFontGenerator.generateFont(parameter);

        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        String s = "[" + networkAddress + "]";
        headerLabel = new Label(s, labelStyle);
        headerLabel.setSize(screenWidth - 2 * margin, unitHeight);
        headerLabel.setPosition(0, backToPlayButton.getY(), Align.bottomLeft);
        headerLabel.setAlignment(Align.center);
        infoStage.addActor(headerLabel);

        parameter.color = GameSuper.palette.onSecondary;

        font = GameSuper.mainFontGenerator.generateFont(parameter);

        labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Pixmap pic = Drawing.getIcon("profile-pic.png", unitHeight - 2 * margin, unitHeight - 2 * margin, GameSuper.palette.onSecondary);

        Pixmap eye = Drawing.getIcon("eye.png", pic.getWidth() - 2 * margin, pic.getHeight() - 2 * margin, GameSuper.palette.primary);

        Pixmap divider = new Pixmap(screenWidth - 2 * margin, 10, Pixmap.Format.RGB888);
        divider.setColor(GameSuper.palette.primary);
        divider.fill();

        for (int i = 0; i < 10; i++){
            Label label = new Label("some text", labelStyle);
            label.setAlignment(Align.left);
            label.setPosition(unitHeight, unitHeight * (9 - i));
            label.setSize(screenWidth - unitHeight, unitHeight);
            playerInfoLabels.add(label);
            infoStage.addActor(playerInfoLabels.get(i));

            GameObject2D[] batch = new GameObject2D[3];

            batch[0] = new GameObject2D(pic, 0, unitHeight * (9 - i));
            batch[1] = new GameObject2D(eye, screenWidth - eye.getWidth(), unitHeight * (9 - i));
            batch[2] = new GameObject2D(divider, margin, unitHeight * (9 - i) - divider.getHeight() / 2f);
            playerInfoCosmeticElements.add(batch);
        }
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

        if (!NetworkingManager.client.isConnected()) GameSuper.instance.setScreen(new MenuScreen());

        scoreLabel.setText("SCORE: " + NetworkingManager.playerInfo.score + " | TARGET: " + NetworkingManager.playerInfo.targetID + " | STACK: " + NetworkingManager.playerInfo.stackToAdd);

        startGameButton.setActive(NetworkingManager.clientSideRoom.status == Networking.Room.STATUS_IDLE && isAdmin);
        startGameLabel.setVisible(startGameButton.isActive());

        switch (NetworkingManager.clientSideRoom.status) {
            case Networking.Room.STATUS_CD1:
            case Networking.Room.STATUS_CD2:
            case Networking.Room.STATUS_CD3:
                NetworkingManager.playerInfo.canPlay = true;
                statsLabel.setText(NetworkingManager.clientSideRoom.status);
                break;
            case Networking.Room.STATUS_PLAYING:
                startGameLabel.setVisible(false);
                startGameButton.setActive(false);
                gameStatusOverlay.setActive(!NetworkingManager.playerInfo.canPlay);
                if (statsLabel.isVisible() == NetworkingManager.playerInfo.canPlay && !NetworkingManager.playerInfo.canPlay) {
                    int playersAlive = 0;
                    for (Networking.PlayerContainer player : NetworkingManager.clientSideRoom.players) {
                        if (player.id != NetworkingManager.playerInfo.id && player.canPlay) playersAlive++;
                    }
                    statsLabel.setText(getNumberWithSuffix(playersAlive + 1));
                }
                statsLabel.setVisible(!NetworkingManager.playerInfo.canPlay);
                break;
            case Networking.Room.STATUS_IDLE:
                startGameLabel.setVisible(isAdmin);
                startGameButton.setActive(isAdmin);
                gameStatusOverlay.setActive(true);
                statsLabel.setVisible(true);
                break;
        }

        if (NetworkingManager.clientSideRoom.status == Networking.Room.STATUS_PLAYING &&
                previousRoomStatus != Networking.Room.STATUS_PLAYING && NetworkingManager.playerInfo.canPlay) {
            Tetris.start();
            Gdx.app.log("Some tag", "some log!");
        }
        previousRoomStatus = NetworkingManager.clientSideRoom.status;

        //Logic
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            if (state == STATE_PLAYING) state = STATE_INFO;
            else if (state == STATE_INFO) state = STATE_PLAYING;
        }

        if (Gdx.input.justTouched()){
            int x = Gdx.input.getX(), y = screenHeight - Gdx.input.getY();
            if (state == STATE_PLAYING) {
                if (startGameButton.contains()) NetworkingManager.client.sendTCP(new Networking.StartGameRequest());
                else if (playStateInfoButton.contains()) state = STATE_INFO;
                else if (stepLeftButton.contains()) Tetris.moveLeft();
                else if (stepRightButton.contains()) Tetris.moveRight();
                else if (instantPlaceButton.contains()) Tetris.instantDown();
                else if (stepDownButton.contains()) Tetris.moveDown();
                else if (rotateClockwiseButton.contains()) Tetris.rotateClockwise();
                else if (rotateAntiClockwiseButton.contains()) Tetris.rotateAnticlockwise();
                else if (rotate180Button.contains()) Tetris.rotate180();
                else if (exchangeButton.contains()) Tetris.performHold();
            }
            else if (state == STATE_INFO) {
                if (backToPlayButton.contains()) state = STATE_PLAYING;
                else if (exitGameButton.contains()) {
                    NetworkingManager.client.sendTCP(new Networking.DisconnectRequest(NetworkingManager.playerInfo.id));
                    NetworkingManager.client.close();
                    if (isAdmin) {
                        NetworkingManager.server.sendToAllTCP(new Networking.GameEndRequest());
                        NetworkingManager.server.close();
                    }
                    nextScreen = new MenuScreen();
                }
            }
        }
        else if (Gdx.input.isTouched()) {
            if (state == STATE_PLAYING) {
                Tetris.autoShiftDelay += delta;
                Tetris.autoRepeatDelay += delta;
                if (Tetris.autoShiftDelay >= 10 / 30f && Tetris.autoRepeatDelay >= 2 / 30f) {
                    Tetris.autoRepeatDelay = 0;
                    if (stepLeftButton.contains()) Tetris.moveLeft();
                    else if (stepRightButton.contains()) Tetris.moveRight();
                    else if (stepDownButton.contains()) Tetris.moveDown();
                }
            }
        }
        else {
            Tetris.autoShiftDelay = 0;
            Tetris.autoRepeatDelay = 0;
        }

        //Logic for PC key presses
        if (state == STATE_PLAYING) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) Tetris.moveRight();
            else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) Tetris.moveLeft();
            else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) Tetris.instantDown();
            else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) Tetris.rotateAnticlockwise();
        }

        spriteBatch.begin();
        if (state == STATE_PLAYING) {
            for (GameObject2D o : playSateObjects) spriteBatch.draw(o);

            GameObject2D field, stack;

            Pixmap newField = new Pixmap(playFieldPixmap.getWidth(), playFieldPixmap.getHeight(), playFieldPixmap.getFormat());
            newField.drawPixmap(playFieldPixmap, 0, 0);

            int cellSize = newField.getWidth() / 10;
            int[][] figure = Tetris.getFigure();
            newField.setColor(Tetris.figureColors[NetworkingManager.playerInfo.figureID]);
            for (int x = 0; x < figure[0].length; x++){
                for (int y = 0; y < figure.length; y++){
                    if (figure[y][x] == 1) {
                        newField.fillRectangle((NetworkingManager.playerInfo.figureX + x) * cellSize,
                                (NetworkingManager.playerInfo.figureY + y) * cellSize, cellSize, cellSize);
                    }
                }
            }

            for (int x = 0; x < NetworkingManager.playerInfo.field[0].length; x++){
                for (int y = 0; y < NetworkingManager.playerInfo.field.length; y++){
                    if (NetworkingManager.playerInfo.field[y][x] >= 0) {
                        newField.setColor(Tetris.figureColors[NetworkingManager.playerInfo.field[y][x]]);
                        newField.fillRectangle(x * cellSize, y * cellSize, cellSize, cellSize);
                    }
                }
            }

            if (NetworkingManager.playerInfo.canPlay) {
                boolean shouldStop = false;
                int yd = 0;
                while (true) {
                    for (int y = 0; y < figure.length; y++) {
                        for (int x = 0; x < figure[0].length; x++) {
                            if (figure[y][x] == 1 &&
                                    (NetworkingManager.playerInfo.figureY + y + yd >= Tetris.fieldHeight - 1 ||
                                            NetworkingManager.playerInfo.field[NetworkingManager.playerInfo.figureY + y + yd + 1][NetworkingManager.playerInfo.figureX + x] > -1)) {
                                shouldStop = true;
                                break;
                            }
                        }
                    }
                    if (shouldStop) break;
                    yd++;
                }
                Color figureColor = new Color(Tetris.figureColors[NetworkingManager.playerInfo.figureID]);
                figureColor.a = 0.5f;
                newField.setColor(figureColor);
                for (int x = 0; x < figure[0].length; x++) {
                    for (int y = 0; y < figure.length; y++) {
                        if (figure[y][x] == 1) {
                            newField.fillRectangle((NetworkingManager.playerInfo.figureX + x) * cellSize,
                                    (NetworkingManager.playerInfo.figureY + y + yd) * cellSize, cellSize, cellSize);
                        }
                    }
                }
            }


            field = new GameObject2D(newField, 0, 0);
            field.setX(50 * ratioWidth + 20);
            field.setY(screenHeight - field.getHeight() - 100 * ratioHeight);

            spriteBatch.draw(field);

            Pixmap pixmap = new Pixmap(20, field.getHeight(), Pixmap.Format.RGB888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            pixmap.setColor(GameSuper.palette.onPrimary);
            pixmap.drawRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
            int s = 0;
            if (NetworkingManager.playerInfo.targetID != -1) {
                for (int i = 0; i < NetworkingManager.clientSideRoom.players.size(); i++) {
                    if (NetworkingManager.clientSideRoom.players.get(i).targetID == NetworkingManager.playerInfo.id)
                        s = NetworkingManager.clientSideRoom.players.get(i).stackToAdd;
                }
            }
            pixmap.fillRectangle(0, pixmap.getHeight() - pixmap.getHeight() / 20 * s, pixmap.getWidth(), pixmap.getHeight() / 20 * s);
            stack = new GameObject2D(pixmap, field.getX() - 20, field.getY());
            spriteBatch.draw(stack);
            pixmap.dispose();

            spriteBatch.draw(gameStatusOverlay);

            GameObject2D holdObject = null;
            if (NetworkingManager.playerInfo.holdID != -1) {
                int[][] hold = Tetris.figures[NetworkingManager.playerInfo.holdID][0].clone();
                pixmap = new Pixmap(holdBG.getWidth(), holdBG.getHeight(), Pixmap.Format.RGBA8888);
                cellSize = Math.min((holdBG.getWidth() / 2 - 2 * margin) / hold[0].length, (holdBG.getHeight() - 2 * margin) / hold.length);
                if (!NetworkingManager.playerInfo.holdPerformed) pixmap.setColor(Tetris.figureColors.clone()[NetworkingManager.playerInfo.holdID]);
                else pixmap.setColor(Color.GRAY);
                for (int fx = 0; fx < hold[0].length; fx++) {
                    for (int fy = 0 ; fy < hold.length; fy++) {
                        if (hold[fy][fx] == 1)
                            pixmap.fillRectangle(holdBG.getWidth() / 2 + margin + fx * cellSize, (int)(margin * 1.5f + fy * cellSize), cellSize, cellSize);
                    }
                }
                holdObject = new GameObject2D(pixmap, holdBG.getX(), holdBG.getY());
                spriteBatch.draw(holdObject);
                pixmap.dispose();
            }

            spriteBatch.end();


            int slotWidth = Math.min((nextPieceBG.getHeight() - margin) / 4, nextPieceBG.getWidth() / 2);
            // Drawing next pieces
            for (int i = 1; i <= 4; i++){
                pixmap = new Pixmap(slotWidth, slotWidth, Pixmap.Format.RGBA8888);
                int figureID = new Random(NetworkingManager.clientSideRoom.seed + NetworkingManager.playerInfo.turn + i).nextInt(7);
                int[][] nextFigure = Tetris.figures[figureID][0].clone();
                cellSize = Math.min((slotWidth - margin) / nextFigure.length, (slotWidth - 2 * margin) / nextFigure[0].length);
                pixmap.setColor(Tetris.figureColors[figureID]);
                for (int fy = 0; fy < nextFigure.length; fy++) {
                    for (int fx = 0; fx < nextFigure[0].length; fx++) {
                        if (nextFigure[fy][fx] == 1) pixmap.fillRectangle(margin * 2 + fx * cellSize, margin * 2 + fy * cellSize, cellSize, cellSize);
                    }
                }
                GameObject2D o = new GameObject2D(pixmap, nextPieceBG.getX() + nextPieceBG.getWidth() / 2f, nextPieceBG.getY() + nextPieceBG.getHeight() - i * slotWidth);
                spriteBatch.begin();
                spriteBatch.draw(o);
                spriteBatch.end();
                o.dispose();

                pixmap.dispose();
            }
            spriteBatch.begin();

            spriteBatch.draw(startGameButton);

            spriteBatch.end();

            field.dispose();
            stack.dispose();
            newField.dispose();
            if (holdObject != null) holdObject.dispose();

            playStateStage.act();
            playStateStage.draw();
        }
        else if (state == STATE_INFO) {
            for (int i = 0; i < 10; i++){
                if (i < NetworkingManager.clientSideRoom.players.size())
                    playerInfoLabels.get(i).setText(NetworkingManager.clientSideRoom.players.get(i).name);
                else playerInfoLabels.get(i).setText("");

                playerInfoCosmeticElements.get(i)[0].setActive(i < NetworkingManager.clientSideRoom.players.size());
                playerInfoCosmeticElements.get(i)[1].setActive(i < NetworkingManager.clientSideRoom.players.size());
                playerInfoCosmeticElements.get(i)[2].setActive(i < NetworkingManager.clientSideRoom.players.size());
            }

            for (GameObject2D o : infoStateObjects) spriteBatch.draw(o);
            for (GameObject2D[] o : playerInfoCosmeticElements) {
                spriteBatch.draw(o[0]);
                spriteBatch.draw(o[1]);
                spriteBatch.draw(o[2]);
            }
            spriteBatch.draw(backToPlayButton);
            spriteBatch.draw(exitGameButton);
            spriteBatch.end();

            infoStage.act();
            infoStage.draw();
        }

        if (NetworkingManager.playerInfo.canPlay) {
            timePassedFromTick += Gdx.graphics.getDeltaTime();
            if (timePassedFromTick >= 1) {
                Tetris.tick();
                timePassedFromTick = 0;
            }

            Tetris.noClearsTime += delta;
            if (Tetris.noClearsTime >= 5) {
                NetworkingManager.client.sendTCP(new Networking.ReleaseStackRequest(NetworkingManager.playerInfo.id));
                Tetris.noClearsTime = 0;
            }
        }

        if (NetworkingManager.playerInfo.performStackRelease > 0) {
            Gdx.app.log("TAG", "Perform the stack thing!");
            int obtainedStack = NetworkingManager.playerInfo.performStackRelease;
            for (int x = 0; x < Tetris.fieldWidth; x++) {
                if (NetworkingManager.playerInfo.field[obtainedStack][x] != -1) {
                    NetworkingManager.playerInfo.canPlay = false;
                    break;
                }
            }
            for (int y = 0; y < Tetris.fieldHeight - obtainedStack - 1; y++) {
                NetworkingManager.playerInfo.field[y] = NetworkingManager.playerInfo.field[y + 1].clone();
            }
            int emptyColumn = new Random().nextInt(Tetris.fieldWidth);
            for (int y = Tetris.fieldHeight - obtainedStack; y < Tetris.fieldHeight; y++) {
                for (int x = 0; x < Tetris.fieldWidth; x++) {
                    if (x != emptyColumn) NetworkingManager.playerInfo.field[y][x] = 7;
                    else NetworkingManager.playerInfo.field[y][x] = -1;
                }
            }
            NetworkingManager.playerInfo.figureY = 0;
            NetworkingManager.playerInfo.performStackRelease = 0;
        }

        NetworkingManager.client.sendTCP(new Networking.UpdatedGameStateRequest());

        // Transition animation
        if (nextScreen != null) {
            fadeOutAnimationProgress += 1 / 15f;
            if (fadeOutAnimationProgress >= 1.5) {
                GameSuper.instance.setScreen(nextScreen);
            }
        }
        else if (fadeOutAnimationProgress > 0) {
            fadeOutAnimationProgress -= 1 / 15f;
            if (fadeOutAnimationProgress < 0) fadeOutAnimationProgress = 0;
        }

        Pixmap pad = new Pixmap(screenWidth, screenHeight, Pixmap.Format.RGBA8888);
        Color color = new Color(GameSuper.palette.secondary);
        float alpha = fadeOutAnimationProgress;
        if (alpha > 1) alpha = 1;
        color.a = alpha;
        pad.setColor(color);
        pad.fill();
        spriteBatch.begin();
        GameObject2D padObject = new GameObject2D(pad, 0, 0);
        spriteBatch.draw(padObject);
        spriteBatch.end();
        pad.dispose();
        padObject.dispose();

        if (fadeOutAnimationProgress == -1) fadeOutAnimationProgress = 1;
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
