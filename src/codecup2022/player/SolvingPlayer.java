package codecup2022.player;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import java.util.ArrayList;
import java.util.List;

public class SolvingPlayer extends Player {

    private TreeNode root;
    private final Player earlyGamePlayer;
    private final int solveTurn;

    public SolvingPlayer(Player player, int solveTurns) {
        super(player.getName() + "-Sol-" + solveTurns);
        this.earlyGamePlayer = player;
        this.solveTurn = 61 - solveTurns;
    }
    
    @Override
    public void initialize(Board board, boolean blue) {
        super.initialize(new ArrayBoard(board), blue);
        earlyGamePlayer.initialize(board, blue);
    }
    
    /*
    empty spaces : time to solve
    3: 0.0068789 s 0.003843 s 0.0045859 s
    4: 0.0079479 s 0.007815 s 0.0075110 s
    5: 0.0323653 s 0.034497 s 0.0344700 s
    6: 0.2654360 s 0.255340 s 0.2359435 s
    7: 3.0762806 s 2.983034 s 3.0829068 s
    8: OOM and >30s
    */
    
    @Override
    protected int selectMove() {
        if (board.getTurn() < solveTurn) {
            return earlyGamePlayer.selectMove();
        }
        
        // Solve
        if (root == null) {
            root = new TreeNode(TreeNode.NO_MOVE, board);
        }

        // Return best move
        return root.bestMove;
    }

    @Override
    public void processMove(int move) {
        super.processMove(move);
        earlyGamePlayer.processMove(move);
        if (root != null) {
            root = root.getChild(move);
        }
    }
    
    private void printTree(TreeNode node, int indent) {
        System.err.printf("%" + indent + "s(%s (%d) B: %d R: %d -> %s)%n", "", node.move == TreeNode.NO_MOVE ? "---" : Move.toString(node.move), node.move, node.blueScore, node.redScore, node.bestMove == TreeNode.NO_MOVE ? "---" : Move.toString(node.bestMove));
        if (node.children != null) {
            for (TreeNode child : node.children) {
                printTree(child, indent + 2);
            }
        }
    }
    
    private final class TreeNode {
        public static final int NO_MOVE = -1;
        
        final int move; // Move that leads to this position from the parent
        final int bestMove; // The best move in this position
        final List<TreeNode> children; // The nodes cooresponding to the result of playing each legal move from this position
        // TODO: Try a linked list by giving each child node a pointer to the next one?
        final int redScore, blueScore;
        final double averageChildRedScore, averageChildBlueScore;

        public TreeNode(int move, Board board) {
            this.move = move;
            
            if (board.isGameOver()) {
                bestMove = NO_MOVE;
                children = null;
                redScore = board.getScore(false);
                blueScore = board.getScore(true);
                averageChildRedScore = 0;
                averageChildBlueScore = 0;
            } else {
                int[] moves = board.possibleMoves();
                children = new ArrayList<>(moves.length);
                
                TreeNode bestChild = null;
                double bestScore = Double.NEGATIVE_INFINITY;
                double averageRedScore = 0;
                double averageBlueScore = 0;
                
                for (int childMove : moves) {
                    board.applyMove(childMove);
                    TreeNode child = new TreeNode(childMove, board);
                    board.undoMove(childMove);
                    
                    children.add(child);
                    
                    double childRedScore = child.redScore + child.averageChildRedScore / 200;
                    double childBlueScore = child.blueScore + child.averageChildBlueScore / 200;
                    
                    double childScore = board.isCurrentPlayerBlue() ? childBlueScore : childRedScore;
                    if (childScore > bestScore) {
                        bestChild = child;
                        bestScore = childScore;
                    }
                    averageRedScore += childRedScore;
                    averageBlueScore += childBlueScore;
                }
                
                bestMove = bestChild.move;
                redScore = bestChild.redScore;
                blueScore = bestChild.blueScore;
                averageChildRedScore = averageRedScore / moves.length;
                averageChildBlueScore = averageBlueScore / moves.length;
            }
        }
        
        TreeNode getChild(int move) {
            if (children == null) {
                return null;
            }
            
            for (TreeNode child : children) {
                if (child.move == move) {
                    return child;
                }
            }
            
            printTree(root, 1);
            throw new IllegalArgumentException("No child found for move: " + Move.toString(move) + " (" + move + ")");
        }
    }
}
