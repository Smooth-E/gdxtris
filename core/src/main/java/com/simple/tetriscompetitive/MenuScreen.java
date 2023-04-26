package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

// TODO: Overall note. It would be much nicer to use LibGDX' Stage + Actors system instead of
//  relying on a custom written GameObject2D class. This project was written some reasonable time
//  ago, when I didn't know about the existence of such system.

public class MenuScreen implements Screen {

    private final GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    private final ArrayList<GameObject2D> widgets = new ArrayList<>();
    private final Stage stage = new Stage();

    private TextField playerNameTextField;
    private TextField roomNameTextField;
    private TextField remoteHostNameTextField;

    private GameObject2D hostButton;
    private GameObject2D connectButton;
    private GameObject2D brushButton;

    private int screenWidth;
    private int screenHeight;

    private final ArrayList<GameObject2D> exitConfirmationDialogWigets = new ArrayList<>();
    private final Stage exitConfirmationDialogStage = new Stage();
    private GameObject2D acceptExitButton;
    private GameObject2D declineExitButton;
    private boolean exitConfirmationDialogOpened = false;

    private float fadeOutAnimationProgress = -1;

    private Screen nextScreen = null;

    @Override
    public void show() {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();

        float aspectModifierX = screenWidth / 1080f;
        float aspectModifierY = screenHeight / 1920f;

        int cornerRadius = (int) (70 * aspectModifierY);

        // Create boxes around action blocks

        int actionBoxWidth = screenWidth - 40;
        int actionBoxHeight = (int) (500 * aspectModifierY);
        int actionBoxX = 20;
        int actionBoxY = (int) (100 * aspectModifierY);
        float topActionBoxY = actionBoxY + actionBoxHeight + 20;

        Pixmap actionBoxPixmap = Drawing.createRoundedRectangle(
            actionBoxWidth,
            actionBoxHeight,
            cornerRadius,
            GameSuper.palette.primary
        );

        GameObject2D bottomActionBox = new GameObject2D(actionBoxPixmap, actionBoxX, actionBoxY);
        widgets.add(bottomActionBox);

        GameObject2D topActionBox = new GameObject2D(actionBoxPixmap, actionBoxX, topActionBoxY);
        widgets.add(topActionBox);

        actionBoxPixmap.dispose();

        // Define actions' dimensions

        int actionWidth = actionBoxWidth - 40;
        int actionHeight = actionBoxHeight / 2 - 20 - 20;
        int topActionOffsetY = actionBoxHeight + 20;
        float actionX = actionBoxX + 20;

        // Create backgrounds for text fields

        float bottomTextFieldY = actionBoxY + 10 + actionBoxHeight / 2f;
        float topTextFieldY = bottomTextFieldY + topActionOffsetY;

        Pixmap textFieldBackgroundPixmap = Drawing.createRoundedRectangle(
            actionWidth,
            actionHeight,
            cornerRadius,
            GameSuper.palette.secondary
        );

        widgets.add(new GameObject2D(textFieldBackgroundPixmap, actionX, bottomTextFieldY));
        widgets.add(new GameObject2D(textFieldBackgroundPixmap, actionX, topTextFieldY));

        textFieldBackgroundPixmap.dispose();

        // Create backgrounds for actionButtons

        float bottomActionButtonY = actionBoxY + 20;

        Pixmap buttonBackgroundPixmap = Drawing.createRoundedRectangle(
            actionWidth,
            actionHeight,
            cornerRadius,
            GameSuper.palette.onSecondary
        );

        connectButton = new GameObject2D(buttonBackgroundPixmap, actionX, bottomActionButtonY);
        widgets.add(connectButton);

        hostButton = new GameObject2D(
            buttonBackgroundPixmap,
            actionX,
            bottomActionButtonY + topActionOffsetY
        );
        widgets.add(hostButton);

        buttonBackgroundPixmap.dispose();

        // Create the profile picture

        int profilePictureSize = (int) (340 * aspectModifierX) + 20;

        Pixmap profilePicturePixmap =
                new Pixmap(profilePictureSize, profilePictureSize, Pixmap.Format.RGBA8888);

        int profilePictureRadius = profilePictureSize / 2;
        profilePicturePixmap.setColor(GameSuper.palette.onSecondary);
        profilePicturePixmap
                .fillCircle(profilePictureRadius, profilePictureRadius, profilePictureRadius);

        int profilePictureIconSize = profilePictureSize - 20;
        String profilePictureIconFileNane = "profile-pic.png";

        Pixmap profilePictureIconPixmap = Drawing.getIcon(
                profilePictureIconFileNane,
                profilePictureIconSize,
                profilePictureIconSize,
                GameSuper.palette.secondary
        );

        profilePicturePixmap.drawPixmap(profilePictureIconPixmap, 10, 10);

        float profilePictureX = (screenWidth - profilePictureSize) / 2f;
        float profilePictureY = screenHeight - 100 - profilePictureSize;
        widgets.add(new GameObject2D(profilePicturePixmap, profilePictureX, profilePictureY));

        profilePictureIconPixmap.dispose();
        profilePicturePixmap.dispose();

        // Create the player's name text field background

        int playerNameTextFieldBackgroundWidth = screenWidth - 200;
        int playerNameTextFieldBackgroundHeight = (int) (150 * aspectModifierY);
        float playerNameTextFieldBackgroundX = 100;

        float playerNameTextFieldBackgroundY =
                profilePictureY - playerNameTextFieldBackgroundHeight - 20;

        Pixmap playerNameBackgroundPixmap = Drawing.createRoundedRectangle(
            playerNameTextFieldBackgroundWidth,
            playerNameTextFieldBackgroundHeight,
            cornerRadius,
            GameSuper.palette.onSecondary
        );

        widgets.add(new GameObject2D(
            playerNameBackgroundPixmap,
            playerNameTextFieldBackgroundX,
            playerNameTextFieldBackgroundY
        ));

        playerNameBackgroundPixmap.dispose();

        // Create the clickable part of a player name text field

        int clickablePlayerNameWidth =
                playerNameTextFieldBackgroundWidth - playerNameTextFieldBackgroundHeight;

        Pixmap clickablePlayerNamePartPixmap = Drawing.createRoundedRectangle(
            clickablePlayerNameWidth,
            playerNameTextFieldBackgroundHeight, cornerRadius,
            GameSuper.palette.onSecondary
        );

        GameObject2D clickablePlayerName = new GameObject2D(
            clickablePlayerNamePartPixmap,
            playerNameTextFieldBackgroundX,
            playerNameTextFieldBackgroundY
        );

        widgets.add(clickablePlayerName);

        clickablePlayerNamePartPixmap.dispose();

        // Create the brush button

        String brushIconFileName = "brush.png";
        int brushIconSize = playerNameTextFieldBackgroundHeight / 3 * 2;
        int brushIconOffset = playerNameTextFieldBackgroundHeight / 6;

        Pixmap brushButtonPixmap = new Pixmap(
            playerNameTextFieldBackgroundHeight,
            playerNameTextFieldBackgroundHeight,
            Pixmap.Format.RGBA8888
        );

        Pixmap brushIconPixmap = Drawing.getIcon(
            brushIconFileName,
            brushIconSize,
            brushIconSize,
            GameSuper.palette.secondary
        );

        brushButtonPixmap.drawPixmap(brushIconPixmap, brushIconOffset, brushIconOffset);

        brushButton = new GameObject2D(
            brushButtonPixmap,
            playerNameTextFieldBackgroundX + clickablePlayerNameWidth,
            playerNameTextFieldBackgroundY
        );

        widgets.add(brushButton);

        brushIconPixmap.dispose();
        brushButtonPixmap.dispose();

        // Create labels and other actors

        // TODO: When wrapping UI creation into methods, generate labels and
        //  other actors along with pixmaps


        // Create labels for action buttons

        // TODO: Move font creation into the loading sequence

        FreeTypeFontGenerator.FreeTypeFontParameter actionButtonFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        actionButtonFreetypeParameter.color = GameSuper.palette.secondary;
        actionButtonFreetypeParameter.size = actionHeight - (int) (40 * aspectModifierY) - 30;

        BitmapFont actionButtonFont =
                GameSuper.mainFontGenerator.generateFont(actionButtonFreetypeParameter);

        Label.LabelStyle actionButtonLabelStyle =
                new Label.LabelStyle(actionButtonFont, Color.WHITE);

        // TODO: Create an index of all strings for translation

        String messageConnect = "CONNECT";
        Label connectButtonLabel = new Label(messageConnect, actionButtonLabelStyle);
        connectButtonLabel.setSize(actionWidth, actionHeight);
        connectButtonLabel.setAlignment(Align.center);
        connectButtonLabel.setPosition(actionX, bottomActionButtonY, Align.bottomLeft);
        stage.addActor(connectButtonLabel);

        String messageHost = "HOST";
        Label hostButtonLabel = new Label(messageHost, actionButtonLabelStyle);
        hostButtonLabel.setSize(connectButtonLabel.getWidth(), connectButtonLabel.getHeight());
        hostButtonLabel.setPosition(hostButton.getX(), hostButton.getY());
        hostButtonLabel.setAlignment(Align.center);
        stage.addActor(hostButtonLabel);


        // Create label for the player name field

        FreeTypeFontGenerator.FreeTypeFontParameter playerNameFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        playerNameFreetypeParameter.size = (clickablePlayerNameWidth - 40) / 10;
        playerNameFreetypeParameter.color = GameSuper.palette.secondary;

        BitmapFont playerNameFont =
                GameSuper.mainFontGenerator.generateFont(playerNameFreetypeParameter);

        int playerNameCursorWidth = 10;
        int playerNameCursorHeight = (int) playerNameFont.getLineHeight();

        Pixmap playerNameCursorPixmap = Drawing.createRoundedRectangle(
            playerNameCursorWidth,
            playerNameCursorHeight,
            playerNameCursorWidth / 2,
            GameSuper.palette.secondary
        );

        Texture playerNameCursorTexture = new Texture(playerNameCursorPixmap);

        TextField.TextFieldStyle playerNameTextFieldStyle = new TextField.TextFieldStyle();
        playerNameTextFieldStyle.font = playerNameFont;
        playerNameTextFieldStyle.fontColor = Color.WHITE;
        playerNameTextFieldStyle.cursor = new TextureRegionDrawable(playerNameCursorTexture);
        playerNameTextFieldStyle.messageFontColor = playerNameTextFieldStyle.fontColor;
        playerNameTextFieldStyle.messageFont = playerNameTextFieldStyle.font;

        float playerNameTextFieldX = playerNameTextFieldBackgroundX + cornerRadius;
        float playerNameTextFieldWidth = clickablePlayerNameWidth - cornerRadius;

        playerNameTextField = new TextField(DataManagement.data.nickname, playerNameTextFieldStyle);
        playerNameTextField.setAlignment(Align.center);
        playerNameTextField.setPosition(playerNameTextFieldX, playerNameTextFieldBackgroundY);
        playerNameTextField.setSize(playerNameTextFieldWidth, playerNameTextFieldBackgroundHeight);
        stage.addActor(playerNameTextField);

        playerNameCursorPixmap.dispose();

        // Create text fields for IP address and room name

        FreeTypeFontGenerator.FreeTypeFontParameter actionTextFieldFreetypeParameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        actionTextFieldFreetypeParameter.size = actionHeight / 3 + 1;
        actionTextFieldFreetypeParameter.color = GameSuper.palette.onSecondary;

        BitmapFont actionTextFieldFont =
                GameSuper.mainFontGenerator.generateFont(actionTextFieldFreetypeParameter);

        int actionTextFieldCursorWidth = 10;

        Pixmap actionTextFieldCursorPixmap = Drawing.createRoundedRectangle(
            actionTextFieldCursorWidth,
            (int) actionTextFieldFont.getLineHeight(),
            actionTextFieldCursorWidth / 2,
            GameSuper.palette.onSecondary
        );

        Texture actionTextFieldCursorTexture = new Texture(actionTextFieldCursorPixmap);

        TextField.TextFieldStyle actionTextFieldStyle = new TextField.TextFieldStyle();
        actionTextFieldStyle.font = actionTextFieldFont;
        actionTextFieldStyle.messageFont = actionTextFieldFont;
        actionTextFieldStyle.cursor = new TextureRegionDrawable(actionTextFieldCursorTexture);
        actionTextFieldStyle.fontColor = Color.WHITE;
        actionTextFieldStyle.messageFontColor = Color.WHITE;

        // TODO: Create a parent for both Keyboards that will take a TextField as a parameter

        Input.TextInputListener remoteHstNameTextInputListener = new Input.TextInputListener() {

            @Override
            public void input(String text) {
                if (!text.isEmpty())
                    remoteHostNameTextField.setText(text);

                stage.unfocus(remoteHostNameTextField);
            }

            @Override
            public void canceled() {
                stage.unfocus(remoteHostNameTextField);
            }

        };

        String remoteHostNameKeyboardTitle = "Enter the host name you want to connect to:";
        String remoteHostNameKeyboardHint = "192.168.0.1";

        TextField.OnscreenKeyboard remoteHostNameKeyboard = visible -> Gdx.input.getTextInput(
            remoteHstNameTextInputListener,
            remoteHostNameKeyboardTitle,
            "",
            remoteHostNameKeyboardHint
        );

        String messageRoomIP= "ROOM IP";

        remoteHostNameTextField = new TextField("", actionTextFieldStyle);
        remoteHostNameTextField.setMessageText(messageRoomIP);
        remoteHostNameTextField.setAlignment(Align.center);
        remoteHostNameTextField.setPosition(actionX, bottomTextFieldY, Align.bottomLeft);
        remoteHostNameTextField.setSize(actionWidth, actionHeight);
        remoteHostNameTextField.setOnscreenKeyboard(remoteHostNameKeyboard);
        stage.addActor(remoteHostNameTextField);

        Input.TextInputListener roomNameTextInputListener = new Input.TextInputListener() {

            @Override
            public void input(String text) {
                if (!text.isEmpty())
                    roomNameTextField.setText(text);

                stage.unfocus(roomNameTextField);
            }

            @Override
            public void canceled() {
                stage.unfocus(roomNameTextField);
            }

        };

        String roomNameKeyboardTitle = "Enter a name for the room:";
        String roomNameKeyboardHint = "Sample Room";

        TextField.OnscreenKeyboard roomNameKeyboard = visible -> Gdx.input.getTextInput(
            roomNameTextInputListener,
            roomNameKeyboardTitle,
            "",
            roomNameKeyboardHint
        );

        String messageRoomName = "ROOM NAME";

        roomNameTextField = new TextField("", actionTextFieldStyle);
        roomNameTextField.setAlignment(Align.center);
        roomNameTextField.setPosition(actionX, topTextFieldY, Align.bottomLeft);
        roomNameTextField.setSize(actionWidth, actionHeight);
        roomNameTextField.setMessageText(messageRoomName);
        roomNameTextField.setOnscreenKeyboard(roomNameKeyboard);
        stage.addActor(roomNameTextField);

        actionTextFieldCursorPixmap.dispose();

        constructExitConfirmationDialog();
    }

