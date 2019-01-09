package task3;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

/**
 * @author Pavlo Rozbytskyi
 * @version 2.0.1
 */
// after each generation loop you must recalculate fitness off all dna's and
// figure out maximal and minimal fitness
public class DNAPool {
    private DNA[] currentGeneration;
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

    public DNAPool(){

    }

    // calculating fitness after each loop and after creating new generation
    public DNAPool(int generationLen, int geneLen, int initRate, float mutationRate, int replicationSchema, int crossOverSchema, float recombinationRate, boolean protect){
        this.replicationSchema = replicationSchema;
        this.crossOverSchema   = crossOverSchema;
        this.recombinationRate = recombinationRate;

        this.mutationRate  = mutationRate;
        this.generationLen = generationLen;
        this.protect       = protect; // protecting best gene from mutation and cross over

        createGenerations(generationLen, geneLen, initRate);
    }

    private void createGenerations(int generationLen, int geneLen, int initRate){
        this.currentGeneration = new DNA[generationLen];
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
    public void calcMaxFitnessOfGeneration(){
        Optional<DNA> dnaMaxFitness = Arrays.stream(currentGeneration).max(Comparator.comparing(DNA::getFitness));
        dnaMaxFitness.ifPresent(DNA -> maxFitness = DNA.getFitness());

        if(maxFitness == geneLen) {
            finished = true;
            //printInfo();
        }
    }

    /**
     * calculating min fitness of gen
     */
    public void calcMinFitnessOfGeneration(){
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
        int randPos = 0;

        int bestPos = getBestGenePos();

        do {
            firstGenePos  = (int) (Math.random() * generationLen);
            secondGenePos = (int) (Math.random() * generationLen);

            if(!(firstGenePos == bestPos || secondGenePos == bestPos)){
                randPos = (int) (Math.random() * geneLen);

                DNA dna1 = new DNA(geneLen);
                DNA dna2 = new DNA(geneLen);

                switch (crossOverSchema){
                    case 1: {
                        dna1 = crossOverOne(currentGeneration[firstGenePos], currentGeneration[secondGenePos], randPos);
                        dna2 = crossOverOne(currentGeneration[secondGenePos], currentGeneration[firstGenePos], randPos);
                    }break;

                    case 2: {
                        int randPos1 = randPosCrossOverTwo(randPos);
                        dna1 = crossOverTwo(currentGeneration[firstGenePos], currentGeneration[secondGenePos], randPos, randPos1, true);
                        dna2 = crossOverTwo(currentGeneration[firstGenePos], currentGeneration[secondGenePos], randPos, randPos1, false);
                    }break;

                    default: {
                        Main.logger.error("Chose cross over schema");
                        Main.printError("Chose cross over schema");
                    }
                }

                currentGeneration[firstGenePos] = dna1;
                currentGeneration[secondGenePos] = dna2;

                crossOverCount--;
                crossOverPerf++;
            }
        }while (crossOverCount > 0);

        assert (int)(currentGeneration.length * recombinationRate) == crossOverPerf;
    }

    private int randPosCrossOverTwo(int pos){
        int pos1 = 0;

        do{
            pos1 = (int)(Math.random() * geneLen);
        }while (!(Math.abs(pos1 - pos) > geneLen / Constants.GENES_SCALE && Math.abs(pos1 - pos) < Constants.GENES_SCALE / 2));

        return pos1;
    }

    private DNA crossOverTwo(DNA DNA1, DNA DNA2, int randPos1, int randPos2, boolean first){
        int pos2 = randPos1 > randPos2 ? randPos1 : randPos2;
        int pos1 = randPos1 > randPos2 ? randPos2 : randPos1;

        DNA newDNA = new DNA(DNA1.getGene().length);
        Integer [] test1   = new Integer[Math.abs(pos1 - pos2)];
        Integer [] test2   = new Integer[Math.abs(pos1 - pos2)];
        System.arraycopy(DNA1.getGene(), pos1, test1, 0, Math.abs(pos1 - pos2));
        System.arraycopy(DNA2.getGene(), pos1, test2, 0, Math.abs(pos1 - pos2));

        int test1Fitness = (int) Arrays.stream(test1).filter(elem -> elem == 1).count();
        int test2Fitness = (int) Arrays.stream(test2).filter(elem -> elem == 1).count();

        test1 = test1Fitness > test2Fitness ? test1 : test2;

        if(first) {
            Integer[] testGene1 = new Integer[geneLen];
            System.arraycopy(DNA1.getGene(), 0, testGene1, 0, pos1);
            System.arraycopy(test1, 0, testGene1, pos1, test1.length);
            System.arraycopy(DNA1.getGene(), pos2, testGene1, pos2, geneLen - pos2);

            newDNA.setGene(testGene1);
        }else {
            Integer[] testGene2 = new Integer[geneLen];
            System.arraycopy(DNA2.getGene(), 0, testGene2, 0, pos1);
            System.arraycopy(test1, 0, testGene2, pos1, test1.length);
            System.arraycopy(DNA2.getGene(), pos2, testGene2, pos2, geneLen - pos2);

            newDNA.setGene(testGene2);
        }
        return newDNA;
    }
    public DNA crossOverOne(DNA DNA1, DNA DNA2, int randPos){
        DNA newDNA = new DNA(DNA1.getGene().length);
        Integer [] newGene = new Integer[DNA1.getGene().length];

        System.arraycopy(DNA1.getGene(), 0, newGene, 0, randPos);
        System.arraycopy(DNA2.getGene(), randPos, newGene, randPos, DNA1.getGene().length - randPos);

        newDNA.setGene(newGene);
        return newDNA;
    }

    public void sortGeneration(){
        Arrays.sort(currentGeneration, Comparator.comparing(DNA::getFitness));
    }

    public void processMutation(){
        setBestGene();

        int mutationCount     = (int) (mutationRate * geneLen * generationLen) + 1;
        int mutationPerformed = 0;

        int randGen = 0;
        int randPos = 0;

        int bestGenePos = getBestGenePos();

        do{
            randGen = (int) (Math.random() * generationLen);

            if(randGen != bestGenePos) {
                randPos = (int) (Math.random() * geneLen);

                currentGeneration[randGen].invertCell(randPos);

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

    public void processReplication(){
        switch(replicationSchema){
            case 1:
                replicationSchemaOne();
                break;
            case 2:
                replicationSchemaTwo();
                break;
            default:
                throw new RuntimeException("please input replication schema");
        }

        calcMinFitnessOfGeneration();
        calcMaxFitnessOfGeneration();
    }

    // rank based selection
    private void replicationSchemaTwo(){
        processRanking();
        passRankedGenesIntoGeneration();
        clearRankings();
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

    private void passRankedGenesIntoGeneration(){
        DNA [] newGeneration = new DNA[generationLen];
        Arrays.fill(newGeneration, new DNA(geneLen));
        for(int i = 0; i < newGeneration.length; i++){
            newGeneration[i] = getBestRankedDNA();
        }

        this.currentGeneration = newGeneration;
    }

    private DNA getBestRankedDNA(){
        double probability = Math.random();

        for(int i = 1; i < currentGeneration.length; i++){
            if(probability == currentGeneration[i - 1].getPsCum()){
                return currentGeneration[i - 1];
            }else if(probability <= currentGeneration[i].getPsCum() && probability > currentGeneration[i - 1].getPsCum()){
                return currentGeneration[i];
            }
        }
        return currentGeneration[currentGeneration.length - 1];
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

    //TODO: print out rank based selection table for debugging reasons
    public void printOutRankTable(){
        Arrays.stream(currentGeneration).forEach(DNA::printRank);
    }

}