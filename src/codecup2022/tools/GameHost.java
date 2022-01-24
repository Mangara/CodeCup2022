package codecup2022.tools;

import codecup2022.data.ArrayBoard;
import codecup2022.data.BitBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.player.Player;

import java.util.Random;

public class GameHost {
    public static int[] runGame(Player p1, Player p2, boolean print) {
        return runGame(p1, p2, new Random(), print);
    }
    
    public static int[] runGame(Player p1, Player p2, Random juryRand, boolean print) {
        Board board = new ArrayBoard();
        
        p1.initialize(new BitBoard(board), true);
        p2.initialize(new BitBoard(board), false);

        if (print) {
            System.err.printf("GAME: %s (Player 1) vs %s (Player 2)%n", p1.getName(), p2.getName());
        }

        runOpening(p1, p2, board, juryRand, print);
        runMidGame(p1, p2, board, print);

        int[] scores = getScores(board);

        if (print) {
            System.err.printf("GAME: The game ended with scores of %d vs %d%n", scores[0], scores[1]);

            if (scores[0] > scores[1]) {
                System.err.println("GAME: PLAYER 1 WINS!");
            } else if (scores[0] < scores[1]) {
                System.err.println("GAME: PLAYER 2 WINS!");
            } else {
                System.err.println("GAME: THE GAME IS A TIE!");
            }
        }

        return scores;
    }

    private static void runOpening(Player p1, Player p2, Board board, Random rand, boolean print) {
        if (print) {
            System.err.println("GAME: Generating jury moves");
        }

        int[] juryMoves = generateJuryMoves(rand);

        if (print) {
            System.err.printf("GAME: Jury moves: %s, %s. Checking%n", Move.toString(juryMoves[0]), Move.toString(juryMoves[1]));
        }

        verifyMove(board, juryMoves[0], p1, true);
        board.applyMove(juryMoves[0]);
        verifyMove(board, juryMoves[1], p1, true);
        board.applyMove(juryMoves[1]);

        if (print) {
            board.print();
            System.err.println("GAME: Sending jury moves to player 1");
        }

        p1.processMove(juryMoves[0]);
        p1.processMove(juryMoves[1]);
        
        if (print) {
            board.print();
            System.err.println("GAME: Sending jury moves to player 2");
        }

        p2.processMove(juryMoves[0]);
        p2.processMove(juryMoves[1]);
    }

    private static void runMidGame(Player p1, Player p2, Board board, boolean print) {
        Player currentPlayer = p1;

        while (!board.isGameOver()) {
            if (print) {
                System.err.printf("GAME (%d): Asking player %d for a move%n", board.getTurn(), currentPlayer == p1 ? 1 : 2);
            }

            int move = currentPlayer.move();

            if (print) {
                System.err.printf("GAME (%d): Player %d returned move: %s. Checking%n", board.getTurn(), currentPlayer == p1 ? 1 : 2, Move.toString(move));
            }

            verifyMove(board, move, currentPlayer, currentPlayer == p1);
            board.applyMove(move);

            if (print) {
                board.print();
                System.err.printf("GAME (%d): Sending move to player %d%n", board.getTurn(), currentPlayer == p1 ? 2 : 1);
            }

            currentPlayer = (currentPlayer == p1 ? p2 : p1);
            currentPlayer.processMove(move);
        }
    }

    private static void verifyMove(Board board, int move, Player currentPlayer, boolean isP1) {
        int row = Move.getRow(move);
        int col = Move.getColumn(move);

        if (!board.isEmpty(row, col)) {
            System.err.printf("Current player: %s as %s%n", currentPlayer.getName(), isP1 ? "P1" : "P2");
            System.err.println("Board:");
            board.print();
            System.err.println("Move: " + Move.toString(move));
            throw new IllegalArgumentException("Tried to play on a non-empty intersection.");
        }
    }
    
    private static int[] generateJuryMoves(Random rand) {
        int row1 = rand.nextInt(9);
        int col1 = rand.nextInt(7);
        
        int row2, col2;
        do {
            row2 = rand.nextInt(9);
            col2 = rand.nextInt(7);
        } while (row2 == row1 && col2 == col1);
        
        int tile1 = rand.nextInt(3);
        int tile2 = rand.nextInt(3);
        
        return new int[] { Move.fromRowColumnTile(row1, col1, tile1), Move.fromRowColumnTile(row2, col2, tile2) };
    }

    private static int[] getScores(Board board) {
        return new int[] {board.getScore(true), board.getScore(false)};
    }
}
