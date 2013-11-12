package ru.kwanza.dbtool.core;

import java.util.Collections;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public class UpdateException extends Exception {
    private List constrainted;
    private List optimistic;
    private long updateCount;

    public UpdateException() {
        this(null, null, null, 0,null);
    }

    public UpdateException(String message) {
        this(message, null, null, 0,null);
    }

    public UpdateException(String message, Throwable cause) {
        this(message,  null,null,0,cause);
    }

    public UpdateException(Throwable cause) {
        this(null,  null,null,0,cause);
    }

    public UpdateException(String msg, List constrainted, List optimistic, long updateCount) {
        this(msg,constrainted,optimistic,updateCount,null);
    }

    public UpdateException(String msg, List constrainted, List optimistic, long updateCount, Throwable cause) {
        super(msg,cause);
        this.constrainted = constrainted != null ? constrainted : Collections.emptyList();
        this.optimistic = optimistic != null ? optimistic : Collections.emptyList();
        this.updateCount = updateCount;
    }

    public <T> List<T> getConstrainted() {
        return constrainted;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public <T> List<T> getOptimistic() {
        return optimistic;
    }

}