    private void constructExitConfirmationDialog() {
        int margin = screenWidth / 10;

        // Create a dimmer pixmap

        Pixmap dimmerPixmap = new Pixmap(screenWidth, screenHeight, Pixmap.Format.RGBA8888);

        Color dimmingColor = new Color(GameSuper.palette.primary);
        dimmingColor.a = 0.5f;

        dimmerPixmap.setColor(dimmingColor);
        dimmerPixmap.fill();

        exitConfirmationDialogWigets.add(new GameObject2D(dimmerPixmap, 0, 0));

        dimmerPixmap.dispose();

        // Create the dialog body box

        int dialogWidth = screenWidth - 2 * margin;
        int dialogHeight = screenHeight / 3;
        int dialogCornerRadius = screenHeight / 3 / 4;

        Pixmap dialogBodyBoxPixmap = Drawing.createRoundedRectangle(
            dialogWidth,
            dialogHeight,
            dialogCornerRadius,
            GameSuper.palette.secondary
        );

        float dialogY = screenHeight / 3f;

        exitConfirmationDialogWigets.add(new GameObject2D(dialogBodyBoxPixmap, margin, dialogY));

        dialogBodyBoxPixmap.dispose();

        // Create the heading caption

        String headingMessage = "Are you sure\nyou want to exit?";
        String headingMessageFirstLine = headingMessage.split("\n")[0];

        int characterSize = Math.min(
            screenHeight / 6 / 2,
            dialogWidth / headingMessageFirstLine.length()
        );

        // TODO: Move font generation into the loading sequence

        FreeTypeFontGenerator.FreeTypeFontParameter parameter =
                new FreeTypeFontGenerator.FreeTypeFontParameter();

        parameter.size = characterSize;
        parameter.color = GameSuper.palette.onSecondary;

        BitmapFont font = GameSuper.mainFontGenerator.generateFont(parameter);

        float headingLabelHeight = dialogHeight / 2f;
        float headingLabelY = screenHeight / 3f + headingLabelHeight;

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        Label headingLabel = new Label(headingMessage, labelStyle);
        headingLabel.setPosition(margin, headingLabelY, Align.bottomLeft);
        headingLabel.setSize(dialogWidth, headingLabelHeight);
        headingLabel.setAlignment(Align.center);
        exitConfirmationDialogStage.addActor(headingLabel);

        // Create buttons for the dialog

        int buttonWidth = (dialogWidth - margin) / 2;
        int buttonHeight = dialogHeight / 2 - margin;
        float buttonY = dialogY + margin / 2f;
        int iconSize = buttonHeight * 2 / 3;
        int iconX = (buttonWidth - buttonHeight * 2 / 3) / 2;
        int iconY = buttonHeight / 3 / 2;

        Pixmap buttonBackgroundPixmap = Drawing.createRoundedRectangle(
                buttonWidth * 2,
                buttonHeight,
                buttonHeight / 2,
                GameSuper.palette.onSecondary
        );

        // Create a button to accept and exit the game

        // TODO:
        //  Create the following method:
        //      GameObject2D constructExitConfirmationDialogButton(
        //          String iconFileName,
        //          float x,
        //          float y
        //      ) { ... }

        Pixmap buttonAcceptPixmap =
                new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);

