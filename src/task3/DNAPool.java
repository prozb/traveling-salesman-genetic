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

        for(int i = 0; i < currentGeneration.length; i++){
            currentGeneration[i] = new DNA(geneLen);
        }

        calcMaxFitnessOfGeneration();
        calcMinFitnessOfGeneration();
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
    private void calcMinFitnessOfGeneration(){
        Optional<DNA> dnaMinFitness = Arrays.stream(currentGeneration).min(Comparator.comparing(DNA::getFitness));
        dnaMinFitness.ifPresent(DNA -> minFitness = DNA.getFitness());

        if(minFitness <= geneLen){
            finished = true;
        }
    }

    public void switchToNextGeneration(){
        calcMinFitnessOfGeneration();
        calcMaxFitnessOfGeneration();
        generationsCount++;

        currentGeneration = nextGeneration;
    }

    // created for testing reasons
    public void processCrossOver(){
        setBestGene();
        crossOverSchema();
        unsetBestGene();
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
                        new1 = greedyCrossOver(dna1, dna2);

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

    private DNA greedyCrossOver(DNA dna1, DNA dna2){
        //available cities for this gene
        ArrayList<Integer> availableCities = getAvailableCities();
        //here are stored next cities to current city to choose from
        ArrayList<Integer> nextCities      = new ArrayList<>();
        Integer [] citiesArray = null;
        Integer [] gene    = new Integer[dna1.getGene().length];

        Integer [] dna1Gene = dna1.getGene();
        Integer [] dna2Gene = dna2.getGene();

        int currentCity    = dna1.getGene()[0];
        int currentPosDna1 = 0;
        int currentPosDna2 = 0;

        currentPosDna2 = Arrays.asList(dna2Gene).indexOf(currentCity);
        //removing first city from available
        availableCities.removeAll(Collections.singleton(currentCity));
        int [] next1;
        int [] next2;

        for(int i = 0; i < gene.length; i++) {
            //adding current city to new dna
            gene[i] = currentCity;
            if(i == gene.length - 1)
                break;
            nextCities.clear();
            //getting 2 next cities from dna1
            next1 = getNextPos(currentPosDna1, gene);
            next2 = getNextPos(currentPosDna2, gene);
            //adding all cities to next cities array list if they are available
            if (availableCities.contains(dna1Gene[next1[0]]))
                nextCities.add(dna1Gene[next1[0]]); //do not add already inserted cities
            if (availableCities.contains(dna1Gene[next1[1]]) && !nextCities.contains(dna1Gene[next1[1]]))
                nextCities.add(dna1Gene[next1[1]]);
            if (availableCities.contains(dna2Gene[next2[0]]) && !nextCities.contains(dna2Gene[next2[0]]))
                nextCities.add(dna2Gene[next2[0]]);
            if (availableCities.contains(dna2Gene[next2[1]]) && !nextCities.contains(dna2Gene[next2[1]]))
                nextCities.add(dna2Gene[next2[1]]);
            //converting next cities array list into array for getCityWithLowestDistance method
            citiesArray = nextCities.toArray(new Integer[nextCities.size()]);
            //get city with lowest distance to current city
            int nextCity = getCityWithLowestDistance(citiesArray, currentCity);

            if (nextCity == -1) {
                //choosing random city from available
                int pos = getRandomPos(availableCities.size());
                nextCity = availableCities.get(pos);
            }
            currentCity = nextCity;

            currentPosDna1 = Arrays.asList(dna1Gene).indexOf(nextCity);
            currentPosDna2 = Arrays.asList(dna2Gene).indexOf(nextCity);

            availableCities.removeAll(Collections.singleton(currentCity));
        }
        gene[gene.length - 1] = currentCity;
        DNA newDna = new DNA(geneLen);
        newDna.setGene(gene);

        return newDna;
    }

    //calculating next position in first dna for greedy crossover
    private int newCityPos(Integer[] cities, int pos, boolean right){
        int nextPos = -1;

        if(right && pos + 1 < cities.length && pos >= 0){
            nextPos = pos + 1;
        }if(right && pos == cities.length - 1 && pos >= 0 && pos < cities.length){
            nextPos = 0;
        }else if (!right && pos - 1 >= 0 && pos < cities.length){
            nextPos = pos - 1;
        }else if(!right && pos == 0 && pos < cities.length){
            nextPos = cities.length - 1;
        }

        return nextPos;
    }
    //figuring out city with lowest distance to current city
    private int getCityWithLowestDistance(Integer[] cities, int currentCity){
        if(cities != null && cities.length > 0) {
            int city = cities[0];
            double dist = getDistance(currentCity, city);

            for (int i = 1; i < cities.length; i++) {
                double nextDist = getDistance(currentCity, cities[i]);
                if (nextDist < dist){
                    city = cities[i];
                    dist = nextDist;
                }
            }
            return city;
        }
        return -1;
    }
    //calculating next position according to current position
    private int [] getNextPos(int pos, Integer [] array){
        int [] next  = new int [2];
        int posArray = 0;

        //handle pos 0
        if(pos == 0 && pos + 1 < array.length){
            next[posArray++] = pos + 1;
            next[posArray]   = array.length - 1;
        //handle pos array.length - 1
        }else if(pos == array.length - 1 && pos - 1 >= 0){
            next[posArray++] = 0;
            next[posArray]   = pos - 1;
        //handle middle point
        }else if (pos >= 0 && pos < array.length){
            next[posArray++] = pos + 1;
            next[posArray]   = pos - 1;
        //incorrect points
        }else {
            next = null;
        }

        return next;
    }
    private DNA[] alternativeCrossOver(DNA dna1, DNA dna2, Integer point1, Integer point2){
        this.alternativeMap.clear();
        this.alternativeMap1.clear();

        Integer [] gene1     = dna1.getGene();
        Integer [] gene2     = dna2.getGene();

        for(int i = --point1; i < point2; i++){
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
            nextGeneration[i] = bestDNAS[getRandomPos(1)];
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
        if(minFitness <= geneLen){
            return true;
        }
        return false;
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