package ru.kwanza.dbtool;


import java.util.List;

/**
 * @author Guzanov Alexander
 */
public class UpdateException extends Exception {
    private List constrainted;
    private List optimistic;
    private long updateCount;

    public UpdateException() {
    }

    public UpdateException(String message) {
        super(message);
    }

    public UpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateException(Throwable cause) {
        super(cause);
    }

    public UpdateException(List constrainted, List optimistic, long updateCount) {
        this.constrainted = constrainted;
        this.optimistic = optimistic;
        this.updateCount = updateCount;
    }

    public <T> List<T> getConstrainted() {
        return constrainted;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    void setConstrainted(List constrainted) {
        this.constrainted = constrainted;
    }

    public <T> List<T> getOptimistic() {
        return optimistic;
    }

    void setOptimistic(List optimistic) {
        this.optimistic = optimistic;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }
}
