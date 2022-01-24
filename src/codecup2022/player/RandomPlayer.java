package codecup2022.player;

import codecup2022.movegenerator.MoveGenerator;

import java.util.Random;

public class RandomPlayer extends Player {

    private final Random rand;
    private final MoveGenerator generator;

    public RandomPlayer(MoveGenerator generator) {
        this(generator, new Random());
    }

    public RandomPlayer(MoveGenerator generator, Random rand) {
        super("Random-" + generator.name());
        this.generator = generator;
        this.rand = rand;
    }

    @Override
    protected int selectMove() {
        int[] moves = generator.generateMoves(board);
        return moves[rand.nextInt(moves.length)];
    }
}
