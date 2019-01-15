package test;

import org.junit.Assert;
import org.junit.Test;
import task3.DNA;
import task3.Main;
import task3.TextProcessor;

import java.util.Arrays;
import java.util.HashSet;

public class DNATest {
    @Test
    public void initGeneTest(){
        int dnaLen = 36;
        DNA dna1   = new DNA(dnaLen);

        Integer [] gene         = dna1.getGene();
        HashSet<Integer> cities = new HashSet<>(Arrays.asList(gene));

        Assert.assertEquals(cities.size(), dnaLen);
    }

    @Test
    public void test(){

    }
}
