package test;

import org.junit.Assert;
import org.junit.Test;
import task3.DNA;
import task3.DNAPool;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

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
    public void alternativeCrossOverTest1() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
}
