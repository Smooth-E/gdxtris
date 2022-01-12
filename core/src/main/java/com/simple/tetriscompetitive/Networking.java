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
        public int[][] field = new int[22][10];
        public int figureID = 2, figureRotation, figureX, figureY, holdID = -1, turn;
        public boolean canPlay = false;
        public boolean holdPerformed = false;
    }

    public static class Room {
        public ArrayList<PlayerContainer> players = new ArrayList<>();
        public String name = "Sample Room";
        public int status = STATUS_IDLE;
        public static final int STATUS_PLAYING = 0, STATUS_CD1 = 1, STATUS_CD2 = 2, STATUS_CD3 = 3, STATUS_IDLE = 4;
        public long seed = new Random().nextLong();
        public boolean gameEnded = false;

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

    public static class StartGameRequest {}

    public static class UpdatedGameStateRequest {

        PlayerContainer playerState;

        public UpdatedGameStateRequest(){
            this.playerState = NetworkingManager.playerInfo;
        }

        public UpdatedGameStateRequest (PlayerContainer player) {
            this.playerState = player;
        }
    }

    public static class  UpdatedGameStateResponse{

        Room roomInfo;

        public UpdatedGameStateResponse(){}

        public UpdatedGameStateResponse(Room roomInfo) {
            this.roomInfo = roomInfo;
        }
    }

    public static class GameEndRequest {}

    public static  class DisconnectRequest {
        public int id;

        public DisconnectRequest(){}
        public DisconnectRequest(int id){this.id = id;}
    }

    public static class GameEndResponse {}

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
            else if (object instanceof UpdatedGameStateResponse) {
                UpdatedGameStateResponse response = (UpdatedGameStateResponse) object;
                NetworkingManager.clientSideRoom = response.roomInfo;
                for (int i = 0; i < NetworkingManager.clientSideRoom.players.size(); i++){
                    if (NetworkingManager.clientSideRoom.players.get(i).id == NetworkingManager.playerInfo.id) {
                        PlayerContainer playerOnServer = NetworkingManager.clientSideRoom.players.get(i);
                        NetworkingManager.playerInfo.canPlay = playerOnServer.canPlay;
                        NetworkingManager.playerInfo.stack = playerOnServer.stack;
                    }
                }
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
            else if (object instanceof StartGameRequest) {
                Gdx.app.log("", "Received to start a game!");
                for (int i = 0; i < NetworkingManager.roomInfo.players.size(); i++){
                    PlayerContainer p = NetworkingManager.roomInfo.players.get(i);
                    p.canPlay = true;
                    NetworkingManager.roomInfo.players.set(i, p);
                }
                new StartGameThread().start();
            }
            else if (object instanceof UpdatedGameStateRequest) {
                UpdatedGameStateRequest request = (UpdatedGameStateRequest) object;
                for (int i = 0; i < NetworkingManager.roomInfo.players.size(); i++){
                    if (NetworkingManager.roomInfo.players.get(i).id == request.playerState.id) {
                        NetworkingManager.roomInfo.players.set(i, request.playerState);
                        break;
                    }
                }

                boolean playersAlive = false;
                for (PlayerContainer player : NetworkingManager.roomInfo.players) {
                    if (player.canPlay) {
                        playersAlive = true;
                        break;
                    }
                }
                if (!playersAlive && NetworkingManager.roomInfo.status == Room.STATUS_PLAYING) NetworkingManager.roomInfo.status = Room.STATUS_IDLE;

                UpdatedGameStateResponse response = new UpdatedGameStateResponse(NetworkingManager.roomInfo);
                connection.sendTCP(response);
            }
            else if (object instanceof DisconnectRequest) {
                DisconnectRequest request = (DisconnectRequest) object;
                for (int i = 0; i < NetworkingManager.roomInfo.players.size(); i++) {
                    if (NetworkingManager.roomInfo.players.get(i).id == request.id) {
                        NetworkingManager.roomInfo.players.remove(i);
                        break;
                    }
                }
            }
        }
    }

    static class StartGameThread extends Thread {
        @Override
        public void run() {
            try {
                NetworkingManager.roomInfo.status = Room.STATUS_CD3;
                Thread.sleep(1000);
                NetworkingManager.roomInfo.status = Room.STATUS_CD2;
                Thread.sleep(1000);
                NetworkingManager.roomInfo.status = Room.STATUS_CD1;
                Thread.sleep(1000);
                NetworkingManager.roomInfo.status = Room.STATUS_PLAYING;
            }
            catch (java.lang.InterruptedException exception) {
                Gdx.app.log("ERROR", "Error in status thread!");
            }
        }
    }
}
