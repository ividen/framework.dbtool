package ru.kwanza.dbtool.orm.impl.fetcher;

import ru.kwanza.dbtool.orm.IFetcher;
import ru.kwanza.dbtool.orm.mapping.IEntityMappingRegistry;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alexander Guzanov
 */
public class FetcherImpl implements IFetcher {
    private IEntityMappingRegistry registry;
    // this cache contains all paths, with is used to get relational fields
    private ConcurrentHashMap<PathKey, PathValue> pathCache = new ConcurrentHashMap<PathKey, PathValue>();
    // this cache contains all relations for all entities and queries used to read this relations
    private ConcurrentHashMap<RelationKey, RelationValue> relationCache = new ConcurrentHashMap<RelationKey, RelationValue>();

    private static final String MAIN_CLASS_WITH_INNER_REGEXP = "([a-zA-Z0-9])+";

    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath) {
        PathKey key = new PathKey(entityClass, relationPath);
        PathValue value = pathCache.get(key);

        if (value != null) {

        } else {

        }
    }


    public void scan(String relationPath) {
        char[] chars = relationPath.toCharArray();
        int prev = -1;
        int marker = -1;
        int i;
        String propertyName
        for (i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == ',') {
                if (marker - prev <= 0) {
                    throw new IllegalArgumentException("Path expression is not valid!");
                }
                String propertyName = new String(chars, prev + 1, marker - prev);
                System.out.println(propertyName);
                prev = i;
                marker = i;

            } else if (c == '{') {

            } else if (c == '}') {

            } else if (c == ' ' || c == '\n' || c == '\t') {
                if (marker == prev) {
                    prev++;
                    marker = prev;
                }
            } else {
                marker = i;
            }
        }

        if (marker - prev > 0) {
            String propertyName = new String(chars, prev+1, marker - prev);
        }


    }

    public static void main(String[] args) {
        new FetcherImpl().scan("  a           ,  d   , d,dfddd                 ,   sd");
    }
}
