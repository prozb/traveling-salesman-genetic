package task3;

import org.mockito.internal.util.collections.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Pavlo Rozbytskyi
 * @version 2.0.1
 */
// after each generation loop you must recalculate fitness off all dna's and
// figure out maximal and minimal fitness
public class DNAPool {
    private static String TAG = "DNAPool";

    private HashMap<Integer, Integer> alternativeMap;
    private HashMap<Integer, Integer> alternativeMap1;
    private DNA[] currentGeneration;
    private DNA[] nextGeneration;
    private double maxFitness;
    private double minFitness;
    private int generationsCount;
    private int generationLen;
    private int geneLen;
    private float mutationRate;
    private float recombinationRate;
    private int replicationSchema;
    private int crossOverSchema;
    private boolean finished;
    private boolean protect;
    private int [] cities;

    private double minFitnessGeneral; //will be printed at the end of the simulation

    public DNAPool(){
        this.alternativeMap    = new HashMap<>();
        this.alternativeMap1    = new HashMap<>();
    }

    // calculating fitness after each loop and after creating new generation
    public DNAPool(int generationLen, int geneLen, int initRate, float mutationRate, int replicationSchema, int crossOverSchema, float recombinationRate, boolean protect){
        this.replicationSchema = replicationSchema;
        this.crossOverSchema   = crossOverSchema;
        this.recombinationRate = recombinationRate;
        this.alternativeMap    = new HashMap<>();
        this.alternativeMap1   = new HashMap<>();
        this.minFitnessGeneral = Double.MAX_VALUE;

        this.finished      = false;
        this.mutationRate  = mutationRate;
        this.generationLen = generationLen;
        // protecting best gene from mutation and cross over
        this.protect       = protect;
        //possible cities
        this.cities        = new int[geneLen];

        for(int i = 0; i < cities.length; i++){
            cities[i] = i;
        }
        createGenerations(generationLen, geneLen, initRate);
    }

    private void createGenerations(int generationLen, int geneLen, int initRate){
        this.currentGeneration = new DNA[generationLen];
        this.nextGeneration    = new DNA[generationLen];
        this.geneLen           = geneLen;
        this.generationsCount  = 0;

        for(int i = 0; i < currentGeneration.length; i++) {
            currentGeneration[i] = new DNA(geneLen);
        }
    }

    /**
     * calculating max fitness of gen
     */
    private void calcMaxFitnessOfGeneration(){
        Optional<DNA> dnaMaxFitness = Arrays.stream(currentGeneration).max(Comparator.comparing(DNA::getFitness));
        dnaMaxFitness.ifPresent(DNA -> maxFitness = DNA.getFitness());
    }

    /**
     * calculating min fitness of gen
     */
    public void calcMinFitnessOfGeneration(){
        Optional<DNA> dnaMinFitness = Arrays.stream(currentGeneration).min(Comparator.comparing(DNA::getFitness));
        dnaMinFitness.ifPresent(DNA -> minFitness = DNA.getFitness());

        if(minFitnessGeneral <= 50){
            finished = true;
            minFitnessGeneral = minFitness;
        }

        if(minFitness < minFitnessGeneral)
            minFitnessGeneral = minFitness;
    }

    public double getMinFitnessGeneral(){
        return minFitnessGeneral;
    }

    public void calculateFitness(){
        Arrays.stream(currentGeneration).forEach(DNA::calcFitness);
    }
    public void switchToNextGeneration(){
        calcMinFitnessOfGeneration();
        calcMaxFitnessOfGeneration();
        generationsCount++;
    }

    // created for testing reasons
    public void processCrossOver(){
        setBestGene();
        crossOverSchema();
        unsetBestGene();

        currentGeneration = nextGeneration;

        calculateFitness();
        calcMinFitnessOfGeneration();
        calcMaxFitnessOfGeneration();
    }

