package test;

import org.junit.Assert;
import org.junit.Test;
import task3.DNA;
import task3.DNAPool;
import task3.Main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class DNAPoolTest {
    @Test
    public void alternativeCrossOverTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        DNAPool dnaPool = new DNAPool();
        Method method   = dnaPool.getClass().getDeclaredMethod("alternativeCrossOver", DNA.class, DNA.class,
                                                                                     Integer.class, Integer.class);
        method.setAccessible(true);

        DNA dna1 = new DNA(36);
        DNA dna2 = new DNA(36);

        int start = 5;
        int end   = 10;

        method.invoke(dnaPool, dna1, dna2, start, end);

        Field mapField = DNAPool.class.getDeclaredField("alternativeMap");
        mapField.setAccessible(true);

        int expected = end - start + 1;
        int actual   = ((HashMap<Integer, Integer>)(mapField.get(dnaPool))).size();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void alternativeCrossOverTest1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method method   = dnaPool.getClass().getDeclaredMethod("alternativeCrossOver", DNA.class, DNA.class,
                Integer.class, Integer.class);
        method.setAccessible(true);

        DNA dna1 = new DNA(6);
        DNA dna2 = new DNA(6);

        Integer [] gene1 = new Integer[]{4,1,3,2,0,5};
        Integer [] gene2 = new Integer[]{1,2,3,4,5,0};

        dna1.setGene(gene1);
        dna2.setGene(gene2);

        int start = 3;
        int end   = 5;

        DNA [] dnas = (DNA[]) method.invoke(dnaPool, dna1, dna2, start, end);

        Integer [] expected1 = new Integer[] {2,1,3,4,5,0};
        Integer [] expected2 = new Integer[] {1,4,3,2,0,5};

        Assert.assertArrayEquals(expected1, dnas[0].getGene());
        Assert.assertArrayEquals(expected2, dnas[1].getGene());
    }

    @Test
    public void mutateGenesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method method   = dnaPool.getClass().getDeclaredMethod("mutateGenes", DNA.class, Integer.class, Integer.class);
        method.setAccessible(true);

        DNA dna  = new DNA(10);
        int pos1 = 0;
        int pos2 = 3;

        int start1 = dna.getGene()[pos1];
        int start2 = dna.getGene()[pos2];

        method.invoke(dnaPool, dna, pos1, pos2);

        int end1 = dna.getGene()[pos1];
        int end2 = dna.getGene()[pos2];

        Assert.assertEquals(start1, end2);
        Assert.assertEquals(start2, end1);
    }

    @Test
    public void getAvailableCitiesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int geneLen = 36;

        DNAPool dnaPool = new DNAPool();
        dnaPool.setGeneLen(geneLen);

        Method getAvailableCities = dnaPool.getClass().getDeclaredMethod("getAvailableCities");
        getAvailableCities.setAccessible(true);

        ArrayList<Integer> cities = (ArrayList<Integer>) getAvailableCities.invoke(dnaPool);

        Assert.assertEquals(geneLen, cities.size());
    }

    @Test
    public void getNextPosTest_posMiddle_returnCorrect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer [] arr = new Integer[10];

        int pos  = 5;
        int next = 6;
        int prev = 4;

        DNAPool dnaPool = new DNAPool();
        Method getNextPos = DNAPool.class.getDeclaredMethod("getNextPos", Integer.TYPE, Integer [].class);
        getNextPos.setAccessible(true);

        int [] posArray = (int [])getNextPos.invoke(dnaPool, pos, arr);

        Assert.assertEquals(posArray[0], next);
        Assert.assertEquals(posArray[1], prev);
    }

    @Test
    public void getNextPosTest_posEnd_returnCorrect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer [] arr = new Integer[10];

        int pos  = arr.length - 1;
        int next = 0;
        int prev = pos - 1;

        DNAPool dnaPool = new DNAPool();
        Method getNextPos = DNAPool.class.getDeclaredMethod("getNextPos", Integer.TYPE, Integer [].class);
        getNextPos.setAccessible(true);

        int [] posArray = (int [])getNextPos.invoke(dnaPool, pos, arr);

        Assert.assertEquals(posArray[0], next);
        Assert.assertEquals(posArray[1], prev);
    }

    @Test
    public void getNextPosTest_posStart_returnCorrect() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Integer [] arr = new Integer[10];

        int pos  = 0;
        int next = 1;
        int prev = arr.length - 1;

        DNAPool dnaPool = new DNAPool();
        Method getNextPos = DNAPool.class.getDeclaredMethod("getNextPos", Integer.TYPE, Integer [].class);
        getNextPos.setAccessible(true);

        int [] posArray = (int [])getNextPos.invoke(dnaPool, pos, arr);

        Assert.assertEquals(posArray[0], next);
        Assert.assertEquals(posArray[1], prev);
    }

    @Test
    public void getCityWithLowestDistanceTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        int currentCity = 2;
        DNAPool pool    = new DNAPool();

        HashMap<Vector<Integer>, Double> distances = new HashMap<>();
        distances.put(new Vector<>(1,2), 1.0d);
        distances.put(new Vector<>(2,3), 10.0d);
        distances.put(new Vector<>(1,3), 5.0d);
        Main.setDistances(distances);

        Integer [] cities = new Integer[2];
        cities[0] = 1;
        cities[1] = 3;

        Method getCityWithLowestDistance = DNAPool.class.getDeclaredMethod("getCityWithLowestDistance", Integer[].class, Integer.TYPE);
        getCityWithLowestDistance.setAccessible(true);
        int cityActual = (int) getCityWithLowestDistance.invoke(pool, cities, currentCity);
        int expected   = 1;

        Assert.assertEquals(expected, cityActual);
    }

    @Test
    public void newCityPosTest_posZeroRight() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method newCityPos = dnaPool.getClass().getDeclaredMethod("newCityPos", Integer[].class, Integer.TYPE, Boolean.TYPE);
        newCityPos.setAccessible(true);

        Integer[] arr = new Integer[10];
        int pos     = 0;
        int nextPos = 1;

        int expected = (int)newCityPos.invoke(dnaPool, arr, pos, true);

        Assert.assertEquals(expected, nextPos);
    }

    @Test
    public void newCityPosTest_posSizeRight() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method newCityPos = dnaPool.getClass().getDeclaredMethod("newCityPos", Integer[].class, Integer.TYPE, Boolean.TYPE);
        newCityPos.setAccessible(true);

        Integer[] arr = new Integer[10];
        int pos     = arr.length - 1;
        int nextPos = 0;

        int expected = (int)newCityPos.invoke(dnaPool, arr, pos, true);

        Assert.assertEquals(expected, nextPos);
    }

    @Test
    public void newCityPosTest_posSizeLeft() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method newCityPos = dnaPool.getClass().getDeclaredMethod("newCityPos", Integer[].class, Integer.TYPE, Boolean.TYPE);
        newCityPos.setAccessible(true);

        Integer[] arr = new Integer[10];
        int pos     = arr.length - 1;
        int nextPos = pos - 1;

        int expected = (int)newCityPos.invoke(dnaPool, arr, pos, false);

        Assert.assertEquals(expected, nextPos);
    }

    @Test
    public void newCityPosTest_posZeroLeft() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method newCityPos = dnaPool.getClass().getDeclaredMethod("newCityPos", Integer[].class, Integer.TYPE, Boolean.TYPE);
        newCityPos.setAccessible(true);

        Integer[] arr = new Integer[10];
        int pos     = 0;
        int nextPos = arr.length - 1;

        int expected = (int)newCityPos.invoke(dnaPool, arr, pos, false);

        Assert.assertEquals(expected, nextPos);
    }

    @Test
    public void newCityPosTest_incorrectPos() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DNAPool dnaPool = new DNAPool();
        Method newCityPos = dnaPool.getClass().getDeclaredMethod("newCityPos", Integer[].class, Integer.TYPE, Boolean.TYPE);
        newCityPos.setAccessible(true);

        Integer[] arr = new Integer[10];
        int pos     = -1;
        int nextPos = -1;

        int expected = (int)newCityPos.invoke(dnaPool, arr, pos, false);

        Assert.assertEquals(expected, nextPos);
    }
}
