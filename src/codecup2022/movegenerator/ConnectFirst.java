package codecup2022.movegenerator;

import codecup2022.data.Board;

public class ConnectFirst extends MoveGenerator {

    @Override
    public String name() {
        return "ConnectFirst";
    }

    @Override
    public int[] generateMoves(Board board) {
        int[] moves = board.connectingMoves();
        
        if (moves.length == 0) {
            moves = board.possibleMoves();
        }
        
        return moves;
    }
}
