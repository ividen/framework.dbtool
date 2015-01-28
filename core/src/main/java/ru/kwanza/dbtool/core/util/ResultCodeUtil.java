package ru.kwanza.dbtool.core.util;

/*
 * #%L
 * dbtool-core
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

import java.util.Arrays;

/**
 * @author Guzanov Alexander
 */
public class ResultCodeUtil {
    public static Object processResult(int[] resultCode, Skip skip, int totalCount) {
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
