package codecup2022.movegenerator;

import codecup2022.data.Board;

public class AllMoves extends MoveGenerator {

    @Override
    public String name() {
        return "All";
    }
    
    @Override
    public int[] generateMoves(Board board) {
        return board.possibleMoves();
    }
}
