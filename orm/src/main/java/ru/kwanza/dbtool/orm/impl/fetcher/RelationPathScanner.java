package ru.kwanza.dbtool.orm.impl.fetcher;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Alexander Guzanov
 */
class RelationPathScanner {
    private char[] chars;

    RelationPathScanner(String relationPath) {
        this.chars = relationPath.toCharArray();
    }

    public Map<String, Object> scan() {
        LinkedHashMap<String, Object> scanResult = new LinkedHashMap<String, Object>();
        scan(0, scanResult);

        return scanResult;
    }

    private int scan(int from, LinkedHashMap<String, Object> scanResult) {
        int prev = from - 1;
        int marker = from - 1;
        int i;
        String propertyName = null;
        boolean expectedWord = true;
        for (i = from; i < chars.length; i++) {
            char c = chars[i];
            if (c == ',') {
                if (expectedWord == true) {
                    if (marker - prev <= 0) {
                        throw new IllegalArgumentException("Path expression is not valid!");
                    }
                    propertyName = new String(chars, prev + 1, marker - prev);
                    scanResult.put(propertyName, null);
                } else {
                    expectedWord = true;
                }

                prev = i;
                marker = i;
            } else if (c == '{') {
                if (marker - prev <= 0) {
                    throw new IllegalArgumentException("Path expression is not valid!");
                }
                propertyName = new String(chars, prev + 1, marker - prev);
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                scanResult.put(propertyName, map);
                i = marker = prev = scan(i + 1, map);
                expectedWord = false;
            } else if (c == '}') {
                if (expectedWord == true) {
                    if (marker - prev <= 0) {
                        throw new IllegalArgumentException("Path expression is not valid!");
                    }
                    propertyName = new String(chars, prev + 1, marker - prev);
                    scanResult.put(propertyName, null);
                }
                if (from == 0) {
                    throw new IllegalArgumentException("Path expression is not valid!");
                }
                return i;
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
            propertyName = new String(chars, prev + 1, marker - prev);
            scanResult.put(propertyName, null);
        }else if(expectedWord){
            throw new IllegalArgumentException("Path expression is not valid!");
        }

        return i;
    }
}
