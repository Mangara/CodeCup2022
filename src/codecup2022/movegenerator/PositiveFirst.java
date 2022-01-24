package codecup2022.movegenerator;

import codecup2022.data.Board;
import java.util.Arrays;

public class PositiveFirst extends MoveGenerator {

    @Override
    public String name() {
        return "PositiveFirst";
    }

    @Override
    public int[] generateMoves(Board board) {
        final int[] connectingMoves = board.connectingMoves();
        
        if (connectingMoves.length == 0) {
            return board.possibleMoves();
        } else {
            return positiveMoves(board, connectingMoves);
        }
    }

    private int[] positiveMoves(Board board, int[] connectingMoves) {
        int[] positiveMoves = new int[connectingMoves.length];
        int index = 0;
        
        final boolean blue = board.isCurrentPlayerBlue();
        final int scoreBefore = board.getScore(blue);
        
        for (int move : connectingMoves) {
            if (board.scoreAfterMove(move, blue) > scoreBefore) {
                positiveMoves[index] = move;
                index++;
            }
        }
        
        if (index == 0) {
            // All connecting moves are non-positive (e.g. they only score 1-2 points and also create a cycle)
            return board.possibleMoves();
        } else {
            return Arrays.copyOf(positiveMoves, index);
        }
    }
}
