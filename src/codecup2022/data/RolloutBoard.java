package codecup2022.data;

public class RolloutBoard extends Board {

    private final Paths paths;
    private long empty = Long.MAX_VALUE;

    public RolloutBoard() {
        paths = new Paths();
    }

    public RolloutBoard(byte[][] grid) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                set(Move.location(row, col), grid[row][col]);
            }
        }
        initializeTurn();
        paths = new Paths(this);
    }

    public RolloutBoard(Board board) {
        if (board instanceof RolloutBoard) {
            RolloutBoard rolloutBoard = (RolloutBoard) board;
            this.empty = rolloutBoard.empty;
            paths = new Paths(rolloutBoard.paths);
        } else if (board instanceof BitBoard) {
            BitBoard bitBoard = (BitBoard) board;
            this.empty = bitBoard.empty;
            paths = new Paths(bitBoard.paths);
        } else if (board instanceof ArrayBoard) {
            ArrayBoard arrayBoard = (ArrayBoard) board;
            for (int loc = 0; loc < 63; loc++) {
                set(loc, board.get(loc));
            }
            paths = new Paths(arrayBoard.paths);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty(int loc) {
        return (empty & (1L << loc)) > 0;
    }
    
    private void set(final int loc, final int tile) {
        if (tile != EMPTY) {
            empty &= ~(1L << loc);
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
        throw new UnsupportedOperationException();
    }
    
    private int scoreDelta(int move, boolean blue) {
        return paths.addEdge(move, Paths.Edge.FIRST, blue) + paths.addEdge(move, Paths.Edge.SECOND, blue);
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
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int[] nonNegativeMoves() {
        throw new UnsupportedOperationException();
    }
}
