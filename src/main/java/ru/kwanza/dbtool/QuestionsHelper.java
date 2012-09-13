package ru.kwanza.dbtool;

/**
 * @author Guzanov Alexander
 */
abstract class QuestionsHelper {
    static final int MAX_SELECT_IN = 1000;

    /**
     * 1,2,4,8,16,32,64,128,256,512,1000
     */

    private static final String[] questions = new String[11];

    static {
        int last = 1;
        int prev = 0;
        StringBuffer question = new StringBuffer("");
        for (int i = 0; i < questions.length; i++) {
            for (int j = prev; j < last; j++) {
                question.append("?,");
            }

            questions[i] = question.substring(0, question.length() - 1);
            prev = last;
            last *= 2;
            if (last > MAX_SELECT_IN) {
                last = MAX_SELECT_IN;
            }
        }

    }


    public static String getQuestions(int count) {
        if (count > MAX_SELECT_IN) {
            return questions[questions.length - 1];
        }

        return questions[getIndexOfNears(count)];
    }

    public static int getCountOfQuestions(int count) {
        int result = nearestPowerOfTwo(count);
        if (result > MAX_SELECT_IN) {
            return MAX_SELECT_IN;
        }

        return result;
    }

    private static int nearestPowerOfTwo(int v) {
        v--;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v++;
        return v;
    }


    private static int getIndexOfNears(int x) {
        int i = nearestPowerOfTwo(x);
        return Integer.numberOfTrailingZeros(i);
    }
}
