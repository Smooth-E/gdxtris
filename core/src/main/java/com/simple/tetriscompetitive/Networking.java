package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.util.ArrayList;
import java.util.Random;

public class Networking {

    public static class PlayerContainer {
        public String name;
        public int score = 0, stack = 10, id, targetID = -1;
        public int[][] field = new int[10][22];
        public int figureID, figureRotation, figureX, figureY;
    }

    public static class Room {
        public ArrayList<PlayerContainer> players = new ArrayList<>();
        public String name = "Sample Room";
        public int status = STATUS_IDLE;
        public static final int STATUS_PLAYING = 0, STATUS_CD1 = 1, STATUS_CD2 = 2, STATUS_CD3 = 3, STATUS_IDLE = 4;

        public Room(String name){
            this.name = name;
            this.status = Room.STATUS_IDLE;
            this.players = new ArrayList<>();
        }

        public Room(){}
    }

    public static class ConnectionRequest {
        String playerName;

        public ConnectionRequest(String name){
            this.playerName = name;
        }

        public ConnectionRequest(){}
    }

    public static class ConnectionResponse {
        Room roomInfo;
        int playerID;

        public ConnectionResponse(){}
    }

    public static class ClientListener extends Listener {
        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof ConnectionResponse) {
                ConnectionResponse response = (ConnectionResponse) object;
                NetworkingManager.playerInfo = new PlayerContainer();
                NetworkingManager.playerInfo.name = DataManagement.data.nickname;
                NetworkingManager.playerInfo.id = response.playerID;
                NetworkingManager.clientSideRoom = response.roomInfo;
                Gdx.app.log("NETWORK", "Received a response!\n" + NetworkingManager.playerInfo.toString());
            }
        }
    }

    public static class ServerListener extends Listener {
        @Override
        public void received(Connection connection, Object object) {
            if (object instanceof ConnectionRequest) {
                ConnectionRequest request = (ConnectionRequest) object;
                PlayerContainer player = new PlayerContainer();
                player.name = request.playerName;
                int id;
                while (true) {
                    id = new Random().nextInt();
                    boolean collide = false;
                    for (PlayerContainer p : NetworkingManager.roomInfo.players){
                        if (p.id == id) {
                            collide = true;
                            break;
                        }
                    }
                    if (!collide) break;
                }
                player.id = id;
                NetworkingManager.roomInfo.players.add(player);
                ConnectionResponse response = new ConnectionResponse();
                response.playerID = id;
                response.roomInfo = NetworkingManager.roomInfo;
                connection.sendTCP(response);
            }
        }
    }
}
