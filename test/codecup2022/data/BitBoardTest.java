package codecup2022.data;

public class BitBoardTest extends BoardTests {
    @Override
    protected Board initializeBoard(Board board) {
        return new BitBoard(board);
    }
}
