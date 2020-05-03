package com.newage.plantedaqua;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public void checkCalendarMonths(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR,11,31,12,0,0);
        calendar.add(Calendar.DATE,1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        assertEquals(1,day);
        assertEquals(0,month);
    }
}