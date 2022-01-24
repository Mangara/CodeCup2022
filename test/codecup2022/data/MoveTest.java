package codecup2022.data;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class MoveTest {
    
    public MoveTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testStringRoundtrip() {
        List<String> moves = Arrays.asList("aas", "cds", "ebr", "ids", "cbl", "egs", "cas", "cer", "ggs", "bal", "igr");
        for (String move : moves) {
            assertEquals(move, Move.toString(Move.fromString(move)));
        }
    }
    
    @Test
    public void testStringElementRoundtrip() {
        List<String> moves = Arrays.asList("aas", "cds", "ebr", "ids", "cbl", "egs", "cas", "cer", "ggs", "bal", "igr");
        for (String move : moves) {
            int moveInt = Move.fromString(move);
            int row = Move.getRow(moveInt);
            int col = Move.getColumn(moveInt);
            int tile = Move.getTile(moveInt);
            assertEquals(move, Move.toString(Move.fromRowColumnTile(row, col, tile)));
        }
    }
    
    @Test
    public void testGetRow() {
        List<String> moves = Arrays.asList("aas", "cds", "ebr", "ids", "cbl", "egs", "cas", "cer", "ggs", "bal", "igr");
        List<Integer> rows = Arrays.asList( 0,     2,     4,     8,     2,     4,     2,     2,     6,     1,     8);
        for (int i = 0; i < moves.size(); i++) {
            String move = moves.get(i);
            int row = rows.get(i);
            assertEquals(move, row, Move.getRow(Move.fromString(move)));
        }
    }
    
    @Test
    public void testGetColumn() {
        List<String> moves = Arrays.asList("aas", "cds", "ebr", "ids", "cbl", "egs", "cas", "cer", "ggs", "bal", "igr");
        List<Integer> cols = Arrays.asList(  0,     3,     1,     3,     1,     6,     0,     4,     6,     0,     6);
        for (int i = 0; i < moves.size(); i++) {
            String move = moves.get(i);
            int col = cols.get(i);
            assertEquals(move, col, Move.getColumn(Move.fromString(move)));
        }
    }
    
    @Test
    public void testGetTile() {
        List<String> moves = Arrays.asList("aas", "cds", "ebr", "ids", "cbl", "egs", "cas", "cer", "ggs", "bal", "igr");
        List<Integer> exps = Arrays.asList(   0,     0,     2,     0,     1,     0,     0,     2,     0,     1,     2);
        for (int i = 0; i < moves.size(); i++) {
            String move = moves.get(i);
            int expected = exps.get(i);
            assertEquals(move, expected, Move.getTile(Move.fromString(move)));
        }
    }
}
