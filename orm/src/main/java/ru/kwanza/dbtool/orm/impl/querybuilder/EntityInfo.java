package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.IEntityManager;
import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.api.internal.IRelationMapping;
import ru.kwanza.dbtool.orm.impl.fetcher.FetchInfo;
import ru.kwanza.toolbox.fieldhelper.FieldHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class EntityInfo {
    private String alias;
    private Join join;
    private IEntityType entityType;
    private IRelationMapping relationMapping;
    private Map<String, EntityInfo> childs;
    private FetchInfo fetchInfo;

    public static final FieldHelper.Field<EntityInfo, FetchInfo> FETCH_INFO = new FieldHelper.Field<EntityInfo, FetchInfo>() {
        public FetchInfo value(EntityInfo entityInfo) {
            return entityInfo.fetchInfo;
        }
    };

    EntityInfo(IEntityManager em, Join join, IEntityType entityType, String alias, IRelationMapping relationMapping) {
        this.join = join;
        this.alias = alias;
        this.entityType = entityType;
        this.relationMapping = relationMapping;

        if (this.join.getType() == Join.Type.FETCH) {
            fetchInfo = new FetchInfo(em, relationMapping, join.getSubJoins());
        }

    }

    EntityInfo(IEntityType entityType) {
        this.entityType = entityType;
        this.alias = entityType.getTableName();
    }

    IEntityType getEntityType() {
        return entityType;
    }

    boolean isRoot() {
        return join == null;
    }

    Join.Type getJoinType() {
        return join.getType();
    }

    Join getJoin() {
        return join;
    }

    String getAlias() {
        return alias;
    }

    IRelationMapping getRelationMapping() {
        return relationMapping;
    }

    void addChild(String name, EntityInfo relation) {
        if (childs == null) {
            childs = new HashMap<String, EntityInfo>();
        }

        childs.put(name, relation);
    }

    EntityInfo getChild(String propertyName) {
        return childs == null ? null : childs.get(propertyName);
    }

    Map<String, EntityInfo> getAllChilds() {
        return childs;
    }

    boolean hasChilds() {
        return childs != null && !childs.isEmpty();
    }

    public static List<FetchInfo> getFetchInfo(List<EntityInfo> fetchEntities) {
        return FieldHelper.getFieldList(fetchEntities, FETCH_INFO);
    }
}
