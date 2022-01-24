package codecup2022.tools;

import codecup2022.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ParallelGauntlet {

    public static void runGauntlet(PlayerFactory player, List<PlayerFactory> opponents, int nGames) {
        System.out.println("Setting up...");
        List<Match> matches = setupMatches(player, opponents, nGames);

        System.out.println("Playing...");
        final int totalGames = opponents.size() * nGames;
        final AtomicInteger matchesPlayed = new AtomicInteger();
        List<MatchResult> scores = matches.parallelStream()
                .map((match) -> playMatch(match))
                .peek(result -> {
                    int currentGame = matchesPlayed.incrementAndGet();
                    if (currentGame % 10 == 0) {
                        System.out.printf("%10d/%d%n", currentGame, totalGames);
                    }
                })
                .collect(Collectors.toList());

        System.out.println("Analyzing...");
        analyzeScores(scores, player, opponents, nGames);
    }
    
    private static class Match {

        final Player blue, red;
        final boolean playerIsBlue;
        final int opponentId;

        Match(Player blue, Player red, boolean playerIsBlue, int opponentId) {
            this.blue = blue;
            this.red = red;
            this.playerIsBlue = playerIsBlue;
            this.opponentId = opponentId;
        }
    }
    
    private static class MatchResult {

        final Match match;
        final int myScore, oppScore;

        public MatchResult(Match match, int myScore, int oppScore) {
            this.match = match;
            this.myScore = myScore;
            this.oppScore = oppScore;
        }
    }

    private static void analyzeScores(List<MatchResult> results, PlayerFactory player, List<PlayerFactory> opponents, final int nGames) {
        int nOpponents = opponents.size();
        
        // Gather scores
        int[][] playerScores = new int[nOpponents][nGames];
        int[][] oppScores = new int[nOpponents][nGames];
        
        int[] matchIndex = new int[nOpponents];
        
        for (MatchResult result : results) {
            int opp = result.match.opponentId;
            int matchId = matchIndex[opp];
            
            playerScores[opp][matchId] = result.myScore;
            oppScores[opp][matchId] = result.oppScore;
                    
            matchIndex[opp]++;
        }
        
        // Process scores
        int[] wins = new int[nOpponents];
        double[] avgScore = new double[nOpponents];
        double[] avgOppScore = new double[nOpponents];
        double[] stdDev = new double[nOpponents];
        
        for (int i = 0; i < nOpponents; i++) {
            wins[i] = countWins(playerScores[i], oppScores[i]);
            avgScore[i] = computeAverage(playerScores[i]);
            avgOppScore[i] = computeAverage(oppScores[i]);
            stdDev[i] = computeHeadToHeadStdDev(playerScores[i], oppScores[i]);
        }

        report(player, opponents, nGames, wins, avgScore, avgOppScore, stdDev);
    }

    private static List<Match> setupMatches(PlayerFactory player, List<PlayerFactory> opponents, int nGames) {
        List<Match> matches = new ArrayList<>();

        for (int opp = 0; opp < opponents.size(); opp++) {
            PlayerFactory oppFactory = opponents.get(opp);
            for (int i = 0; i < nGames; i++) {
                if (i % 2 == 0) {
                    matches.add(new Match(player.player(), oppFactory.player(), true, opp));
                } else {
                    matches.add(new Match(oppFactory.player(), player.player(), false, opp));
                }
            }
        }

        return matches;
    }

    private static MatchResult playMatch(Match match) {
        int[] score = GameHost.runGame(match.blue, match.red, ThreadLocalRandom.current(), false);

        if (match.playerIsBlue) {
            return new MatchResult(match, score[0], score[1]);
        } else {
            return new MatchResult(match, score[1], score[0]);
        }
    }

    private static void report(
            PlayerFactory player, List<PlayerFactory> opponents, int nGames,
            int[] wins, double[] avgScore, double[] avgOppScore, double[] stdDev
    ) {
        // List players
        System.out.println("Player: " + player.player().getName());
        System.out.println("Opponents:");
        for (int i = 0; i < opponents.size(); i++) {
            System.out.printf("%d: %s%n", i, opponents.get(i).player().getName());
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
                System.out.printf("    %s > %s with p = %f%n", player.player().getName(), opponents.get(opponent).player().getName(), pValue[opponent]);
            } else {
                System.out.printf("    %s > %s with p = %f%n", opponents.get(opponent).player().getName(), player.player().getName(), pValue[opponent]);
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
