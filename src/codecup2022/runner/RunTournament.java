package codecup2022.runner;

import codecup2022.movegenerator.*;
import codecup2022.player.*;
import codecup2022.stopcriterion.EqualTurnTime;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.tools.ParallelTournament;
import codecup2022.tools.PlayerFactory;
import codecup2022.tools.Tournament;
import codecup2022.tools.XoRoShiRo128PlusRandom;

import java.util.Arrays;

public class RunTournament {
    public static void main(String[] args) {
        Player.DEBUG = false;

        PlayerFactory rando = () -> { return new RandomPlayer(new AllMoves()); };
        PlayerFactory sirando = () -> { return new RandomPlayer(new ScoreSign()); };
        PlayerFactory straight = () -> { return new StraightPlayer(); };
        PlayerFactory max = () -> { return new SimpleMax(new AllMoves()); };
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
        PlayerFactory vicky = () -> { return new ValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new IterationCount(100_000), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom()); };
        
        PlayerFactory epsSimon10s = () -> { return new EpsGreedyRolloutPlayer(new AllMoves(), new EqualTurnTime(10), new XoRoShiRo128PlusRandom(), 0.4); };
        PlayerFactory monty10s = () -> { return new MCTSPlayer(new AllMoves(), new EqualTurnTime(10), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory montyPositive10s = () -> { return new MCTSGenPlayer(new AllMoves(), new PositiveFirst(), new EqualTurnTime(10), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory montyScore10s = () -> { return new MCTSGenPlayer(new AllMoves(), new ScoreSign(), new EqualTurnTime(10), 0.2, new XoRoShiRo128PlusRandom()); };
        PlayerFactory demon10s = () -> { return new DecayingMCTSPlayer(new AllMoves(), new EqualTurnTime(10), 0.45, 0.20, new XoRoShiRo128PlusRandom()); };
        PlayerFactory demons10s = () -> { return new SolvingPlayer(new DecayingMCTSPlayer(new AllMoves(), new EqualTurnTime(10), 0.45, 0.20, new XoRoShiRo128PlusRandom()), 6); };
        PlayerFactory demonPositive10s = () -> { return new SolvingPlayer(new DecayingMCTSGenPlayer(new AllMoves(), new PositiveFirst(), new EqualTurnTime(10), 0.45, 0.20, new XoRoShiRo128PlusRandom()), 6); };
        PlayerFactory ucty10s = () -> { return new UCTPlayer(new AllMoves(), new EqualTurnTime(10), new XoRoShiRo128PlusRandom()); };
        PlayerFactory lucky10s  = () -> { return new LimitedUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1500, 6.0, new XoRoShiRo128PlusRandom()); };
        // Liv
        PlayerFactory liv10s = () -> { return new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom()); };
        PlayerFactory liv_s5 = () -> { return new SolvingPlayer(new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(10), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom()), 5); };
        PlayerFactory liv_s6 = () -> { return new SolvingPlayer(new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(9.75), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom()), 6); };
        PlayerFactory liv_s7 = () -> { return new SolvingPlayer(new LimitedValueUCTPlayer(new AllMoves(), new EqualTurnTime(7), 1000, 7.0, 34.0, new XoRoShiRo128PlusRandom()), 7); };
        
//        ParallelTournament.runTournament(Arrays.<PlayerFactory>asList(
        Tournament.runTournamentFromFactories(Arrays.<PlayerFactory>asList(
//               rando, max, ucty, vicky
                lucky10s, liv, liv10s, liv_s5, liv_s6, liv_s7
        ), 50);
    }
}