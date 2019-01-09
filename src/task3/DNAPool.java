package task3;

import java.util.*;

/**
 * @author Pavlo Rozbytskyi
 * @version 2.0.1
 */
// after each generation loop you must recalculate fitness off all dna's and
// figure out maximal and minimal fitness
public class DNAPool {
    private HashMap<Integer, Integer> alternativeMap;
    private HashMap<Integer, Integer> alternativeMap1;
    private DNA[] currentGeneration;
    private DNA[] nextGeneration;
    private int maxFitness;
    private int minFitness;
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

        if(maxFitness == geneLen) {
            finished = true;
        }
    }

    /**
     * calculating min fitness of gen
     */
    private void calcMinFitnessOfGeneration(){
        Optional<DNA> dnaMinFitness = Arrays.stream(currentGeneration).min(Comparator.comparing(DNA::getFitness));
        dnaMinFitness.ifPresent(DNA -> minFitness = DNA.getFitness());
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
                        new2 = new DNA(36);

                        new2.setGene(Objects.requireNonNull(new1).getGene());
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
                    }
                    break;
                    default: {
                        Main.logger.error("Chose cross over schema");
                        Main.printError("Chose cross over schema");
                    }
                }

                nextGeneration[nextGenPos++] = new1;
                nextGeneration[nextGenPos++] = new2;

                crossOverCount--;
                crossOverPerf++;
            }
        }while (crossOverCount > 0);

        //fill left genes with random genes
        for(int i = nextGenPos; i < nextGeneration.length; i++){
            nextGeneration[i] = currentGeneration[getRandomPos()];
        }
        currentGeneration = nextGeneration;
        assert (int)(currentGeneration.length * recombinationRate) == crossOverPerf;
    }

    private int getDistance(int city1, int city2){
        // TODO: 09.01.19 implement distance calculation
        return 0;
    }

    private int getRandomPos(){
        return (int)(Math.random() * geneLen);
    }

    private DNA greedyCrossOver(DNA dna1, DNA dna2){
        // TODO: 09.01.19 implement greedy cross over
        return null;
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
        // TODO: 09.01.19 change mutation method
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

    private void clearRankings(){
        Arrays.stream(currentGeneration).forEach(DNA::clearPs);
    }

    private void processRanking(){
        currentGeneration[0].calcProbability(0, generationLen);

        for(int rank = 1; rank < currentGeneration.length; rank++){
            currentGeneration[rank].calcProbability(rank, generationLen);
            double prev = currentGeneration[rank - 1].getPsCum();
            currentGeneration[rank].calcCumulProbability(prev);
        }
    }

    private void replicationSchemaOne(){
        int selectionPercent = 10;

        DNA[] bestDNAS = getBestGenes(selectionPercent);

        for(int i = 0; i < currentGeneration.length; i++){
            currentGeneration[i] = bestDNAS[i / 10];
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

    public DNA[] getBestGenes(int selectionPercent){
        int selectCount = (int) (selectionPercent / 100.0f * generationLen);

        return Arrays.copyOfRange(currentGeneration, currentGeneration.length - selectCount, currentGeneration.length);
    }

    public int getGenerationsCount(){
        return generationsCount;
    }

    public int getMaxFitness(){
        return maxFitness;
    }

    public int getMinFitness() {
        return minFitness;
    }

    public DNA[] getGeneration(){
        return currentGeneration;
    }

    public boolean isFinished(){
        if(maxFitness >= geneLen){
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
}