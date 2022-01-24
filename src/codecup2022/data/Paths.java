package codecup2022.data;

import java.util.Arrays;

public class Paths {

    public enum Edge {
        FIRST, // Incident on left side
        SECOND; // Other
    }

    public enum Side {
        TOP, LEFT, BOTTOM, RIGHT;
    }

    private final int[] endpoint = new int[142];
    private final int[] length = new int[142];

    public Paths() {
        for (int i = 0; i < 142; i++) {
            endpoint[i] = i;
        }
    }

    public Paths(Paths paths) {
        System.arraycopy(paths.endpoint, 0, endpoint, 0, 142);
        System.arraycopy(paths.length, 0, length, 0, 142);
    }
    
    public Paths(Board board) {
        this();
        for (int loc = 0; loc < 63; loc++) {
            if (!board.isEmpty(loc)) {
                int move = Move.fromLocationTile(loc, board.get(loc));
                addEdge(move, Edge.FIRST, true);
                addEdge(move, Edge.SECOND, true);
            }
        }
    }

    /**
     * Returns the possible squares where the path starting at midpoint ends.
     * If the path ends at the boundary, this returns null.
     * Otherwise, it returns a length-4 array of [row1, col1, row2, col2].
     *
     * @param midpoint
     * @return
     */
    public int[] getPossibleEndpointLocations(int midpoint) {
        int end = endpoint[midpoint];

        if (onBoundary(end)) {
            return null;
        }

        return getSquares(end);
    }

    public boolean anyPathEndsAtRightSide(final int row, final int col) {
        final int top = 15 * row + col;
        final int left = top + 7;
        final int right = top + 8;
        final int bottom = top + 15;

        return rightBoundary(endpoint[top])
                || rightBoundary(endpoint[left])
                || rightBoundary(endpoint[right])
                || rightBoundary(endpoint[bottom]);
    }

    public boolean couldBeNegative(final int row, final int col) {
        final int top = 15 * row + col;
        final int left = top + 7;
        final int right = top + 8;
        final int bottom = top + 15;

        final int endTop = endpoint[top];
        final int endLeft = endpoint[left];
        final int endRight = endpoint[right];
        final int endBottom = endpoint[bottom];

        if (endTop == left || endTop == right || endTop == bottom
                || endLeft == right || endLeft == bottom
                || endRight == bottom) {
            return true; // possible cycle
        }

        int nLeft = 0;

        if (leftBoundary(endTop)) {
            nLeft++;
        }
        if (leftBoundary(endLeft)) {
            nLeft++;
        }
        if (leftBoundary(endRight)) {
            nLeft++;
        }
        if (leftBoundary(endBottom)) {
            nLeft++;
        }

        if (nLeft > 1) {
            return true; // possible Left-Left connection
        }

        int nRight = 0;

        if (rightBoundary(endTop)) {
            nRight++;
        }
        if (rightBoundary(endLeft)) {
            nRight++;
        }
        if (rightBoundary(endRight)) {
            nRight++;
        }
        if (rightBoundary(endBottom)) {
            nRight++;
        }

        return nRight > 1; // possible Right-Right connection
    }

    private int getMidpoint(final int row, final int col, final Side side) {
        switch (side) {
            case TOP:
                return 15 * row + col;
            case LEFT:
                return 15 * row + col + 7;
            case BOTTOM:
                return 15 * row + col + 15;
            default: // RIGHT
                return 15 * row + col + 8;
        }
    }

    private int[] getSquares(int midpoint) {
        int row = (midpoint - 7) / 15;
        int withinRow = midpoint - 15 * row;

        if (withinRow < 15) { // vertical
            int col = withinRow - 8;

            return new int[]{
                row, col,
                row, col + 1,};
        } else { // horizontal
            int col = withinRow - 15;

            return new int[]{
                row, col,
                row + 1, col
            };
        }
    }

    private boolean leftBoundary(int midpoint) {
        return midpoint % 15 == 7;
    }

    private boolean rightBoundary(int midpoint) {
        return midpoint % 15 == 14;
    }

    private boolean topBoundary(int midpoint) {
        return midpoint < 7;
    }

    private boolean bottomBoundary(int midpoint) {
        return midpoint > 134;
    }

    private boolean onBoundary(int midpoint) {
        return midpoint < 7
                || midpoint > 134
                || midpoint % 15 == 7
                || midpoint % 15 == 14;
    }

    private static final Side[][] FIRST_SIDE_FOR = new Side[][]{
        new Side[]{Side.LEFT, Side.BOTTOM}, // Straight
        new Side[]{Side.LEFT, Side.BOTTOM}, // Left
        new Side[]{Side.LEFT, Side.TOP} // Right
    };

    private static final Side[][] SECOND_SIDE_FOR = new Side[][]{
        new Side[]{Side.RIGHT, Side.TOP}, // Straight
        new Side[]{Side.TOP, Side.RIGHT}, // Left
        new Side[]{Side.BOTTOM, Side.RIGHT} // Right
    };

