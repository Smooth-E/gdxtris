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

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

public class PlayScreen implements Screen {

    private final boolean isAdmin;

    private static final int STATE_PLAYING = 0;
    private static final int STATE_INFO = 1;
    private static final int STATE_WATCHING = 3;

    private int state = STATE_PLAYING;

    private int previousRoomStatus = Networking.Room.STATUS_IDLE;

    private float timePassedFromTick = 0;

    private final GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    private final Stage stage = new Stage();
    private int screenHeight;
    private int screenWidth;
    private int margin;
    private float ratioWidth;
    private float ratioHeight;

    private String networkAddress = "NO ADDRESS";

    private final ArrayList<GameObject2D> playStateObjects = new ArrayList<>();
    private GameObject2D infoButton;
    private GameObject2D stepLeftButton;
    private GameObject2D stepRightButton;
    private GameObject2D stepDownButton;
    private GameObject2D rotateClockwiseButton;
    private GameObject2D rotateAntiClockwiseButton;
    private GameObject2D rotate180Button;
    private GameObject2D instantPlaceButton;
    private GameObject2D exchangeButton;
    private GameObject2D holdPieceBackground;
    private GameObject2D nextPieceBackground;
    private GameObject2D gameStatusOverlay;
    private GameObject2D startGameButton;
    private Pixmap playFieldPixmap;
    private Label statsLabel;
    private Label startGameLabel;
    private Label scoreLabel;
    private final Stage playStateStage = new Stage();

    private final ArrayList<GameObject2D> infoStateObjects = new ArrayList<>();
    private final ArrayList<Label> playerInfoLabels = new ArrayList<>(10);

    private final ArrayList<GameObject2D[]> playerInfoCosmeticElements =
            new ArrayList<>(10);

    private GameObject2D backToPlayButton;
    private GameObject2D exitGameButton;
    private final Stage infoStage = new Stage();

    private float fadeOutAnimationProgress = 1;
    private Screen nextScreen = null;

    private final ArrayList<GameObject2D> watchingStateObjects = new ArrayList<>();
    private final Stage watchingStateStage = new Stage();
    private GameObject2D watchingStateBackButton;
    private Pixmap watchingStateFieldBackground;
    private int watchingID = 0;
    private Label watchingStatePlayerNameLabel;
    private Label watchingStateScoreLabel;

