package codecup2022;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;

public class TestUtils {
    public static Board parseBoard(String board) {
        String[] rows = board.split("\\n");

        byte[][] grid = new byte[9][7];

        for (byte row = 0; row < 9; row++) {
            for (byte col = 0; col < 7; col++) {
                grid[row][col] = parseTile(rows[row + 1].charAt(col + 1));
            }
        }

        return new ArrayBoard(grid);
    }

    private static byte parseTile(char tile) {
        switch (tile) {
            case ' ':
                return Board.EMPTY;
            case 'S':
                return Board.STRAIGHT;
            case 'L':
                return Board.LEFT;
            case 'R':
                return Board.RIGHT;
            default:
                throw new InternalError("Expected '.', 'S', 'R', or 'L', got '" + tile + "' instead.");
        }
    }

    public static boolean boardsEqual(Board board1, Board board2) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 7; col++) {
                if (board1.get(row, col) != board2.get(row, col)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String board1 =
                " abcdefg\n" +
                "aS R L  \n" +
                "b  L    \n" +
                "cSS     \n" +
                "dL   RS \n" +
                "e  S R  \n" +
                "fR  LR  \n" +
                "gLRSR   \n" +
                "hL    SS\n" +
                "i      S";

        parseBoard(board1).print();
    }
}
