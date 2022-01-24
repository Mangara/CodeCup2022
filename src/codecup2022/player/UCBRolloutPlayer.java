package codecup2022.player;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.movegenerator.MoveGenerator;
import static codecup2022.player.Player.DEBUG;
import codecup2022.stopcriterion.StopCriterion;
import java.util.Arrays;
import java.util.Random;

public class UCBRolloutPlayer extends Player {
    
    private final MoveGenerator generator;
    private final StopCriterion stop;
    private final Random rand;
    
    public UCBRolloutPlayer(MoveGenerator generator, StopCriterion stop, Random rand) {
        super(String.format("UCBRollout-%s-%s", generator.name(), stop.name()));
        this.generator = generator;
        this.stop = stop;
        this.rand = rand;
    }

    @Override
    protected int selectMove() {
        stop.started();
        final int[] moves = generator.generateMoves(board);
        final double[] scores = new double[moves.length];
        final int[] visits = new int[moves.length];
        
        // Initialize by playing each move once
        for (int i = 0; i < moves.length && !stop.shouldStop(); i++) {
            final int score = evaluateMove(moves[i]);
            scores[i] = score;
        }
        Arrays.fill(visits, 1);
        
        // Keep playing moves to get a better approximation of their true score
        for (int t = moves.length; !stop.shouldStop(); t++) {
            final int index = selectMoveIndex(t, scores, visits);
            final int score = evaluateMove(moves[index]);
            
            // Update score for the played move
            scores[index] = visits[index] * scores[index] + score;
            visits[index]++;
            scores[index] /= visits[index];
        }
        
        int bestMoveIndex = bestMoveIndex(scores);
        
        if (DEBUG) {
            for (int i = 0; i < moves.length; i++) {
                System.err.printf("%s (%.2f, %d)%n", Move.toString(moves[i]), scores[i], visits[i]);
            }
            System.err.printf("Best: %s (%.2f, %d)%n", Move.toString(moves[bestMoveIndex]), scores[bestMoveIndex], visits[bestMoveIndex]);
        }
        
        return moves[bestMoveIndex];
    }
    
    private int selectMoveIndex(int t, double[] scores, int[] visits) {
        double bestValue = Double.NEGATIVE_INFINITY;
        int bestIndex = -1;
        
//        final double turnFactor = 2 * Math.log(1 + t * Math.log(t) * Math.log(t));
        final double turnFactor = 2 * Math.log(t);
        
        for (int i = 0; i < scores.length; i++) {
            final double exploration = Math.sqrt(turnFactor / visits[i]);
            final double ucbValue = scores[i] + exploration;
            
            if (ucbValue > bestValue) {
                bestValue = ucbValue;
                bestIndex = i;
            }
        }
        
        return bestIndex;
    }
    
    private int bestMoveIndex(double[] scores) {
        int bestIndex = 0;
        double bestScore = scores[0];
        
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > bestScore) {
                bestScore = scores[i];
                bestIndex = i;
            }
        }
        
        return bestIndex;
    }

    private int evaluateMove(final int move) {
        final Board b = new ArrayBoard(board);
        
        b.applyMove(move);
        
        int[] empty = b.emptySpaces();
        Move.shuffle(empty, rand);
        
        for (int i = 0; i < empty.length; i++) {
            b.applyMove(Move.setTile(empty[i], rand.nextInt(3)));
        }
        
        return b.getScore(isBlue());
    }
}
