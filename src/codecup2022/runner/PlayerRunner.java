package codecup2022.runner;

import codecup2022.movegenerator.AllMoves;
import codecup2022.player.LimitedValueUCTPlayer;
import codecup2022.player.Player;
import codecup2022.stopcriterion.EqualTurnTime;
import codecup2022.tools.XoRoShiRo128PlusRandom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PlayerRunner {
    public static void main(String[] args) throws IOException {
        Player.TIMING = true;
        Player.DEBUG = false;
        Player.SCORE = true;

        Player p = getPlayer();
        p.play(new BufferedReader(new InputStreamReader(System.in)), System.out);
    }

    public static Player getPlayer() {
//        return new RandomPlayer(new AllMoves()); // First submission, works
//        return new SimulationPlayer(new AllMoves(), new IterationCount(100_000), new Random()); // Placed 8th in test competition on Fri Oct 01 2021
//        return new LimitedUCTPlayer(new AllMoves(), new IterationCount(150_000), 1500, 6.0, new XoRoShiRo128PlusRandom()); // 3rd, Sat Oct 23 2021 ; 7th Sat Nov 13 2021 ; 5th Sat Dec 04 2021 ; 7th Sat Dec 25 2021 ; 8th Sat Jan 08 2022
        return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(29.5), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom());
    }
}
