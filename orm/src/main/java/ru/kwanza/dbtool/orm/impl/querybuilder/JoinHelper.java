package ru.kwanza.dbtool.orm.impl.querybuilder;

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

import ru.kwanza.dbtool.orm.api.Join;
import ru.kwanza.dbtool.orm.impl.RelationPathScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
public class JoinHelper {

    public static List<Join> parse(String join) {
        final ArrayList<Join> result = new ArrayList<Join>();
        parse(join, result);
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
