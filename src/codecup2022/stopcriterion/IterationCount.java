package codecup2022.stopcriterion;

public class IterationCount implements StopCriterion {

    private final int maxIterations;

    public IterationCount(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public int getMaxIterations() {
        return maxIterations;
    }
    
    private int iteration = 0;
    
    @Override
    public void started() {
        iteration = 0;
    }

    @Override
    public boolean shouldStop() {
        return iteration++ > maxIterations;
    }

    @Override
    public String name() {
        return "I" + maxIterations;
    }
    
}
