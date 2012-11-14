package ru.kwanza.dbtool.core.util;

/**
 * @author Guzanov Alexander
 */
public final class Skip {
    public static final int SKIPPED = -4;

    int index;
    int count;
    Skip next;

    Skip(int index, int count) {
        this.index = index;
        this.count = count;
    }
}