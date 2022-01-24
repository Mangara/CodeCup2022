package codecup2022.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayBoard extends Board {

    final byte[] board = new byte[63];
    final Paths paths;

    public ArrayBoard() {
        Arrays.fill(board, EMPTY);
        paths = new Paths();
    }

    public ArrayBoard(byte[][] grid) {
        for (int i = 0; i < 9; i++) {
            System.arraycopy(grid[i], 0, this.board, 7 * i, 7);
        }
        initializeTurn();
        paths = new Paths(this);
    }

    public ArrayBoard(Board board) {
        if (board instanceof ArrayBoard) {
            ArrayBoard arrayBoard = (ArrayBoard) board;
            System.arraycopy(arrayBoard.board, 0, this.board, 0, 63);
            paths = new Paths(arrayBoard.paths);
        } else {
            for (int loc = 0; loc < 63; loc++) {
                this.board[loc] = board.get(loc);
            }
            paths = new Paths(board);
        }
        
        initialize(board);
    }

    @Override
    public byte get(int loc) {
        return board[loc];
    }

    @Override
    protected int applyRegularMove(int move) {
        int loc = Move.getLocation(move);
        board[loc] = (byte) Move.getTile(move);
        return scoreDelta(move, isCurrentPlayerBlue());
    }

    @Override
    protected int undoRegularMove(int move) {
        int loc = Move.getLocation(move);
        board[loc] = EMPTY;
        return undoScoreDelta(move, isCurrentPlayerBlue());
    }
    
    private int scoreDelta(int move, boolean blue) {
        return paths.addEdge(move, Paths.Edge.FIRST, blue) + paths.addEdge(move, Paths.Edge.SECOND, blue);
    }
    
    private int undoScoreDelta(int move, boolean blue) {
        return paths.removeEdge(move, Paths.Edge.SECOND, blue) + paths.removeEdge(move, Paths.Edge.FIRST, blue);
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
