package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class MenuScreen implements Screen {

    static Stage stage = new Stage();
    static GameObject2D.MySpriteBatch spriteBatch = new GameObject2D.MySpriteBatch();
    static Label hostButtonLabel, connectButtonLabel;
    static TextField playerNameTextField, roomNameTextField, remoteHostNameTextField;
    static GameObject2D hostButton, connectButton;
    static ArrayList<GameObject2D> objects = new ArrayList<>();
    static int screenWidth, screenHeight;

    @Override
    public void show() {
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
        objects.add(new GameObject2D(pixmap, x + 20, y + 20));
        objects.add(new GameObject2D(pixmap, x + 20, y + 20 + oldHeight + 20));
        pixmap.dispose();

        Pixmap userPic = new Pixmap((int) (340 * ratioWidth) + 20, (int) (340 * ratioWidth) + 20, Pixmap.Format.RGBA8888);
        userPic.setColor(GameSuper.palette.onSecondary);
        userPic.fillCircle(userPic.getHeight() / 2, userPic.getHeight() / 2, userPic.getHeight() / 2);
        userPic.drawPixmap(new Pixmap(Gdx.files.internal("profile-pic.png")),
                0, 0, 1000, 1000, 10, 10, userPic.getHeight() - 20, userPic.getHeight() - 20);
        objects.add(new GameObject2D(userPic, (screenWidth - userPic.getWidth()) / 2f, screenHeight - 100 - userPic.getHeight()));

        w = screenWidth - 200;
        h = (int)(150 * ratioHeight);
        pixmap = Drawing.createRoundedRectangle(w, h, cornerRadius, GameSuper.palette.onSecondary);
        objects.add(new GameObject2D(pixmap, 100, screenHeight - h - 100 - userPic.getHeight() - 20));
        pixmap.dispose();
        userPic.dispose();
    }

    public static void load(){

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
