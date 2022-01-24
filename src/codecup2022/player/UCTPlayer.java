package codecup2022.player;

import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.data.RolloutBoard;
import codecup2022.movegenerator.MoveGenerator;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.stopcriterion.StopCriterion;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UCTPlayer extends Player {

    private static final int MAX_ITERATIONS = 200_000;
    
    private final MoveGenerator generator;
    private final StopCriterion stop;
    private final Random rand;
    
    public UCTPlayer(MoveGenerator generator, StopCriterion stop, Random rand) {
        super(String.format("UCT-%s-%s", generator.name(), stop.name()));
        this.generator = generator;
        this.stop = stop;
        this.rand = rand;
    }
    
    private double[] sqrt = null;
    private double[] sqrtLog = null;
    
    @Override
    protected int selectMove() {
        stop.started();
        if (sqrt == null) {
            precomputeSquareRoots();
        }
        
        TreeNode root = new TreeNode(null, -1, board, isBlue());
        int majority = (stop instanceof IterationCount ? ((IterationCount) stop).getMaxIterations() / 2 : MAX_ITERATIONS / 2);
        int maxExpandedTurn = board.getTurn();
        
        for (int i = 0; i < MAX_ITERATIONS && !stop.shouldStop(); i++) {
            TreeNode node = root;
            TreeNode firstMoveNode = null;
            Board b = new RolloutBoard(board);
            
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
        int[] empty = b.emptySpaces();
        Move.shuffle(empty, rand);
        
        for (int i = 0; i < empty.length; i++) {
            b.applyMove(Move.setTile(empty[i], rand.nextInt(3)));
        }
        
        return new int[] { b.getScore(true), b.getScore(false) };
    }

    private void precomputeSquareRoots() {
        sqrt = new double[MAX_ITERATIONS + 1];
        sqrtLog = new double[MAX_ITERATIONS + 1];
        
        for (int i = 0; i <= MAX_ITERATIONS; i++) {
            sqrt[i] = Math.sqrt(i);
            sqrtLog[i] = Math.sqrt(2 * Math.log(i));
        }
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

        private TreeNode(final TreeNode parent, final int move, final Board b, final boolean blueToMove) {
            this.parent = parent;
            this.move = move;
            this.blueToMove = blueToMove;
            this.possibleMoves = generator.generateMoves(b);
            this.unexpandedMoveCount = possibleMoves.length;
        }

        private boolean isLeaf() {
            return possibleMoves.length == 0;
        }

        private boolean isFullyExpanded() {
            return unexpandedMoveCount == 0 && possibleMoves.length > 0;
        }

        private TreeNode expand(final Board b) {
            // Pick a random move
            final int moveIndex = rand.nextInt(unexpandedMoveCount);
            final int childMove = possibleMoves[moveIndex];
            
            // Compact possibleMoves so that the first unexpandedMoveCount moves are still unexpanded
            unexpandedMoveCount--;
            possibleMoves[moveIndex] = possibleMoves[unexpandedMoveCount];
            
            b.applyMove(childMove);
            
            final TreeNode child = new TreeNode(this, childMove, b, !blueToMove);
            children.add(child);
            return child;
        }

        private TreeNode selectMoveNode() {
            // UCB
            TreeNode best = null;
            double bestValue = Double.NEGATIVE_INFINITY;

            final double parentVisitFactor = sqrtLog[visits];

            for (final TreeNode child : children) {
                final double ucbValue = child.isLeaf() ? child.value : child.value + parentVisitFactor / sqrt[child.visits];
                
                if (ucbValue > bestValue) {
                    bestValue = ucbValue;
                    best = child;
                }
            }
            
            return best;
        }
        
        private TreeNode mostVisitedChild() {
            TreeNode mostVisited = null;
            int mostVisits = -1;
            
            for (final TreeNode child : children) {
                if (child.visits > mostVisits) {
                    mostVisited = child;
                    mostVisits = child.visits;
                }
            }
            
            return mostVisited;
        }

        private void updateValue(final int score[]) {
            // This node's value is used by the parent to pick the best move
            // Therefore we should pick the score for our parent's color
            final int nodeScore = blueToMove ? score[1] : score[0];
            
            value = value * visits + nodeScore;
            visits++;
            value /= visits;
        }
    }
}
