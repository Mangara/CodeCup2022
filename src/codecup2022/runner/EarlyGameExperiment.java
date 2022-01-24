package codecup2022.runner;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.movegenerator.AllMoves;
import codecup2022.movegenerator.MoveGenerator;
import codecup2022.movegenerator.PositiveFirst;
import codecup2022.tools.XoRoShiRo128PlusRandom;
import java.util.Random;

public class EarlyGameExperiment {
    public static void main(String[] args) {
//        EarlyGameExperiment exp = new EarlyGameExperiment(new PositiveFirst(), true);
//        exp.run(new ArrayBoard(), 3_000_000);

        EarlyGameExperiment exp = new EarlyGameExperiment(new PositiveFirst(), true);
        Board board = new ArrayBoard();
        board.applyMove(Move.fromString("gcl"));
        board.applyMove(Move.fromString("ccl"));
        exp.run(board, 3_000_000);
    }
    
    private final Random rand = new XoRoShiRo128PlusRandom();
    private final MoveGenerator rolloutGenerator;
    private final boolean blue;

    public EarlyGameExperiment(MoveGenerator rolloutGenerator, boolean blue) {
        this.rolloutGenerator = rolloutGenerator;
        this.blue = blue;
    }
    
    public void run(final Board board, final int nSimulationsPerMove) {
        final int[] moves = board.possibleMoves();
        final double[] scores = new double[moves.length];
        
        for (int i = 0; i < moves.length; i++) {
            long totalScore = 0;
            for (int j = 0; j < nSimulationsPerMove; j++) {
                totalScore += evaluateMove(board, moves[i]);
            }
            
            scores[i] = totalScore / (double) nSimulationsPerMove;
            System.out.printf("%d/%d: %s = %f%n", i + 1, moves.length, Move.toString(moves[i]), scores[i]);
        }
    }
    
    private int evaluateMove(final Board board, final int move) {
        final Board b = new ArrayBoard(board);
        
        b.applyMove(move);
        
        while (!b.isGameOver()) {
            final int[] possibleMoves = rolloutGenerator.generateMoves(b);
            b.applyMove(possibleMoves[rand.nextInt(possibleMoves.length)]);
        }
        
        return b.getScore(blue);
    }
}
