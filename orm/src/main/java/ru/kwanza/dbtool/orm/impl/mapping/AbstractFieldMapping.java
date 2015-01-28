package ru.kwanza.dbtool.orm.impl.mapping;

/*
 * #%L
 * dbtool-orm
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

import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;

/**
 * @author Alexander Guzanov
 */
public abstract class AbstractFieldMapping implements IFieldMapping {
    private Integer orderNum;

    public Integer getOrderNum() {
        return orderNum;
    }

    void setOrderNum(int orderNum) {
        if (this.orderNum != null) {
            throw new IllegalStateException("Field  name=" + this.getName()
                    + ", column=" + this.getName() + " already belongs to other entity!");
        }
        this.orderNum = orderNum;
    }
}
