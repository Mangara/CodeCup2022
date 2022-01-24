package codecup2022.tools;

import codecup2022.movegenerator.AllMoves;
import codecup2022.player.Player;
import codecup2022.player.RandomPlayer;

import java.util.Arrays;

public class PlayerTimer {
    private static final boolean CONDENSED = false;
    private static final int GAMES = 12;

    public static void evaluate(Player... players) {
        if (CONDENSED) {
            System.out.println("Player,Average time (s),Min time (s),1st Quartile time (s),Median time (s),3rd Quartile time (s),Max time (s)");
        }

        for (Player p : players) {
            evaluateTiming(p);
        }
    }

    private static void evaluateTiming(Player player) {
        Player random = new RandomPlayer(new AllMoves());
        long[] times = new long[GAMES];

        for (int i = 0; i < GAMES; i++) {
            long start = System.nanoTime();
            if (i % 2 == 0) {
                GameHost.runGame(random, player, false);
            } else {
                GameHost.runGame(player, random, false);
            }
            times[i] = System.nanoTime() - start;

            if (i % 5 == 0) {
                System.out.printf("%10d/%d%n", i + 1, GAMES);
            }
        }

        Arrays.sort(times);

        long totalTime = 0;

        for (int i = 0; i < GAMES; i++) {
            totalTime += times[i];
        }

        System.out.printf(
                (CONDENSED
                        ? "%s,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f%n"
                        : "Player: %s%n"
                        + "Average time (s): %.3f%n"
                        + "Quartiles: %.3f - %.3f - %.3f - %.3f - %.3f%n%n"),
                player.getName(),
                nsToS(totalTime / GAMES),
                nsToS(times[0]),
                nsToS(times[GAMES / 4]),
                nsToS(times[GAMES / 2]),
                nsToS(times[(3 * GAMES) / 4]),
                nsToS(times[GAMES - 1])
        );
    }

    private static double nsToS(long nanoseconds) {
        return nanoseconds / 1000_000_000.0;
    }
}
