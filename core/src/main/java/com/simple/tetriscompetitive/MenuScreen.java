package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class MenuScreen implements Screen {

    GameSuper gameSuper;

    public MenuScreen(GameSuper gameSuper) {
        this.gameSuper = gameSuper;
    }

    Stage stage = new Stage();
    GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    Label playOnlineLabel, playLANLabel, playSoloLabel, playerSettingsLabel, exitLabel;
    GameObject2D playOnlineButton, playLANButton, playSoloButton, playerSettingsButton, exitButton;
    int screenWidth, screenHeight;

    @Override
    public void show() {
        screenHeight = Gdx.graphics.getHeight();
        screenWidth = Gdx.graphics.getWidth();
        int margin = screenWidth / 20;
        int buttonWidth = screenWidth - 2 * margin;
        int buttonHeight = (int)((screenHeight - margin) / 5) - margin;
        playOnlineButton     = new GameObject2D(Drawing.createButtonPixmap(buttonWidth, buttonHeight, 20), margin, screenHeight - (buttonHeight * 1 + margin * 1));
        playLANButton        = new GameObject2D(Drawing.createButtonPixmap(buttonWidth, buttonHeight, 20), margin, screenHeight - (buttonHeight * 2 + margin * 2));
        playSoloButton       = new GameObject2D(Drawing.createButtonPixmap(buttonWidth, buttonHeight, 20), margin, screenHeight - (buttonHeight * 3 + margin * 3));
        playerSettingsButton = new GameObject2D(Drawing.createButtonPixmap(buttonWidth, buttonHeight, 20), margin, screenHeight - (buttonHeight * 4 + margin * 4));
        exitButton           = new GameObject2D(Drawing.createButtonPixmap(buttonWidth, buttonHeight, 20), margin, screenHeight - (buttonHeight * 5 + margin * 5));

        FreeTypeFontGenerator.FreeTypeFontParameter buttonFontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        buttonFontParameter.size = buttonWidth / "play via lan".length();
        BitmapFont buttonFont = GameSuper.mainFontGenerator.generateFont(buttonFontParameter);
        Label.LabelStyle buttonLabelStyle = new Label.LabelStyle(buttonFont, Color.WHITE);

        playOnlineLabel = new Label("Play Online", buttonLabelStyle);
        playOnlineLabel.setAlignment(Align.left);
        playOnlineLabel.setPosition(playOnlineButton.getX() + margin, playOnlineButton.getY() + buttonHeight / 2f);
        stage.addActor(playOnlineLabel);
    }

    @Override
    public void render(float delta) {
        Color c = GameSuper.palettes[DataManagement.data.colorSchemeIndex].secondary;
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(c.r,c.g,c.b,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(playOnlineButton);
        spriteBatch.draw(playLANButton);
        spriteBatch.draw(playSoloButton);
        spriteBatch.draw(playerSettingsButton);
        spriteBatch.draw(exitButton);
        spriteBatch.end();

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
