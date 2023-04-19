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
    static final String SAVE_FILE_NAME = "competitive-tetris-save-file";

    public static void loadData() {
        try {
            String filePath = Gdx.files.getLocalStoragePath() + "/" + SAVE_FILE_NAME;
            FileInputStream fileInputStream = new FileInputStream(filePath);

            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            data = (DataContainer) objectInputStream.readObject();

            objectInputStream.close();
            fileInputStream.close();
        }
        catch (Exception exception) {
            Gdx.app.log(Constants.ERROR, "Failed to load data!\n" + exception);
            data = new DataContainer();
        }
    }

    public static void saveData() {
        if (data == null)
            data = new DataContainer();

        try {
            String filePath = Gdx.files.getLocalStoragePath() + "/" + SAVE_FILE_NAME;
            File saveFile = new File(filePath);
            saveFile.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);

            objectOutputStream.close();
            fileOutputStream.close();
        }
        catch (Exception exception) {
            Gdx.app.log(Constants.ERROR, "Failed to save data!\n" + exception);
        }
    }
}
