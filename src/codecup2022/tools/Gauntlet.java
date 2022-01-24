package codecup2022.tools;

import codecup2022.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Gauntlet {
    public static void runGauntlet(Player player, List<Player> opponents, Random juryRand, int nGames) {
        int n = opponents.size();
        int[] wins = new int[n];
        double[] avgScore = new double[n];
        double[] avgOppScore = new double[n];
        double[] stdDev = new double[n];

        long totalGames = n * nGames;
        long currentGame = 0;
        System.out.println("Playing all the games:");

        for (int i = 0; i < n; i++) {
            Player opponent = opponents.get(i);
            int[] playerScores = new int[nGames];
            int[] oppScores = new int[nGames];

            // Run all games
            for (int k = 0; k < nGames; k++) {
                if (k % 2 == 0) {
                    int[] scores = GameHost.runGame(player, opponent, juryRand, false);
                    playerScores[k] = scores[0];
                    oppScores[k] = scores[1];
                } else {
                    int[] scores = GameHost.runGame(opponent, player, juryRand, false);
                    playerScores[k] = scores[1];
                    oppScores[k] = scores[0];
                }

                currentGame++;
                if (currentGame % 10 == 0) {
                    System.out.printf("%10d/%d%n", currentGame, totalGames);
                }
            }

            // Compute statistics
            wins[i] = countWins(playerScores, oppScores);
            avgScore[i] = computeAverage(playerScores);
            avgOppScore[i] = computeAverage(oppScores);
            stdDev[i] = computeHeadToHeadStdDev(playerScores, oppScores);
        }

        report(player, opponents, nGames, wins, avgScore, avgOppScore, stdDev);
    }

    private static void report(
            Player player, List<Player> opponents, int nGames, 
            int[] wins, double[] avgScore, double[] avgOppScore, double[] stdDev
    ) {
        // List players
        System.out.println("Player: " + player.getName());
        System.out.println("Opponents:");
        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%d: %s%n", i, opponents.get(i).getName());
        }
        System.out.println();

        // Print wins
        int WIN_CELL_WIDTH = 5;

        System.out.println("Number of wins: (out of " + nGames + ")");

        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%" + WIN_CELL_WIDTH + "d", i);
        }
        System.out.println();

        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%" + WIN_CELL_WIDTH + "d", wins[i]);
        }
        System.out.println();
        System.out.println();

        // Print averages
        int AVG_CELL_WIDTH = 6;

        System.out.println("Average score: (player on top, opponent on bottom)");

        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%" + AVG_CELL_WIDTH + "d ", i);
        }
        System.out.println();

        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%" + AVG_CELL_WIDTH + "." + (AVG_CELL_WIDTH - 3) + "g ", avgScore[i]);
        }
        System.out.println();
        
        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%" + AVG_CELL_WIDTH + "." + (AVG_CELL_WIDTH - 3) + "g ", avgOppScore[i]);
        }
        System.out.println();
        System.out.println();

        // Print comparisons
        final double[] pValue = new double[opponents.size()];
        List<Integer> hypotheses = new ArrayList<>();

        for (int i = 0; i < opponents.size(); i++) {
            hypotheses.add(i);
            double headToHeadScore = avgScore[i] - avgOppScore[i];
            if (headToHeadScore > 0) {
                pValue[i] = computeP(headToHeadScore, stdDev[i], nGames);
            } else if (headToHeadScore < 0) {
                pValue[i] = computeP(-headToHeadScore, stdDev[i], nGames);
            }
        }

        hypotheses.sort(Comparator.comparingDouble(o -> pValue[Math.abs(o)]));

        System.out.println("Comparisons:");
        for (int opponent : hypotheses) {
            if (avgScore[opponent] - avgOppScore[opponent] > 0) {
                System.out.printf("    %s > %s with p = %f%n", player.getName(), opponents.get(opponent).getName(), pValue[opponent]);
            } else {
                System.out.printf("    %s > %s with p = %f%n", opponents.get(opponent).getName(), player.getName(), pValue[opponent]);
            }
        }
    }

    private static double computeP(double avg, double stdDev, int nGames) {
        double t = avg * Math.sqrt(nGames) / stdDev; // test value, GAMES - 1 DoF
        double tt2 = -t * t / 2;
        double erftt = (2 / Math.sqrt(Math.PI)) * Math.sqrt(-Math.expm1(tt2)) * (Math.sqrt(Math.PI) / 2 + 31 * Math.exp(tt2) / 200 - 341 * Math.exp(2 * tt2) / 8000);

        return 1 - 0.5 * (erftt + 1);
    }

    private static int countWins(int[] playerScores, int[] oppScores) {
        int wins = 0;
        
        for (int k = 0; k < playerScores.length; k++) {
            if (playerScores[k] > oppScores[k]) {
                wins++;
            }
        }
        
        return wins;
    }
    
    private static double computeHeadToHeadStdDev(int[] playerScores, int[] oppScores) {
        int[] headToHeadScores = new int[playerScores.length];
        
        for (int k = 0; k < playerScores.length; k++) {
            headToHeadScores[k] = playerScores[k] - oppScores[k];
        }
        
        double headToHeadAverage = computeAverage(headToHeadScores);
        return computeStdDev(headToHeadScores, headToHeadAverage);
    }

    private static double computeAverage(int[] scores) {
        long total = 0;

        for (int k = 0; k < scores.length; k++) {
            total += scores[k];
        }

        return total / (double) scores.length;
    }

    private static double computeStdDev(int[] scores, double average) {
        double squaredError = 0;

        for (int k = 0; k < scores.length; k++) {
            double error = scores[k] - average;
            squaredError += error * error;
        }

        return Math.sqrt(squaredError / (scores.length - 1));
    }
}
