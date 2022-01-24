package codecup2022.movegenerator;

import codecup2022.data.Board;

public class ScoreSign extends MoveGenerator {

    @Override
    public String name() {
        return "ScoreSign";
    }

    private enum Sign {
        POSITIVE, NEUTRAL, NEGATIVE;

        static Sign fromScoreDiff(int scoreDiff) {
            if (scoreDiff == 0) {
                return NEUTRAL;
            } else if (scoreDiff < 0) {
                return NEGATIVE;
            } else {
                return POSITIVE;
            }
        }
        
        boolean isBetter(Sign other) {
            switch (this) {
                case POSITIVE:
                    return false;
                case NEUTRAL:
                    return other == POSITIVE;
                case NEGATIVE:
                    return other != NEGATIVE;
                default: // Impossible
                    return false;
            }
        }
    }

    @Override
    public int[] generateMoves(Board board) {
        boolean blue = board.isCurrentPlayerBlue();
        int[] moves = board.possibleMoves();
        Sign[] signs = new Sign[moves.length];
        Sign maxSign = Sign.NEGATIVE;
        int maxSignMoves = 0;

        final int scoreBefore = board.getScore(blue);
        
        for (int i = 0; i < moves.length; i++) {
            signs[i] = Sign.fromScoreDiff(board.scoreAfterMove(moves[i], blue) - scoreBefore);
            
            if (maxSign.isBetter(signs[i])) {
                maxSign = signs[i];
                maxSignMoves = 1;
            } else if (maxSign == signs[i]) {
                maxSignMoves++;
            }
        }
        
        int[] result = new int[maxSignMoves];
        int resultIndex = 0;
        
        for (int i = 0; i < moves.length; i++) {
            if (signs[i] == maxSign) {
                result[resultIndex] = moves[i];
                resultIndex++;
            }
        }
        
        return result;
    }
}
