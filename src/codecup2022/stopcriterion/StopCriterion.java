package codecup2022.stopcriterion;

public interface StopCriterion {
    public abstract void started();
    public abstract boolean shouldStop();
    public abstract String name();
}
