package ru.kwanza.dbtool.orm.impl.querybuilder.db.h2;

import ru.kwanza.dbtool.orm.api.IStatement;
import ru.kwanza.dbtool.orm.impl.querybuilder.AbstractQuery;
import ru.kwanza.dbtool.orm.impl.querybuilder.QueryConfig;
import ru.kwanza.dbtool.orm.impl.querybuilder.db.mysql.MySQLStatement;

/**
 * @author Alexander Guzanov
 */
public class H2Query<T> extends AbstractQuery<T> {
    public H2Query(QueryConfig<T> config) {
        super(config);
    }

    @Override
    public IStatement<T> prepare() {
        return new H2Statement<T>(config);
    }

}
