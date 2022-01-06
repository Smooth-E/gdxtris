package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Tetris {

    public static final int FIGURE_NONE = -1, FIGURE_I = 0, FIGURE_O = 1, FIGURE_J = 2, FIGURE_L = 3, FIGURE_Z = 4, FIGURE_S = 5, FIGURE_T = 6;
    public static final int fieldWidth = 10, fieldHeight = 20;
    public static final Color[] figureColors = new Color[] {
            Color.valueOf("#00E6FE"),
            Color.YELLOW,
            Color.BLUE,
            Color.ORANGE,
            Color.RED,
            Color.GREEN,
            Color.PURPLE
    };

    static int[][][][] figures = new int[][][][] {
            {                                       // I piece
                    {
                            {1},
                            {1},
                            {1},
                            {1}
                    },
                    {
                            {1, 1, 1, 1}
                    },
                    {
                            {1},
                            {1},
                            {1},
                            {1}
                    },
                    {
                            {1, 1, 1, 1}
                    }
            },
            //O piece
            {
                    {
                            {1, 1},
                            {1, 1},
                    },
                    {
                            {1, 1},
                            {1, 1},
                    },
                    {
                            {1, 1},
                            {1, 1},
                    },
                    {
                            {1, 1},
                            {1, 1},
                    },
            },
            //J piece
            {
                    {
                            {1, 0, 0},
                            {1, 1, 1}
                    },
                    {
                            {1, 1},
                            {1, 0},
                            {1, 0},
                    },
                    {
                            {1, 1, 1},
                            {0, 0, 1}
                    },
                    {
                            {0, 1},
                            {0, 1},
                            {1, 1},
                    }
            },
            //L piece
            {
                    {
                            {0, 0, 1},
                            {1, 1, 1}
                    },
                    {
                            {1, 0},
                            {1, 0},
                            {1, 1}
                    },
                    {
                            {1, 1, 1},
                            {1, 0, 0}
                    },
                    {
                            {1, 1},
                            {0, 1},
                            {0, 1}
                    }
            },
            //Z piece
            {
                    {
                            {1, 1, 0},
                            {0, 1, 1}
                    },
                    {
                            {0, 1},
                            {1, 1},
                            {1, 0}
                    },
                    {
                            {1, 1, 0},
                            {0, 1, 1}
                    },
                    {
                            {0, 1},
                            {1, 1},
                            {1, 0}
                    }
            },
            //S piece
            {
                    {
                            {0, 1, 1},
                            {1, 1, 0}
                    },
                    {
                            {1, 0},
                            {1, 1},
                            {0, 1}
                    },
                    {
                            {0, 1, 1},
                            {1, 1, 0}
                    },
                    {
                            {1, 0},
                            {1, 1},
                            {0, 1}
                    }
            },
            //T piece
            {
                    {
                            {0, 1, 0},
                            {1, 1, 1},
                    },
                    {
                            {1, 0},
                            {1, 1},
                            {1, 0},
                    },
                    {
                            {1, 1, 1},
                            {0, 1, 0},
                    },
                    {
                            {0, 1},
                            {1, 1},
                            {0, 1},
                    }
            }
    };

    public static void generateField(){
        NetworkingManager.playerInfo.field = new int[fieldHeight][fieldHeight];
        for (int x = 0; x < fieldWidth; x ++) {
            for (int y = 0; y < fieldHeight; y++){
                NetworkingManager.playerInfo.field[y][x] = -1;
            }
        }
    }

    public static int[][] getFigure(){
        return figures[NetworkingManager.playerInfo.figureID][NetworkingManager.playerInfo.figureRotation];
    }

    public static void start(){
        NetworkingManager.playerInfo.canPlay = true;
        generateField();
        NetworkingManager.playerInfo.holdID = FIGURE_NONE;
        NetworkingManager.playerInfo.turn = 0;
        NetworkingManager.playerInfo.figureID = new Random(NetworkingManager.clientSideRoom.seed).nextInt(7);
        NetworkingManager.playerInfo.figureRotation = 0;
        NetworkingManager.playerInfo.figureX = fieldWidth / 2;
        NetworkingManager.playerInfo.figureY = 0;
    }

    private static void checkCollision(){

    }

    public static boolean tick(){
        boolean shouldStop = false;
        int[][] figure = getFigure();
        for (int y = 0; y < figure.length; y++){
            for (int x = 0; x < figure[0].length; x++){
                if (figure[y][x] == 1 &&
                            (NetworkingManager.playerInfo.figureY + y >= fieldHeight - 1 ||
                            NetworkingManager.playerInfo.field[NetworkingManager.playerInfo.figureY + y + 1][NetworkingManager.playerInfo.figureX + x] > -1)){
                    shouldStop = true;
                    break;
                }
            }
        }
        if (shouldStop) {
            for (int fy = 0; fy < figure.length; fy++) {
                for (int fx = 0; fx < figure[0].length; fx++) {
                    if (figure[fy][fx] != 0)
                        NetworkingManager.playerInfo.field[NetworkingManager.playerInfo.figureY + fy][NetworkingManager.playerInfo.figureX + fx] = NetworkingManager.playerInfo.figureID;
                }
            }
            NetworkingManager.playerInfo.turn++;
            NetworkingManager.playerInfo.figureID = new Random(NetworkingManager.clientSideRoom.seed + NetworkingManager.playerInfo.turn).nextInt(7);
            NetworkingManager.playerInfo.figureY = 0;
            NetworkingManager.playerInfo.figureX = fieldWidth / 2 - figure[0].length / 2;
        }
        else
            NetworkingManager.playerInfo.figureY += 1;

        return !shouldStop;
    }

    public static void moveLeft(){
        int[][] figure = getFigure();
        boolean canMove = true;
        Networking.PlayerContainer player = NetworkingManager.playerInfo;
        for (int x = 0; x < figure[0].length; x++) {
            for (int y = 0; y < figure.length; y++) {
                if (figure[y][x] == 1 &&
                !(player.figureX > 0 &&
                player.field[player.figureY + y][player.figureX + x - 1] < 0)) {
                    canMove = false;
                    break;
                }
            }
        }
        if (canMove) NetworkingManager.playerInfo.figureX -= 1;
    }

    public static void moveRight(){
        int[][] figure = getFigure();
        boolean canMove = true;
        Networking.PlayerContainer player = NetworkingManager.playerInfo;
        for (int x = 0; x < figure[0].length; x++) {
            for (int y = 0; y < figure.length; y++) {
                if (figure[y][x] == 1 &&
                        !(player.figureX + figure[0].length < fieldWidth &&
                                player.field[player.figureY + y][player.figureX + x + 1] < 0)) {
                    canMove = false;
                    break;
                }
            }
        }
        if (canMove) NetworkingManager.playerInfo.figureX += 1;
    }

    public static void moveDown(){
        tick();
    }

    public static void instantDown(){
        while (tick()){}
    }

    private static boolean isFit(int newRotation) {
        Networking.PlayerContainer player = NetworkingManager.playerInfo;
        int[][] figure = figures[player.figureID][newRotation];
        boolean canFit = true;
        for (int x = 0; x < figure[0].length; x++) {
            for (int y = 0; y < figure.length; y++) {
                if (figure[y][x] == 1 &&
                        player.field[player.figureX + x][player.figureY + y] != -1){
                    canFit = false;
                    break;
                }
            }
        }
        return canFit;
    }

    public static void rotateClockwise(){
        Networking.PlayerContainer player = NetworkingManager.playerInfo;
        int newRotation = player.figureRotation + 1;
        if (newRotation >= 4) newRotation = 0;
        if (isFit(newRotation)) NetworkingManager.playerInfo.figureRotation = newRotation;
        else {
            NetworkingManager.playerInfo.figureY -= 1;
            if (isFit(newRotation)) NetworkingManager.playerInfo.figureRotation = newRotation;
            else NetworkingManager.playerInfo.figureY += 1;
        }
    }

    public static void rotateAnticlockwise(){

    }
}
