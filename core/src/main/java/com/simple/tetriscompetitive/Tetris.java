package com.simple.tetriscompetitive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Tetris {

    public static final int FIGURE_NONE = -1, FIGURE_I = 0, FIGURE_O = 1, FIGURE_J = 2, FIGURE_L = 3, FIGURE_Z = 4, FIGURE_S = 5;
    public static final int fieldWidth = 10, fieldHeight = 22;
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

    public static int[][] getFigure(){
        return figures[NetworkingManager.playerInfo.figureID][NetworkingManager.playerInfo.figureRotation];
    }

    public static void start(){
        NetworkingManager.playerInfo.canPlay = true;
        NetworkingManager.playerInfo.field = new int[fieldHeight][fieldWidth];
        NetworkingManager.playerInfo.holdID = FIGURE_NONE;
        NetworkingManager.playerInfo.turn = 0;
        NetworkingManager.playerInfo.figureID = new Random(NetworkingManager.clientSideRoom.seed).nextInt(8);
        NetworkingManager.playerInfo.figureRotation = 0;
        NetworkingManager.playerInfo.figureX = fieldWidth / 2;
        NetworkingManager.playerInfo.figureY = 0;
    }

    private static void checkCollision(){

    }

    public static void tick(){
        boolean shouldStop = false;
        int[][] figure = getFigure();
        for (int y = 0; y < figure.length; y++){
            for (int x = 0; x < figure[0].length; x++){
                if (figure[y][x] == 1 &&
                            (NetworkingManager.playerInfo.figureY + y == fieldHeight ||
                            NetworkingManager.playerInfo.field[NetworkingManager.playerInfo.figureY + y + 1][NetworkingManager.playerInfo.figureX + x] > 0)){
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
            NetworkingManager.playerInfo.figureID = new Random(NetworkingManager.clientSideRoom.seed + NetworkingManager.playerInfo.turn).nextInt(8);
            NetworkingManager.playerInfo.figureY = 0;
            NetworkingManager.playerInfo.figureX = fieldWidth / 2 - figure[0].length / 2;
        }
        else
            NetworkingManager.playerInfo.figureY += 1;
    }

    public static void moveLeft(){

    }

    public static void moveRight(){

    }

    public static void moveDown(){

    }

    public static void instantDown(){

    }

    public static void rotateClockwise(){

    }

    public static void rotateAnticlockwise(){

    }
}
