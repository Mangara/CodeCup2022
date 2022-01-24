package codecup2022.tools;

import codecup2022.player.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Tournament {
    public static void runTournamentFromFactories(List<PlayerFactory> players, int nGames) {
        runTournament(players.stream().map(p -> p.player()).collect(Collectors.toList()), nGames);
    }
    
    public static void runTournament(List<Player> players, int nGames) {
        int n = players.size();
        int[][] wins = new int[n][n];
        double[][] avgScore = new double[n][n];
        double[][] stdDev = new double[n][n];
        Random juryRand = new Random();

        long totalGames = (n * (n - 1) * nGames) / 2;
        long currentGame = 0;
        System.out.println("Playing all the games:");

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int[] p1scores = new int[nGames];
                int[] p2scores = new int[nGames];

                // Run all games
                for (int k = 0; k < nGames; k++) {
                    if (k % 2 == 0) {
                        int[] scores = GameHost.runGame(players.get(i), players.get(j), juryRand, false);
                        p1scores[k] = scores[0];
                        p2scores[k] = scores[1];
                    } else {
                        int[] scores = GameHost.runGame(players.get(j), players.get(i), juryRand, false);
                        p1scores[k] = scores[1];
                        p2scores[k] = scores[0];
                    }

                    currentGame++;
                    if (currentGame % 10 == 0) {
                        System.out.printf("%10d/%d%n", currentGame, totalGames);
                    }
                }

                // Compute statistics
                wins[i][j] = countWins(p1scores, p2scores);
                wins[j][i] = countWins(p2scores, p1scores);
                avgScore[i][j] = computeAverage(p1scores);
                avgScore[j][i] = computeAverage(p2scores);
                stdDev[i][j] = stdDev[j][i] = computeHeadToHeadStdDev(p1scores, p2scores);
            }
        }

        report(players, nGames, wins, avgScore, stdDev);
    }

    private static void report(List<Player> players, int nGames, int[][] wins, double[][] avgScore, double[][] stdDev) {
        // List players
        System.out.println("Players:");
        for (int i = 0; i < players.size(); i++) {
            System.out.printf("%d: %s%n", i, players.get(i).getName());
        }
        System.out.println();

        // Print wins
        int WIN_CELL_WIDTH = 5;

        System.out.println("Number of wins: (out of " + nGames + ")");

        System.out.printf("%" + WIN_CELL_WIDTH + "s", "");
        for (int i = 0; i < players.size(); i++) {
            System.out.printf("%" + WIN_CELL_WIDTH + "d", i);
        }
        System.out.println();

        for (int i = 0; i < players.size(); i++) {
            System.out.printf("%" + WIN_CELL_WIDTH + "d", i);
            for (int j = 0; j < players.size(); j++) {
                System.out.printf("%" + WIN_CELL_WIDTH + "d", wins[i][j]);
            }
            System.out.println();
        }
        System.out.println();

        // Print averages
        int AVG_CELL_WIDTH = 6;
        DecimalFormat smallValueFormat = new DecimalFormat("0.000 ");

        System.out.println("Average score:");

        System.out.printf("%" + AVG_CELL_WIDTH + "s ", "");
        for (int i = 0; i < players.size(); i++) {
            System.out.printf("%" + AVG_CELL_WIDTH + "d ", i);
        }
        System.out.println();

        for (int i = 0; i < players.size(); i++) {
            System.out.printf("%" + AVG_CELL_WIDTH + "d ", i);
            for (int j = 0; j < players.size(); j++) {
                if (i == j) {
                    System.out.print("     - ");
                } else {
                    System.out.printf("%" + AVG_CELL_WIDTH + "." + (AVG_CELL_WIDTH - 3) + "g ", avgScore[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println();

        // Print comparisons
        final double[][] pValue = new double[players.size()][players.size()];
        List<Pair<Integer, Integer>> hypotheses = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                if (avgScore[i][j] > avgScore[j][i]) {
                    hypotheses.add(new Pair<>(i, j));
                    pValue[i][j] = computeP(avgScore[i][j] - avgScore[j][i], stdDev[i][j], nGames);
                } else if (avgScore[i][j] < avgScore[j][i]) {
                    hypotheses.add(new Pair<>(j, i));
                    pValue[j][i] = computeP(avgScore[j][i] - avgScore[i][j], stdDev[j][i], nGames);
                }
            }
        }

        hypotheses.sort(Comparator.comparingDouble(o -> pValue[o.getFirst()][o.getSecond()]));

        System.out.println("Comparisons:");
        for (Pair<Integer, Integer> hypothesis : hypotheses) {
            int p1 = hypothesis.getFirst();
            int p2 = hypothesis.getSecond();
            System.out.printf("    %s > %s with p = %f%n", players.get(p1).getName(), players.get(p2).getName(), pValue[p1][p2]);
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
