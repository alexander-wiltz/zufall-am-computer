/*
* BINGO by Alexander Wiltz, 2021
* Implementation and simulation of a Bingo-Ball 75 game as part of a study project
* for the module stochastics for computer scientists
 */

import java.util.*;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class Bingo {

    private final int CARDROWS = 5;
    private final int CARDCOLS = 5;
    private final int[][] cardArray = new int[CARDROWS][CARDCOLS];
    private final int MIN = 1;
    private final int MAX = 75;
    private int NUMBERDRAWINGS = 20; // default: 20 drawings

    private int count = 0;
    private int row = 0;
    private int col = 0;
    private int diaLR = 0; // diagonal from left-top to right-bottom
    private int diaRL = 0; // diagonal from right-top to left-bottom

    private boolean doubleMarkRowCol = false;

    private boolean activateLogMessages = false;

    private int createRandomNoX(final int min, final int max) {
        assert(min > 0);
        assert(max > min);

        Random rnd = new Random();

        return rnd.nextInt(( max - min ) + 1 ) + min;
    }

    private int createRandomNo(final int min, final int max) {
        assert(min > 0);
        assert(max > min);

        MersenneTwister twister = new MersenneTwister();
        RandomDataGenerator rnd = new RandomDataGenerator(twister);

        return rnd.nextInt(min, max);
    }

    public int[][] createCard() {
        // getting the set-size
        assert(CARDROWS == CARDCOLS); // proof to be squared
        final int cardelems = CARDROWS * CARDCOLS;
        final double cardelemsMiddle = cardelems;
        final double middle = Math.sqrt(cardelemsMiddle);

        // create Hashset for individually numbers in the card
        Set<Integer> cardset = new LinkedHashSet<Integer>();

        // create variable max and min values for the bingo-card
        // in the 1st column are values between 1 to 15
        // in the 2nd column are values between 16 to 30
        // in the 3rd column are values between 31 to 45...
        int[] maxval = new int[CARDCOLS];
        int[] minval = new int[CARDCOLS];
        int factor = MAX / CARDCOLS;

        for(int a = 0; a < CARDCOLS; a++) {
            maxval[a] = factor * (a + 1);
            minval[a] = maxval[a] - (factor - 1);
        }
        int b;
        while (cardset.size() < cardelems) {
            b = cardset.size() % CARDCOLS;
            cardset.add(createRandomNo(minval[b], maxval[b])); // variable version
            // cardset.add(createRandomNo(MIN, MAX)); // simple version with random numbers
        }

        Integer[] tmpCardArr = cardset.toArray(new Integer[cardset.size()]);

        // creating the card with a multidimensional array
        int x = 0;
        for(int i = 0; i < CARDROWS; i++) {
            for(int j = 0; j < CARDCOLS; j++) {
                x = j + (i * (int)middle);
                cardArray[i][j] = tmpCardArr[x];
            }
        }

        return cardArray;
    }

    public void showBingoCard(final int[][] bingocard) {
        assert(bingocard != null);
        assert(bingocard.length > 0);

        for(int[] x : bingocard) {
            System.out.println(Arrays.toString(x));
        }
    }

    public int[] drawNumbers() {
        Set<Integer> numberset = new HashSet<Integer>();
        while (numberset.size() < NUMBERDRAWINGS) {
            numberset.add(createRandomNo(MIN, MAX));
        }
        Integer[] numbersArr = numberset.toArray(new Integer[numberset.size()]);
        int[] numbers = Arrays.stream(numbersArr).mapToInt(i->i).toArray();

        return numbers;
    }

    public boolean checkBingo(final int[][] bingocard, final int[] numbercard) {
        assert(bingocard != null);
        assert(bingocard.length > 0);
        assert(numbercard != null);
        assert(numbercard.length > 0);

        assert(CARDROWS == CARDCOLS); // must be a squared card

        final int NOCARDLENGTH = numbercard.length;
        final int TMPCARDARRLENGTH = CARDROWS*CARDCOLS;
        final double cardelemsMiddle = TMPCARDARRLENGTH;
        final double middle = Math.sqrt(cardelemsMiddle);

        int[] tmpCardArray = new int[TMPCARDARRLENGTH];
        int x = 0;
        for(int i = 0; i < CARDROWS; i++) {
            for(int j = 0; j < CARDCOLS; j++) {
                x = j + (i * (int)middle);
                tmpCardArray[x] = bingocard[i][j];
            }
        }

        // first check how many numbers are in the cardset
        for(int y = 0; y < NOCARDLENGTH; y++) {
            for(int z = 0; z < TMPCARDARRLENGTH; z++) {
                if(numbercard[y] == tmpCardArray[z]) {
                    count++;
                }
            }
        }

        int rowbingo = 0;
        int colbingo = 0;
        int diaLRbingo = 0;
        int diaRLbingo = 0;

        if(count > (middle - 1)) {
            Arrays.sort(numbercard);
            int tmpcount = 0;

            // is there a bingo in a row
            int z = 0;
            while(z < CARDROWS) {
                for(int i = 0; i < CARDCOLS; i++) {
                    for(int j = 0; j < NUMBERDRAWINGS; j++) {
                        if(bingocard[z][i] == numbercard[j]) {
                            tmpcount++;
                            if(tmpcount == 5) {
                                if(activateLogMessages) {
                                    System.out.printf("\nBingo in row %d!\n", (z + 1));
                                }
                                row = z + 1;
                                rowbingo++;
                                //return true;
                            }
                        }
                    }
                }
                z++;
                tmpcount = 0;
            }

            // is there a bingo in a column
            z = 0;
            while(z < CARDCOLS) {
                for(int i = 0; i < CARDROWS; i++) {
                    for(int j = 0; j < NUMBERDRAWINGS; j++) {
                        if(bingocard[i][z] == numbercard[j]) {
                            tmpcount++;
                            if(tmpcount == 5) {
                                if (activateLogMessages) {
                                    System.out.printf("\nBingo in col %d!\n", (z + 1));
                                }
                                col = z + 1;
                                colbingo++;
                                //return true;
                            }
                        }
                    }
                }
                z++;
                tmpcount = 0;
            }

            // is there a bingo in the diagonal way from top-left to right-bottom
            for(int i = 0; i < (int)middle; i++) {
                for(int j = 0; j < NUMBERDRAWINGS; j++) {
                    if(bingocard[i][i] == numbercard[j]) {
                        tmpcount++;
                        if(tmpcount == 5) {
                            if(activateLogMessages) {
                                System.out.println("\nBingo in diagonal way from top-left to right-bottom!");
                            }
                            diaLR = 1;
                            diaLRbingo++;
                            //return true;
                        }
                    }
                }
                tmpcount = 0;
            }

            // is there a bingo in the diagonal way from top-right to left-bottom
            int k = (int)middle - 1;
            for(int i = 0; i < (int)middle; i++) {
                for(int j = 0; j < NUMBERDRAWINGS; j++) {
                    if(k < 0) {
                        if(activateLogMessages) {
                            System.out.println("\nError during parsing!");
                        }
                        System.exit(-1);
                    }
                    if(bingocard[i][k] == numbercard[j]) {
                        tmpcount++;
                        if(tmpcount == 5) {
                            if(activateLogMessages) {
                                System.out.println("\nBingo in diagonal way from top-right to left-bottom!");
                            }
                            diaRL = 1;
                            diaRLbingo++;
                            //return true;
                        }
                    }
                }
                k--;
                tmpcount = 0;
            }
        }
        // mark double wins
        if(rowbingo > 0 && colbingo > 0) {
            doubleMarkRowCol = true;
        }

        if(rowbingo > 0 || colbingo > 0 || diaLRbingo > 0 || diaRLbingo > 0) {
            // in case of all returns in the wincases are be deactivated, the software tracks the multiple-wins
            rowbingo = 0;
            colbingo = 0;
            diaLRbingo = 0;
            diaRLbingo = 0;



            return true;

        } else {
            // No Bingo-Match
            return false;
        }
    }

    public int getCount() {
        return count;
    }

    // getter for which row, col or diagonal-field win
    public int getRow() {
        return row;
    }
    public int getCol() {
        return col;
    }
    public int getDiaLR() {
        return diaLR;
    }
    public int getDiaRL() {
        return diaRL;
    }
    public boolean getLogger() {
        return activateLogMessages;
    }
    public boolean getDoubleMarkRowCol() {
        return doubleMarkRowCol;
    }

    // setter for some params
    public void setLogger(boolean logger) {
        activateLogMessages = logger;
    }
    public void setNoDrawings(int drawings) {
        assert(drawings >= CARDROWS);
        assert(drawings >= CARDCOLS);
        assert(CARDROWS == CARDCOLS);
        assert(drawings <= MAX);

        if(drawings >= CARDROWS && drawings <= MAX) {
            NUMBERDRAWINGS = drawings;
        } else {
            System.out.println("\nWrong number of drawings! Can't play the game.");
            System.exit(-1);
        }
    }
    public void setDoubleMarkRowCol(boolean doubleMark) {
        doubleMarkRowCol = doubleMark;
    }

}

class CTest {

    public static void main(String[] args) {
        Bingo bingo = new Bingo();

        bingo.setLogger(true); // activate gamemessages
        bingo.setNoDrawings(20); // set the number of drawings

        System.out.println("Bingo-Karte:");
        int[][] bingocard = bingo.createCard();
        bingo.showBingoCard(bingocard);

        System.out.println("\nGezogene Zahlen:");
        int[] numbercard = bingo.drawNumbers();
        System.out.println(Arrays.toString(numbercard));

        System.out.println("\nVergleich:");
        boolean win = bingo.checkBingo(bingocard, numbercard);
        System.out.println(bingo.getCount() + " Zahlen sind auf der Bingo-Karte");
        if (win) {
            System.out.println("BINGO!");
        } else {
            System.out.println("Kein BINGO!");
        }
    }
}
