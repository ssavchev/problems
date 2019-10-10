import org.graalvm.compiler.lir.amd64.AMD64Unary.MROp;

import jdk.internal.jshell.tool.resources.l10n;
import sun.awt.www.content.audio.basic;

import java.util.*;

public class KnightsTour2 {

    //
    // # # | #
    // #   | # # #
    // #   | 
    //

    static class Chromosome {
        public static final int startX = 6;
        public static final int startY = 6;
        
        final int[] path = new int[maxLevel];

        public int fitness = 0;

        public Chromosome() {
            for (int l = 0; l < maxLevel; l++) {
                path[l] = 1 + mRand.nextInt(8);
            }

            fitness = checkFitness();
        }

        public Chromosome(Chromosome a, Chromosome b) {
            int cross = boardSize + mRand.nextInt(maxLevel - (2 * boardSize));
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
                if (mutation < 6) {
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
                int xo = x;
                int yo = y;

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
                    || board[x][y] != 0) {
                    // check moves

                    ArrayList<Integer> moves = new ArrayList<>();

                    if ((xo + 2) < boardSize && (yo + 1) < boardSize && board[xo + 2][yo + 1] == 0) {
                        moves.add(1);
                    }
                    if ((xo + 2) < boardSize && (yo - 1) >= 0 && board[xo + 2][yo - 1] == 0) {
                        moves.add(2);
                    }
                    if ((xo - 2) >= 0 && (yo + 1) < boardSize && board[xo - 2][yo + 1] == 0) {
                        moves.add(3);
                    }
                    if ((xo - 2) >= 0 && (yo - 1) >= 0 && board[xo - 2][yo - 1] == 0) {
                        moves.add(4);
                    }if ((xo + 1) < boardSize && (yo + 2) < boardSize && board[xo + 1][yo + 2] == 0) {
                        moves.add(5);
                    }
                    if ((xo + 1) < boardSize && (yo - 2) >= 0 && board[xo + 1][yo - 2] == 0) {
                        moves.add(6);
                    }
                    if ((xo - 1) >= 0 && (yo + 2) < boardSize && board[xo - 1][yo + 2] == 0) {
                        moves.add(7);
                    }
                    if ((xo - 1) >= 0 && (yo - 2) >= 0 && board[xo - 1][yo - 2] == 0) {
                        moves.add(8);
                    }

                    int size = moves.size();
                    if (size > 0) {
                        path[l] = moves.get(size == 1 ? 0 : mRand.nextInt(size));
                        l--;
                        x = xo;
                        y = yo;
                        continue;
                    }

                    // no moves possible
                    return (l - 1);
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

    static final int boardSize = 8;
    static final int maxLevel = boardSize * boardSize;

    static final int mClasses = 5;
    static final int mClassSize = 200;
    
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
        System.out.println("");
    }

    public static void generationNew() {
        PriorityQueue<Chromosome> queue = new PriorityQueue<>(new ChromosomeComparator());

        for (int i = 0; i < mPopulation.length; i++) {
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
        }

        double avg = 0.0;
        for (int i = 0; i < mPopulation.length; i++) {
            mPopulation[i] = queue.poll();
            avg += (double)mPopulation[i].fitness;
        }
        System.out.printf("New average fitness: %d", (int)(avg / (double)mPopulation.length));
        System.out.println("");
    }

    static void printBoard(int[][] board) {
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                if (board[x][y] != 0) {
                    System.out.printf("# ");
                } else {
                    System.out.printf(". ");
                }
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        System.out.println("Start KnightsTour.java");
        System.out.println("");

        generatePopulation();

        for (int i = 0; i < 100; i++) {
            generationNew();
        }

        int x = Chromosome.startX;
        int y = Chromosome.startY;

        final int[][] board = new int[boardSize][boardSize];
        board[x][y] = 1;

        for (int l = 0; l <= mPopulation[0].fitness; l++) {
            switch (mPopulation[0].path[l]) {
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
                    break;
                }
            }

            board[x][y] = 1;
    
            System.out.printf("Move %d", l);
            System.out.println("");
            
            printBoard(board);
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