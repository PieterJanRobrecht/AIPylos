package be.kuleuven.pylos.battle;

import be.kuleuven.pylos.game.*;
import be.kuleuven.pylos.player.PylosPlayer;
import be.kuleuven.pylos.player.PylosPlayerObserver;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.Random;

/**
 * Created by Jan on 19/02/2015.
 */
public class Battle {

    private static final Random random = new Random(0);
    private static TextArea taLog;

    public static double[] play(PylosPlayer playerLight, PylosPlayer playerDark, int runs, TextArea taLog) {
        Battle.taLog = taLog;

        if (runs % 2 != 0) {
            throw new IllegalArgumentException("Please specify an even number of runs");
        }

        double totalPlayTime = 0;
        int lightStartLightWin = 0;
        int lightStartDarkWin = 0;
        int lightStartDraw = 0;
        String playerLightClass = playerLight.getClass().getSimpleName();
        String playerDarkClass = playerDark.getClass().getSimpleName();

        for (int i = 0; i < runs / 2; i++) {
            PylosBoard board = new PylosBoard();
            PylosGame game = new PylosGame(board, playerLight, playerDark, random);
            double startTime = System.currentTimeMillis();
            game.play();
            double playTime = System.currentTimeMillis() - startTime;
            totalPlayTime += playTime;
            String message = (i + 1) + "/" + runs + "\tLight: " + playerLightClass + "\tDark: " + playerDarkClass + "\t";
            if (game.getState() == PylosGameState.DRAW) {
                lightStartDraw++;
                message += "Draw";
            } else {
                if (game.getWinner() == playerLight) {
                    lightStartLightWin++;
                } else {
                    lightStartDarkWin++;
                }
                message += "Winner: " + (game.getWinner() == playerLight ? "Light" : "Dark");
            }
//			System.out.println(message);
        }

        int darkStartLightWin = 0;
        int darkStartDarkWin = 0;
        int darkStartDraw = 0;

        for (int i = 0; i < runs / 2; i++) {
            PylosBoard board = new PylosBoard();
            PylosGame game = new PylosGame(board, playerDark, playerLight, random);
            double startTime = System.currentTimeMillis();
            game.play();
            double playTime = System.currentTimeMillis() - startTime;
            totalPlayTime += playTime;
            String message = (i + 1 + runs / 2) + "/" + runs + "\tLight: " + playerDarkClass + "\tDark: " + playerLightClass + "\t";
            if (game.getState() == PylosGameState.DRAW) {
                darkStartDraw++;
                message += "Draw";
            } else {
                if (game.getWinner() == playerLight) {
                    darkStartLightWin++;
                } else {
                    darkStartDarkWin++;
                }
                message += "Winner: " + (game.getWinner() == playerLight ? "Dark" : "Light");
            }
//			System.out.println(message);
        }

        totalPlayTime /= 1000;
        int totalLightWin = lightStartLightWin + darkStartLightWin;
        int totalDarkWin = lightStartDarkWin + darkStartDarkWin;
        int totalDraw = lightStartDraw + darkStartDraw;

        writeText("");
        writeText("----------------------------");
        writeText(runs / 2 + " games where " + playerLightClass + " starts:");
        writeText(String.format(" * %6s", String.format("%.2f", (double) lightStartLightWin / (runs / 2) * 100)) + "% " + playerLightClass);
        writeText(String.format(" * %6s", String.format("%.2f", (double) lightStartDarkWin / (runs / 2) * 100)) + "% " + playerDarkClass);
        writeText(String.format(" * %6s", String.format("%.2f", (double) lightStartDraw / (runs / 2) * 100)) + "% Draw");
        writeText("");
        writeText(runs / 2 + " games where " + playerDarkClass + " starts:");
        writeText(String.format(" * %6s", String.format("%.2f", (double) darkStartLightWin / (runs / 2) * 100)) + "% " + playerLightClass);
        writeText(String.format(" * %6s", String.format("%.2f", (double) darkStartDarkWin / (runs / 2) * 100)) + "% " + playerDarkClass);
        writeText(String.format(" * %6s", String.format("%.2f", (double) darkStartDraw / (runs / 2) * 100)) + "% Draw");
        writeText("");
        writeText(runs + " games in total:");
        writeText(String.format(" * %6s", String.format("%.2f", (double) totalLightWin / runs * 100)) + "% " + playerLightClass);
        writeText(String.format(" * %6s", String.format("%.2f", (double) totalDarkWin / runs * 100)) + "% " + playerDarkClass);
        writeText(String.format(" * %6s", String.format("%.2f", (double) totalDraw / runs * 100)) + "% Draw");
        writeText("");
        writeText("Time: " + String.format("%.2f", totalPlayTime) + " sec (" + String.format("%.2f", totalPlayTime / runs) + " sec / game)");
        writeText("----------------------------");

        double[] wins = new double[]{(double) (totalLightWin) / runs, (double) (totalDarkWin) / runs, (double) (totalDraw) / runs};
        return wins;
    }

    private static void writeText(String text) {
        Platform.runLater(() -> taLog.appendText(text + "\n"));
    }

    public static void learn(PylosPlayer playerLight, PylosPlayer playerDark, int runs, TextArea taLog) {
        Battle.taLog = taLog;

        writeText("Starting prelearning");
        for (int i = 0; i < runs; i++) {
            PylosBoard board = new PylosBoard();
            PylosGame game = new PylosGame(board, playerLight, playerDark, random, PylosGameObserver.NONE, PylosPlayerObserver.CONSOLE_PLAYER_OBSERVER);
            game.play();
        }

        writeText("Done prelearning");
    }
}
