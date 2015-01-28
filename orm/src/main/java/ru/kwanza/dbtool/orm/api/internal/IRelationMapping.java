package ru.kwanza.dbtool.orm.api.internal;

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

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.toolbox.fieldhelper.Property;
import ru.kwanza.toolbox.splitter.Splitter;

import java.util.List;

/**
 * Информация о поле, описывающем связь между сущностями
 *
 * @author Alexander Guzanov
 */
public interface IRelationMapping {
    /**
     * Дополнительный список связей
     *
     * @see ru.kwanza.dbtool.orm.annotations.Condition
     * @see ru.kwanza.dbtool.orm.annotations.GroupBy
     * @see ru.kwanza.dbtool.orm.api.Join
     */
    public List<Join> getJoins();

    /**
     * Информация по ключевом поле, по которому описана связь.
     * <p/>
     * Это:
     * <ul>
     * <li>Если связь почемена {@link ru.kwanza.dbtool.orm.annotations.OneToMany} - будет поле сущности помеченное {@link ru.kwanza.dbtool.orm.annotations.IdField}</li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.ManyToOne} - будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.ManyToOne#property()}</li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.Association} - будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.Association#property()}</li>
     * </ul>
     */
    public IFieldMapping getKeyMapping();

    /**
     * Класс связанной сущности
     */
    public Class getRelationClass();

    /**
     * Информация о поле связи связанной сущности
     * <p/>
     * Это:
     * <ul>
     * <li>Если связь почемена {@link ru.kwanza.dbtool.orm.annotations.OneToMany} -будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.OneToMany#relationProperty()} </li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.ManyToOne} - будет поле сущности помеченное {@link ru.kwanza.dbtool.orm.annotations.IdField}</li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.Association} - будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.Association#relationProperty()}</li>
     * </ul>
     */
    public IFieldMapping getRelationKeyMapping();

    /**
     * Имя поля связанной сущности, по которому осущю связь
     * Это:
     * <ul>
     * <li>Если связь почемена {@link ru.kwanza.dbtool.orm.annotations.OneToMany} -будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.OneToMany#relationProperty()} </li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.ManyToOne} - будет поле сущности помеченное {@link ru.kwanza.dbtool.orm.annotations.IdField}</li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.Association} - будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.Association#relationProperty()}</li>
     * </ul>
     */
    public String getRelationKeyMappingName();

    /**
     * Свойство для доступа к полю по которому осуществялется связь
     */
    public Property getKeyProperty();

    /**
     * Свойство связи
     */
    public Property getProperty();

    /**
     * Имя поля сущности по которому осуществляется связь
     * <p/>
     * Это:
     * <ul>
     * <li>Если связь почемена {@link ru.kwanza.dbtool.orm.annotations.OneToMany} - будет поле сущности помеченное {@link ru.kwanza.dbtool.orm.annotations.IdField}</li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.ManyToOne} - будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.ManyToOne#property()}</li>
     * <li>Если связь помечена {@link ru.kwanza.dbtool.orm.annotations.Association} - будет поле описанное в {@link ru.kwanza.dbtool.orm.annotations.Association#property()}</li>
     * </ul>
     */
    public String getKeyMappingName();

    /**
     * Имя связи
     */
    public String getName();

    /**
     * Дополнительное условие на связь
     *
     * @see ru.kwanza.dbtool.orm.api.If
     * @see ru.kwanza.dbtool.orm.annotations.Condition
     */
    public If getCondition();

    /**
     * Группировка
     *
     * @see ru.kwanza.dbtool.orm.annotations.GroupBy
     */
    public Splitter getGroupBy();

    /**
     * Тим группировка
     *
     * @see ru.kwanza.dbtool.orm.annotations.GroupByType
     * @see ru.kwanza.dbtool.orm.annotations.GroupBy
     */
    public GroupByType getGroupByType();

    /**
     * Связь одного объекта или коллекции
     */
    public boolean isCollection();
}
