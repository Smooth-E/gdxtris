package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataManagement {

    public static class DataContainer implements Serializable {
        public String nickname = "John Doe";
        public int colorSchemeIndex = 0;
    }

    public static DataContainer data = null;
    static String savingPath = "competitive-tetris-save-file";

    public static void loadData() {
        try {
            FileInputStream fileInputStream = new FileInputStream(Gdx.files.getLocalStoragePath() + "/" + savingPath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            data = (DataContainer) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        }
        catch (Exception e) {
            Gdx.app.log(Constants.ERROR, "Failed to load data!\n" + e.toString());
            data = new DataContainer();
        }
    }

    public static void saveData() {
        if (data == null) data = new DataContainer();
        try {
            File saveFile = new File(Gdx.files.getLocalStoragePath() + "/" + savingPath);
            saveFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (Exception e) {
            Gdx.app.log(Constants.ERROR, "Failed to save data!\n" + e.toString());
        }
    }
}