    public PlayScreen(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    private String getNumberWithSuffix(int number) {
        switch (number % 10) {
            case 1:
                return number + "st";
            case 2:
                return number + "nd";
            default:
                return number + "th";
        }
    }

    private void checkInternetConnection() {

        // TODO: Do something if connection is unreachable

        final String loggingTag = "CONN_CHECK";

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("google.com", 80));
            Gdx.app.log(loggingTag, "Hosted / connected: " + socket.getLocalAddress());
            networkAddress = socket.getLocalAddress().toString();
        }
        catch (Exception exception) {
            Gdx.app.log(loggingTag, "Unable to get host name / address: ");
            exception.printStackTrace();
        }
    }

    private GameObject2D createControllerButton(
            Pixmap basePixmap,
            float x,
            float y,
            String iconFileName
    ) {
        int width = basePixmap.getWidth();
        int height = basePixmap.getHeight();

        Pixmap icon = Drawing.getIcon(iconFileName, width, height, GameSuper.palette.secondary);

        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(basePixmap, 0, 0);
        pixmap.drawPixmap(icon, 0, 0);

        GameObject2D gameObject = new GameObject2D(pixmap, x, y);

        icon.dispose();
        pixmap.dispose();

        return gameObject;
    }

    // TODO: Separate different-purpose scenes into different screens

    private void initializePlayScene() {

        // Create a background for all the controls

        int controlsBackgroundWidth = (int) (980 * ratioWidth);
        int controlsBackgroundHeight = (int) (172 * ratioWidth) * 2 + margin * 3;

        Pixmap controlsBackgroundPixmap = Drawing.createRoundedRectangle(
                controlsBackgroundWidth,
                controlsBackgroundHeight,
                (int) (172 * ratioHeight) / 2,
                GameSuper.palette.primary
        );

        float controlsBackgroundX = (screenWidth - controlsBackgroundWidth) / 2f;
        float controlsBackgroundY = 50 * ratioHeight;

        playStateObjects.add(new GameObject2D(
                controlsBackgroundPixmap,
                controlsBackgroundX,
                controlsBackgroundY
        ));

        controlsBackgroundPixmap.dispose();

        // Create a circle pixmap for controller buttons

        int controlButtonDimension = (int) (172 * ratioWidth);
        int controlButtonRadius = controlButtonDimension / 2;

        Pixmap controlButtonCirclePixmap = new Pixmap(
                controlButtonDimension,
                controlButtonDimension,
                Pixmap.Format.RGBA8888
        );

        controlButtonCirclePixmap.setColor(GameSuper.palette.onSecondary);
        controlButtonCirclePixmap
                .fillCircle(controlButtonRadius, controlButtonRadius, controlButtonRadius);

        // Create controller buttons

        float buttonRowY = 50 * ratioHeight + margin;
        float controllerPanelCenter =  (screenWidth - controlsBackgroundWidth) / 2f;
        float mostLeftButtonX = controllerPanelCenter + margin;
        float perButtonOffset = controlButtonDimension + margin;

        stepLeftButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX,
                buttonRowY,
                "left.png"
        );
        playStateObjects.add(stepLeftButton);

        stepRightButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset,
                buttonRowY,
                "right.png"
        );
        playStateObjects.add(stepRightButton);

        stepDownButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset * 2,
                buttonRowY,
                "down.png"
        );
        playStateObjects.add(stepDownButton);

        rotateClockwiseButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset * 3,
                buttonRowY,
                "clockwise.png"
        );
        playStateObjects.add(rotateClockwiseButton);

        rotateAntiClockwiseButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset * 4,
                buttonRowY,
                "anticlockwise.png"
        );
        playStateObjects.add(rotateAntiClockwiseButton);

        buttonRowY += controlButtonDimension + margin;

        rotate180Button = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset * 2,
                buttonRowY,
                "rotate180.png"
        );
        playStateObjects.add(rotate180Button);

        exchangeButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset * 3,
                buttonRowY,
                "exchange.png"
        );
        playStateObjects.add(exchangeButton);

        instantPlaceButton = createControllerButton(
                controlButtonCirclePixmap,
                mostLeftButtonX + perButtonOffset * 4,
                buttonRowY,
                "instant.png"
        );
        playStateObjects.add(instantPlaceButton);

        controlButtonCirclePixmap.dispose();

        int cellDimension = (int) (63.3 * ratioHeight);
        int playFieldWidth = 10 * cellDimension;
        int playFieldHeight = 20 * cellDimension;

        // Create a playing field

        playFieldPixmap = new Pixmap(playFieldWidth, playFieldHeight, Pixmap.Format.RGB888);

        playFieldPixmap.setColor(Color.BLACK);
        playFieldPixmap.fill();

        playFieldPixmap.setColor(GameSuper.palette.secondary);
        playFieldPixmap.drawRectangle(0, 0, playFieldWidth, playFieldHeight);

        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 20; y++) {
                playFieldPixmap.drawRectangle(
                        x * cellDimension,
                        y * cellDimension,
                        cellDimension,
                        cellDimension
                );
            }
        }

        // Draw a plain rectangle to place on top of the screen

        // TODO: Is this really needed?

        int topRectangleHeight = playFieldHeight + (int) (100 * ratioHeight);
        float topRectangleY = screenHeight - 50 * ratioHeight - topRectangleHeight;

        Pixmap topRectanglePixmap  =
                new Pixmap(screenWidth, topRectangleHeight, Pixmap.Format.RGB888);

        topRectanglePixmap.setColor(GameSuper.palette.onSecondary);
        topRectanglePixmap.fill();
        playStateObjects.add(new GameObject2D(topRectanglePixmap, 0, topRectangleY));

        float holdBackgroundX = 50;
        int holdBackgroundDimension = screenWidth - (int) holdBackgroundX * 2 - playFieldWidth - 20;
        int holdBackgroundCornerRadius = margin;
        int holdBackgroundOutlineSize = 5;

        Pixmap holdBackgroundPixmap = Drawing.createRoundedRectangle(
                holdBackgroundDimension * 2,
                holdBackgroundDimension,
                holdBackgroundCornerRadius,
                GameSuper.palette.secondary
        );

        Pixmap holdBackgroundInnerPartPixmap = Drawing.createRoundedRectangle(
                holdBackgroundPixmap.getWidth() - holdBackgroundOutlineSize * 2,
                holdBackgroundPixmap.getHeight() - holdBackgroundOutlineSize * 2,
                holdBackgroundCornerRadius,
                Color.BLACK
        );

        holdBackgroundPixmap.drawPixmap(
                holdBackgroundInnerPartPixmap,
                holdBackgroundOutlineSize,
                holdBackgroundOutlineSize
        );

        holdBackgroundInnerPartPixmap.dispose();

        holdBackgroundX += margin + playFieldWidth - holdBackgroundDimension;
        float holdBackgroundY = screenHeight - 100 * ratioHeight - holdBackgroundDimension - 1;

        holdPieceBackground =
                new GameObject2D(holdBackgroundPixmap, holdBackgroundX, holdBackgroundY);

        playStateObjects.add(holdPieceBackground);

        // Create a background for incoming pieces list

        int nextPieceBackgroundHeight = holdBackgroundDimension * 3;

        Pixmap nextPieceBackgroundPixmap = Drawing.createRoundedRectangle(
                holdBackgroundDimension * 2,
                nextPieceBackgroundHeight,
                holdBackgroundCornerRadius,
                GameSuper.palette.secondary
        );

        Pixmap nextPieceBackgroundInnerPartPixmap = Drawing.createRoundedRectangle(
                nextPieceBackgroundPixmap.getWidth() - holdBackgroundOutlineSize * 2,
                nextPieceBackgroundPixmap.getHeight() - holdBackgroundOutlineSize * 2,
                holdBackgroundCornerRadius - 2 * holdBackgroundOutlineSize,
                Color.BLACK
        );

        nextPieceBackgroundPixmap.drawPixmap(
                nextPieceBackgroundInnerPartPixmap,
                holdBackgroundOutlineSize,
                holdBackgroundOutlineSize
        );

        nextPieceBackgroundInnerPartPixmap.dispose();

        nextPieceBackground = new GameObject2D(
                nextPieceBackgroundPixmap,
                holdBackgroundX,
                holdBackgroundY - holdBackgroundDimension * 3 - margin
        );
        playStateObjects.add(nextPieceBackground);

        nextPieceBackgroundPixmap.dispose();

        // Create the Info button

        int infoButtonDimension = Math.min(
                playFieldHeight - holdBackgroundDimension - nextPieceBackgroundHeight - margin * 3,
                screenWidth - playFieldWidth - (int) holdBackgroundX - margin * 4
        );

        int infoButtonRadius = infoButtonDimension / 2;
        float infoButtonX = holdBackgroundX + holdBackgroundDimension + margin * 2;

        Pixmap infoButtonPixmap = new Pixmap(
                infoButtonDimension,
                infoButtonDimension,
                Pixmap.Format.RGBA8888
        );

        infoButtonPixmap.setColor(GameSuper.palette.primary);
        infoButtonPixmap.fillCircle(infoButtonRadius, infoButtonRadius, infoButtonRadius);
        infoButtonPixmap.fill();

        /* TODO: The following code causes a GL memory leak
        Pixmap infoButtonIconPixmap = Drawing.getIcon(
                "help.png",
                infoButtonDimension,
                infoButtonDimension,
                GameSuper.palette.secondary
        );

        infoButtonPixmap.drawPixmap(infoButtonIconPixmap, 0, 0);
        infoButtonIconPixmap.dispose();
        */


        float infoButtonY = screenHeight - playFieldHeight - 100 * ratioHeight;

        infoButton = new GameObject2D(infoButtonPixmap, infoButtonX, infoButtonY);
        playStateObjects.add(infoButton);

        infoButtonPixmap.dispose();

        // Create a status overlay (shows the countdown and other info)

        float gameStatusOverlayX = 50 * ratioWidth + 20;
        int gameStatusOverlayHeight = playFieldHeight / 3;

        Pixmap gameStatusOverlayPixmap = new Pixmap(
                playFieldWidth,
                gameStatusOverlayHeight,
                Pixmap.Format.RGBA8888
        );

        Color gameStatusOverlayColor = new Color(GameSuper.palette.onPrimary);
        gameStatusOverlayColor.a = .5f;

        gameStatusOverlayPixmap.setColor(gameStatusOverlayColor);
        gameStatusOverlayPixmap.fill();

        gameStatusOverlay = new GameObject2D(
                gameStatusOverlayPixmap,
                gameStatusOverlayX,
                holdBackgroundY + playFieldHeight / 3f
        );

        // Create the "Start Game" button

        int startGameButtonWidth = gameStatusOverlay.getWidth() / 4 * 3;
        String messageStartGame = "START GAME!";
        int startGameButtonCornerRadius = margin;
        int startGameButtonOutlineSize = 3;

        FreeTypeFontGenerator.FreeTypeFontParameter startGameButtonFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        startGameButtonFreetypeParameter.size = startGameButtonWidth / messageStartGame.length();
        startGameButtonFreetypeParameter.color = GameSuper.palette.secondary;

        BitmapFont startGameButtonFont =
                GameSuper.mainFontGenerator.generateFont(startGameButtonFreetypeParameter);

        int startGameButtonHeight = (int) startGameButtonFont.getLineHeight() + margin;

        Pixmap startGameButtonPixmap = Drawing.createRoundedRectangle(
                startGameButtonWidth,
                startGameButtonHeight,
                startGameButtonCornerRadius,
                GameSuper.palette.onPrimary
        );

        Pixmap startGameButtonInnerPartPixmap = Drawing.createRoundedRectangle(
                startGameButtonWidth - startGameButtonOutlineSize * 2,
                startGameButtonHeight - startGameButtonOutlineSize * 2,
                startGameButtonCornerRadius - startGameButtonOutlineSize,
                GameSuper.palette.primary
        );

        startGameButtonPixmap.drawPixmap(
                startGameButtonInnerPartPixmap,
                startGameButtonOutlineSize,
                startGameButtonOutlineSize
        );

        float startGameButtonX = gameStatusOverlayX + (playFieldWidth - startGameButtonWidth) / 2f;
        float startGameButtonY = gameStatusOverlay.getY() + margin;

        startGameButton =
                new GameObject2D(startGameButtonPixmap, startGameButtonX, startGameButtonY);

        startGameButtonInnerPartPixmap.dispose();
        startGameButtonPixmap.dispose();

        // Create the "Start Game" label

        Label.LabelStyle startGameLabelStyle =
                new Label.LabelStyle(startGameButtonFont, Color.WHITE);

        startGameLabel = new Label(messageStartGame, startGameLabelStyle);
        startGameLabel.setSize(startGameButton.getWidth(), startGameButton.getHeight());
        startGameLabel.setAlignment(Align.center);

        startGameLabel.setPosition(
                startGameButton.getX(),
                startGameButton.getY(),
                Align.bottomLeft
        );

        playStateStage.addActor(startGameLabel);

        // Create the stats label (displays the countdown and place numbers)

        int statsLabelHeight = gameStatusOverlayHeight - startGameButtonHeight - 3 * margin;

        FreeTypeFontGenerator.FreeTypeFontParameter statsLabelFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        statsLabelFreetypeParameter.size = statsLabelHeight - 2 * margin;
        statsLabelFreetypeParameter.color = GameSuper.palette.primary;
        statsLabelFreetypeParameter.borderColor = GameSuper.palette.onPrimary;
        statsLabelFreetypeParameter.borderWidth = 3;
        statsLabelFreetypeParameter.characters = "0123456789snthd";

        BitmapFont statsLabelFont =
                GameSuper.mainFontGenerator.generateFont(statsLabelFreetypeParameter);

        float statsLabelY = startGameButton.getY() + margin * 2;

        Label.LabelStyle counterLabelStyle = new Label.LabelStyle(statsLabelFont, Color.WHITE);
        statsLabel = new Label("4th", counterLabelStyle);
        statsLabel.setPosition(startGameButton.getX(), statsLabelY, Align.bottomLeft);
        statsLabel.setAlignment(Align.center);
        statsLabel.setSize(startGameButton.getWidth(), statsLabelHeight);
        statsLabel.setAlignment(Align.center);

        playStateStage.addActor(statsLabel);

        statsLabel.setText(getNumberWithSuffix(NetworkingManager.clientSideRoom.players.size()));


        // Create the Score label

        String messagePrefixScore = "SCORE: ";

        FreeTypeFontGenerator.FreeTypeFontParameter scoreLabelFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        scoreLabelFreetypeParameter.color = GameSuper.palette.primary;
        scoreLabelFreetypeParameter.size = (int) (50 * ratioHeight);

        BitmapFont scoreLabelFont =
                GameSuper.mainFontGenerator.generateFont(scoreLabelFreetypeParameter);

        Label.LabelStyle scoreLabelStyle = new Label.LabelStyle(scoreLabelFont, Color.WHITE);

        scoreLabel = new Label(messagePrefixScore, scoreLabelStyle);
        scoreLabel.setPosition(0, 454 * ratioHeight, Align.bottomLeft);
        scoreLabel.setSize(screenWidth, 50 * ratioHeight);
        scoreLabel.setAlignment(Align.center);

        playStateStage.addActor(scoreLabel);


        Tetris.generateField();
    }

    private void initializeRoomInfoScene() {
        int listEntryHeight = (screenHeight - 4 * margin) / 11;

        // Create a background for controls on top

        int topControlsBackgroundWidth = screenWidth - margin * 2;
        float topControlsBackgroundY = topControlsBackgroundWidth - listEntryHeight;

        Pixmap topControlsBackgroundPixmap = Drawing.createRoundedRectangle(
            topControlsBackgroundWidth,
                listEntryHeight,
            listEntryHeight / 2 ,
            GameSuper.palette.onSecondary
        );

        infoStateObjects
                .add(new GameObject2D(topControlsBackgroundPixmap, margin, topControlsBackgroundY));

        topControlsBackgroundPixmap.dispose();

        // Create a button that returns you to the playing scene

        Pixmap backButtonPixmap = Drawing.getIcon(
            "left.png",
            listEntryHeight,
            listEntryHeight,
            GameSuper.palette.secondary
        );

        backToPlayButton = new GameObject2D(backButtonPixmap, margin, topControlsBackgroundY);

        backButtonPixmap.dispose();

        // Create a button that allows you to leave the room


        Pixmap buttonExitPixmap = Drawing.getIcon(
            "exit.png",
            listEntryHeight,
            listEntryHeight,
            GameSuper.palette.secondary
        );

        float buttonExitX = screenWidth - margin - buttonExitPixmap.getWidth();

        exitGameButton = new GameObject2D(buttonExitPixmap, buttonExitX, backToPlayButton.getY());

        // Create a label that displays room'roomAddress IP address

        int maxIpAddressMessageLength = " [000.000.000.000]".length();
        int textSize = (screenWidth - 2 * margin) / (10 + maxIpAddressMessageLength) * 2;

        FreeTypeFontGenerator.FreeTypeFontParameter ipAddressFontParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        ipAddressFontParameter.size = textSize;
        ipAddressFontParameter.color = GameSuper.palette.secondary;

        BitmapFont ipAddressMessageFont =
                GameSuper.mainFontGenerator.generateFont(ipAddressFontParameter);

        Label.LabelStyle ipAddressMessageLabelStyle =
                new Label.LabelStyle(ipAddressMessageFont, Color.WHITE);

        String roomAddress = "[" + networkAddress + "]";

        Label headerLabel = new Label(roomAddress, ipAddressMessageLabelStyle);
        headerLabel.setSize(topControlsBackgroundWidth, listEntryHeight);
        headerLabel.setPosition(0, backToPlayButton.getY(), Align.bottomLeft);
        headerLabel.setAlignment(Align.center);

        infoStage.addActor(headerLabel);


        // Create entries to display users in the room

        int entryIconDimension = listEntryHeight - 2 * margin;

        FreeTypeFontGenerator.FreeTypeFontParameter usernameFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        usernameFreetypeParameter.size = textSize;
        usernameFreetypeParameter.color = GameSuper.palette.onSecondary;

        BitmapFont usernameFont =
                GameSuper.mainFontGenerator.generateFont(usernameFreetypeParameter);

        Label.LabelStyle usernameLabelStyle = new Label.LabelStyle(usernameFont, Color.WHITE);

        Pixmap profilePicturePixmap = Drawing.getIcon(
            "profile-pic.png",
            entryIconDimension,
            entryIconDimension,
            GameSuper.palette.onSecondary
        );

        Pixmap eyePixmap = Drawing.getIcon(
            "eye.png",
            entryIconDimension,
            entryIconDimension,
            GameSuper.palette.primary
        );

        Pixmap dividerPixmap =
                new Pixmap(topControlsBackgroundWidth, 10, Pixmap.Format.RGB888);

        dividerPixmap.setColor(GameSuper.palette.primary);
        dividerPixmap.fill();

        for (int i = 0; i < 10; i++) {
            Label label = new Label("some text", usernameLabelStyle);
            label.setAlignment(Align.left);
            label.setPosition(listEntryHeight, listEntryHeight * (9 - i));
            label.setSize(screenWidth - listEntryHeight, listEntryHeight);

            playerInfoLabels.add(label);
            infoStage.addActor(label);

            float buttonY = listEntryHeight * (9 - i);
            float dividerY = buttonY - dividerPixmap.getHeight() / 2f;
            GameObject2D[] batch = new GameObject2D[3];

            batch[0] = new GameObject2D(profilePicturePixmap, 0, buttonY);
            batch[1] = new GameObject2D(eyePixmap, screenWidth - eyePixmap.getWidth(), buttonY);
            batch[2] = new GameObject2D(dividerPixmap, margin, dividerY);

            playerInfoCosmeticElements.add(batch);
        }

    }

    private void initializeWatchingScene() {
        // Creating a watching scene

        int bottomPillWidth = screenWidth - 2 * margin;
        int bottomPillHeight = screenHeight / 10;

        Pixmap bottomPillBackgroundPixmap = Drawing.createRoundedRectangle(
            bottomPillWidth,
            bottomPillHeight,
            bottomPillHeight / 2,
            GameSuper.palette.onSecondary
        );

        Pixmap iconExitPixmap = Drawing.getIcon(
            "left.png",
            bottomPillHeight,
            bottomPillHeight,
            GameSuper.palette.secondary
        );

        bottomPillBackgroundPixmap.drawPixmap(iconExitPixmap, 0, 0);
        watchingStateBackButton = new GameObject2D(bottomPillBackgroundPixmap, margin, margin);

        iconExitPixmap.dispose();
        bottomPillBackgroundPixmap.dispose();

        // Create a label that displays current player's username

        FreeTypeFontGenerator.FreeTypeFontParameter freetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        freetypeParameter.color = GameSuper.palette.secondary;

        freetypeParameter.size = Math.min(
            watchingStateBackButton.getHeight() - 2 * margin,
            (watchingStateBackButton.getWidth() - 2 * watchingStateBackButton.getHeight()) / 10
        );

        BitmapFont font = GameSuper.mainFontGenerator.generateFont(freetypeParameter);


        float usernameLabelWidth = bottomPillWidth - 2 * bottomPillHeight;

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        watchingStatePlayerNameLabel = new Label("John Doe", labelStyle);
        watchingStatePlayerNameLabel.setSize(usernameLabelWidth, bottomPillHeight);
        watchingStatePlayerNameLabel.setAlignment(Align.center);
        watchingStatePlayerNameLabel.setPosition(bottomPillHeight + margin, margin);

        watchingStateStage.addActor(watchingStatePlayerNameLabel);

        // Create a background stripe behind the play field

        float backgroundStripeY = screenHeight / 10f + margin * 2;
        int backgroundStripeHeight = screenHeight / 10 * 9 - 3 * margin;

        Pixmap backgroundStripePixmap =
                new Pixmap(screenWidth, backgroundStripeHeight, Pixmap.Format.RGB888);

        backgroundStripePixmap.setColor(GameSuper.palette.primary);
        backgroundStripePixmap.fill();
        watchingStateObjects.add(new GameObject2D(backgroundStripePixmap, 0, backgroundStripeY));

        backgroundStripePixmap.dispose();

        // Draw a play field

        int possibleFieldHeight =
                (backgroundStripeHeight - 2 * margin - (int) font.getLineHeight());

        int cellDimension = Math.min(
            (screenWidth - 2 * margin) / Tetris.fieldWidth,
            possibleFieldHeight / Tetris.fieldHeight
        );

        watchingStateFieldBackground = new Pixmap(
            cellDimension * Tetris.fieldWidth,
            cellDimension * Tetris.fieldHeight,
            Pixmap.Format.RGB888
        );

        watchingStateFieldBackground.setColor(Color.BLACK);
        watchingStateFieldBackground.fill();
        watchingStateFieldBackground.setColor(GameSuper.palette.secondary);

        for (int x = 0; x < Tetris.fieldWidth; x++) {
            for (int y = 0; y < Tetris.fieldHeight; y++) {
                watchingStateFieldBackground.drawRectangle(
                    x * cellDimension,
                    y * cellDimension,
                    cellDimension,
                    cellDimension
                );
            }
        }

        float scoreLabelY = screenHeight / 10f + margin * 5;
        watchingStateScoreLabel = new Label("score label", labelStyle);
        watchingStateScoreLabel.setPosition(0, scoreLabelY, Align.bottomLeft);
        watchingStateScoreLabel.setSize(screenWidth, 2 * margin);
        watchingStateScoreLabel.setAlignment(Align.top);

        watchingStateStage.addActor(watchingStateScoreLabel);
    }

    @Override
    public void show() {
        checkInternetConnection();

        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        ratioWidth = screenWidth / 1080f;
        ratioHeight = screenHeight / 1920f;
        margin = (int)(20 * ratioWidth);

        initializePlayScene();
        Gdx.app.log("TAG", "1");
        initializeRoomInfoScene();
        Gdx.app.log("TAG", "2");
        initializeWatchingScene();
        Gdx.app.log("TAG", "3");

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (!NetworkingManager.client.isConnected()) GameSuper.instance.setScreen(new MenuScreen());

        scoreLabel.setText("SCORE: " + NetworkingManager.playerInfo.score);

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
                startGameLabel.setVisible(isAdmin && NetworkingManager.clientSideRoom.players.size() > 1);
                startGameButton.setActive(startGameLabel.isVisible());
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
            else if (state == STATE_WATCHING) state = STATE_INFO;
        }

        if (Gdx.input.justTouched()){
            int x = Gdx.input.getX(), y = screenHeight - Gdx.input.getY();
            if (state == STATE_PLAYING) {
                if (startGameButton.contains()) NetworkingManager.client.sendTCP(new Networking.StartGameRequest());
                else if (infoButton.contains()) state = STATE_INFO;
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
                else {
                    for (int i = 0; i < NetworkingManager.clientSideRoom.players.size(); i++) {
                        if (playerInfoCosmeticElements.get(i)[1].contains()) {
                            watchingID = NetworkingManager.clientSideRoom.players.get(i).id;
                            state = STATE_WATCHING;
                        }
                    }
                }
            }
            else if (state == STATE_WATCHING) {
                if (watchingStateBackButton.contains()) {
                    state = STATE_INFO;
                    watchingID = -1;
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
            for (GameObject2D o : playStateObjects) spriteBatch.draw(o);

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
            pixmap.setColor(GameSuper.palette.secondary);
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
                pixmap = new Pixmap(holdPieceBackground.getWidth(), holdPieceBackground.getHeight(), Pixmap.Format.RGBA8888);
                cellSize = Math.min((holdPieceBackground.getWidth() / 2 - 2 * margin) / hold[0].length, (holdPieceBackground.getHeight() - 2 * margin) / hold.length);
                if (!NetworkingManager.playerInfo.holdPerformed) pixmap.setColor(Tetris.figureColors.clone()[NetworkingManager.playerInfo.holdID]);
                else pixmap.setColor(Color.GRAY);
                for (int fx = 0; fx < hold[0].length; fx++) {
                    for (int fy = 0 ; fy < hold.length; fy++) {
                        if (hold[fy][fx] == 1)
                            pixmap.fillRectangle(holdPieceBackground.getWidth() / 2 + margin + fx * cellSize, (int)(margin * 1.5f + fy * cellSize), cellSize, cellSize);
                    }
                }
                holdObject = new GameObject2D(pixmap, holdPieceBackground.getX(), holdPieceBackground.getY());
                spriteBatch.draw(holdObject);
                pixmap.dispose();
            }

            spriteBatch.end();


            int slotWidth = Math.min((nextPieceBackground.getHeight() - margin) / 4, nextPieceBackground.getWidth() / 2);
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
                GameObject2D o = new GameObject2D(pixmap, nextPieceBackground.getX() + nextPieceBackground.getWidth() / 2f,
                        nextPieceBackground.getY() + nextPieceBackground.getHeight() - i * slotWidth);
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
        else if (state == STATE_WATCHING) {
            Pixmap field = new Pixmap(watchingStateFieldBackground.getWidth(), watchingStateFieldBackground.getHeight(), Pixmap.Format.RGB888);
            field.drawPixmap(watchingStateFieldBackground, 0, 0);

            Networking.PlayerContainer player = null;
            for (int i = 0; i < NetworkingManager.clientSideRoom.players.size(); i++) {
                if (NetworkingManager.clientSideRoom.players.get(i).id == watchingID) {
                    player = NetworkingManager.clientSideRoom.players.get(i);
                }
            }
            if (player != null) {
                watchingStatePlayerNameLabel.setText(player.name);
                watchingStateScoreLabel.setText("SCORE: " + player.score);
                int cellDimension = field.getWidth() / Tetris.fieldWidth;
                for (int x = 0; x < Tetris.fieldWidth; x ++) {
                    for (int y = 0; y < Tetris.fieldHeight; y++) {
                        if (player.field[y][x] != -1) {
                            field.setColor(Tetris.figureColors[player.field[y][x]]);
                            field.fillRectangle(x * cellDimension, y * cellDimension, cellDimension, cellDimension);
                        }
                    }
                }
                int[][] figure = Tetris.figures[player.figureID][player.figureRotation].clone();
                field.setColor(Tetris.figureColors[player.figureID]);
                for (int fx = 0; fx < figure[0].length; fx++) {
                    for (int fy = 0; fy < figure.length; fy++) {
                        if (figure[fy][fx] == 1) {
                            field.fillRectangle(cellDimension * (player.figureX + fx), cellDimension * (player.figureY + fy),
                                    cellDimension, cellDimension);
                        }
                    }
                }
            }
            else state = STATE_INFO;

            GameObject2D fieldObject = new GameObject2D(field, screenWidth / 2f - field.getWidth() / 2f,
                    screenHeight - margin - watchingStateFieldBackground.getHeight() - margin);

            Pixmap pixmap = new Pixmap(20, field.getHeight(), Pixmap.Format.RGB888);
            pixmap.setColor(Color.BLACK);
            pixmap.fill();
            pixmap.setColor(GameSuper.palette.secondary);
            pixmap.drawRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
            if (player != null) {
                for (Networking.PlayerContainer attacker : NetworkingManager.clientSideRoom.players) {
                    if (attacker.targetID == player.id) {
                        int cellDimension = pixmap.getHeight() / Tetris.fieldHeight;
                        pixmap.fillRectangle(0, (Tetris.fieldHeight - attacker.stackToAdd) * cellDimension, 20, pixmap.getHeight());
                    }
                }
            }
            GameObject2D stack = new GameObject2D(pixmap, fieldObject.getX() - 20, fieldObject.getY());

            spriteBatch.draw(watchingStateBackButton);
            for (GameObject2D o : watchingStateObjects) spriteBatch.draw(o);
            spriteBatch.draw(fieldObject);
            spriteBatch.draw(stack);
            spriteBatch.end();
            watchingStateStage.act();
            watchingStateStage.draw();

            stack.dispose();
            fieldObject.dispose();
            field.dispose();
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
            for (int y = 0; y < Tetris.fieldHeight - obtainedStack; y++) {
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

        float newDelta = delta;
        if (newDelta > 1 / 60f) newDelta = 1 / 60f;
        if (nextScreen != null) {
            fadeOutAnimationProgress += 4 * newDelta;
            if (fadeOutAnimationProgress >= 1.5) {
                GameSuper.instance.setScreen(nextScreen);
            }
        }
        else if (fadeOutAnimationProgress > 0) {
            fadeOutAnimationProgress -= 4 * newDelta;
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

        if (fadeOutAnimationProgress == -1)
            fadeOutAnimationProgress = 1;
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

}