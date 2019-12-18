import javafx.util.Pair;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.Line2D;


public class greedySalesman {


    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 10;
    static int MAXINPUTSIZE  = (int) Math.pow(1.5,28);
    static int MININPUTSIZE  =  1;
    static int Nums = 10;
    static long fibResult = 0;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/diana/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static class infinity {

        long inf;

    }

    public static class PathAndMatrix{
        int[] travelNodes;
        int[][] costMatrix;
    }

    public static class PathAndCost{
        int[] path;
        int cost;
    }

    public static class vertex {
        String name;
        int x;
        int y;
    }

    public static void main(String[] args) {

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized

        System.out.println("Running first full experiment...");
        runFullExperiment("TSPGreedy-Exp1-ThrowAway.txt");
        System.out.println("Running second full experiment...");
        runFullExperiment("TSPGreedy-Exp2.txt");
        System.out.println("Running third full experiment...");
        runFullExperiment("TSPGreedy-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName){
        //declare variables for doubling ratio
        double[] averageArray = new double[1000];
        double currentAv = 0;
        double doublingTotal = 0;
        int x = 0;
        int angle = 90;
        int r = 12;
        int n = 360/angle;
        int[] vPath = new int[]{};
        double[][] costMatrix = new double[][]{};
        Pair <double[][], int[]> ans = new Pair<double[][], int[]>(costMatrix, vPath);
        Pair <double[][], int[]> ans2 = new Pair<double[][], int[]>(costMatrix, vPath);
        int n2 = 10;
       ans =  GenerateRandomCircularGraphCostMatrix(n, r, angle);
        //ans = GenerateRandomCostMatrix(n2);
        //ans= GenerateRandomEuclideanCostMatrix(n2);
        //ans2 =  GenerateRandomCircularGraphCostMatrix(n, r, angle);
        //ans2 = GenerateRandomCostMatrix(n2);

        // If the array is empty
        // or the index is not in array range
        // return the original array
        //PathMatrix.travelNodes = removeElements(PathMatrix.travelNodes);

        int[] bestPath =  new int[]{};
        int[] bestPath2 =  new int[]{};

          bestPath2 =  TSPGreedy(ans.getKey(), ans.getValue());


        System.out.println(Arrays.toString(bestPath2));

        //set up print to file
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        //declare variables for stop watch
        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        //add headers to text file
        resultsWriter.println("#X(Value)  N(Size)  AverageTime    NumberOfTrials"); // # marks a comment in gnuplot data
        resultsWriter.flush();

        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize=3;inputSize<=Nums; inputSize++) {
            //test run for fibonacci numbers
            //verifyGreedySalesman(inputSize);

            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            // generate a list of randomly spaced integers in ascending sorted order to use as test input
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial
            //System.out.print("    Generating test data...");

            //generate random integer list
            ans2= GenerateRandomEuclideanCostMatrix(inputSize);
            //long resultFib = Fib(x);

            //print progress to screen
            //System.out.println("...done.");
            System.out.print("    Running trial batch...");

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();


            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random key to search in the range of a the min/max numbers in the list
                //long testSearchKey = (long) (0 + Math.random() * (testList[testList.length-1]));
                /* force garbage collection before each trial run so it is not included in the time */
                // System.gc();

                //TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */
                bestPath = TSPGreedy(ans2.getKey(), ans2.getValue());

                //System.out.println(Arrays.toString(bestPath));
                //fibResult = Greedy(inputSize);
                //System.out.println(result);
                // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }
            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            //put current average time in array of average times. We will be able to use this to calculate the doubling ratio
            averageArray[x] = averageTimePerTrialInBatch;

            //skip this round if this is the first one (no previous average for calculation)
            //if(inputSize != 0){
                //doublingTotal = averageTimePerTrialInBatch/averageArray[x-1]; //Calculate doubling ratio

           // }
            x++;
            int countingbits = countBits(inputSize);
            /* print data for this size of input */
            resultsWriter.printf("%6d %6d %15.2f %4d\n",inputSize, countingbits, averageTimePerTrialInBatch, numberOfTrials); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    /*Verify merge sort is working*/
    static void verifyGreedySalesman(int x){

        //System.out.println("Testing..." + x + " = " + Greedy(x));
    }

    //Remove first and last elements from travelNodes
    static int[] removeElements(int[] travelNodes){
        int index = 0;
        int[] tempNodes = new int[travelNodes.length-1];

        for(int i = 0, k = 0; i < travelNodes.length; i++){
            if(i == index){
                continue;
            }

            // if the index is not
            // the removal element index
            tempNodes[k++] = travelNodes[i];
        }
        travelNodes = tempNodes;

        index = travelNodes.length-1;
        tempNodes = new int[travelNodes.length-1];

        for(int i = 0, k = 0; i < travelNodes.length; i++){
            if(i == index){
                continue;
            }

            // if the index is not
            // the removal element index
            tempNodes[k++] = travelNodes[i];
        }

        travelNodes = tempNodes;

        return travelNodes;
    }

    public static Pair<double[][], int[]> GenerateRandomCostMatrix(int n){
        double[][] randomCostMatrix = new double[n][n];
        int num = 1;

        int[] vertexList = new int[n];

        for(int i = 0; i<n; i++){
            vertexList[i] = i;
        }

        for(int t = 0; t < n; t++){
            for(int q =num; q < n; q++) {
                if(t == q){
                    randomCostMatrix[t][q] = 0;
                }
                else{
                    randomCostMatrix[t][q] = (int) (Math.random()*20 + 1) + 1;
                    randomCostMatrix[q][t] = randomCostMatrix[t][q];
                }
            }
            num = num + 1;
        }
        //System.out.println(Arrays.deepToString(randomCostMatrix));
        Pair<double[][], int[]> ans = new Pair<double[][], int[]>(randomCostMatrix, vertexList);

        return  ans;
    }

    public static Pair<double[][], int[]> GenerateRandomEuclideanCostMatrix(int n){
        double[][] randomEuclideanCostMatrix = new double[n][n];
        vertex[] v = new vertex[n];
        for(int s = 0; s<v.length; s++){
            v[s] = new vertex();
        }
        int maxX = 100;
        int maxY = 100;

        for(int i =0; i < n; i++) {
            v[i].name = Integer.toString(i);
            v[i].x = (int) (Math.random() * (maxX + 1) + 1);
            v[i].y = (int) (Math.random() * (maxY + 1) + 1);
            //System.out.println(v[i].x + "," + v[i].y);
        }
        int[] vertexList = new int[v.length];
        Pair <double[][], int[]> ans = new Pair<double[][], int[]>(randomEuclideanCostMatrix, vertexList);

        for(int t = 0; t < n; t++){
            for(int q = 0; q < n; q++){
                randomEuclideanCostMatrix[t][q] = (int) Math.sqrt(Math.pow(v[t].x - v[q].x,2) +
                        Math.pow(v[t].y - v[q].y, 2) *1);
            }
        }
        for(int i = 0; i< v.length-1; i++){
            vertexList[i] = Integer.parseInt(v[i].name);
        }

        //System.out.println(Arrays.deepToString(randomEuclideanCostMatrix));
        //System.out.println(Arrays.toString(vertexList));
        return  ans;
    }

    public static Pair<double[][], int[]> GenerateRandomCircularGraphCostMatrix(int n, int r, int angle){

        //PathAndMatrix pm = new PathAndMatrix();
        double[][] randomCircularCostMatrix = new double[n][n];
        //int[] vertexList = new int[n+1];
        vertex[] v = new vertex[n+1];
        vertex[] v2 = new vertex[n];
        int angle2 = 0;



        for(int s = 0; s<v.length; s++){
            v[s] = new vertex();
        }

        for(int i =0; i < n; i++) {
            angle2 = angle2 + angle;
            v[i].name = Integer.toString(i);
            v[i].x = Math.abs((int) ( r * Math.cos(angle2)));
            v[i].y = Math.abs((int) (r * Math.sin(angle2)));
            //System.out.println(v[i].x + "," + v[i].y);
        }

        int[] vertexList = new int[v.length];
        Pair <double[][], int[]> ans = new Pair<double[][], int[]>(randomCircularCostMatrix, vertexList);

        Random rnd = ThreadLocalRandom.current();
        for (int i = v.length - 2; i > 0; i--){
            int index = rnd.nextInt(i + 1);
            if(index == 0){

            }
            else{
                // Simple swap
                vertex a = v[index];
                v[index] = v[i];
                v[i] = a;
            }
        }
        v[n].x = 0;
        v[n].y = 0;
        v[n].name = "0";

        for(int i = 0; i<v.length-1; i++) {
           // System.out.println(v[i].name + ',' + v[i].x + "," + v[i].y);
        }

        for(int c = 0; c<v.length-1; c++) {
            for(int t = 0; t<v.length-1; t++) {
                if(c == t){
                    randomCircularCostMatrix[c][t] = 0;
                }
                else{
                    randomCircularCostMatrix[c][t] = -1;
                    randomCircularCostMatrix[t][c] = -1;
                }
            }
        }

        for(int t= 0; t<v.length; t++) {
            for (int q = t + 1; q < v.length; q++) {

            }
        }

        for(int t = 0; t<v.length-1; t++){
            randomCircularCostMatrix[Integer.parseInt((v[t].name))][Integer.parseInt(v[t+1].name)] = (int) Math.sqrt(Math.pow(v[t].x - v[t+1].x,2) +
                    Math.pow(v[t].y - v[t+1].y, 2) *1);
            randomCircularCostMatrix[Integer.parseInt((v[t+1].name))][Integer.parseInt(v[t].name)] =  randomCircularCostMatrix[Integer.parseInt((v[t].name))][Integer.parseInt(v[t+1].name)];
        }

        for(int a = 0; a < randomCircularCostMatrix.length; a++){
            System.out.print("[");
            for(int b = 0; b < randomCircularCostMatrix.length; b++){

                System.out.print(randomCircularCostMatrix[a][b] + " ");
            }
            System.out.println("]");
        }
        //System.out.println(Arrays.deepToString(randomCircularCostMatrix));



        for(int i = 0; i< v.length-1; i++){
            vertexList[i] = Integer.parseInt(v[i].name);
        }



        //System.out.println(Arrays.toString(vertexList));

        return  ans;
    }



    static int[] TSPGreedy(double[][] costMatrix, int[] vertexList){

        double lowestCost = 0;
        double tempCost = 0;
        int lowestNode = 0;
        int visitedNode = 0;
        double minCost = Double.POSITIVE_INFINITY;;
        int i, j, k = 0;
        List<Integer> tempPath = new ArrayList<>();
        int[] resultPath = new int[costMatrix.length];

        int numVisited = 1;
        tempPath.add(0);

        int currentVertex = 0;
        int minIndex = 0;

        do{
           for(j = 0; j< costMatrix.length; j++){
               if(currentVertex!=j && costMatrix[currentVertex][j] != -1){
                   if(!tempPath.contains(j)){
                       if(costMatrix[currentVertex][j]< minCost){
                           minCost = costMatrix[currentVertex][j];
                           minIndex = j;
                       }
                   }
               }
           }
           if(minIndex != 0){
               tempPath.add(minIndex);
               currentVertex = minIndex;
           }
           j = 0;
           minCost = Double.POSITIVE_INFINITY;
            minIndex = 0;
            numVisited++;
        } while (numVisited < costMatrix.length);



        for(i = 0; i < tempPath.size(); i++){
            resultPath[i] = tempPath.get(i);
        }

        return resultPath;
    }

    static int calcMin(double[][] costMatrix, int[]bestPath){
        int minIndex = 0;



        return minIndex;
    }

    //count the number of bits required for current fib number
    static int countBits(int n)
    {
        int count = 0;
        //if n == 0, count will be 1
        if(n == 0){
            count = 1;
        }
        //loop while n does not equal 0
        while (n != 0)
        {
            //each loop add 1 to count
            count++;
            //shift n to the left by 1
            n >>= 1;
        }
        //System.out.println("number of bits = " + count);
        return count;
    }






}
