package codecup2022.data;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class PathsTest {
    
    public PathsTest() {
    }

    @Test
    public void testSingleEdge() {
        System.out.println("single edge");
        Paths paths = new Paths();
        
        assertEquals(0, paths.addEdge(Move.fromString("aas"), Paths.Edge.FIRST, true));
        check("aa", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("aas"), Paths.Edge.FIRST, true));
        check("aa", paths);
        
        assertEquals(0, paths.addEdge(Move.fromString("aas"), Paths.Edge.SECOND, true));
        check("aa", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("aas"), Paths.Edge.SECOND, true));
        check("aa", paths);
        
        assertEquals(0, paths.addEdge(Move.fromString("aal"), Paths.Edge.FIRST, true));
        check("aa", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("aal"), Paths.Edge.FIRST, true));
        check("aa", paths);
        
        assertEquals(0, paths.addEdge(Move.fromString("aal"), Paths.Edge.SECOND, true));
        check("aa", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("aal"), Paths.Edge.SECOND, true));
        check("aa", paths);
        
        assertEquals(0, paths.addEdge(Move.fromString("aar"), Paths.Edge.FIRST, true));
        check("aa", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("aar"), Paths.Edge.FIRST, true));
        check("aa", paths);
        
        assertEquals(0, paths.addEdge(Move.fromString("aar"), Paths.Edge.SECOND, true));
        check("aa", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("aar"), Paths.Edge.SECOND, true));
        check("aa", paths);
    }
    
    @Test
    public void testPath() {
        System.out.println("path");
        Paths paths = new Paths();
        
        assertEquals(0, paths.addEdge(Move.fromString("aas"), Paths.Edge.FIRST, true));
        paths.checkPaths(0, 1);
        assertEquals(0, paths.addEdge(Move.fromString("abr"), Paths.Edge.FIRST, true));
        paths.checkPaths(1, 1);
        assertEquals(0, paths.addEdge(Move.fromString("bbs"), Paths.Edge.SECOND, true));
        paths.checkPaths(2, 1);
        assertEquals(0, paths.addEdge(Move.fromString("cbs"), Paths.Edge.SECOND, true));
        paths.checkPaths(3, 1);
        assertEquals(0, paths.addEdge(Move.fromString("dbr"), Paths.Edge.SECOND, true));
        paths.checkPaths(3, 2);
        assertEquals(0, paths.addEdge(Move.fromString("dcl"), Paths.Edge.FIRST, true));
        paths.checkPaths(2, 2);
        assertEquals(0, paths.addEdge(Move.fromString("ccl"), Paths.Edge.SECOND, true));
        paths.checkPaths(2, 3);
        assertEquals(0, paths.addEdge(Move.fromString("cds"), Paths.Edge.FIRST, true));
        paths.checkPaths(2, 4);
        assertEquals(0, paths.addEdge(Move.fromString("cel"), Paths.Edge.FIRST, true));
        paths.checkPaths(1, 4);
        assertEquals(0, paths.addEdge(Move.fromString("bel"), Paths.Edge.SECOND, true));
        paths.checkPaths(1, 5);
        assertEquals(0, paths.addEdge(Move.fromString("bfr"), Paths.Edge.FIRST, true));
        paths.checkPaths(2, 5);
        assertEquals(0, paths.addEdge(Move.fromString("cfs"), Paths.Edge.SECOND, true));
        paths.checkPaths(3, 5);
        assertEquals(0, paths.addEdge(Move.fromString("dfr"), Paths.Edge.SECOND, true));
        paths.checkPaths(3, 6);
        
        assertEquals(14, paths.addEdge(Move.fromString("dgs"), Paths.Edge.FIRST, true));
        assertEquals(-14, paths.removeEdge(Move.fromString("dgs"), Paths.Edge.FIRST, true));
        
        paths.checkPaths(3, 6);
        
        assertEquals(1, paths.addEdge(Move.fromString("dgs"), Paths.Edge.FIRST, false));
        assertEquals(-1, paths.removeEdge(Move.fromString("dgs"), Paths.Edge.FIRST, false));
        
        paths.checkPaths(3, 6);
        assertEquals(0, paths.removeEdge(Move.fromString("dfr"), Paths.Edge.SECOND, true));
        paths.checkPaths(3, 5);
        assertEquals(0, paths.removeEdge(Move.fromString("cfs"), Paths.Edge.SECOND, true));
        paths.checkPaths(2, 5);
        assertEquals(0, paths.removeEdge(Move.fromString("bfr"), Paths.Edge.FIRST, true));
        paths.checkPaths(1, 5);
        assertEquals(0, paths.removeEdge(Move.fromString("bel"), Paths.Edge.SECOND, true));
        paths.checkPaths(1, 4);
        assertEquals(0, paths.removeEdge(Move.fromString("cel"), Paths.Edge.FIRST, true));
        paths.checkPaths(2, 4);
        assertEquals(0, paths.removeEdge(Move.fromString("cds"), Paths.Edge.FIRST, true));
        paths.checkPaths(2, 3);
        assertEquals(0, paths.removeEdge(Move.fromString("ccl"), Paths.Edge.SECOND, true));
        paths.checkPaths(2, 2);
        assertEquals(0, paths.removeEdge(Move.fromString("dcl"), Paths.Edge.FIRST, true));
        paths.checkPaths(3, 2);
        assertEquals(0, paths.removeEdge(Move.fromString("dbr"), Paths.Edge.SECOND, true));
        paths.checkPaths(3, 1);
        assertEquals(0, paths.removeEdge(Move.fromString("cbs"), Paths.Edge.SECOND, true));
        paths.checkPaths(2, 1);
        assertEquals(0, paths.removeEdge(Move.fromString("bbs"), Paths.Edge.SECOND, true));
        paths.checkPaths(1, 1);
        assertEquals(0, paths.removeEdge(Move.fromString("abr"), Paths.Edge.FIRST, true));
        paths.checkPaths(0, 1);
        assertEquals(0, paths.removeEdge(Move.fromString("aas"), Paths.Edge.FIRST, true));
    }
    
    @Test
    public void testSimpleCycle() {
        System.out.println("simple cycle");
        Paths paths = new Paths();
        
        assertEquals(0, paths.addEdge(Move.fromString("hfl"), Paths.Edge.SECOND, true));
        check("hg", paths);
        check("if", paths);
        check("ig", paths);
        assertEquals(0, paths.addEdge(Move.fromString("hgr"), Paths.Edge.FIRST, true));
        check("if", paths);
        check("ig", paths);
        assertEquals(0, paths.addEdge(Move.fromString("ifr"), Paths.Edge.SECOND, true));
        check("ig", paths);
        assertEquals(-5, paths.addEdge(Move.fromString("igl"), Paths.Edge.FIRST, true));
        
        assertEquals(5, paths.removeEdge(Move.fromString("igl"), Paths.Edge.FIRST, true));
        check("ig", paths);
        assertEquals(-5, paths.addEdge(Move.fromString("igl"), Paths.Edge.FIRST, false));
        
        assertEquals(5, paths.removeEdge(Move.fromString("igl"), Paths.Edge.FIRST, false));
        check("ig", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("ifr"), Paths.Edge.SECOND, true));
        check("ig", paths);
        check("if", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("hgr"), Paths.Edge.FIRST, true));
        check("ig", paths);
        check("if", paths);
        check("hg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("hfl"), Paths.Edge.SECOND, true));
    }
    
    @Test
    public void testBlueBlueConnection() {
        System.out.println("blue blue");
        Paths paths = new Paths();
        
        check("ca", paths);
        check("cb", paths);
        check("da", paths);
        check("db", paths);
        check("dc", paths);
        check("eb", paths);
        check("ec", paths);
        check("ed", paths);
        check("fc", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("fcr"), Paths.Edge.SECOND, true));
        check("ca", paths);
        check("cb", paths);
        check("da", paths);
        check("db", paths);
        check("dc", paths);
        check("eb", paths);
        check("ec", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("das"), Paths.Edge.FIRST, true));
        check("ca", paths);
        check("cb", paths);
        check("db", paths);
        check("dc", paths);
        check("eb", paths);
        check("ec", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("ebr"), Paths.Edge.SECOND, true));
        check("ca", paths);
        check("cb", paths);
        check("db", paths);
        check("dc", paths);
        check("ec", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("ecs"), Paths.Edge.FIRST, true));
        check("ca", paths);
        check("cb", paths);
        check("db", paths);
        check("dc", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("ecs"), Paths.Edge.SECOND, true));
        check("ca", paths);
        check("cb", paths);
        check("db", paths);
        check("dc", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("cas"), Paths.Edge.FIRST, true));
        check("cb", paths);
        check("db", paths);
        check("dc", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("dcr"), Paths.Edge.FIRST, true));
        check("cb", paths);
        check("db", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("dbr"), Paths.Edge.FIRST, true));
        check("cb", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("dbr"), Paths.Edge.SECOND, true));
        check("cb", paths);
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("cbr"), Paths.Edge.FIRST, true));
        check("ed", paths);
        check("fd", paths);
        assertEquals(0, paths.addEdge(Move.fromString("edr"), Paths.Edge.FIRST, true));
        check("fd", paths);
        
        assertEquals(-3, paths.addEdge(Move.fromString("fdl"), Paths.Edge.FIRST, true));
        assertEquals(3, paths.removeEdge(Move.fromString("fdl"), Paths.Edge.FIRST, true));
        check("fd", paths);
        assertEquals(-3, paths.addEdge(Move.fromString("fdl"), Paths.Edge.FIRST, false));
        assertEquals(3, paths.removeEdge(Move.fromString("fdl"), Paths.Edge.FIRST, false));
        
        check("fd", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("edr"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("cbr"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        check("cb", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("dbr"), Paths.Edge.SECOND, true));
        check("fd", paths);
        check("ed", paths);
        check("cb", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("dbr"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        check("db", paths);
        check("cb", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("dcr"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        check("dc", paths);
        check("db", paths);
        check("cb", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("cas"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        check("dc", paths);
        check("db", paths);
        check("cb", paths);
        check("ca", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("ecs"), Paths.Edge.SECOND, true));
        check("fd", paths);
        check("ed", paths);
        check("dc", paths);
        check("db", paths);
        check("cb", paths);
        check("ca", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("ecs"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        check("ec", paths);
        check("dc", paths);
        check("db", paths);
        check("cb", paths);
        check("ca", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("ebr"), Paths.Edge.SECOND, true));
        check("fd", paths);
        check("ed", paths);
        check("ec", paths);
        check("eb", paths);
        check("dc", paths);
        check("db", paths);
        check("cb", paths);
        check("ca", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("das"), Paths.Edge.FIRST, true));
        check("fd", paths);
        check("ed", paths);
        check("ec", paths);
        check("eb", paths);
        check("dc", paths);
        check("db", paths);
        check("da", paths);
        check("cb", paths);
        check("ca", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("fcr"), Paths.Edge.SECOND, true));
        check("fd", paths);
        check("fc", paths);
        check("ed", paths);
        check("ec", paths);
        check("eb", paths);
        check("dc", paths);
        check("db", paths);
        check("da", paths);
        check("cb", paths);
        check("ca", paths);
    }
    
    @Test
    public void testRedRedConnection() {
        System.out.println("red red");
        Paths paths = new Paths();
        
        check("dg", paths);
        check("ee", paths);
        check("ef", paths);
        check("eg", paths);
        check("fe", paths);
        check("ff", paths);
        check("fg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("fer"), Paths.Edge.SECOND, true));
        check("dg", paths);
        check("ee", paths);
        check("ef", paths);
        check("eg", paths);
        check("ff", paths);
        check("fg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("eel"), Paths.Edge.SECOND, true));
        check("dg", paths);
        check("ef", paths);
        check("eg", paths);
        check("ff", paths);
        check("fg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("ffs"), Paths.Edge.FIRST, true));
        check("dg", paths);
        check("ef", paths);
        check("eg", paths);
        check("fg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("efs"), Paths.Edge.FIRST, true));
        check("dg", paths);
        check("eg", paths);
        check("fg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("fgl"), Paths.Edge.FIRST, true));
        check("dg", paths);
        check("eg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("egl"), Paths.Edge.FIRST, true));
        check("dg", paths);
        assertEquals(0, paths.addEdge(Move.fromString("egl"), Paths.Edge.SECOND, true));
        check("dg", paths);
        
        assertEquals(-3, paths.addEdge(Move.fromString("dgl"), Paths.Edge.SECOND, true));
        assertEquals(3, paths.removeEdge(Move.fromString("dgl"), Paths.Edge.SECOND, true));
        check("dg", paths);
        assertEquals(-3, paths.addEdge(Move.fromString("dgl"), Paths.Edge.SECOND, false));
        assertEquals(3, paths.removeEdge(Move.fromString("dgl"), Paths.Edge.SECOND, false));
        
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("egl"), Paths.Edge.SECOND, true));
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("egl"), Paths.Edge.FIRST, true));
        check("eg", paths);
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("fgl"), Paths.Edge.FIRST, true));
        check("fg", paths);
        check("eg", paths);
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("efs"), Paths.Edge.FIRST, true));
        check("fg", paths);
        check("eg", paths);
        check("ef", paths);
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("ffs"), Paths.Edge.FIRST, true));
        check("fg", paths);
        check("ff", paths);
        check("eg", paths);
        check("ef", paths);
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("eel"), Paths.Edge.SECOND, true));
        check("fg", paths);
        check("ff", paths);
        check("eg", paths);
        check("ef", paths);
        check("ee", paths);
        check("dg", paths);
        assertEquals(0, paths.removeEdge(Move.fromString("fer"), Paths.Edge.SECOND, true));
        check("fg", paths);
        check("ff", paths);
        check("fe", paths);
        check("eg", paths);
        check("ef", paths);
        check("ee", paths);
        check("dg", paths);
    }
    
    @Test
    public void testCycleRepeat() {
        System.out.println("cycleRepeat");
        Paths paths = new Paths();
        
        assertEquals(0, paths.addEdge(Move.fromString("icl"), Paths.Edge.FIRST, true));
        assertEquals(0, paths.addEdge(Move.fromString("hcr"), Paths.Edge.FIRST, false));
        assertEquals(0, paths.addEdge(Move.fromString("ibr"), Paths.Edge.SECOND, true));
        
        for (int i = 0; i < 100; i++) {
            check("hb", paths);
            assertEquals(-5, paths.addEdge(Move.fromString("hbl"), Paths.Edge.SECOND, true));
            assertEquals(5, paths.removeEdge(Move.fromString("hbl"), Paths.Edge.SECOND, true));
        }
    }
    
    @Test
    public void testGetPossibleEndpointLocations1() {
        System.out.println("getPossibleEndpointLocations1");
        Paths paths = new Paths();
        
        paths.addEdge(Move.fromString("aas"), Paths.Edge.FIRST, true);
        paths.addEdge(Move.fromString("aas"), Paths.Edge.SECOND, true);
        
        int[] expected = new int[] {0, 0, 0, 1};
        int[] result = paths.getPossibleEndpointLocations(7);
        
        assertArrayEquals(String.format("Expected %s but was %s", Arrays.toString(expected), Arrays.toString(result)), expected, result);
    }
    
    private void check(String location, Paths paths) {
        int move = Move.fromString(location + 's');
        paths.checkPaths(Move.getRow(move), Move.getColumn(move));
    }
}
