package ru.kwanza.dbtool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Guzanov Alexander
 */
public class VersionGenerator {

    public static final String NODE_ID = "VersionGenerator.nodeId";
    private int nodeId;
    private ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<String, AtomicLong>();

    public VersionGenerator() {
        String property = System.getProperties().getProperty(NODE_ID);
        if (property == null) {
            nodeId = 0;
        } else {
            setNodeId(Integer.valueOf(property));
        }
    }

    public long generate(String entityName, long oldVersion) {
        long value = oldVersion;
        while (value == oldVersion) {
            value = getCounter(entityName).incrementAndGet();
            value = value * 100 + nodeId;
        }

        return value;
    }

    public void setNodeId(int nodeId) {
        if (nodeId >= 100) {
            throw new RuntimeException("NodeId must be between 0 and 99!");
        }
        this.nodeId = nodeId;
    }

    private AtomicLong getCounter(String entityName) {
        AtomicLong atomicLong = counters.get(entityName);
        if (atomicLong == null) {
            atomicLong = new AtomicLong(0);
            counters.putIfAbsent(entityName, atomicLong);
            atomicLong = counters.get(entityName);
        }
        return atomicLong;
    }
}