        buttonAcceptPixmap.drawPixmap(buttonBackgroundPixmap, 0, 0);

        String tickIconFileName = "tick.png";

        Pixmap tickIconPixmap =
                Drawing.getIcon(tickIconFileName, iconSize, iconSize, GameSuper.palette.secondary);

        buttonAcceptPixmap.drawPixmap(tickIconPixmap, iconX, iconY);

        acceptExitButton = new GameObject2D(buttonAcceptPixmap, margin * 1.5f, buttonY);

        buttonAcceptPixmap.dispose();

        // Create a button to dismiss the dialog

        Pixmap dismissButtonPixmap = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
        dismissButtonPixmap.drawPixmap(buttonBackgroundPixmap, -buttonWidth, 0);

        String crossIconFileName = "cross.png";

        Pixmap crossIconPixmap =
                Drawing.getIcon(crossIconFileName, iconSize, iconSize, GameSuper.palette.secondary);

        dismissButtonPixmap.drawPixmap(crossIconPixmap, iconX, iconY);

        declineExitButton =
                new GameObject2D(dismissButtonPixmap, margin * 1.5f + buttonWidth, buttonY);

        dismissButtonPixmap.dispose();

        buttonBackgroundPixmap.dispose();
    }

    private void drawMainStage() {
        spriteBatch.begin();

        for (GameObject2D widget : widgets)
            spriteBatch.draw(widget);

        spriteBatch.end();

        stage.draw();
    }

    private void consumeInputForMainSage() {
        Gdx.input.setInputProcessor(stage);

        if (playerNameTextField.hasKeyboardFocus())
            playerNameTextField.getOnscreenKeyboard().show(true);

        boolean shouldSaveNickname =
            !playerNameTextField.getText().equals(DataManagement.data.nickname) &&
            playerNameTextField.getText().length() > 0
        ;

        if (shouldSaveNickname) {
            DataManagement.data.nickname = playerNameTextField.getText();
            DataManagement.saveData();
            playerNameTextField.setMessageText(DataManagement.data.nickname);
        }

        if (!Gdx.input.justTouched())
            return;

        if (GameObject2D.checkContains(playerNameTextField)) {
            stage.setKeyboardFocus(playerNameTextField);
            playerNameTextField.getOnscreenKeyboard().show(true);
        }
        else {
            stage.unfocus(playerNameTextField);
            playerNameTextField.getOnscreenKeyboard().show(false);
        }

        if (connectButton.contains() && !remoteHostNameTextField.getText().equals("")) {
            if (NetworkingManager.startClient(remoteHostNameTextField.getText()))
                nextScreen = new PlayScreen(false);
        }

        if (hostButton.contains()) {
            if (NetworkingManager.startHost(roomNameTextField.getText()))
                nextScreen = new PlayScreen(true);
        }

        if (brushButton.contains())
            nextScreen = new SettingsScreen();
    }

    private void drawExitConfirmationDialog() {
        spriteBatch.begin();
        for (GameObject2D o : exitConfirmationDialogWigets) spriteBatch.draw(o);
        spriteBatch.draw(acceptExitButton);
        spriteBatch.draw(declineExitButton);
        spriteBatch.end();
        exitConfirmationDialogStage.act();
        exitConfirmationDialogStage.draw();
    }

    private void consumeInputForExitConfirmationDialog() {
        Gdx.input.setInputProcessor(exitConfirmationDialogStage);

        if (!Gdx.input.justTouched())
            return;

        if (acceptExitButton.contains())
            Gdx.app.exit();
        else if (declineExitButton.contains())
            exitConfirmationDialogOpened = false;
    }

    private void drawScreenTransition(float deltaTime) {
        float newDelta = deltaTime;

        if (newDelta > 1 / 60f)
            newDelta = 1 / 60f;

        if (nextScreen != null) {
            fadeOutAnimationProgress += 4 * newDelta;

            if (fadeOutAnimationProgress >= 1.5)
                GameSuper.instance.setScreen(nextScreen);
        }
        else if (fadeOutAnimationProgress > 0) {
            fadeOutAnimationProgress -= 4 * newDelta;

            if (fadeOutAnimationProgress < 0)
                fadeOutAnimationProgress = 0;
        }

        Pixmap pad = new Pixmap(screenWidth, screenHeight, Pixmap.Format.RGBA8888);
        Color anotherColor = new Color(GameSuper.palette.secondary);
        float alpha = fadeOutAnimationProgress;

        if (alpha > 1)
            alpha = 1;

        anotherColor.a = alpha;

        pad.setColor(anotherColor);
        pad.fill();
        GameObject2D padObject = new GameObject2D(pad, 0, 0);

        spriteBatch.begin();
        spriteBatch.draw(padObject);
        spriteBatch.end();

        pad.dispose();
        padObject.dispose();

        if (fadeOutAnimationProgress == -1)
            fadeOutAnimationProgress = 1;
    }

    @Override
    public void render(float delta) {
        Color color = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(color.r, color.g, color.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        drawMainStage();

        if (!exitConfirmationDialogOpened)
            consumeInputForMainSage();
        else {
            drawExitConfirmationDialog();
            consumeInputForExitConfirmationDialog();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK))
            exitConfirmationDialogOpened = !exitConfirmationDialogOpened;

        drawScreenTransition(delta);
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
