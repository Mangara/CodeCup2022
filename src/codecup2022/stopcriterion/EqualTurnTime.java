package codecup2022.stopcriterion;

public class EqualTurnTime implements StopCriterion {

    private final double totalSeconds;
    private final long nsPerTurn;

    public EqualTurnTime(double totalSeconds) {
        this.totalSeconds = totalSeconds;
        double secondsPerTurn = totalSeconds / 30; // 61 tiles placed, the last one shouldn't take very long
        this.nsPerTurn = (long) (secondsPerTurn * 1_000_000_000);
    }
    
    private long started;
    
    @Override
    public void started() {
        started = System.nanoTime();
    }

    @Override
    public boolean shouldStop() {
        return System.nanoTime() - started > nsPerTurn;
    }

    @Override
    public String name() {
        return String.format("EqT%.1f", totalSeconds);
    }
    
}
