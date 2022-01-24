package codecup2022.player;

import codecup2022.data.Move;
import codecup2022.movegenerator.MoveGenerator;

public class SimpleMax extends Player {

    private final MoveGenerator generator;
    
    public SimpleMax(MoveGenerator generator) {
        super("Max-" + generator.name());
        this.generator = generator;
    }

    @Override
    protected int selectMove() {
        int[] moves = generator.generateMoves(board);
        
        int bestMove = moves[0];
        int bestScore = -1000;
        
        for (int move : moves) {
            int score = board.scoreAfterMove(move, isBlue());
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        
        return bestMove;
    }
}
