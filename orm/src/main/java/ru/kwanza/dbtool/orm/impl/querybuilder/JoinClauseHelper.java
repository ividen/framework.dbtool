package ru.kwanza.dbtool.orm.impl.querybuilder;

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.RelationPathScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class JoinClauseHelper {

    public static List<Join> parse(String joinClause) {
        final ArrayList<Join> result = new ArrayList<Join>();
        parse(joinClause, result);
        return result;
    }

    public static void parse(String joinClause, List<Join> result) {
        processScanRelations(new RelationPathScanner(joinClause).scan(), result);
    }

    private static void processScanRelations(Map<String, Object> scan, List<Join> result) {
        for (Map.Entry<String, Object> entry : scan.entrySet()) {
            Join[] subJoins = null;
            if (entry.getValue() instanceof Map) {
                ArrayList<Join> list = new ArrayList<Join>();
                processScanRelations((Map<String, Object>) entry.getValue(), list);
                subJoins = list.toArray(new Join[list.size()]);
            }

            if (entry.getKey().startsWith("&")) {
                result.add(Join.left(entry.getKey().substring(1).trim(), subJoins));
            } else if (entry.getKey().startsWith("!")) {
                result.add(Join.inner(entry.getKey().substring(1).trim(), subJoins));
            } else {
                result.add(Join.fetch(entry.getKey(), subJoins));
            }
        }
    }
}
