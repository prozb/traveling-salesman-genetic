package task3;

import java.util.*;

/**
 * @author Pavlo Rozbytskyi
 * @version 2.0.1
 */
public class DNA {
    private int len;
    private double fitness;
    private Integer [] gene;
    private boolean best;

    // fitness will be calculated after creating gene
    public DNA(int len){
        this.len     = len;
        this.fitness = 0;
        initGene();
//        calcFitness();
    }

    public void calcFitness(){
        this.fitness = 0;

        int x = 0;
        int y = 0;

        for(int i = 0; i < gene.length - 1; i++){
            x = gene[i];
            y = gene[i + 1];

            fitness +=  Main.getDistanceBetweenTwoCities(x, y);
        }
    }

    /**
     *  initRate represents percent of all cells set on 1
     */
    private void initGene(){
        this.gene = new Integer[len];

        List<Integer> cities = new ArrayList<>();
        for(int i = 1; i <= gene.length; i++){
            cities.add(i);
        }
        Collections.shuffle(cities);
        Collections.shuffle(cities);

        for(int i = 0; i < gene.length; i++){
            gene[i] = cities.get(i);
        }
    }

    // please unset best gene after each generation
    public void setBest(){
        this.best = true;
    }

    public void unsetBest(){
        this.best = false;
    }

    public boolean isBest(){
        return best;
    }

    /**
     * sets gene and recalculates fitness
     * @param gene gene must be set
     */
    public void setGene(Integer [] gene){
        this.gene = gene;

//        calcFitness();
    }

    public Integer[] getGene() {
        return gene;
    }

    public double getFitness() {
        return fitness;
    }

    /**
     * fitness setter
     * @param fitness new fitness
     */
    public void setFitness(double fitness){
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return Arrays.toString(gene) + "\n";
    }
}