    public void crossOverSchema(){
        int crossOverCount = (int) (recombinationRate * generationLen);
        int crossOverPerf  = 0;
        int firstGenePos   = 0;
        int secondGenePos  = 0;
        int nextGenPos     = 0;

        int bestPos = getBestGenePos();

        do {
            firstGenePos  = (int) (Math.random() * generationLen);
            secondGenePos = (int) (Math.random() * generationLen);

            int firsRandPos = getRandomPos();
            int secRandPos  = getRandomPos();

            if(!(firstGenePos == bestPos || secondGenePos == bestPos)){
                DNA dna1 = new DNA(geneLen);
                DNA dna2 = new DNA(geneLen);

                DNA new1 = null;
                DNA new2 = null;

                switch (crossOverSchema){
                    case 3: {
                        new1 = greedyCrossOver2(currentGeneration[firstGenePos], currentGeneration[secondGenePos]);

                        nextGeneration[nextGenPos++] = new1;
                    }
                    break;
                    case 4: {
                        if(firsRandPos > secondGenePos){
                            int c       = secRandPos;
                            secRandPos  = firsRandPos;
                            firsRandPos = c;
                        }

                        DNA [] dnas = alternativeCrossOver(dna1, dna2, firsRandPos, secRandPos);
                        new1 = Objects.requireNonNull(dnas)[0];
                        new2 = Objects.requireNonNull(dnas[1]);

                        nextGeneration[nextGenPos++] = new1;
                        nextGeneration[nextGenPos++] = new2;
                    }
                    break;
                    default: {
                        Main.printError("Choose cross over schema",TAG);
                    }
                }

                crossOverCount--;
                crossOverPerf++;
            }
        }while (crossOverCount > 0);

        //fill left genes with random genes
        for(int i = nextGenPos; i < nextGeneration.length; i++){
            nextGeneration[i] = currentGeneration[getRandomPos(currentGeneration.length - 1)];
        }

        currentGeneration = nextGeneration;
        assert (int)(currentGeneration.length * recombinationRate) == crossOverPerf;
    }

    private double getDistance(int city1, int city2){
        return Main.getDistanceBetweenTwoCities(city1, city2);
    }

    private int getRandomPos(int count){
        return (int)(Math.random() * count);
    }

    private int getRandomPos(){
        return getRandomPos(geneLen);
    }

    private DNA greedyCrossOver2(DNA dna1, DNA dna2){
        //available cities for this gene
        ArrayList<Integer> availableCities = getAvailableCities();

        Integer [] gene1 = dna1.getGene();
        Integer [] gene2 = dna2.getGene();
        //save new cities in this array
        Integer [] geneN = new Integer[gene1.length];

        //getting random city from first path
        int currentCity = gene1[0];
        availableCities.remove(Integer.valueOf(currentCity));
        //removing current city from available cities
        geneN[0] = currentCity;

        for(int i = 1; i < geneN.length; i++){
            int posCurrentInGene1 = Arrays.asList(gene1).indexOf(currentCity);
            int posCurrentInGene2 = Arrays.asList(gene2).indexOf(currentCity);

            int nextPosGene1 = getNextPosition(geneLen, posCurrentInGene1);
            int nextPosGene2 = getNextPosition(geneLen, posCurrentInGene2);

            if( availableCities.contains(gene1[nextPosGene1]) &&
                availableCities.contains(gene2[nextPosGene2])){
                currentCity = getCityWithLowestDistance1(currentCity, gene1[nextPosGene1], gene2[nextPosGene2]);
            }else if(availableCities.contains(gene1[nextPosGene1])){
                currentCity = gene1[nextPosGene1];
            }else if(availableCities.contains(gene2[nextPosGene2])){
                currentCity = gene2[nextPosGene2];
            }else{
                currentCity = availableCities.get(getRandomPos(availableCities.size()));
            }
            availableCities.remove(Integer.valueOf(currentCity));
            geneN[i] = currentCity;
        }
        //new generated after crossover dna
        DNA dnaNew = new DNA(gene1.length);
        dnaNew.setGene(geneN);

        return dnaNew;
    }

    private int getCityWithLowestDistance1(int currentCity, int city1, int city2){
        double cityCity1 = Main.getDistanceBetweenTwoCities(currentCity, city1);
        double cityCity2 = Main.getDistanceBetweenTwoCities(currentCity, city2);

        if(cityCity1 < cityCity2)
            return city1;
        else
            return city2;
    }

    private int getNextPosition(int arrayLen, int currentPos){
        int ret = 0;

        if(currentPos > -1 && currentPos < arrayLen){
            if(currentPos + 1 >= arrayLen){
                ret = 0;
            }else{
                ret = currentPos + 1;
            }
        }else {
            Main.printError("current position is out of bounds", TAG);
        }

        return ret;
    }

    private DNA[] alternativeCrossOver(DNA dna1, DNA dna2, Integer point1, Integer point2){
        this.alternativeMap.clear();
        this.alternativeMap1.clear();

        Integer [] gene1     = dna1.getGene();
        Integer [] gene2     = dna2.getGene();

        for(int i = point1; i < point2; i++){
            alternativeMap.put(gene1[i], gene2[i]);
            alternativeMap1.put(gene2[i], gene1[i]);
        }

        for(int i = 0; i < gene1.length; i++){
            if(alternativeMap.containsKey(gene1[i])){
                gene1[i] = alternativeMap.get(gene1[i]);
            }else if(alternativeMap1.containsKey(gene1[i])){
                gene1[i] = alternativeMap1.get(gene1[i]);
            }

            if(alternativeMap1.containsKey(gene2[i])){
                gene2[i] = alternativeMap1.get(gene2[i]);
            }else if(alternativeMap.containsKey(gene2[i])){
                gene2[i] = alternativeMap.get(gene2[i]);
            }
        }

        DNA [] dnas = new DNA[2];
        DNA new1    = new DNA(dna1.getGene().length);
        DNA new2    = new DNA(dna1.getGene().length);

        new1.setGene(gene1);
        new2.setGene(gene2);
        dnas[0] = new1;
        dnas[1] = new2;

        return dnas;
    }

