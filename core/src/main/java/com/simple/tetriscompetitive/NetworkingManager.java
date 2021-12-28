package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

public class NetworkingManager {

    public static Server server;
    public static Client client;
    static final int PORT = 8080;
    static final String NETWORK_ERROR = "NETWORK_ERROR";

    static Networking.Room roomInfo;
    public static Networking.PlayerContainer playerInfo;
    public static Networking.Room clientSideRoom;

    static void initializeKryo(Kryo kryo){
        kryo.register(Networking.Room.class);
        kryo.register(Networking.PlayerContainer.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(int[][].class);
        kryo.register(int[].class);
        kryo.register(Networking.ConnectionRequest.class);
        kryo.register(Networking.ConnectionResponse.class);
        kryo.register(Networking.UpdatedGameStateRequest.class);
        kryo.register(Networking.UpdatedGameStateResponse.class);
        kryo.register(Networking.StartGameRequest.class);
    }

    public static boolean startServer() {
        server = new Server();
        initializeKryo(server.getKryo());
        server.addListener(new Networking.ServerListener());
        server.start();
        try {
            server.bind(PORT);
            return true;
        }
        catch (Exception e) {
            Gdx.app.log(NETWORK_ERROR, "Unable to start server!\n" + e.toString());
            return false;
        }
    }

    public static boolean startClient(String address) {
        client = new Client();
        initializeKryo(client.getKryo());
        client.addListener(new Networking.ClientListener());
        client.start();
        try {
            client.connect(5000, address, PORT);
            Networking.ConnectionRequest request = new Networking.ConnectionRequest(DataManagement.data.nickname);
            client.sendTCP(request);
            return true;
        }
        catch (Exception e) {
            Gdx.app.log(NETWORK_ERROR, "Unable to connect!\n" + e.toString());
            return false;
        }
    }

    public static boolean startHost(String roomName){
        if (startServer()) {
            roomInfo = new Networking.Room(roomName);
            return startClient("localhost");
        }
        else return false;
    }
}
