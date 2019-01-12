package test;

import org.junit.Assert;
import org.junit.Test;
import task3.Main;

import java.lang.annotation.Native;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

public class MainTest {
    @Test
    public void calcPythagorasTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int x1 = 1;
        int x2 = 2;
        int y1 = 1;
        int y2 = 1;

        double distance = 1d;
        Main main = new Main();

        Method calcPythagoras = Main.class.getDeclaredMethod("calcPythagoras"
                                                    ,Integer.TYPE, Integer.TYPE
                                                    ,Integer.TYPE, Integer.TYPE);
        calcPythagoras.setAccessible(true);
        double actual = (double) calcPythagoras.invoke(main, x1, y1, x2, y2);

        Assert.assertEquals(actual, distance, 0.00001);
    }

    @Test
    public void calcDistanceBetweenTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int size = 10;
        int [][] cities = new int[size][size];

        int x2 = 0;
        int y2 = 0;
        int x1 = 2;
        int y1 = 5;

        int val1 = 10;
        int val2 = 12;

        cities[x2][y2] = val1;
        cities[x1][y1] = val2;

        Main main = new Main();
        Method calcPythagoras = Main.class.getDeclaredMethod("calcPythagoras"
                ,Integer.TYPE, Integer.TYPE
                ,Integer.TYPE, Integer.TYPE);
        Method calcDistanceBetween = Main.class.getDeclaredMethod("calcDistanceBetween", int [][].class,
                Integer.TYPE, Integer.TYPE);

        calcDistanceBetween.setAccessible(true);
        calcPythagoras.setAccessible(true);


        double expectedDist = (double) calcPythagoras.invoke(main, x1, y1, x2, y2);
        double actualDist   = (double) calcDistanceBetween.invoke(main, cities, val1, val2);

        Assert.assertEquals(actualDist, expectedDist, 0.00001);
    }

    @Test
    public void calculateDistancesTest(){
        int size  = 4;
        int count = 6;

        int [][] cities =              {{1,0,0,3},
                                        {0,0,0,0},
                                        {0,4,0,0},
                                        {2,0,0,0}};
        Main.calculateDistances(cities, size);
        HashMap<Vector<Integer>, Double> distances = Main.getDistances();

        Assert.assertEquals(distances.size(), count);
    }

    private int calcFactorial(int n){
        if(n < 2){
            return n;
        }

        return n * calcFactorial(n - 1);
    }
}
