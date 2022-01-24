package codecup2022.player;

import codecup2022.data.Board;
import codecup2022.data.Move;
import java.util.Random;

public class StraightPlayer extends Player {

    private final Random rand;

    public StraightPlayer() {
        this(new Random());
    }
    
    public StraightPlayer(Random rand) {
        super("Straight");
        this.rand = rand;
    }
    
    @Override
    protected int selectMove() {
        int[] empty = board.emptySpaces();
        Move.shuffle(empty, rand);
        
        for (int move : empty) {
            int row = Move.getRow(move);
            int col = Move.getColumn(move);
            
            if (isBlue()) {
                if (col == 0 || !board.isEmpty(row, col - 1)) {
                    return move;
                }
            } else {
                if (col == 6 || !board.isEmpty(row, col + 1)) {
                    return move;
                }
            }
        }
        
        throw new Error("Illegal state");
    }
    
}
