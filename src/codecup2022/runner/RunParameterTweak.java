package codecup2022.runner;

import codecup2022.movegenerator.*;
import codecup2022.player.*;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.tools.ParallelGauntlet;
import codecup2022.tools.PlayerFactory;
import codecup2022.tools.XoRoShiRo128PlusRandom;

import java.util.Arrays;
import java.util.List;

public class RunParameterTweak {
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
        
        PlayerFactory liv_OAA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 0, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_NAA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 750, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_PAA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 3000, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory liv_AOA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 2.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ANA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 4.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_APA  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 12.0, 28.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory liv_AAO  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 0.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_AAN  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 14.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_AAP  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 56.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory liv_ew_1  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 3.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ew_2  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 4.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ew_3  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 5.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ew_4  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 6.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ew_5  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 7.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ew_6  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 8.0, 28.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_ew_7  = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1500, 9.0, 28.0, new XoRoShiRo128PlusRandom()); };
        
        List<PlayerFactory> opponents = Arrays.asList(
//                rando, max
//                simon, epsSimon, ucbSimon, epsConnectSimon, monty
//                rando, max, epsSimon, monty, demon, demonPositive
//                rando, max, epsSimon, monty, ucty, lucky, vicky
//                liv, liv_OAA, liv_NAA, liv_PAA, liv_AOA, liv_ANA, liv_APA, liv_AAO, liv_AAN, liv_AAP
                liv_ew_1, liv_ew_2, liv_ew_3, liv_ew_4, liv_ew_5, liv_ew_6, liv_ew_7
        );

        ParallelGauntlet.runGauntlet(max, opponents, 20);
    }
}