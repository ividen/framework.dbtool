package ru.kwanza.dbtool.orm.api.internal;

import ru.kwanza.dbtool.orm.annotations.GroupByType;
import ru.kwanza.dbtool.orm.api.If;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.toolbox.fieldhelper.Property;
import ru.kwanza.toolbox.splitter.Splitter;

import java.util.List;

/**
 * @author Alexander Guzanov
 */
public interface IRelationMapping {
    public List<Join> getJoins();

    public IFieldMapping getKeyMapping();

    public Class getRelationClass();

    public IFieldMapping getRelationKeyMapping();

    public String getRelationKeyMappingName();

    public Property getKeyProperty();

    public Property getProperty();

    public String getKeyMappingName();

    public String getName();

    public If getCondition();

    public Splitter getGroupBy();

    public GroupByType getGroupByType();

    public boolean isCollection();
}
