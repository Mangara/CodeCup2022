package codecup2022.player;

import codecup2022.data.Move;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsolePlayer extends Player {
    private final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    public ConsolePlayer() {
        super("Console");
    }

    @Override
    protected int selectMove() {
        System.out.println("* Select a move:");
        try {
            return Move.fromString(console.readLine());
        } catch (IOException e) {
            e.printStackTrace();
            throw new InternalError(e);
        }
    }
}
