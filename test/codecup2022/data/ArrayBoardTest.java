package codecup2022.data;

public class ArrayBoardTest extends BoardTests {
    @Override
    protected Board initializeBoard(Board board) {
        return new ArrayBoard(board);
    }
}
