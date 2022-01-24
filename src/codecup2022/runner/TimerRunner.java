package codecup2022.runner;

import codecup2022.movegenerator.*;
import codecup2022.player.*;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.tools.PlayerTimer;
import codecup2022.tools.XoRoShiRo128PlusRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TimerRunner {
    public static void main(String[] args) {
        Player.DEBUG = false;
        
        Player rando = new RandomPlayer(new AllMoves());
        Player sirando = new RandomPlayer(new ScoreSign());
        Player straight = new StraightPlayer();
        Player max = new SimpleMax(new AllMoves());
        Player simonK = new SimulationPlayer(new AllMoves(), 100_000, new Random());
        Player epsSimon = new EpsGreedyRolloutPlayer(new AllMoves(), new IterationCount(100_000), new Random(), 0.5);
        Player epsScoreSimon = new EpsGreedyGenRolloutPlayer(new AllMoves(), new ScoreSign(), new IterationCount(20_000), new Random(), 0.5);
        Player epsConnectSimon = new EpsGreedyGenRolloutPlayer(new AllMoves(), new ConnectFirst(), new IterationCount(20_000), new Random(), 0.5);
        Player ucbSimon = new UCBRolloutPlayer(new AllMoves(), new IterationCount(100_000), new Random());
        Player monty = new MCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom());
        Player monty10K = new MCTSPlayer(new AllMoves(), new IterationCount(10_000), 0.2, new XoRoShiRo128PlusRandom());
        Player montyConnect = new MCTSGenPlayer(new AllMoves(), new ConnectFirst(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom());
        Player montyPositive = new MCTSGenPlayer(new AllMoves(), new PositiveFirst(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom());
        Player montyScore = new MCTSGenPlayer(new AllMoves(), new ScoreSign(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom());
        Player ucty = new UCTPlayer(new AllMoves(), new IterationCount(100_000), new Random());
        Player lemon = new LinkedListMCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom());
        Player demon = new DecayingMCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom());
        Player lucky = new LimitedUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, new XoRoShiRo128PlusRandom());
        Player liv = new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom());
        
        PlayerTimer.evaluate(lucky);
    }
}

/******* Laptop timings *************
Player: LimUCT-All-I100000-1500-6.00
Average time (s): 7.180
Quartiles: 6.779 - 7.042 - 7.130 - 7.592 - 7.650

* (BitBoard)
Player: LimUCT-All-I100000-1500-6.00
Average time (s): 6.916
Quartiles: 6.277 - 6.744 - 6.985 - 7.167 - 7.669
Player: LimUCT-All-I100000-1500-6.00
Average time (s): 6.845
Quartiles: 6.613 - 6.729 - 6.840 - 7.008 - 7.118
* 
Player: LimValUCT-All-I100000-1000-7.00-34.00
Average time (s): 9.858
Quartiles: 8.920 - 9.133 - 9.737 - 10.606 - 11.891
*/

/******* Desktop timings *************
(No other activity)
Player: MCTS-All-100000-0.20
Average time (s): 10.148
Quartiles: 9.342 - 9.938 - 10.170 - 10.451 - 10.807

Player: MCTS-All-100000-0.45-0.20
Average time (s): 10.742
Quartiles: 9.707 - 10.069 - 11.005 - 11.278 - 12.144

Player: MCTS-All-100000-0.45-0.20
Average time (s): 10.567
Quartiles: 9.972 - 10.173 - 10.586 - 10.982 - 11.279

Player: LimUCT-All-100000-1500-6.00
Average time (s): 11.548
Quartiles: 10.337 - 11.185 - 11.643 - 12.166 - 12.745

*********** After Move refactor

Player: LimUCT-All-100000-1500-6.00
Average time (s): 10.190
Quartiles: 9.049 - 9.920 - 10.376 - 10.682 - 10.890
*/