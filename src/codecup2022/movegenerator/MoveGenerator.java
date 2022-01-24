package codecup2022.movegenerator;

import codecup2022.data.Board;

public abstract class MoveGenerator {
    public abstract int[] generateMoves(Board board);

    public abstract String name();
    
    public void initialize(Board board) {
        // No-op for subclasses to override
    }

    public void applyMove(Board board, int move) {
        // No-op for subclasses to override
    }

    public void undoMove(Board board, int move) {
        // No-op for subclasses to override
    }
}
