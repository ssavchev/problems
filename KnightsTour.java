//import java.util.List;
//import java.util.PriorityQueue;
//import java.util.Queue;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.Collections;

import org.graalvm.compiler.lir.amd64.AMD64Unary.MROp;

import jdk.internal.jshell.tool.resources.l10n;
import sun.awt.www.content.audio.basic;

import java.util.*;

public class KnightsTour {

    //
    // # # | #
    // #   | # # #
    // #   | 
    //

    static final int boardSize = 7;
    static final int maxLevel = boardSize * boardSize;

    static final int[] mPathX = new int[maxLevel];
    static final int[] mPathY = new int[maxLevel];

    static final int[][] mBoard = new int[boardSize][boardSize];

    static boolean findTour(int x, int y, int level) {
        if (x >= boardSize
            || y >= boardSize
            || x < 0
            || y < 0
            || mBoard[x][y] != 0) {
            return false;
        }

        //System.out.printf("Visit %d %d", x, y);
        //System.out.println("");

        // add path
        mPathX[level] = x;
        mPathY[level] = y;

        // mark square and increase level
        level++;
        mBoard[x][y] = 1;

        // check for termination
        if (level == (boardSize * boardSize)) {
            System.out.println("Path found");
            System.out.println("");

            return true;
        }

        if (findTour(x + 2, y + 1, level)) {
            return true;
        }
        if (findTour(x + 2, y - 1, level)) {
            return true;
        }
        if (findTour(x - 2, y + 1, level)) {
            return true;
        }
        if (findTour(x - 2, y - 1, level)) {
            return true;
        }
        if (findTour(x + 1, y + 2, level)) {
            return true;
        }
        if (findTour(x + 1, y - 2, level)) {
            return true;
        }
        if (findTour(x - 1, y + 2, level)) {
            return true;
        }
        if (findTour(x - 1, y - 2, level)) {
            return true;
        }

        mBoard[x][y] = 0;

        return false;
    }

    static class Chromosome {
        final int startX = 0;
        final int startY = 0;
        
        final int[] path = new int[maxLevel];

        public int fitness = 0;

        public Chromosome() {
            for (int l = 0; l < maxLevel; l++) {
                path[l] = 1 + mRand.nextInt(8);
            }

            fitness = checkFitness();
        }

        public Chromosome(Chromosome a, Chromosome b) {
            int cross = boardSize + mRand.nextInt(maxLevel - boardSize);
            int selector = mRand.nextInt(2);

            for (int l = 0; l < maxLevel; l++) {
                if (l <= cross) {
                    path[l] = (selector == 1 ? a.path[l] : b.path[l]);
                } else {
                    path[l] = (selector == 1 ? b.path[l] : a.path[l]);
                }
            }

            for (int l = 0; l < maxLevel; l++) {
                int mutation = mRand.nextInt(1000);
                if (mutation < 5) {
                    path[l] = 1 + mRand.nextInt(8);
                }
            }

            fitness = checkFitness();
        }

        public int checkFitness() {
            final int[][] board = new int[boardSize][boardSize];

            int x = startX;
            int y = startY;
            board[x][y] = 1;

            for (int l = 0; l < maxLevel; l++) {
                switch (path[l]) {
                    case 1: {
                        x = x + 2;
                        y = y + 1;
                        break;
                    }
                    case 2: {
                        x = x + 2;
                        y = y - 1;
                        break;
                    }
                    case 3: {
                        x = x - 2;
                        y = y + 1;
                        break;
                    }
                    case 4: {
                        x = x - 2;
                        y = y - 1;
                        break;
                    }
                    case 5: {
                        x = x + 1;
                        y = y + 2;
                        break;
                    }
                    case 6: {
                        x = x + 1;
                        y = y - 2;
                        break;
                    }
                    case 7: {
                        x = x - 1;
                        y = y + 2;
                        break;
                    }
                    case 8: {
                        x = x - 1;
                        y = y - 2;
                        break;
                    }
                    default: {
                        return 0;
                    }
                }

                if (x >= boardSize
                    || y >= boardSize
                    || x < 0
                    || y < 0
                    || mBoard[x][y] != 0) {
                    return l;
                }

                board[x][y] = 1;
            }

            return (maxLevel - 1);
        }
    }

