package ru.kwanza.dbtool.core;

import java.util.Arrays;

/**
 * @author Guzanov Alexander
 */
class ResultCodeUtil {
    static Object processResult(int[] resultCode, Skip skip, int totalCount) {
        if (skip == null) {
            return resultCode;
        }
        int[] result = new int[totalCount];
        if (resultCode == null) {
            Arrays.fill(result, Skip.SKIPPED);
            return result;
        }
        int r = 0;
        int i = 0;
        for (; (i < result.length); i++) {
            if ((skip == null || r < skip.index) && (r < resultCode.length)) {
                result[i] = resultCode[r];
                r++;
            } else if (skip != null && r >= skip.index) {
                result[i] = Skip.SKIPPED;
                skip.count--;
                if (skip.count <= 0) {
                    skip = skip.next;
                }
            } else {
                break;
            }
        }

        if (i < result.length) {
            int[] cutResult = new int[i];
            System.arraycopy(result, 0, cutResult, 0, i);
            return cutResult;
        }

        return result;
    }
}
