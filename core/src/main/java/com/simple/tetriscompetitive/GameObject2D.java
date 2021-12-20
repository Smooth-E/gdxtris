package com.simple.tetriscompetitive;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class GameObject2D extends Texture {

    private Vector2 position;
    private Vector2 origin;
    private int layer;
    private boolean isActive = true;
    public Stage stage;
    public Label label;

    public boolean isActive(){return isActive;}

    public void setActive(boolean status){isActive = status;}

    public void setLayer(int layer){this.layer = layer;}

    public int getLayer(){return layer;}

    public void update(){}
    public void setPosition(Vector2 position){
        this.position = position;
    }

    public void setPosition(float x, float y){
        this.position = new Vector2(x, y);
    }

    public Vector2 getPosition(){
        return new Vector2(position.x, position.y);
    }

    public float getX(){return position.x;}

    public float getY(){return position.y;}

    public void setX(float x){position = new Vector2(x, position.y);}

    public void setY(float y){position = new Vector2(position.x, y);}


    public boolean contains(float x, float y){
        return this.isActive && x >= position.x && x <= position.x + getWidth() && y >= position.y && y <= position.y + getHeight();
    }

    public boolean contains(Vector2 position){
        return contains(position.x, position.y);
    }

    public void setOrigin(float x, float y){
        origin = new Vector2(x, y);
    }

    public Vector2 getOrigin(){
        return new Vector2(origin.x, origin.y);
    }


    public static class MySpriteBatch extends SpriteBatch {
        public void draw(GameObject2D object){
            if(object.isActive()) this.draw(object, object.getX(), object.getY());
        }
    }



    //Constructors
    public GameObject2D(Pixmap pixmap, float x, float y){
        super(pixmap);
        position = new Vector2(x, y);
        origin = new Vector2(x, y);;
    }

    public GameObject2D(Pixmap pixmap, float x, float y, int layer){
        this(pixmap, x, y);
        this.layer = layer;
    }

    public GameObject2D(Pixmap pixmap, float x, float y, Label label, Stage stage){
        this(pixmap, x, y);
        this.label = label;
        this.stage = stage;
        stage.addActor(label);
    }

    public GameObject2D(String internalPath) {
        super(internalPath);
    }

    public GameObject2D(FileHandle file) {
        super(file);
    }

    public GameObject2D(FileHandle file, boolean useMipMaps){
        super(file, useMipMaps);
    }

    public GameObject2D(FileHandle file, Pixmap.Format format, boolean useMipMaps){
        super(file, format, useMipMaps);
    }

    public GameObject2D(Pixmap pixmap) {
        super(pixmap);
    }

    public GameObject2D(Pixmap pixmap, boolean useMipMaps){
        super(pixmap, useMipMaps);
    }

    public GameObject2D(Pixmap pixmap, Pixmap.Format format, boolean useMipMaps){
        super(pixmap, format, useMipMaps);
    }

    public GameObject2D(int width, int height, Pixmap.Format format){
        super(width, height, format);
    }

    public GameObject2D(TextureData data) {
        super(data);
    }
}
