package ru.kwanza.dbtool.core;

/**
 * @author Guzanov Alexander
 */
final class Skip {
    public static final int SKIPPED = -4;

    int index;
    int count;
    Skip next;

    Skip(int index, int count) {
        this.index = index;
        this.count = count;
    }
}
