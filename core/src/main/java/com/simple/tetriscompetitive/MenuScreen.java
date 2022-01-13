package com.simple.tetriscompetitive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class MenuScreen implements Screen {

    boolean askToExit = false;

    Stage stage = new Stage();
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    Label hostButtonLabel, connectButtonLabel;
    TextField playerNameTextField, roomNameTextField, remoteHostNameTextField;
    GameObject2D hostButton, connectButton, settingsButton;
    ArrayList<GameObject2D> objects = new ArrayList<>();
    int screenWidth, screenHeight;

    ArrayList<GameObject2D> askToExitObjects = new ArrayList<>();
    GameObject2D acceptExitButton, declineExitButton;
    Label askExitLabel;
    Stage askToExitStage = new Stage();

    float fadeOutAnimationProgress = 1;
    Screen nextScreen = null;

    @Override
    public void show() {}

    public void init() {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        float ratioHeight = screenHeight / 1920f, ratioWidth = screenWidth / 1080f;
        int cornerRadius = (int)(70 * ratioHeight);

        int w = screenWidth - 40, h = (int) (500 * ratioHeight);
        int x = 20, y = (int) (100 * ratioHeight);
        Pixmap pixmap = Drawing.createRoundedRectangle(w, h, cornerRadius, GameSuper.palette.primary);
        objects.add(new GameObject2D(pixmap, x, y));
        objects.add(new GameObject2D(pixmap, x, y + h + 20));
        pixmap.dispose();
        int oldHeight = h;
        h = oldHeight / 2 - 20 - 20;
        w = w - 40;
        pixmap = Drawing.createRoundedRectangle(w, h, cornerRadius, GameSuper.palette.secondary);
        objects.add(new GameObject2D(pixmap, x + 20, y + 10 + oldHeight / 2f));
        objects.add(new GameObject2D(pixmap, x + 20, y + 10 + oldHeight + 20 + oldHeight / 2f));
        pixmap.dispose();
        pixmap = Drawing.createRoundedRectangle(w, h, cornerRadius, GameSuper.palette.onSecondary);
        objects.add(new GameObject2D(pixmap, x + 20, y + 20)); // Connect button
        connectButton = objects.get(objects.size() - 1);
        objects.add(new GameObject2D(pixmap, x + 20, y + 20 + oldHeight + 20));
        hostButton = objects.get(objects.size() - 1);
        pixmap.dispose();

        Pixmap userPic = new Pixmap((int) (340 * ratioWidth) + 20, (int) (340 * ratioWidth) + 20, Pixmap.Format.RGBA8888);
        userPic.setColor(GameSuper.palette.onSecondary);
        userPic.fillCircle(userPic.getHeight() / 2, userPic.getHeight() / 2, userPic.getHeight() / 2);
        userPic.drawPixmap(Drawing.getIcon("profile-pic.png", userPic.getWidth() - 20,
                userPic.getHeight() - 20, GameSuper.palette.secondary), 10, 10);
        objects.add(new GameObject2D(userPic, (screenWidth - userPic.getWidth()) / 2f, screenHeight - 100 - userPic.getHeight()));

        w = screenWidth - 200;
        h = (int)(150 * ratioHeight);
        pixmap = Drawing.createRoundedRectangle(w, h, cornerRadius, GameSuper.palette.onSecondary);
        objects.add(new GameObject2D(pixmap, 100, screenHeight - h - 100 - userPic.getHeight() - 20));
        pixmap = Drawing.createRoundedRectangle(w - h, h, cornerRadius, GameSuper.palette.onSecondary);
        objects.add(new GameObject2D(pixmap, 100, screenHeight - h - 100 - userPic.getHeight() - 20));
        GameObject2D playerNameBG = objects.get(objects.size() - 1);
        pixmap = new Pixmap(h, h, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(Drawing.getIcon("brush.png", h / 3 * 2, h / 3 * 2, GameSuper.palette.secondary), h / 6, h / 6);
        objects.add(new GameObject2D(pixmap, playerNameBG.getX() + playerNameBG.getWidth(), playerNameBG.getY()));
        settingsButton = objects.get(objects.size() - 1);
        pixmap.dispose();
        userPic.dispose();

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = GameSuper.palette.secondary;
        parameter.size = connectButton.getHeight() - (int)(40 * ratioHeight) - 30;
        BitmapFont font = GameSuper.mainFontGenerator.generateFont(parameter);

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        connectButtonLabel = new Label("CONNECT", style);
        connectButtonLabel.setSize(connectButton.getWidth(), connectButton.getHeight());
        connectButtonLabel.setAlignment(Align.center);
        connectButtonLabel.setPosition(connectButton.getX(), connectButton.getY(), Align.bottomLeft);
        stage.addActor(connectButtonLabel);

        hostButtonLabel = new Label("HOST", style);
        hostButtonLabel.setSize(connectButtonLabel.getWidth(), connectButtonLabel.getHeight());
        hostButtonLabel.setPosition(hostButton.getX(), hostButton.getY());
        hostButtonLabel.setAlignment(Align.center);
        stage.addActor(hostButtonLabel);

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (playerNameBG.getWidth() - 40) / 10;
        parameter.color = GameSuper.palette.secondary;
        font = GameSuper.mainFontGenerator.generateFont(parameter);

        pixmap = Drawing.createRoundedRectangle(10, (int)font.getLineHeight(), 5, GameSuper.palette.secondary);

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new TextureRegionDrawable(new Texture(pixmap));
        textFieldStyle.messageFontColor = textFieldStyle.fontColor;
        textFieldStyle.messageFont = textFieldStyle.font;

        playerNameTextField = new TextField(DataManagement.data.nickname, textFieldStyle);
        playerNameTextField.setAlignment(Align.center);
        playerNameTextField.setPosition(playerNameBG.getX() + cornerRadius, playerNameBG.getY());
        playerNameTextField.setSize(playerNameBG.getWidth() - cornerRadius, playerNameBG.getHeight());
        stage.addActor(playerNameTextField);

        parameter.size = connectButton.getHeight() / 3 + 1;
        parameter.color = GameSuper.palette.onSecondary;

        font = GameSuper.mainFontGenerator.generateFont(parameter);

        pixmap = Drawing.createRoundedRectangle(10,(int)font.getLineHeight(), 5, GameSuper.palette.onSecondary);

        TextField.TextFieldStyle bigTextFieldStyle = new TextField.TextFieldStyle();
        bigTextFieldStyle.font = font;
        bigTextFieldStyle.messageFont = font;
        bigTextFieldStyle.cursor = new TextureRegionDrawable(new Texture(pixmap));
        bigTextFieldStyle.fontColor = Color.WHITE;
        bigTextFieldStyle.messageFontColor = Color.WHITE;

        remoteHostNameTextField = new TextField("192.168.0.193", bigTextFieldStyle);
        remoteHostNameTextField.setMessageText("ROOM IP");
        remoteHostNameTextField.setAlignment(Align.center);
        remoteHostNameTextField.setPosition(connectButton.getX(), connectButton.getY() + connectButton.getHeight() + 10, Align.bottomLeft);
        remoteHostNameTextField.setSize(connectButton.getWidth(), connectButton.getHeight());
        remoteHostNameTextField.setOnscreenKeyboard(new TextField.OnscreenKeyboard() {
            @Override
            public void show(boolean visible) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        if (!text.equals("")) remoteHostNameTextField.setText(text);
                        stage.unfocus(remoteHostNameTextField);
                    }

                    @Override
                    public void canceled() {
                        stage.unfocus(remoteHostNameTextField);
                    }
                }, "Enter the host name you want to connect to:", "", "192.168.0.1");
            }
        });
        stage.addActor(remoteHostNameTextField);

        roomNameTextField = new TextField("", bigTextFieldStyle);
        roomNameTextField.setAlignment(Align.center);
        roomNameTextField.setPosition(hostButton.getX(), hostButton.getY() + 10 + hostButton.getHeight(), Align.bottomLeft);
        roomNameTextField.setSize(hostButton.getWidth(), hostButton.getHeight());
        roomNameTextField.setMessageText("ROOM NAME");
        roomNameTextField.setOnscreenKeyboard(new TextField.OnscreenKeyboard() {
            @Override
            public void show(boolean visible) {
                Gdx.input.getTextInput(new Input.TextInputListener() {
                    @Override
                    public void input(String text) {
                        if (!text.equals("")) roomNameTextField.setText(text);
                        stage.unfocus(roomNameTextField);
                    }

                    @Override
                    public void canceled() {
                        stage.unfocus(roomNameTextField);
                    }
                }, "Enter room name:", "", "Sample Room");
            }
        });
        stage.addActor(roomNameTextField);

        // Create an exit dialog
        int margin = screenWidth / 10;

        pixmap = new Pixmap(screenWidth, screenHeight, Pixmap.Format.RGBA8888);
        Color color = new Color(GameSuper.palette.primary);
        color.a = .5f;
        pixmap.setColor(color);
        pixmap.fill();
        askToExitObjects.add(new GameObject2D(pixmap, 0, 0));
        pixmap.dispose();

        pixmap = new Pixmap(screenWidth - 2 * margin, screenHeight / 3, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth(), pixmap.getHeight(),
                screenHeight / 3 / 4, GameSuper.palette.secondary), 0, 0);
        askToExitObjects.add(new GameObject2D(pixmap, margin, screenHeight / 3f));
        pixmap.dispose();

        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = Math.min(screenHeight / 6 / 2, (screenWidth - 2 * margin) / "Are you sure".length());
        parameter.color = GameSuper.palette.onSecondary;

        font = GameSuper.mainFontGenerator.generateFont(parameter);

        askExitLabel = new Label("Are you sure\nyou want to exit?", new Label.LabelStyle(font, Color.WHITE));
        askExitLabel.setPosition(margin, screenHeight / 3f + screenHeight / 3f / 2f, Align.bottomLeft);
        askExitLabel.setSize(screenWidth - 2 * margin, screenHeight / 3f / 2f);
        askExitLabel.setAlignment(Align.center);
        askToExitStage.addActor(askExitLabel);

        pixmap = new Pixmap((screenWidth - 2 * margin - margin) / 2, screenHeight / 3 / 2 - margin, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() * 2, pixmap.getHeight(),
                pixmap.getHeight() / 2, GameSuper.palette.onSecondary), 0, 0);
        pixmap.drawPixmap(Drawing.getIcon("tick.png", pixmap.getHeight() * 2 / 3, pixmap.getHeight() * 2 / 3,
                GameSuper.palette.secondary), (pixmap.getWidth() - pixmap.getHeight() * 2 / 3) / 2, pixmap.getHeight() / 3 / 2);
        acceptExitButton = new GameObject2D(pixmap, margin * 1.5f, screenHeight / 3f + margin / 2f);
        pixmap.dispose();

        pixmap = new Pixmap((screenWidth - 2 * margin - margin) / 2, screenHeight / 3 / 2 - margin, Pixmap.Format.RGBA8888);
        pixmap.drawPixmap(Drawing.createRoundedRectangle(pixmap.getWidth() * 2, pixmap.getHeight(),
                pixmap.getHeight() / 2, GameSuper.palette.onSecondary), -pixmap.getWidth(), 0);
        pixmap.drawPixmap(Drawing.getIcon("cross.png", pixmap.getHeight() * 2 / 3, pixmap.getHeight() * 2 / 3,
                GameSuper.palette.secondary), (pixmap.getWidth() - pixmap.getHeight() * 2 / 3) / 2, pixmap.getHeight() / 3 / 2);
        declineExitButton = new GameObject2D(pixmap, margin * 1.5f + pixmap.getWidth(), screenHeight / 3f + margin / 2f);
        pixmap.dispose();

        fadeOutAnimationProgress = -1;
    }

    @Override
    public void render(float delta) {
        if (!askToExit) Gdx.input.setInputProcessor(stage);
        else Gdx.input.setInputProcessor(askToExitStage);

        Color c = GameSuper.palette.secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) askToExit = !askToExit;

        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX(), mouseY = Gdx.input.getY();

            if (!askToExit) {
                if (GameObject2D.checkContains(playerNameTextField)) {
                    stage.setKeyboardFocus(playerNameTextField);
                    playerNameTextField.getOnscreenKeyboard().show(true);
                } else {
                    stage.unfocus(playerNameTextField);
                    playerNameTextField.getOnscreenKeyboard().show(false);
                }

                if (connectButton.contains() && !remoteHostNameTextField.getText().equals("")) {
                    if (NetworkingManager.startClient(remoteHostNameTextField.getText()))
                        GameSuper.instance.playScreen.isAdmin = false;
                        nextScreen = GameSuper.instance.playScreen;
                }

                if (hostButton.contains()) {
                    if (NetworkingManager.startHost(roomNameTextField.getText()))
                        GameSuper.instance.playScreen.isAdmin = true;
                        nextScreen = GameSuper.instance.playScreen;
                }

                if (settingsButton.contains()) nextScreen = GameSuper.instance.settingsScreen;
            }
            else {
                if (acceptExitButton.contains()) Gdx.app.exit();
                else if (declineExitButton.contains()) askToExit = false;
            }
        }

        if (playerNameTextField.hasKeyboardFocus()) playerNameTextField.getOnscreenKeyboard().show(true);

        if (!playerNameTextField.getText().equals(DataManagement.data.nickname) && playerNameTextField.getText().length() > 0){
            DataManagement.data.nickname = playerNameTextField.getText();
            DataManagement.saveData();
            playerNameTextField.setMessageText(DataManagement.data.nickname);
        }


        spriteBatch.begin();
        for (GameObject2D o : objects) spriteBatch.draw(o);
        spriteBatch.end();

        stage.draw();

        if (askToExit) {
            spriteBatch.begin();
            for (GameObject2D o : askToExitObjects) spriteBatch.draw(o);
            spriteBatch.draw(acceptExitButton);
            spriteBatch.draw(declineExitButton);
            spriteBatch.end();
            askToExitStage.act();
            askToExitStage.draw();
        }

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
