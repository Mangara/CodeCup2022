package codecup2022.runner;

import codecup2022.movegenerator.*;
import codecup2022.player.*;
import codecup2022.stopcriterion.EqualTurnTime;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.tools.ParallelGauntlet;
import codecup2022.tools.PlayerFactory;
import codecup2022.tools.XoRoShiRo128PlusRandom;

import java.util.Arrays;
import java.util.List;

public class RunGauntlet {
    public static void main(String[] args) {
        Player.DEBUG = false;

        PlayerFactory rando = () -> { return new RandomPlayer(new AllMoves()); };
        PlayerFactory sirando = () -> { return new RandomPlayer(new ScoreSign()); };
        PlayerFactory straight = () -> { return new StraightPlayer(); };
        PlayerFactory max = () -> { return new SimpleMax(new AllMoves()); };
        PlayerFactory maxi1 = () -> { return new MaximaxPlayer(new AllMoves(), 1); };
        PlayerFactory maxi2 = () -> { return new MaximaxPlayer(new AllMoves(), 2); };
        PlayerFactory maxi4 = () -> { return new MaximaxPlayer(new AllMoves(), 4); };
        PlayerFactory simon = () -> { return new SimulationPlayer(new AllMoves(), 20_000, new XoRoShiRo128PlusRandom()); };
        PlayerFactory epsSimon = () -> { return new EpsGreedyRolloutPlayer(new AllMoves(), new IterationCount(20_000), new XoRoShiRo128PlusRandom(), 0.5); };
        PlayerFactory epsScoreSimon = () -> { return new EpsGreedyGenRolloutPlayer(new AllMoves(), new ScoreSign(), new IterationCount(2_000), new XoRoShiRo128PlusRandom(), 0.5); };
        PlayerFactory epsConnectSimon = () -> { return new EpsGreedyGenRolloutPlayer(new AllMoves(), new ConnectFirst(), new IterationCount(20_000), new XoRoShiRo128PlusRandom(), 0.5); };
        PlayerFactory ucbSimon = () -> { return new UCBRolloutPlayer(new AllMoves(), new IterationCount(20_000), new XoRoShiRo128PlusRandom()); };
        PlayerFactory monty = () -> { return new MCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory monty10K = () -> { return new MCTSPlayer(new AllMoves(), new IterationCount(10_000), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory montyConnect = () -> { return new MCTSGenPlayer(new AllMoves(), new ConnectFirst(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory montyPositive = () -> { return new MCTSGenPlayer(new AllMoves(), new PositiveFirst(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory montyNeutral = () -> { return new MCTSGenPlayer(new AllMoves(), new ConnectFirstNoCycle(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory montyScore = () -> { return new MCTSGenPlayer(new AllMoves(), new ScoreSign(), new IterationCount(100_000), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory demon = () -> { return new DecayingMCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom()); };
        PlayerFactory demons = () -> { return new SolvingPlayer(new DecayingMCTSPlayer(new AllMoves(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom()), 6); };
        PlayerFactory demonPositive = () -> { return new SolvingPlayer(new DecayingMCTSGenPlayer(new AllMoves(), new PositiveFirst(), new IterationCount(100_000), 0.45, 0.20, new XoRoShiRo128PlusRandom()), 6); };
        PlayerFactory ucty = () -> { return new UCTPlayer(new AllMoves(), new IterationCount(100_000), new XoRoShiRo128PlusRandom()); };
        PlayerFactory lucky  = () -> { return new LimitedUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory vicky = () -> { return new ValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory liv_lim_1  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 750, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_lim_2  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1000, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_lim_3  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1250, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_lim_4  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_lim_5  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1750, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_lim_6  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 2000, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_lim_7  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 2250, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory liv_vw_1  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 16.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_vw_2  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 20.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_vw_3  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 24.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_vw_4  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_vw_5  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 32.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_vw_6  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 36.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_vw_7  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 40.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory liv_AAA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_AAB  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 6.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_AAC  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 6.0, 40.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ABA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 7.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ABB  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ABC  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 7.0, 40.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ACA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 8.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ACB  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 8.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ACC  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 8.0, 40.0, new XoRoShiRo128PlusRandom()); };

        PlayerFactory liv_BAA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BAB  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 6.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BAC  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 6.0, 40.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BBA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 7.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BBB  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 7.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BBC  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 7.0, 40.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BCA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 8.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BCB  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 8.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_BCC  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 8.0, 40.0, new XoRoShiRo128PlusRandom()); };

        
        List<PlayerFactory> opponents = Arrays.asList(
//                rando, max
//                simon, epsSimon, ucbSimon, epsConnectSimon, monty
//                rando, max, epsSimon, monty, demon, demonPositive
//                rando, max, epsSimon, monty, ucty, lucky, vicky
//                liv_lim_1, liv_lim_2, liv_lim_3, liv_lim_4, liv_lim_5, liv_lim_6, liv_lim_7
//                  liv_vw_1, liv_vw_2, liv_vw_3, liv_vw_4, liv_vw_5, liv_vw_6, liv_vw_7
                liv_AAA, liv_AAB, liv_AAC, liv_ABA, liv_ABB, liv_ABC, liv_ACA, liv_ACB, liv_ACC, 
                liv_BAA, liv_BAB, liv_BAC, liv_BBA, liv_BBB, liv_BBC, liv_BCA, liv_BCB, liv_BCC
        );

        ParallelGauntlet.runGauntlet(max, opponents, 50);
    }
}

/*
Player: MCTS-All-100000-0.20
Opponents:
0: Random-All
1: Max-All
2: εGreedyRollout-All-20000-0.500000

Number of wins:
    0    1    2
   20   16   18

Average score: (player on top, opponent on bottom)
     0      1      2 
  91.8   73.5   81.7 
  44.7   58.1   62.5 


Player: MCTS-All-100000-0.45-0.20
Opponents:
0: Random-All
1: Max-All
2: εGreedyRollout-All-20000-0.500000
3: MCTS-All-100000-0.20

Number of wins: (out of 20)
    0    1    2    3
   20   17   20   12

Average score: (player on top, opponent on bottom)
     0      1      2      3 
  96.2   75.7   87.5   77.6 
  46.1   55.6   58.2   72.3 

Player: MCTS-All-100000-0.45-0.20-Sol-6
Opponents:
0: Random-All
1: Max-All
2: εGreedyRollout-All-20000-0.500000
3: MCTS-All-100000-0.20
4: MCTS-All-100000-0.45-0.20

Number of wins: (out of 50)
    0    1    2    3    4
   50   42   46   26   26

Average score: (player on top, opponent on bottom)
     0      1      2      3      4 
  98.3   73.6   85.8   76.9   77.0 
  45.7   56.9   60.1   74.6   73.7 


Player: LimUCT-All-100000-1500-6.00
Opponents:
0: Random-All
1: Max-All
2: εGreedyRollout-All-20000-0.500000
3: MCTS-All-100000-0.20
4: MCTS-All-100000-0.45-0.20
5: MCTS-All-PositiveFirst-100000-0.45-0.20-Sol-6

Number of wins: (out of 20)
    0    1    2    3    4    5
   20   19   19   12   12   15

Average score: (player on top, opponent on bottom)
     0      1      2      3      4      5 
  97.7   74.1   86.5   80.2   77.6   82.3 
  47.9   53.6   59.2   75.3   76.9   65.7 


Player: LimValUCT-All-I100000-1500-6.00-28.00
Opponents:
0: Random-All
1: Max-All
2: εGreedyRollout-All-I20000-0.500000
3: MCTS-All-I100000-0.20
4: UCT-All-I100000
5: LimUCT-All-I100000-1500-6.00
6: ValUCT-All-I100000-28.00

Number of wins: (out of 20)
    0    1    2    3    4    5    6
   20   19   17   13   18   11   12

Average score: (player on top, opponent on bottom)
     0      1      2      3      4      5      6 
  93.0   82.1   83.1   77.1   82.0   76.8   76.2 
  46.6   53.1   55.3   69.8   61.4   74.1   71.0 

*/