    public static class ChromosomeComparator implements Comparator<Chromosome> {
        public int compare(Chromosome s2, Chromosome s1) {
            if (s1.fitness > s2.fitness) {
                return 1;
            } else if (s1.fitness < s2.fitness) {
                return -1;
            }
            return 0;
        }
    }

    static final Random mRand = new Random();

    static final int mClasses = 5;
    static final int mClassSize = 10;
    static final Chromosome[] mPopulation = new Chromosome[mClasses * mClassSize];

    public static void generatePopulation() {
        PriorityQueue<Chromosome> queue = new PriorityQueue<>(new ChromosomeComparator());

        for (int i = 0; i < mPopulation.length; i++) {
            Chromosome c = new Chromosome();

            if (c.fitness == 0) {
                i--;
                continue;
            }

            queue.add(c);

            System.out.printf("Added chromosome with fitness %d", c.fitness);
            System.out.println("");
        }
        System.out.println("");

        System.out.println("Fitness chart: ");
        for (int i = 0; i < mPopulation.length; i++) {
            mPopulation[i] = queue.poll();

            System.out.printf("%d ", mPopulation[i].fitness);
        }
        System.out.println("");
    }

    public static void generationNew() {
        PriorityQueue<Chromosome> queue = new PriorityQueue<>(new ChromosomeComparator());

        for (int i = 0; i < 3 * mPopulation.length; i++) {
            int classA = mRand.nextInt(100);
            int classB = mRand.nextInt(100);
    
            if (classA < 60) {
                classA = 0; // 60%
            } else if (classA < 80) {
                classA = 1; // 20%
            } else if (classA < 90) {
                classA = 2; // 10% 
            } else if (classA < 98) {
                classA = 3; // 8%
            } else {
                classA = 4; // 2%
            }
            if (classB < 60) {
                classB = 0; // 60%
            } else if (classB < 80) {
                classB = 1; // 20%
            } else if (classB < 90) {
                classB = 2; // 10% 
            } else if (classB < 98) {
                classB = 3; // 8%
            } else {
                classB = 4; // 2%
            }
    
            int parentA = classA * mClassSize + mRand.nextInt(mClassSize);
            int parentB = classB * mClassSize + mRand.nextInt(mClassSize);
    
            Chromosome c = new Chromosome(mPopulation[parentA], mPopulation[parentB]);

            queue.add(c);

            //System.out.printf("Added chromosome with fitness %d", c.fitness);
            //System.out.println("");
        }
        //System.out.println("");

        //System.out.printf("New offspring %d, parents %d %d", mPopulation[parentA].fitness, mPopulation[parentB].fitness);
        //System.out.println("");
        //System.out.println("");

        System.out.println("New fitness chart: ");
        for (int i = 0; i < mPopulation.length; i++) {
            mPopulation[i] = queue.poll();

            System.out.printf("%d ", mPopulation[i].fitness);
        }
        System.out.println("");
    }

    public static void main(String[] args) {
        System.out.println("Start KnightsTour.java");
        System.out.println("");

        generatePopulation();

        for (int i = 0; i < 25; i++) {
            generationNew();
        }

        /*
        findTour(1, 1, 0);

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                mBoard[x][y] = 0;
            }
        }

        for (int l = 0; l < maxLevel; l++) {
            mBoard[mPathX[l]][mPathY[l]] = 1;
    
            System.out.printf("Move %d", l);
            System.out.println("");
            
            for (int y = 0; y < boardSize; y++) {
                for (int x = 0; x < boardSize; x++) {
                    if (mBoard[x][y] != 0) {
                        System.out.printf("# ");
                    } else {
                        System.out.printf(". ");
                    }
                }
                System.out.println("");
            }
        }
        */
    }
}