    public void sortGeneration(){
        Arrays.sort(currentGeneration, Comparator.comparing(DNA::getFitness));
    }

    public void processMutation(){
        setBestGene();

        int mutationCount     = (int) (mutationRate * geneLen * generationLen) + 1;
        int mutationPerformed = 0;

        int randGen  = 0;
        int randPos  = 0;
        int randPos1 = 0;

        int bestGenePos = getBestGenePos();

        do{
            randGen = (int) (Math.random() * generationLen);

            if(randGen != bestGenePos) {
                randPos  = getRandomPos();
                randPos1 = getRandomPos();

                mutateGenes(currentGeneration[randGen], randPos, randPos1);

                mutationCount--;
                mutationPerformed++;
            }
        }while (mutationCount > 0);

        // testing reasons
        if((int) (mutationRate * geneLen * generationLen) != --mutationPerformed){
            throw new RuntimeException();
        }

        unsetBestGene();
        calcMaxFitnessOfGeneration();
        calcMinFitnessOfGeneration();
    }

    private void mutateGenes(DNA dna, Integer pos1, Integer pos2){
        int c = dna.getGene()[pos1];
        dna.getGene()[pos1] = dna.getGene()[pos2];
        dna.getGene()[pos2] = c;
    }

    public void processReplication(){
        switch(replicationSchema){
            case 1:
                replicationSchemaOne();
                break;
            default:
                throw new RuntimeException("please input replication schema");
        }

        calcMinFitnessOfGeneration();
        calcMaxFitnessOfGeneration();
    }

    private void replicationSchemaOne(){
        DNA[] bestDNAS = getBestTwoGenes();

        for(int i = 0; i < nextGeneration.length / 2; i++){
            nextGeneration[i] = bestDNAS[getRandomPos(2)];
        }

        for(int i = nextGeneration.length / 2; i < nextGeneration.length; i++){
            nextGeneration[i] = currentGeneration[getRandomPos(currentGeneration.length - 1)];
        }
    }

    public int getBestGenePos(){
        Optional<DNA> bestOpt = Arrays.stream(currentGeneration).filter(DNA::isBest).findFirst();
        int bestPos = 0;
        if(bestOpt.isPresent()) {
            bestPos = Arrays.asList(currentGeneration).indexOf(bestOpt.get());
        }
        return bestPos;
    }

    /**
     * selecting best two genes from the generation
     * @return returns two genes
     */
    public DNA[] getBestTwoGenes(){
        int selectCount = 2;

        DNA [] dnas = new DNA[selectCount];
        dnas[0]     = currentGeneration[0];
        //find next best gene not equal to first
        for(int i = 1; i < currentGeneration.length; i++){
            if(currentGeneration[i].getFitness() != dnas[0].getFitness()){
                dnas[1] = currentGeneration[i];
                break;
            }
        }
        return dnas;
    }

    public int getGenerationsCount(){
        return generationsCount;
    }

    public double getMaxFitness(){
        return maxFitness;
    }

    public double getMinFitness() {
        return minFitness;
    }

    public DNA[] getGeneration(){
        return currentGeneration;
    }

    public boolean isFinished(){
        return finished;
    }

    public void printInfo(){
        System.out.println("max fitness: " + maxFitness + " min fitness: " + minFitness +
                " gen number: " + generationsCount + " \n---------------------------------------------------");
    }

    public void printGeneration(){
        System.out.println("---------------------------------------------------------");
        System.out.println(Arrays.toString(currentGeneration));
        System.out.println("---------------------------------------------------------");
    }

    public void setBestGene(){
        if(protect) {
            Optional<DNA> bestGene = Arrays.stream(currentGeneration).max(Comparator.comparing(DNA::getFitness));
            bestGene.ifPresent(DNA::setBest);
        }
    }

    public void unsetBestGene(){
        if(protect) {
            Optional<DNA> bestGene = Arrays.stream(currentGeneration).max(Comparator.comparing(DNA::isBest));
            bestGene.ifPresent(DNA::unsetBest);
        }
    }

    public void setGeneLen(int geneLen){
        this.geneLen = geneLen;
    }

    private ArrayList<Integer> getAvailableCities(){
        ArrayList <Integer> cities = new ArrayList<>();
        for(int i = 1; i <= geneLen; i++){
            cities.add(i);
        }
        return cities;
    }

    /**
     * current generation setters
     * @param generation generation to set
     */
    public void setCurrentGeneration(DNA [] generation){
        this.currentGeneration = generation;
    }
}