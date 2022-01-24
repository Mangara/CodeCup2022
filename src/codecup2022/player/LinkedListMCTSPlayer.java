package codecup2022.player;

import codecup2022.data.ArrayBoard;
import codecup2022.data.Board;
import codecup2022.data.Move;
import codecup2022.movegenerator.MoveGenerator;
import codecup2022.stopcriterion.IterationCount;
import codecup2022.stopcriterion.StopCriterion;
import java.util.Random;

public class LinkedListMCTSPlayer extends Player {

    private static final int MAX_ITERATIONS = 200_000;
    
    private final MoveGenerator generator;
    private final StopCriterion stop;
    private final Random rand;
    private final double[] epsilon;
    
    public LinkedListMCTSPlayer(MoveGenerator generator, StopCriterion stop, double epsilonStart, double epsilonEnd, Random rand) {
        super(String.format("LLMCTS-%s-%s-%.2f-%.2f", generator.name(), stop.name(), epsilonStart, epsilonEnd));
        this.generator = generator;
        this.stop = stop;
        this.rand = rand;
        
        this.epsilon = new double[MAX_ITERATIONS];
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            epsilon[i] = epsilonEnd + epsilonStart / Math.sqrt(1 + i / 60.0);
        }
    }
    
    @Override
    protected int selectMove() {
        stop.started();
        TreeNode root = new TreeNode(null, null, -1, board, isBlue());
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
        int[] empty = b.emptySpaces();
        Move.shuffle(empty, rand);
        
        for (int i = 0; i < empty.length; i++) {
            b.applyMove(Move.setTile(empty[i], rand.nextInt(3)));
        }
        
        return new int[] { b.getScore(true), b.getScore(false) };
    }
    
    private class TreeNode {
        final TreeNode parent;
        final TreeNode nextChild;
        final int move;
        final boolean blueToMove;
        final int[] possibleMoves;
        
        int unexpandedMoveCount;
        int visits = 0;
        double value = 0;
        TreeNode firstChild = null;

        private TreeNode(TreeNode parent, TreeNode nextChild, int move, Board b, boolean blueToMove) {
            this.parent = parent;
            this.nextChild = nextChild;
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

        private TreeNode expand(Board b) {
            // Pick a random move
            int moveIndex = rand.nextInt(unexpandedMoveCount);
            int childMove = possibleMoves[moveIndex];
            
            // Compact possibleMoves so that the first unexpandedMoveCount moves are still unexpanded
            unexpandedMoveCount--;
            possibleMoves[moveIndex] = possibleMoves[unexpandedMoveCount];
            
            b.applyMove(childMove);
            
            TreeNode child = new TreeNode(this, firstChild, childMove, b, !blueToMove);
            firstChild = child;
            
            return child;
        }

        private TreeNode selectMoveNode() {
            // Epsilon-greedy
            if (rand.nextDouble() < epsilon[visits]) {
                return randomChild();
            } else {
                return bestChild();
            }
        }
        
        private TreeNode randomChild() {
            TreeNode child = firstChild;
            for (int i = rand.nextInt(possibleMoves.length); i > 0; i--) {
                child = child.nextChild;
            }
            return child;
        }

        private TreeNode bestChild() {
            TreeNode best = null;
            double bestValue = -1;
            
            for (TreeNode child = firstChild; child != null; child = child.nextChild) {
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
            
            for (TreeNode child = firstChild; child != null; child = child.nextChild) {
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
