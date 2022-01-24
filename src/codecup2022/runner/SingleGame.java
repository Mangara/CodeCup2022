package codecup2022.runner;

import codecup2022.movegenerator.*;
import codecup2022.player.*;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.tools.GameHost;
import codecup2022.tools.XoRoShiRo128PlusRandom;

import java.io.IOException;
import java.util.Random;

public class SingleGame {
    public static void main(String[] args) throws IOException {
        Player.DEBUG = false;
        Player.TIMING = true;

        Random rand = new Random();
        long jurySeed = rand.nextLong();
        long seed1 = rand.nextLong();
        long seed2 = rand.nextLong();
        System.err.printf("long jurySeed = %dL; long seed1 = %dL; long seed2 = %dL%n", jurySeed, seed1, seed2);

        Player console = new ConsolePlayer();
        Player rando = new RandomPlayer(new AllMoves(), new XoRoShiRo128PlusRandom(seed1));
        Player straight = new StraightPlayer(new XoRoShiRo128PlusRandom(seed2));
        Player simon = new SimulationPlayer(new AllMoves(), 1, new XoRoShiRo128PlusRandom(seed2));
        Player simonK = new SimulationPlayer(new AllMoves(), 1000, new XoRoShiRo128PlusRandom(seed2));
        Player epsSimon = new EpsGreedyRolloutPlayer(new AllMoves(), new IterationCount(200), new XoRoShiRo128PlusRandom(seed2), 0.5);
        Player ucbSimon = new UCBRolloutPlayer(new AllMoves(), new IterationCount(200), new XoRoShiRo128PlusRandom(seed2));
        Player epsScoreSimon = new EpsGreedyGenRolloutPlayer(new AllMoves(), new ScoreSign(), new IterationCount(20), new Random(), 0.5);
        Player epsConnectSimon = new EpsGreedyGenRolloutPlayer(new AllMoves(), new ConnectFirst(), new IterationCount(200), new Random(), 0.5);
        
        Player montyK = new MCTSPlayer(new AllMoves(), new IterationCount(1000), 0.2, new XoRoShiRo128PlusRandom(seed2));
        Player monty10K = new MCTSPlayer(new AllMoves(), new IterationCount(10_000), 0.2, new XoRoShiRo128PlusRandom(seed2));
        Player monty100K = new MCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom(seed2));
        Player ucty100K = new UCTPlayer(new AllMoves(), new IterationCount(100_000), new XoRoShiRo128PlusRandom(seed2));
        Player montyConnect = new MCTSGenPlayer(new AllMoves(), new ConnectFirst(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom(seed2));
        Player montyPositive = new MCTSGenPlayer(new AllMoves(), new PositiveFirst(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom(seed2));
        Player montyScore = new MCTSGenPlayer(new AllMoves(), new ScoreSign(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom(seed2));
        Player demon = new DecayingMCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom(seed2));
        Player demonPositive = new SolvingPlayer(new DecayingMCTSGenPlayer(new AllMoves(), new PositiveFirst(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom(seed2)), 6);
        Player lucky = new LimitedUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, new XoRoShiRo128PlusRandom(seed2));
        Player lucky5K = new LimitedUCTPlayer(new AllMoves(), new IterationCount(5_000), 1500, 6.0, new XoRoShiRo128PlusRandom(seed2));
        Player liv = new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 28.0, new XoRoShiRo128PlusRandom());
        
//        new BufferedReader(new InputStreamReader(System.in)).readLine(); // This helps with profiling
        GameHost.runGame(lucky, liv, new XoRoShiRo128PlusRandom(jurySeed), true);
    }
}