    final public int addEdge(final int move, final Edge edge, final boolean blue) {
        // Find midpoints involved
        final int row = Move.getRow(move);
        final int col = Move.getColumn(move);
        final int tile = Move.getTile(move);

        final int mid1 = getMidpoint(row, col, FIRST_SIDE_FOR[tile][edge.ordinal()]);
        final int mid2 = getMidpoint(row, col, SECOND_SIDE_FOR[tile][edge.ordinal()]);

        // Find endpoints
        // end1 ----- mid1 <-E-> mid2 ----- end2
        final int end1 = endpoint[mid1];
        final int end2 = endpoint[mid2];

        // Compute score
        if (end1 == mid2) { // Cycle
            return -5;
        }

        final boolean left1 = leftBoundary(end1);
        final boolean left2 = leftBoundary(end2);
        final boolean right1 = rightBoundary(end1);
        final boolean right2 = rightBoundary(end2);

        int score = 0;

        if ((left1 && left2) || (right1 && right2)) {
            score = -3;
        } else if (left1 && right2) {
            score = 1 + (blue ? length[end1] : length[end2]);
        } else if (right1 && left2) {
            score = 1 + (blue ? length[end2] : length[end1]);
        }

        // Update paths
        endpoint[end1] = end2;
        endpoint[end2] = end1;

        final int newLength = length[end1] + length[end2] + 1;
        length[end1] = newLength;
        length[end2] = newLength;

        return score;
    }

    final public int removeEdge(final int move, final Edge edge, final boolean blue) {
        // Find midpoints involved
        final int row = Move.getRow(move);
        final int col = Move.getColumn(move);
        final int tile = Move.getTile(move);

        final int mid1 = getMidpoint(row, col, FIRST_SIDE_FOR[tile][edge.ordinal()]);
        final int mid2 = getMidpoint(row, col, SECOND_SIDE_FOR[tile][edge.ordinal()]);

        // Find endpoints
        // end1 ----- mid1 <-E-> mid2 ----- end2
        final int end1 = endpoint[mid1];
        final int end2 = endpoint[mid2];

        // Update paths
        if (length[end1] == 1) {
            // Isolated edge: mid1 <-E-> mid2
            endpoint[mid1] = mid1;
            endpoint[mid2] = mid2;

            length[mid1] = 0;
            length[mid2] = 0;
        } else if (end1 == end2) {
            // The edge was the last on its path
            if (length[mid1] > length[mid2]) {
                // mid1 <-E-> mid2 ----- end1
                endpoint[end1] = mid2;
                length[end1] = length[mid2];

                endpoint[mid1] = mid1;
                length[mid1] = 0;
            } else {
                // end1 ----- mid1 <-E-> mid2
                endpoint[end1] = mid1;
                length[end1] = length[mid1];

                endpoint[mid2] = mid2;
                length[mid2] = 0;
            }
        } else {
            endpoint[end1] = mid1;
            endpoint[end2] = mid2;

            length[end1] = length[mid1];
            length[end2] = length[mid2];
        }

        // Compute score
        int score = 0;
        final int preEnd1 = endpoint[mid1];
        final int preEnd2 = endpoint[mid2];

        if (preEnd1 == mid2) { // Cycle
            score = 5;
        } else {
            final boolean left1 = leftBoundary(preEnd1);
            final boolean left2 = leftBoundary(preEnd2);
            final boolean right1 = rightBoundary(preEnd1);
            final boolean right2 = rightBoundary(preEnd2);

            if ((left1 && left2) || (right1 && right2)) {
                score = 3;
            } else if (left1 && right2) {
                score = -1 - (blue ? length[preEnd1] : length[preEnd2]);
            } else if (right1 && left2) {
                score = -1 - (blue ? length[preEnd2] : length[preEnd1]);
            }
        }

        return score;
    }

    public void checkPaths(int row, int col) {
        for (Side side : Side.values()) {
            checkPathFrom(getMidpoint(row, col, side));
        }
    }

    public void printDebug() {
        System.err.println("Endpoints:");
        System.err.println(Arrays.toString(endpoint));
        System.err.println("Lengths:");
        System.err.println(Arrays.toString(length));
    }

    private void checkPathFrom(int midpoint) {
        int end = endpoint[midpoint];

        if (endpoint[end] != midpoint) {
            printDebug();
            throw new IllegalStateException(
                    String.format("Incorrect path. Midpoint %d has endpoint %d. Expected midpoint %d to have endpoint %d, but was %d",
                            midpoint, end, end, midpoint, endpoint[end])
            );
        }
        if (length[end] != length[midpoint]) {
            printDebug();
            throw new IllegalStateException(
                    String.format("Length mismatch. Midpoint %d has endpoint %d and length %d, but midpoint %d has length %d",
                            midpoint, end, length[midpoint], end, length[end])
            );
        }
        if (length[end] < 0 || length[end] > 126) {
            printDebug();
            throw new IllegalStateException(
                    String.format("Illegal length value. Midpoint %d and its endpoint %d have length %d",
                            midpoint, end, length[end])
            );
        }
    }
}
