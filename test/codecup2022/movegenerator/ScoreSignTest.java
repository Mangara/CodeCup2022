package codecup2022.movegenerator;

import codecup2022.TestUtils;
import codecup2022.data.Board;
import codecup2022.data.Move;
import org.junit.Test;
import static org.junit.Assert.*;

public class ScoreSignTest {

    public ScoreSignTest() {
    }

    @Test
    public void testBoard1() {
        System.out.println("board1");
        Board board = TestUtils.parseBoard(
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
        int[] expected = new int[]{ Move.fromString("ces"), Move.fromString("cel"), Move.fromString("cer") };
        int[] result = new ScoreSign().generateMoves(board);
        assertArrayEquals("Expected: " + movesToString(expected) + " but got: " + movesToString(result), expected, result);
    }
    
    @Test
    public void testBoard2() {
        System.out.println("board2");
        Board board = TestUtils.parseBoard(
            " abcdefg\n" +
            "aLLLLLRL\n" +
            "b RLSSRL\n" +
            "cSLSSSRS\n" +
            "dSLLRRLR\n" +
            "eLRRSSSS\n" +
            "fLRSRRLL\n" +
            "gSLSRLS \n" +
            "hSLSRRSR\n" +
            "iSRSSRSS");
        int[] expected = new int[]{ 
            Move.fromString("bas"), Move.fromString("bal"), Move.fromString("bar"),
            Move.fromString("ggs"), Move.fromString("ggl"), Move.fromString("ggr")
        };
        int[] result = new ScoreSign().generateMoves(board);
        assertArrayEquals("Expected: " + movesToString(expected) + " but got: " + movesToString(result), expected, result);
    }
    
    @Test
    public void testBoard3() {
        System.out.println("board3");
        Board board = TestUtils.parseBoard(
            " abcdefg\n" +
            "aLLLLLRL\n" +
            "b RLSSRL\n" +
            "cRLSSSRS\n" +
            "dSLLRRLR\n" +
            "eLRRSSSS\n" +
            "fLRSRRLL\n" +
            "gSLSRLS \n" +
            "hSLSRRSR\n" +
            "iSRSSRSS");
        int[] expected = new int[]{ Move.fromString("bas"), Move.fromString("bar") };
        int[] result = new ScoreSign().generateMoves(board);
        assertArrayEquals("Expected: " + movesToString(expected) + " but got: " + movesToString(result), expected, result);
    }

    private String movesToString(int[] moves) {
        StringBuffer sb = new StringBuffer("[");
        
        for (int i = 0; i < moves.length; i++) {
            sb.append(Move.toString(moves[i])).append(", ");
        }
        
        sb.delete(sb.length() - 2, sb.length());
        sb.append(']');
        return sb.toString();
    }

}
