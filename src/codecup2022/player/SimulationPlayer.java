package codecup2022.player;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.movegenerator.MoveGenerator;
import java.util.Random;

public class SimulationPlayer extends Player {

    private final MoveGenerator generator;
    private final int nSimulations;
    private final Random rand;
    
    public SimulationPlayer(MoveGenerator generator, int nSimulations, Random rand) {
        super(String.format("Rollout-%s-%d", generator.name(), nSimulations));
        this.generator = generator;
        this.nSimulations = nSimulations;
        this.rand = rand;
    }

    @Override
    protected int selectMove() {
        final int[] moves = generator.generateMoves(board);
        final int nSimulationsPerMove = nSimulations / moves.length;
        
        int bestMove = moves[0];
        int bestScore = -1000;
        
        for (int move : moves) {
            int score = evaluateMove(move, nSimulationsPerMove);
            
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }

    private int evaluateMove(final int move, final int nSimulationsPerMove) {
        int totalScore = 0;
        board.applyMove(move);
        
        final int[] empty = board.emptySpaces();
        
        for (int i = 0; i < nSimulationsPerMove; i++) {
            totalScore += runSimulation(empty);
        }
        
        board.undoMove(move);
        return totalScore;
    }
    
    private int runSimulation(final int[] empty) {
        final Board b = new ArrayBoard(board);
        
        Move.shuffle(empty, rand);
        
        for (int i = 0; i < empty.length; i++) {
            b.applyMove(Move.setTile(empty[i], rand.nextInt(3)));
        }
        
        return b.getScore(isBlue());
    }
}
