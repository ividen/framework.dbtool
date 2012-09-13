package ru.kwanza.dbtool;

import ru.kwanza.dbtool.QuestionsHelper;
import junit.framework.TestCase;

/**
 * @author Guzanov Alexander
 */
public class TestQuestionHelper extends TestCase {

    public void testQuestions() {
        String questions = QuestionsHelper.getQuestions(1);
        assertEquals(1, questions.length());
        questions = QuestionsHelper.getQuestions(2);
        assertEquals(3, questions.length());
        questions = QuestionsHelper.getQuestions(3);
        assertEquals(7, questions.length());
        questions = QuestionsHelper.getQuestions(4);
        assertEquals(7, questions.length());
        questions = QuestionsHelper.getQuestions(5);
        assertEquals(15, questions.length());
        questions = QuestionsHelper.getQuestions(8);
        assertEquals(15, questions.length());
        questions = QuestionsHelper.getQuestions(15);
        assertEquals(31, questions.length());
        questions = QuestionsHelper.getQuestions(16);
        assertEquals(31, questions.length());
        questions = QuestionsHelper.getQuestions(31);
        assertEquals(63, questions.length());
        questions = QuestionsHelper.getQuestions(32);
        assertEquals(63, questions.length());
        questions = QuestionsHelper.getQuestions(63);
        assertEquals(127, questions.length());
        questions = QuestionsHelper.getQuestions(64);
        assertEquals(127, questions.length());
        questions = QuestionsHelper.getQuestions(127);
        assertEquals(255, questions.length());
        questions = QuestionsHelper.getQuestions(128);
        assertEquals(255, questions.length());
        questions = QuestionsHelper.getQuestions(255);
        assertEquals(511, questions.length());
        questions = QuestionsHelper.getQuestions(256);
        assertEquals(511, questions.length());
        questions = QuestionsHelper.getQuestions(511);
        assertEquals(1023, questions.length());
        questions = QuestionsHelper.getQuestions(512);
        assertEquals(1023, questions.length());
        questions = QuestionsHelper.getQuestions(800);
        assertEquals(1999, questions.length());
        questions = QuestionsHelper.getQuestions(1000);
        assertEquals(1999, questions.length());
        questions = QuestionsHelper.getQuestions(2000);
        assertEquals(1999, questions.length());

    }


    public void testQuestionsCount() {
        assertEquals(1, QuestionsHelper.getCountOfQuestions(1));
        assertEquals(2, QuestionsHelper.getCountOfQuestions(2));
        assertEquals(4, QuestionsHelper.getCountOfQuestions(3));
        assertEquals(4, QuestionsHelper.getCountOfQuestions(4));
        assertEquals(8, QuestionsHelper.getCountOfQuestions(5));
        assertEquals(8, QuestionsHelper.getCountOfQuestions(6));
        assertEquals(8, QuestionsHelper.getCountOfQuestions(7));
        assertEquals(8, QuestionsHelper.getCountOfQuestions(8));
        assertEquals(16, QuestionsHelper.getCountOfQuestions(15));
        assertEquals(16, QuestionsHelper.getCountOfQuestions(16));
        assertEquals(32, QuestionsHelper.getCountOfQuestions(31));
        assertEquals(32, QuestionsHelper.getCountOfQuestions(32));
        assertEquals(64, QuestionsHelper.getCountOfQuestions(61));
        assertEquals(64, QuestionsHelper.getCountOfQuestions(64));
        assertEquals(128, QuestionsHelper.getCountOfQuestions(127));
        assertEquals(128, QuestionsHelper.getCountOfQuestions(128));
        assertEquals(256, QuestionsHelper.getCountOfQuestions(255));
        assertEquals(256, QuestionsHelper.getCountOfQuestions(256));
        assertEquals(512, QuestionsHelper.getCountOfQuestions(257));
        assertEquals(512, QuestionsHelper.getCountOfQuestions(511));
        assertEquals(512, QuestionsHelper.getCountOfQuestions(512));
        assertEquals(1000, QuestionsHelper.getCountOfQuestions(513));
        assertEquals(1000, QuestionsHelper.getCountOfQuestions(800));
        assertEquals(1000, QuestionsHelper.getCountOfQuestions(1000));
        assertEquals(1000, QuestionsHelper.getCountOfQuestions(2000));


    }


}
