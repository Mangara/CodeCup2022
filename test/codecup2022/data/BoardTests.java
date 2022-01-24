package codecup2022.data;

import codecup2022.TestUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class BoardTests {
    protected final Board startBoard = TestUtils.parseBoard(
            " abcdefg\n" +
            "a       \n" +
            "b       \n" +
            "c  S    \n" +
            "d       \n" +
            "e       \n" +
            "f       \n" +
            "g   R   \n" +
            "h       \n" +
            "i       ");
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
    protected final Board cycleBoard = TestUtils.parseBoard(
            " abcdefg\n" +
            "a       \n" +
            "b LR    \n" +
            "c R R   \n" +
            "d  RL   \n" +
            "e       \n" +
            "f       \n" +
            "g       \n" +
            "h       \n" +
            "i       ");
    
    protected abstract Board initializeBoard(Board board);

    @Test
    public void testGet() {
        Board board = initializeBoard(sampleBoard);
        
        assertEquals(Board.LEFT, board.get(0, 0));
        assertEquals(Board.LEFT, board.get(0, 1));
        assertEquals(Board.LEFT, board.get(0, 2));
        assertEquals(Board.LEFT, board.get(0, 3));
        assertEquals(Board.LEFT, board.get(0, 4));
        assertEquals(Board.RIGHT, board.get(0, 5));
        assertEquals(Board.LEFT, board.get(0, 6));

        assertEquals(Board.EMPTY, board.get(1, 0));
        assertEquals(Board.RIGHT, board.get(1, 1));
        assertEquals(Board.LEFT, board.get(1, 2));
        assertEquals(Board.STRAIGHT, board.get(1, 3));
        assertEquals(Board.STRAIGHT, board.get(1, 4));
        assertEquals(Board.RIGHT, board.get(1, 5));
        assertEquals(Board.LEFT, board.get(1, 6));

        assertEquals(Board.STRAIGHT, board.get(2, 0));
        assertEquals(Board.LEFT, board.get(2, 1));
        assertEquals(Board.STRAIGHT, board.get(2, 2));
        assertEquals(Board.STRAIGHT, board.get(2, 3));
        assertEquals(Board.EMPTY, board.get(2, 4));
        assertEquals(Board.RIGHT, board.get(2, 5));
        assertEquals(Board.STRAIGHT, board.get(2, 6));

        assertEquals(Board.STRAIGHT, board.get(3, 0));
        assertEquals(Board.LEFT, board.get(3, 1));
        assertEquals(Board.LEFT, board.get(3, 2));
        assertEquals(Board.RIGHT, board.get(3, 3));
        assertEquals(Board.RIGHT, board.get(3, 4));
        assertEquals(Board.LEFT, board.get(3, 5));
        assertEquals(Board.RIGHT, board.get(3, 6));

        assertEquals(Board.LEFT, board.get(4, 0));
        assertEquals(Board.RIGHT, board.get(4, 1));
        assertEquals(Board.RIGHT, board.get(4, 2));
        assertEquals(Board.STRAIGHT, board.get(4, 3));
        assertEquals(Board.STRAIGHT, board.get(4, 4));
        assertEquals(Board.STRAIGHT, board.get(4, 5));
        assertEquals(Board.STRAIGHT, board.get(4, 6));

        assertEquals(Board.LEFT, board.get(5, 0));
        assertEquals(Board.RIGHT, board.get(5, 1));
        assertEquals(Board.STRAIGHT, board.get(5, 2));
        assertEquals(Board.RIGHT, board.get(5, 3));
        assertEquals(Board.RIGHT, board.get(5, 4));
        assertEquals(Board.LEFT, board.get(5, 5));
        assertEquals(Board.LEFT, board.get(5, 6));

        assertEquals(Board.STRAIGHT, board.get(6, 0));
        assertEquals(Board.LEFT, board.get(6, 1));
        assertEquals(Board.STRAIGHT, board.get(6, 2));
        assertEquals(Board.RIGHT, board.get(6, 3));
        assertEquals(Board.LEFT, board.get(6, 4));
        assertEquals(Board.STRAIGHT, board.get(6, 5));
        assertEquals(Board.EMPTY, board.get(6, 6));

        assertEquals(Board.STRAIGHT, board.get(7, 0));
        assertEquals(Board.LEFT, board.get(7, 1));
        assertEquals(Board.STRAIGHT, board.get(7, 2));
        assertEquals(Board.RIGHT, board.get(7, 3));
        assertEquals(Board.RIGHT, board.get(7, 4));
        assertEquals(Board.STRAIGHT, board.get(7, 5));
        assertEquals(Board.RIGHT, board.get(7, 6));

        assertEquals(Board.STRAIGHT, board.get(8, 0));
        assertEquals(Board.RIGHT, board.get(8, 1));
        assertEquals(Board.STRAIGHT, board.get(8, 2));
        assertEquals(Board.STRAIGHT, board.get(8, 3));
        assertEquals(Board.RIGHT, board.get(8, 4));
        assertEquals(Board.STRAIGHT, board.get(8, 5));
        assertEquals(Board.STRAIGHT, board.get(8, 6));
    }

    @Test
    public void testGetTurn() {
        assertEquals(0, initializeBoard(startBoard).getTurn());
        assertEquals(58, initializeBoard(sampleBoard).getTurn());
    }
    
    @Test
    public void testApplyMove() {
        Board board = initializeBoard(startBoard);
        
        board.applyMove(Move.fromString("cdr"));
        assertEquals(Board.RIGHT, board.get(2, 3));
        assertEquals(1, board.getTurn());
        assertEquals(60, board.getNumFreeSpaces());
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        
        board.applyMove(Move.fromString("dgs"));
        assertEquals(Board.STRAIGHT, board.get(3, 6));
        assertEquals(2, board.getTurn());
        assertEquals(59, board.getNumFreeSpaces());
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        
        board = initializeBoard(sampleBoard);
        board.applyMove(Move.fromString("cer"));
        assertEquals(Board.RIGHT, board.get(2, 4));
        assertEquals(59, board.getTurn());
        assertEquals(2, board.getNumFreeSpaces());
        assertEquals(60, board.getScore(true));
        assertEquals(50, board.getScore(false));
        
        board.applyMove(Move.fromString("ggs"));
        assertEquals(Board.STRAIGHT, board.get(6, 6));
        assertEquals(60, board.getTurn());
        assertEquals(1, board.getNumFreeSpaces());
        assertEquals(60, board.getScore(true));
        assertEquals(47, board.getScore(false));
    }
    
    @Test
    public void testUndoMove() {
        Board board = initializeBoard(sampleBoard);
        
        board.undoMove(Move.fromString("cds"));
        assertEquals(Board.EMPTY, board.get(2, 3));
        assertEquals(57, board.getTurn());
        assertEquals(4, board.getNumFreeSpaces());
        
        board.undoMove(Move.fromString("dgr"));
        assertEquals(Board.EMPTY, board.get(3, 6));
        assertEquals(56, board.getTurn());
        assertEquals(5, board.getNumFreeSpaces());
    }
    
    @Test
    public void testGetNumFreeSpaces() {
        assertEquals(61, initializeBoard(startBoard).getNumFreeSpaces());
        assertEquals(3, initializeBoard(sampleBoard).getNumFreeSpaces());
    }
    
    @Test
    public void testEmptySpaces() {
        List<String> sampleFree = Arrays.asList("bas", "ces", "ggs");
        Set<Integer> sampleMine = sampleFree.stream().map(s -> Move.fromString(s)).collect(Collectors.toSet());
        movesEqual(sampleMine, initializeBoard(sampleBoard).emptySpaces());
        
        Set<Integer> allSpaces = new HashSet<>(63);
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                allSpaces.add(Move.fromRowColumnTile(row, col, Board.STRAIGHT));
            }
        }
        Set<Integer> startSpaces = new HashSet<>(allSpaces);
        startSpaces.removeAll(Arrays.asList(Move.fromString("ccs"), Move.fromString("gds")));
        movesEqual(startSpaces, initializeBoard(startBoard).emptySpaces());
    }
    
    @Test
    public void testPossibleMoves() {
        List<String> sampleFree = Arrays.asList("bas", "ces", "ggs");
        Set<Integer> sampleMoves = sampleFree.stream()
                .flatMap(s -> Arrays.asList(
                        Move.fromString(s), 
                        Move.setTile(Move.fromString(s), Board.LEFT), 
                        Move.setTile(Move.fromString(s), Board.RIGHT)
                ).stream())
                .collect(Collectors.toSet());
        movesEqual(sampleMoves, initializeBoard(sampleBoard).possibleMoves());
        
        Set<Integer> allSpaces = new HashSet<>(63);
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                allSpaces.add(Move.fromRowColumnTile(row, col, Board.STRAIGHT));
            }
        }
        Set<Integer> startSpaces = new HashSet<>(allSpaces);
        startSpaces.removeAll(Arrays.asList(Move.fromString("ccs"), Move.fromString("gds")));
        Set<Integer> startMoves = startSpaces.stream()
                .flatMap(m -> Arrays.asList(
                        m, 
                        Move.setTile(m, Board.LEFT), 
                        Move.setTile(m, Board.RIGHT)
                ).stream())
                .collect(Collectors.toSet());
        movesEqual(startMoves, initializeBoard(startBoard).possibleMoves());
    }
    
    private void movesEqual(Set<Integer> expected, int[] actual) {
        Set<Integer> actualSet = Arrays.stream(actual).boxed().collect(Collectors.toSet());
        assertEquals(expected, actualSet);
        assertEquals(expected.size(), actual.length);
    }

    @Test
    public void testGetScore() {
        Board board = initializeBoard(new ArrayBoard());

        // Jury
        board.applyMove(Move.fromString("bbs"));
        board.applyMove(Move.fromString("hbl"));

        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("cas"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("aal"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ges"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("eds"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ecs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("gds"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("idl"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ifr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("gas"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("aer"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ggs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bfs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("fbr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("gfs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("cds"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("abr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("gcs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("das"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("del"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ccl"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("fds"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ddl"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("hfr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("hal"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ier"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("dgl"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ibs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("hds"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("hgl"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ear"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bal"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("efr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("acs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("afl"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bes"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bcr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bgs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("gbr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ads"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ffr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("dcr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("hel"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("cbs"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("fer"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("eel"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bds"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("hcr"));
        assertEquals(50, board.getScore(true));
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("fas"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("fcr"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("agr"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("dbr"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("dfl"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("igs"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("cfr"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("icr"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("egl"));
        assertEquals(50, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("fgl"));
        assertEquals(47, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("cel"));
        assertEquals(47, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("cgl"));
        assertEquals(39, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("ias"));
        assertEquals(39, board.getScore(true));
        assertEquals(55, board.getScore(false));
        board.applyMove(Move.fromString("ebr"));
        assertEquals(36, board.getScore(true));
        assertEquals(55, board.getScore(false));
        
        // Reverse
        
        assertEquals(55, board.getScore(false));
        assertEquals(36, board.getScore(true));
        board.undoMove(Move.fromString("ebr"));
        assertEquals(55, board.getScore(false));
        assertEquals(39, board.getScore(true));
        board.undoMove(Move.fromString("ias"));
        assertEquals(55, board.getScore(false));
        assertEquals(39, board.getScore(true));
        board.undoMove(Move.fromString("cgl"));
        assertEquals(55, board.getScore(false));
        assertEquals(47, board.getScore(true));
        board.undoMove(Move.fromString("cel"));
        assertEquals(55, board.getScore(false));
        assertEquals(47, board.getScore(true));
        board.undoMove(Move.fromString("fgl"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("egl"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("icr"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("cfr"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("igs"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("dfl"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("dbr"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("agr"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("fcr"));
        assertEquals(55, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("fas"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("hcr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("bds"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("eel"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("fer"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("cbs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("hel"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("dcr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ffr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ads"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("gbr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("bgs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("bcr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("bes"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("afl"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("acs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("efr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("bal"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ear"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("hgl"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("hds"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ibs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("dgl"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ier"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("hal"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("hfr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ddl"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("fds"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ccl"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("del"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("das"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("gcs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("abr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("cds"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("gfs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("fbr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("bfs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ggs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("aer"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("gas"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ifr"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("idl"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("gds"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ecs"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("eds"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("ges"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("aal"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
        board.undoMove(Move.fromString("cas"));
        assertEquals(50, board.getScore(false));
        assertEquals(50, board.getScore(true));
    }
    
//    @Test
//    public void testGetScoreDoubleEarn() {
//        Board board = initializeBoard(sampleBoard);
//        
//        assertTrue(board.isCurrentPlayerBlue());
//        assertEquals(50, board.getScore(true));
//        board.applyMove(Move.fromString("cer"));
//        assertEquals(60, board.getScore(true));
//        
//        board = initializeBoard(sampleBoard);
//        board.applyMove(Move.fromString("ggs"));
//        
//        assertFalse(board.isCurrentPlayerBlue());
//        assertEquals(50, board.getScore(false));
//        board.applyMove(Move.fromString("cer"));
//        assertEquals(61, board.getScore(false));
//    }
    
    @Test
    public void testGetScoreSameSideConnection() {
        Board board = initializeBoard(sampleBoard);
        
        assertTrue(board.isCurrentPlayerBlue());
        assertEquals(50, board.getScore(true));
        board.applyMove(Move.fromString("ggs"));
        assertEquals(47, board.getScore(true));
        
        board = initializeBoard(sampleBoard);
        board.applyMove(Move.fromString("bas"));
        
        assertFalse(board.isCurrentPlayerBlue());
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("ggs"));
        assertEquals(47, board.getScore(false));
        
        board = initializeBoard(sampleBoard);
        
        assertTrue(board.isCurrentPlayerBlue());
        assertEquals(50, board.getScore(true));
        board.applyMove(Move.fromString("bal"));
        assertEquals(47, board.getScore(true));
        
        board = initializeBoard(sampleBoard);
        board.applyMove(Move.fromString("ggs"));
        
        assertFalse(board.isCurrentPlayerBlue());
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("bal"));
        assertEquals(47, board.getScore(false));
    }
    
    @Test
    public void testGetScoreCycle() {
        Board board = initializeBoard(sampleBoard);
        
        assertTrue(board.isCurrentPlayerBlue());
        assertEquals(50, board.getScore(true));
        board.applyMove(Move.fromString("cel"));
        assertEquals(51, board.getScore(true));
        
        board = initializeBoard(sampleBoard);
        board.applyMove(Move.fromString("ggs"));
        
        assertFalse(board.isCurrentPlayerBlue());
        assertEquals(50, board.getScore(false));
        board.applyMove(Move.fromString("cel"));
        assertEquals(52, board.getScore(false));
    }
    
    @Test
    public void testGetScoreCycle2() {
        Board board = initializeBoard(cycleBoard);
        
        assertEquals(50, board.getScore(true));
        board.applyMove(Move.fromString("ccs"));
        assertEquals(45, board.getScore(true));
        
        board = initializeBoard(cycleBoard);
        
        assertEquals(50, board.getScore(true));
        board.applyMove(Move.fromString("ccl"));
        assertEquals(40, board.getScore(true));
        
        board = initializeBoard(cycleBoard);
        
        assertEquals(50, board.getScore(true));
        board.applyMove(Move.fromString("ccr"));
        assertEquals(45, board.getScore(true));
    }
}
