package codecup2022.player;

import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.data.RolloutBoard;
import codecup2022.movegenerator.MoveGenerator;
import codecup2022.stopcriterion.StopCriterion;
import java.util.Arrays;
import java.util.Random;

public class EpsGreedyRolloutPlayer extends Player {

    private final MoveGenerator generator;
    private final StopCriterion stop;
    private final Random rand;
    private final double epsilon;
    
    public EpsGreedyRolloutPlayer(MoveGenerator generator, StopCriterion stop, Random rand, double epsilon) {
        super(String.format("εGreedyRollout-%s-%s-%f", generator.name(), stop.name(), epsilon));
        this.generator = generator;
        this.stop = stop;
        this.rand = rand;
        this.epsilon = epsilon;
    }

    @Override
    protected int selectMove() {
        stop.started();
        final int[] moves = generator.generateMoves(board);
        final double[] averageScore = new double[moves.length];
        final int[] visits = new int[moves.length];
        int bestMoveIndex = 0;
        double bestScore = -1000;
        
        // Initialize by playing each move once
        for (int i = 0; i < moves.length && !stop.shouldStop(); i++) {
            final int score = evaluateMove(moves[i]);
            averageScore[i] = score;
            
            if (score > bestScore) {
                bestScore = score;
                bestMoveIndex = i;
            }
        }
        Arrays.fill(visits, 1);
        
        // Keep playing moves to get a better approximation of their true score
        while (!stop.shouldStop()) {
            final int index = selectMoveIndex(moves.length, bestMoveIndex);
            final int score = evaluateMove(moves[index]);
            
            // Update score for the played move
            averageScore[index] = visits[index] * averageScore[index] + score;
            visits[index]++;
            averageScore[index] /= visits[index];
            
            // Update best move
            if (index == bestMoveIndex) {
                if (averageScore[index] > bestScore) {
                    // The best move got better
                    bestScore = averageScore[index];
                } else {
                    // Search others to see if another move is now better
                    bestScore = averageScore[index];
                    
                    for (int j = 0; j < moves.length; j++) {
                        if (averageScore[j] > bestScore) {
                            bestScore = averageScore[j];
                            bestMoveIndex = j;
                        }
                    }
                }
            } else if (averageScore[index] > bestScore) {
                bestScore = averageScore[index];
                bestMoveIndex = index;
            }
        }
        
        if (DEBUG) {
            for (int i = 0; i < moves.length; i++) {
                System.err.printf("%s (%.2f, %d)%n", Move.toString(moves[i]), averageScore[i], visits[i]);
            }
            System.err.printf("Best: %s (%.2f, %d)%n", Move.toString(moves[bestMoveIndex]), bestScore, visits[bestMoveIndex]);
        }
        
        return moves[bestMoveIndex];
    }
    
    private int selectMoveIndex(int nMoves, int bestMoveIndex) {
        if (rand.nextDouble() < epsilon) {
            return rand.nextInt(nMoves);
        } else {
            return bestMoveIndex;
        }
    }
    
    private int evaluateMove(final int move) {
        final Board b = new RolloutBoard(board);
        
        b.applyMove(move);
        
        int[] empty = b.emptySpaces();
        Move.shuffle(empty, rand);
        
        for (int i = 0; i < empty.length; i++) {
            b.applyMove(Move.setTile(empty[i], rand.nextInt(3)));
        }
        
        return b.getScore(isBlue());
    }
}
