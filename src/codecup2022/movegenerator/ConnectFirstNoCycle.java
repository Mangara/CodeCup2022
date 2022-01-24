package codecup2022.movegenerator;

import codecup2022.data.Board;

public class ConnectFirstNoCycle extends MoveGenerator {

    @Override
    public String name() {
        return "ConnectNoCycle";
    }

    @Override
    public int[] generateMoves(final Board board) {
        final int[] connectingMoves = board.connectingMoves();
        
        if (connectingMoves.length > 0) {
            return connectingMoves;
        }
        
        final int[] nonCycleMoves = board.nonNegativeMoves();
        
        if (nonCycleMoves.length > 0) {
            return nonCycleMoves;
        }
        
        return board.possibleMoves();
    }
}
