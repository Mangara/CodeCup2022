package codecup2022.player;

import codecup2022.TestUtils;
import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.movegenerator.AllMoves;
import org.junit.Test;
import static org.junit.Assert.*;

public class SolvingPlayerTest {
    
    public SolvingPlayerTest() {
    }
    
    protected final Board sampleBoard = TestUtils.parseBoard(
            " abcdefg\n" +
            "aLLLLLRL\n" +
            "b RLSSRL\n" +
            "cSLSS RS\n" +
            "dSLLRRLR\n" +
            "eLRRSSSS\n" +
            "fLRSRRLL\n" +
            "gSLSRLS \n" +
            "hSLSRRSR\n" +
            "iSRSSRSS");

    @Test
    public void testSample1() {
        System.out.println("sample1");
        
        Board board = new ArrayBoard(sampleBoard);
        SolvingPlayer player = new SolvingPlayer(new RandomPlayer(new AllMoves()), 6);
        player.initialize(board, true);
        
        assertEquals(Move.fromString("ces"), player.selectMove());
    }
    
    @Test
    public void testSample2() {
        System.out.println("sample2");
        
        Board board = new ArrayBoard(sampleBoard);
        board.applyMove(Move.fromString("bar"));
        SolvingPlayer player = new SolvingPlayer(new RandomPlayer(new AllMoves()), 6);
        player.initialize(board, false);
        
        assertEquals(Move.fromString("ces"), player.selectMove());
    }
}
