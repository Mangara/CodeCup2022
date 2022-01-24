package codecup2022.data;

public abstract class Board {

    public static final byte EMPTY = -1;
    public static final byte STRAIGHT = 0;
    public static final byte LEFT = 1;
    public static final byte RIGHT = 2;

    private int turn = -2;
    private int scoreBlue = 50;
    private int scoreRed = 50;

    public abstract byte get(int loc);
    
    public byte get(int row, int col) {
        return get(Move.location(row, col));
    }
    
    public boolean isEmpty(int loc) {
        return get(loc) == EMPTY;
    }
    
    public boolean isEmpty(int row, int col) {
        return isEmpty(Move.location(row, col));
    }
    
    protected abstract int applyRegularMove(int move);

    protected abstract int undoRegularMove(int move);

    public int getTurn() {
        return turn;
    }

    public int getNumFreeSpaces() {
        return 61 - turn; // 63 - 2 (jury) - turn
    }

    public boolean isGameOver() {
        return turn == 61; // getNumFreeSpaces() == 0
    }

    public boolean isLegalMove(int move) {
        return isEmpty(Move.getLocation(move));
    }

    public void applyMove(int move) {
        updateScore(applyRegularMove(move));
        turn++;
    }

    public void undoMove(int move) {
        turn--;
        updateScore(undoRegularMove(move));
    }
    
    private void updateScore(int scoreDelta) {
        if (isCurrentPlayerBlue()) {
            scoreBlue += scoreDelta;
        } else {
            scoreRed += scoreDelta;
        }
    }

    public int getScore(boolean blue) {
        return blue ? scoreBlue : scoreRed;
    }

    public boolean isCurrentPlayerBlue() {
        return turn % 2 == 0;
    }
    
    public int scoreAfterMove(int move, boolean blue) {
        applyMove(move);
        int score = getScore(blue);
        undoMove(move);
        return score;
    }
    
    protected void initialize(Board board) {
        turn = board.getTurn();
        scoreBlue = board.getScore(true);
        scoreRed = board.getScore(false);
    }

    protected void initializeTurn() {
        int nTiles = 0;

        for (int loc = 0; loc < 63; loc++) {
            if (!isEmpty(loc)) {
                nTiles++;
            }
        }

        turn = nTiles - 2;
    }

    private static final int LEFT_TILE = Move.fromLocationTile(0, LEFT);
    private static final int RIGHT_TILE = Move.fromLocationTile(0, RIGHT);
    
    /**
     * Returns a list of moves by the given player that includes each empty
     * space with each possible tile.
     *
     * @return
     */
    public int[] possibleMoves() {
        int[] moves = new int[3 * getNumFreeSpaces()];
        int index = 0;

        for (int loc = 0; loc < 63; loc++) {
            if (isEmpty(loc)) {
                moves[index] = loc;
                moves[index + 1] = loc | LEFT_TILE;
                moves[index + 2] = loc | RIGHT_TILE;
                index += 3;
            }
        }

        return moves;
    }

    /**
     * Returns a list of moves by the given player that includes each empty
     * space with the STRAIGHT tile.
     *
     * @return
     */
    public int[] emptySpaces() {
        int[] moves = new int[getNumFreeSpaces()];
        int index = 0;

        for (int loc = 0; loc < 63; loc++) {
            if (isEmpty(loc)) {
                moves[index] = loc;
                index++;
            }
        }

        return moves;
    }

    /**
     * @return Returns all moves in squares that could connect the left and right sides of the board.
     */
    public abstract int[] connectingMoves();
    
    /**
     * @return Returns all moves in squares that could never create a cycle or same-side connection.
     */
    public abstract int[] nonNegativeMoves();
    
    public void print() {
        System.err.println(" abcdefg");

        for (int row = 0; row < 9; row++) {
            System.err.print((char) (row + 'a'));
            for (int col = 0; col < 7; col++) {
                System.err.print(charForValue(get(row, col)));
            }
            System.err.println();
        }
        System.err.printf("B: %3d R: %3d%n", scoreBlue, scoreRed);
    }
    
    private char charForValue(int boardValue) {
        switch (boardValue) {
            case EMPTY:
                return ' ';
            case Board.STRAIGHT:
                return 'S';
            case Board.LEFT:
                return 'L';
            case Board.RIGHT:
                return 'R';
            default:
                throw new IllegalArgumentException();
        }
    }

    public void printMoves() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                if (!isEmpty(row, col)) {
                    System.err.println(Move.toString(Move.fromRowColumnTile(row, col, get(row, col))));
                }
            }
        }
    }
}
