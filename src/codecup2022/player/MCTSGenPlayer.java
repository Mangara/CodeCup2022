package codecup2022.player;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.movegenerator.MoveGenerator;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.stopcriterion.StopCriterion;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTSGenPlayer extends Player {

    private static final int MAX_ITERATIONS = 200_000;
    
    private final MoveGenerator mainGenerator;
    private final MoveGenerator rolloutGenerator;
    private final StopCriterion stop;
    private final double epsilon;
    private final Random rand;
    
    public MCTSGenPlayer(MoveGenerator mainGenerator, MoveGenerator rolloutGenerator, StopCriterion stop, double epsilon, Random rand) {
        super(String.format("MCTSGen-%s-%s-%s-%.2f", mainGenerator.name(), rolloutGenerator.name(), stop.name(), epsilon));
        this.mainGenerator = mainGenerator;
        this.rolloutGenerator = rolloutGenerator;
        this.stop = stop;
        this.epsilon = epsilon;
        this.rand = rand;
    }
    
    @Override
    protected int selectMove() {
        stop.started();
        TreeNode root = new TreeNode(null, -1, board, isBlue());
        int majority = (stop instanceof IterationCount ? ((IterationCount) stop).getMaxIterations() / 2 : MAX_ITERATIONS / 2);
        int maxExpandedTurn = board.getTurn();
        
        for (int i = 0; i < MAX_ITERATIONS && !stop.shouldStop(); i++) {
            TreeNode node = root;
            TreeNode firstMoveNode = null;
            Board b = new ArrayBoard(board);
            
            // Traverse the tree
            while (node.isFullyExpanded()) {
                node = node.selectMoveNode();
                b.applyMove(node.move);
                
                if (firstMoveNode == null) {
                    firstMoveNode = node;
                }
            }
            
            // Expand
            if (!node.isLeaf()) {
                node = node.expand(b);
            }
            
            maxExpandedTurn = Math.max(maxExpandedTurn, b.getTurn());
            
            // Play the rest of the game randomly
            int[] score = rollout(b);
            
            // Update nodes along the path we followed
            while (node != null) {
                node.updateValue(score);
                node = node.parent;
            }
            
            // No improvement possible?
            if (firstMoveNode != null && firstMoveNode.visits >= majority) {
                break;
            }
        }
        
        TreeNode result = root.mostVisitedChild();
        
        if (Player.SCORE) {
            System.err.printf("%s (%.2f, %d) D=%d%n", Move.toString(result.move), result.value, result.visits, maxExpandedTurn);
        }
        
        return result.move;
    }
    
    private int[] rollout(Board b) {
        while (!b.isGameOver()) {
            final int[] possibleMoves = rolloutGenerator.generateMoves(b);
            b.applyMove(possibleMoves[rand.nextInt(possibleMoves.length)]);
        }
        
        return new int[] { b.getScore(true), b.getScore(false) };
    }
    
    private class TreeNode {
        final TreeNode parent;
        final int move;
        final boolean blueToMove;
        final int[] possibleMoves;
        final List<TreeNode> children = new ArrayList<>();
        
        int unexpandedMoveCount;
        int visits = 0;
        double value = 0;

        private TreeNode(TreeNode parent, int move, Board b, boolean blueToMove) {
            this.parent = parent;
            this.move = move;
            this.blueToMove = blueToMove;
            this.possibleMoves = mainGenerator.generateMoves(b);
            this.unexpandedMoveCount = possibleMoves.length;
        }

        private boolean isLeaf() {
            return possibleMoves.length == 0;
        }

        private boolean isFullyExpanded() {
            return unexpandedMoveCount == 0 && possibleMoves.length > 0;
        }

        private TreeNode expand(Board b) {
            // Pick a random move
            int moveIndex = rand.nextInt(unexpandedMoveCount);
            int childMove = possibleMoves[moveIndex];
            
            // Compact possibleMoves so that the first unexpandedMoveCount moves are still unexpanded
            unexpandedMoveCount--;
            possibleMoves[moveIndex] = possibleMoves[unexpandedMoveCount];
            
            b.applyMove(childMove);
            
            TreeNode child = new TreeNode(this, childMove, b, !blueToMove);
            children.add(child);
            return child;
        }

        private TreeNode selectMoveNode() {
            // Epsilon-greedy
            if (rand.nextDouble() < epsilon) {
                return randomChild();
            } else {
                return bestChild();
            }
        }
        
        private TreeNode randomChild() {
            return children.get(rand.nextInt(children.size()));
        }

        private TreeNode bestChild() {
            TreeNode best = null;
            double bestValue = -1;
            
            for (TreeNode child : children) {
                if (child.value > bestValue) {
                    best = child;
                    bestValue = child.value;
                }
            }
            
            return best;
        }
        
        private TreeNode mostVisitedChild() {
            TreeNode mostVisited = null;
            int mostVisits = -1;
            
            for (TreeNode child : children) {
                if (child.visits > mostVisits) {
                    mostVisited = child;
                    mostVisits = child.visits;
                }
            }
            
            return mostVisited;
        }

        private void updateValue(int score[]) {
            // This node's value is used by the parent to pick the best move
            // Therefore we should pick the score for our parent's color
            int nodeScore = blueToMove ? score[1] : score[0];
            
            value = value * visits + nodeScore;
            visits++;
            value /= visits;
        }
    }
}
