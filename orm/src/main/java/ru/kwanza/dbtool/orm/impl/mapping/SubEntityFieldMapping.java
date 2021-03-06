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

import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IFieldMapping;
import ru.kwanza.toolbox.fieldhelper.Property;

/**
 * @author Alexander Guzanov
 */
public class SubEntityFieldMapping extends AbstractFieldMapping {
    private final String name;
    private final int newOrderNum;
    private final IFieldMapping originalField;
    private final String column;


    public SubEntityFieldMapping(int orderNum,IFieldMapping originalField, String alias) {
        this.newOrderNum = orderNum;
        this.originalField = originalField;
        this.name = this.column = alias;
    }


    @Override
    public Integer getOrderNum() {
        return newOrderNum;
    }

    public String getColumn() {
        return column;
    }

    public String getOriginalColumn() {
        return originalField.getColumn();
    }

    public int getType() {
        return originalField.getType();
    }

    public String getName() {
        return name;
    }

    public Property getProperty() {
        return originalField.getProperty();
    }
}
