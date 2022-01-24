package codecup2022.player;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

public abstract class Player {

    public static boolean TIMING = false;
    public static boolean DEBUG = false;
    public static boolean SCORE = false;
    
    protected final String name;
    
    protected Board board;
    protected boolean blue; 

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isBlue() {
        return blue;
    }

    public Board getBoard() {
        return board;
    }

    public void play(BufferedReader in, PrintStream out) throws IOException {
        long start = 0;

        if (TIMING) {
            start = System.currentTimeMillis();
        }

        initialize(false);

        if (TIMING) {
            System.err.printf("TI:%dms%n", System.currentTimeMillis() - start);
        }

        playOpening(in, out);

        for (String input = in.readLine(); !(input == null || "Quit".equals(input)); input = in.readLine()) {
            if (TIMING) {
                start = System.currentTimeMillis();
            }

            processMove(Move.fromString(input));
            
            if (SCORE) {
                System.err.printf("%s-B:%dR:%d%n", input, board.getScore(true), board.getScore(false));
            }

            int move = move();

            if (TIMING) {
                System.err.printf("T%d:%dms%n", board.getTurn() - 1, System.currentTimeMillis() - start);
            }
            
            if (SCORE) {
                System.err.printf("%s-B:%dR:%d%n", Move.toString(move), board.getScore(true), board.getScore(false));
            }

            out.println(Move.toString(move));
        }
    }

    private void playOpening(BufferedReader in, PrintStream out) throws IOException {
        long start = 0;
        if (TIMING) {
            start = System.currentTimeMillis();
        }
        
        // Read two jury moves
        processMove(Move.fromString(in.readLine()));
        processMove(Move.fromString(in.readLine()));
        
        if (TIMING) {
            System.err.printf("Processing jury moves took %d ms.%n", System.currentTimeMillis() - start);
            start = System.currentTimeMillis();
        }
        
        // Play
        String input = in.readLine();
        if ("Start".equals(input)) {
            blue = true;
        } else {
            blue = false;
            processMove(Move.fromString(input));
        }
        
        int move = move();

        if (TIMING) {
            System.err.printf("First move took %d ms.%n", System.currentTimeMillis() - start);
        }

        out.println(Move.toString(move));
    }

    public void initialize(boolean blue) {
        initialize(new ArrayBoard(), blue);
    }

    public void initialize(Board board, boolean blue) {
        this.board = board;
        this.blue = blue;
    }

    public void processMove(int move) {
        board.applyMove(move);
    }

    public int move() {
        int move = selectMove();
        processMove(move);
        return move;
    }

    protected abstract int selectMove();
}
