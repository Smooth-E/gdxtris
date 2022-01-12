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
            Color.PURPLE,
            Color.GRAY
    };

    static int[][][][] figures = new int[][][][] {
            // I piece
            {
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
                    },
                    {
                            {1},
                            {1},
                            {1},
                            {1}
                    },
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
    public static float autoShiftDelay = 0, autoRepeatDelay = 0;
    public static int ticksBeforeLock = 1;

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

    public static boolean tick(){
        if (NetworkingManager.playerInfo.canPlay) {
            for (int x = 0; x < fieldWidth; x++) {
                if (NetworkingManager.playerInfo.field[0][x] != -1) {
                    NetworkingManager.playerInfo.canPlay = false;
                    for (int fy = 0; fy < fieldHeight; fy++) {
                        for (int fx = 0; fx < fieldWidth; fx++) {
                            if (NetworkingManager.playerInfo.field[fy][fx] != -1) {
                                NetworkingManager.playerInfo.field[fy][fx] = 7;
                            }
                        }
                    }
                }
            }

            boolean shouldStop = false;
            int[][] figure = getFigure();
            for (int y = 0; y < figure.length; y++) {
                for (int x = 0; x < figure[0].length; x++) {
                    if (figure[y][x] == 1 &&
                            (NetworkingManager.playerInfo.figureY + y >= fieldHeight - 1 ||
                                    NetworkingManager.playerInfo.field[NetworkingManager.playerInfo.figureY + y + 1][NetworkingManager.playerInfo.figureX + x] > -1)) {
                        shouldStop = true;
                        break;
                    }
                }
            }
            if (shouldStop) {
                if (ticksBeforeLock == 0) {
                    for (int fy = 0; fy < figure.length; fy++) {
                        for (int fx = 0; fx < figure[0].length; fx++) {
                            if (figure[fy][fx] != 0)
                                NetworkingManager.playerInfo.field[NetworkingManager.playerInfo.figureY + fy][NetworkingManager.playerInfo.figureX + fx] = NetworkingManager.playerInfo.figureID;
                        }
                    }
                    NetworkingManager.playerInfo.figureX = fieldWidth / 2;
                    NetworkingManager.playerInfo.figureRotation = 0;
                    NetworkingManager.playerInfo.figureY = 0;
                    NetworkingManager.playerInfo.turn++;
                    NetworkingManager.playerInfo.holdPerformed = false;
                    NetworkingManager.playerInfo.figureID = new Random(NetworkingManager.clientSideRoom.seed + NetworkingManager.playerInfo.turn).nextInt(7);
                    autoRepeatDelay = 0;
                    autoShiftDelay = 0;
                    ticksBeforeLock = 1;

                    int linesCleared = 0;
                    for (int y = 0; y < fieldHeight; y++) {
                        boolean clear = true;
                        for (int x = 0; x < fieldWidth; x++) {
                            if (NetworkingManager.playerInfo.field[y][x] == -1) {
                                clear = false;
                                break;
                            }
                        }
                        if (clear) {
                            linesCleared++;
                            for (int newY = y; newY > 0; newY--) {
                                NetworkingManager.playerInfo.field[newY] = NetworkingManager.playerInfo.field[newY - 1].clone();
                            }
                        }
                    }
                    if (linesCleared > 0)
                        NetworkingManager.playerInfo.score += 100 + 200 * (linesCleared - 1);
                    if (linesCleared == 4) NetworkingManager.playerInfo.score += 1;
                } else ticksBeforeLock--;
            } else
                NetworkingManager.playerInfo.figureY += 1;

            return !shouldStop;
        }
        else return false;
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
        while (true){
            if (!tick()) break;
        }
        tick();
    }

    public static boolean rotateClockwise(){
        Networking.PlayerContainer player = NetworkingManager.playerInfo;
        int newRotation = player.figureRotation + 1;
        if (newRotation >= 4) newRotation = 0;
        int[][] figure = getFigure(), newFigure = figures[player.figureID][newRotation];

        if (player.figureX + newFigure[0].length >= fieldWidth)
            player.figureX = fieldWidth - newFigure[0].length;
        if (player.figureX < 0) player.figureX = 0;

        if (player.figureY + newFigure.length >= fieldHeight) player.figureY += (fieldHeight - player.figureY - newFigure.length);

        boolean fits = true;
        for (int x = 0; x < newFigure[0].length; x++) {
            for (int y = 0; y < newFigure.length; y++) {
                if (newFigure[y][x] == 1 && !(player.field[player.figureY + y][player.figureX + x] == -1)) {
                    fits = false;
                    break;
                }
            }
        }
        player.figureRotation = newRotation;
        if (fits) NetworkingManager.playerInfo = player;
        return fits;
    }

    public static void rotateAnticlockwise(){
        int oldRotation = NetworkingManager.playerInfo.figureRotation;
        for (int i = 0; i < 3; i++) {
            if (!rotateClockwise()) {
                NetworkingManager.playerInfo.figureRotation = oldRotation;
                break;
            }
        }
    }

    public static void rotate180(){
        int oldRotation = NetworkingManager.playerInfo.figureRotation;
        for (int i = 0; i < 2; i++) {
            if (!rotateClockwise()) {
                NetworkingManager.playerInfo.figureRotation = oldRotation;
                break;
            }
        }
    }

    public static void performHold(){
        if (!NetworkingManager.playerInfo.holdPerformed) {
            if (NetworkingManager.playerInfo.holdID == -1) {
                NetworkingManager.playerInfo.holdID = NetworkingManager.playerInfo.figureID;
                NetworkingManager.playerInfo.figureID = new Random(NetworkingManager.clientSideRoom.seed + NetworkingManager.playerInfo.turn).nextInt(7);
                NetworkingManager.playerInfo.figureX = fieldWidth / 2;
                NetworkingManager.playerInfo.turn++;
            }
            else {
                int id = NetworkingManager.playerInfo.figureID;
                NetworkingManager.playerInfo.figureID = NetworkingManager.playerInfo.holdID;
                NetworkingManager.playerInfo.holdID = id;
            }
            NetworkingManager.playerInfo.figureRotation = 0;
            NetworkingManager.playerInfo.figureY = 0;
            autoRepeatDelay = 0;
            autoShiftDelay = 0;
            NetworkingManager.playerInfo.holdPerformed = true;
        }
    }
}
