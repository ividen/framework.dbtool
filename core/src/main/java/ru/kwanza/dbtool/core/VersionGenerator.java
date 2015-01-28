package ru.kwanza.dbtool.core;

/*
 * #%L
 * dbtool-core
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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

    public long generate(String entityName, Long oldVersion) {
        final long currVersion = oldVersion == null ? 0 : oldVersion;
        long value = currVersion;
        while (value == currVersion) {
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
