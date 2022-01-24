package codecup2022.player;

import codecup2022.movegenerator.MoveGenerator;

public class MaximaxPlayer extends Player {

    private final MoveGenerator generator;
    private final int maxDepth;

    public MaximaxPlayer(MoveGenerator generator, int maxDepth) {
        super(String.format("Maximax-%s-%d", generator.name(), maxDepth));
        this.generator = generator;
        this.maxDepth = maxDepth;
    }

    @Override
    protected int selectMove() {
        int[] bestMoveAndScore = selectMove(maxDepth);
        return bestMoveAndScore[0];
    }

    private int[] selectMove(int depthLeft) {
        if (depthLeft == 0 || board.isGameOver()) {
            return new int[]{-1, board.getScore(true), board.getScore(false)};
        }

        int[] bestMove = new int[]{-1, Integer.MIN_VALUE, Integer.MIN_VALUE};
        int bestScore = Integer.MIN_VALUE;
        int[] moves = generator.generateMoves(board);

        for (int move : moves) {
            board.applyMove(move);
            int[] moveResult = selectMove(depthLeft - 1);
            board.undoMove(move);

            int score = board.isCurrentPlayerBlue() ? moveResult[1] : moveResult[2];

            if (score > bestScore) {
                bestMove = moveResult;
                bestMove[0] = move;
                bestScore = score;
            }
        }

        return bestMove;
    }

}
