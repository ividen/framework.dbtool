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

import java.util.Collections;
import java.util.List;

/**
 * @author Guzanov Alexander
 */
public class UpdateException extends Exception {
    private List constrainted;
    private List optimistic;
    private List updated;

    public UpdateException() {
        this(null, null, null, null, null);
    }

    public UpdateException(String message) {
        this(message, null, null, null, null);
    }

    public UpdateException(String message, Throwable cause) {
        this(message, null, null, null, cause);
    }

    public UpdateException(Throwable cause) {
        this(null, null, null, null, cause);
    }

    public UpdateException(String msg, List constrainted, List optimistic, List updated) {
        this(msg, constrainted, optimistic, updated, null);
    }

    public UpdateException(String msg, List constrainted, List optimistic, List updated, Throwable cause) {
        super(msg, cause);
        this.constrainted = constrainted != null ? constrainted : Collections.emptyList();
        this.optimistic = optimistic != null ? optimistic : Collections.emptyList();
        this.updated = updated != null ? updated : Collections.emptyList();
    }

    public <T> List<T> getConstrainted() {
        return constrainted;
    }

    public <T> List<T> getOptimistic() {
        return optimistic;
    }

    public <T> List<T> getUpdated() {
        return updated;
    }

}
