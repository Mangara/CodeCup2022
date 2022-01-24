package codecup2022.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BitBoard extends Board {

    final Paths paths;
    long empty = Long.MAX_VALUE;
    long straight = 0;
    long left = 0;
    long right = 0;

    public BitBoard() {
        paths = new Paths();
    }

    public BitBoard(byte[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                set(Move.location(row, col), grid[row][col]);
            }
        }
        initializeTurn();
        paths = new Paths(this);
    }

    public BitBoard(Board board) {
        if (board instanceof BitBoard) {
            BitBoard bitBoard = (BitBoard) board;
            this.empty = bitBoard.empty;
            this.straight = bitBoard.straight;
            this.left = bitBoard.left;
            this.right = bitBoard.right;
            paths = new Paths(bitBoard.paths);
        } else {
            for (int loc = 0; loc < 63; loc++) {
                set(loc, board.get(loc));
            }
            paths = new Paths(board);
        }
        
        initialize(board);
    }

    @Override
    public byte get(final int loc) {
        final long locBit = 1L << loc;
//        System.err.printf("Empty:    %s%n", debugString(empty));
//        System.err.printf("Straight: %s%n", debugString(straight));
//        System.err.printf("Left:     %s%n", debugString(left));
//        System.err.printf("Right:    %s%n", debugString(right));
//        System.err.printf("LocBit:   %s%n", debugString(locBit));
//        System.err.printf("LB&E:     %s%n", debugString(empty & locBit));
//        System.err.printf("LB&S:     %s%n", debugString(straight & locBit));
//        System.err.printf("LB&L:     %s%n", debugString(left & locBit));
//        System.err.printf("LB&R:     %s%n", debugString(right & locBit));
        if ((empty & locBit) > 0) {
            return EMPTY;
        }
        if ((straight & locBit) > 0) {
            return STRAIGHT;
        }
        if ((left & locBit) > 0) {
            return LEFT;
        }
        return RIGHT;
    }

    @Override
    public boolean isEmpty(int loc) {
        return (empty & (1L << loc)) > 0;
    }
    
    private String debugString(long state) {
        StringBuilder sb = new StringBuilder(64);
        sb.append(Long.toString(state, 2));
        while (sb.length() < 64) {
            sb.insert(0, '0');
        }
        return sb.toString();
    }
    
    private void set(final int loc, final int tile) {
        final long locBit = 1L << loc;
        switch (tile) {
            case EMPTY:
                empty |= locBit;
                straight &= ~locBit;
                left &= ~locBit;
                right &= ~locBit;
                break;
            case STRAIGHT:
                empty &= ~locBit;
                straight |= locBit;
                left &= ~locBit;
                right &= ~locBit;
                break;
            case LEFT:
                empty &= ~locBit;
                straight &= ~locBit;
                left |= locBit;
                right &= ~locBit;
                break;
            default:
                empty &= ~locBit;
                straight &= ~locBit;
                left &= ~locBit;
                right |= locBit;
                break;
        }
    }

    @Override
    protected int applyRegularMove(int move) {
        int loc = Move.getLocation(move);
        set(loc, Move.getTile(move));
        return scoreDelta(move, isCurrentPlayerBlue());
    }

    @Override
    protected int undoRegularMove(int move) {
        int loc = Move.getLocation(move);
        set(loc, EMPTY);
        return undoScoreDelta(move, isCurrentPlayerBlue());
    }
    
    private int scoreDelta(int move, boolean blue) {
        return paths.addEdge(move, Paths.Edge.FIRST, blue) + paths.addEdge(move, Paths.Edge.SECOND, blue);
    }
    
    private int undoScoreDelta(int move, boolean blue) {
        return paths.removeEdge(move, Paths.Edge.SECOND, blue) + paths.removeEdge(move, Paths.Edge.FIRST, blue);
    }

    @Override
    public int[] emptySpaces() {
        final int n = getNumFreeSpaces();
        final int[] locations = new int[n];
        long tempEmpty = empty;
        
        for (int i = 0; i < n; i++) {
            locations[i] = Long.numberOfTrailingZeros(tempEmpty);
            tempEmpty &= ~Long.lowestOneBit(tempEmpty);
        }
        
        return locations;
    }
    
    private static final int LEFT_TILE = Move.fromLocationTile(0, LEFT);
    private static final int RIGHT_TILE = Move.fromLocationTile(0, RIGHT);
    
    @Override
    public int[] possibleMoves() {
        final int n = 3 * getNumFreeSpaces();
        final int[] moves = new int[n];
        long tempEmpty = empty;
        
        for (int i = 0; i < n; i += 3) {
            int loc = Long.numberOfTrailingZeros(tempEmpty);
            moves[i] = loc;
            moves[i + 1] = loc | LEFT_TILE;
            moves[i + 2] = loc | RIGHT_TILE;
            tempEmpty &= ~Long.lowestOneBit(tempEmpty);
        }
        
        return moves;
    }
    
    @Override
    public int[] connectingMoves() {
        // For each left side endpoint a, find the square where the path starting at a ends
        // For all other endpoints b around that square, if the path starting at b ends at the right side, add all moves for this square
        
        int[] leftSideMidpoints = new int[] {7, 22, 37, 52, 67, 82, 97, 112, 127};
        List<Integer> connectingMoves = new ArrayList<>();
        
        for (int left : leftSideMidpoints) {
            int[] endSquares = paths.getPossibleEndpointLocations(left);
            
            if (endSquares == null) {
                continue; // Path leads to boundary
            }
            
            int row, col;
            
            if (isEmpty(endSquares[0], endSquares[1])) {
                row = endSquares[0];
                col = endSquares[1];
            } else {
                row = endSquares[2];
                col = endSquares[3];
            }
            
            if (paths.anyPathEndsAtRightSide(row, col)) {
                connectingMoves.add(Move.fromRowColumnTile(row, col, Board.STRAIGHT));
                connectingMoves.add(Move.fromRowColumnTile(row, col, Board.LEFT));
                connectingMoves.add(Move.fromRowColumnTile(row, col, Board.RIGHT));
            }
        }
        
        return Move.arrayFromList(connectingMoves);
    }
    
    @Override
    public int[] nonNegativeMoves() {
        int[] moves = new int[3 * getNumFreeSpaces()];
        int index = 0;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                if (isEmpty(row, col) && !paths.couldBeNegative(row, col)) {
                    moves[index] = Move.fromRowColumnTile(row, col, Board.STRAIGHT);
                    moves[index + 1] = Move.fromRowColumnTile(row, col, Board.LEFT);
                    moves[index + 2] = Move.fromRowColumnTile(row, col, Board.RIGHT);
                    index += 3;
                }
            }
        }

        return Arrays.copyOf(moves, index);
    }
}
