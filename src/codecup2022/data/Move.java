package codecup2022.data;

import java.util.List;
import java.util.Random;

public class Move {

    /*
     * The grid is 9 (rows) x 7 (columns), with rows labelled a through i and columns labelled a through g.
     * This gives 63 possible location, which fit into 6 bits. Moves are given row-column, so "ic".
     * There are 3 tiles, which require 2 bits.
     *
     * Internally, moves are represented by a single 8-bit integer:
     *        00_000000
     *      tile location
     *
     * The 6 lowest bits denote the location (0=aa, 1=ab, 2=ac, ..., 62=ig).
     * The 2 highest bits denote the tile (0=straight, 1=left, 2=right)
     */
    private static final int LOCATION_MASK = 0b00_111111;
    private static final int TILE_MASK     = 0b11_000000;
    
    private Move() {
        // Not instantiable
    }

    public static final int location(final int row, final int column) {
        return 7 * row + column;
    }
    
    public static final int row(final int location) {
        return location / 7;
    }
    
    public static final int column(final int location) {
        return location % 7;
    }
    
    public static final int getLocation(final int move) {
        return move & LOCATION_MASK;
    }
    
    public static final int getRow(final int move) {
        return row(getLocation(move));
    }

    public static final int getColumn(final int move) {
        return column(getLocation(move));
    }

    public static final int getTile(final int move) {
        return (move & TILE_MASK) >> 6;
    }
    
    public static final int setTile(int move, int tile) {
        return (move & ~TILE_MASK) | (tile << 6);
    }
    
    public static final int fromLocationTile(final int location, final int tile) {
        return (tile << 6) | location;
    }
    
    public static final int fromLocation(final int location) {
        return location;
    }
    
    public static final int fromRowColumnTile(final int row, final int column, final int tile) {
        return fromLocationTile(location(row, column), tile);
    }
    
    private static final char[] TILE_CHARS = new char[]{'s', 'l', 'r'};
    
    public static String toString(int move) {
        char row = (char) (getRow(move) + 'a');
        char col = (char) (getColumn(move) + 'a');
        char tile = TILE_CHARS[getTile(move)];
        return new StringBuilder().append(row).append(col).append(tile).toString();
    }

    public static int fromString(String move) {
        int row = move.charAt(0) - 'a';
        int col = move.charAt(1) - 'a';
        int tile = tileFromChar(move.charAt(2));
        return fromRowColumnTile(row, col, tile);
    }

    private static int tileFromChar(char tile) {
        switch (tile) {
            case 's':
                return Board.STRAIGHT;
            case 'l':
                return Board.LEFT;
            default:
                return Board.RIGHT;
        }
    }

    public static int[] arrayFromList(List<Integer> moves) {
        int[] result = new int[moves.size()];
        int i = 0;
        for (int move : moves) {
            result[i] = move;
            i++;
        }
        return result;
    }
    
    public static void shuffle(int[] moves, Random rand) {
        for (int i = moves.length - 1; i > 0; i--) {
            int newIndex = rand.nextInt(i + 1);
            int temp = moves[newIndex];
            moves[newIndex] = moves[i];
            moves[i] = temp;
        }
    }
}
