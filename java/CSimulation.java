import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;
import me.tongfei.progressbar.ProgressBar;

public class CSimulation {

    private static final int simuCount = 10000000;
    private static final int simuDrawings = 5;

    private static final String CSV_FILE_NAME = "bingostatics";
    private static final String CSV_FILE_NAME_ROUNDTRIP = "roundtrip";

    private static int roundtripcounter = 0;

    private static int countZeros = 0;
    private static int countOnes = 0;
    private static int countTwos = 0;
    private static int countThrees = 0;
    private static int countFours = 0;
    private static int countFives = 0;
    private static int countMore = 0;
    private static int countSum = 0;

    private static int rowOne = 0;
    private static int rowTwo = 0;
    private static int rowThree = 0;
    private static int rowFour = 0;
    private static int rowFive = 0;
    private static int rowSum = 0;

    private static int colOne = 0;
    private static int colTwo = 0;
    private static int colThree = 0;
    private static int colFour = 0;
    private static int colFive = 0;
    private static int colSum = 0;

    private static int diaOne = 0;
    private static int diaTwo = 0;
    private static int diaSum = 0;

    private static int wins = 0;
    private static int doublewin = 0;

    private static List<String[]> roundtrip = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {

        int cases = 10; // that many simulations
        int y = 0;

        while (y < cases) { // for 10 testcases for the number of simuCounts

            ProgressBar pb = new ProgressBar("Playing Bingo", simuCount);
            System.out.println("Start simulations with " + simuDrawings + " drawings per game...\n");

            int count;

            for (int i = 0; i < simuCount; i++) {
                count = bingoGame(i);
                // write statics
                counts(count);
                pb.step();
            }
            pb.close();

            System.out.println("\nFinished");
            java.awt.Toolkit.getDefaultToolkit().beep(); // generate a beep for minimized simulations

            // write statics
            System.out.println("\nWriting to csv...");
            fileWriter(designCSVStatics(), CSV_FILE_NAME);
            fileWriter(roundtrip, CSV_FILE_NAME_ROUNDTRIP);

            // read statics - only for the console
            System.out.println("");
            System.out.println(simuCount + " Simulationen durchgefuehrt!");

            System.out.println("\nWins: " + wins);
            System.out.println("Double-Wins: " + doublewin);

            System.out.println("\nStatics written successfully.");

            y++;

            // delay for fast iterations
            TimeUnit.SECONDS.sleep(1);

            // clear static variables for next roundtrip
            roundtrip.clear();
            setAllToZero();
        }

    }

    public static int bingoGame(int number) {
        Bingo bingo = new Bingo();

        bingo.setLogger(false); // activate gamemessages
        bingo.setNoDrawings(simuDrawings);

        int[][] bingocard = bingo.createCard();
        int[] numbercard = bingo.drawNumbers();
        String mark = "";
        boolean win = bingo.checkBingo(bingocard, numbercard);
        if (win) {
            wins++;
            rowCounter(bingo.getRow());
            colCounter(bingo.getCol());
            diaCounter(bingo.getDiaLR(), bingo.getDiaRL());

            doublewin = (rowSum + colSum) - wins;

            if (bingo.getDoubleMarkRowCol()) {
                mark = "x";
            }
            setCounter(getCounter() + 1);
            roundtrip.add(new String[] {Integer.toString(getCounter()),Integer.toString(number),mark});

            bingo.setDoubleMarkRowCol(false);

            if(bingo.getLogger()) {
                System.out.println("\nBingocard");
                bingo.showBingoCard(bingocard);
                System.out.println("\nNumbercard");
                System.out.println(Arrays.toString(numbercard));
                System.out.println("");
            }
        }
        return bingo.getCount();
    }

    public static void counts(int counts) {
        if(counts == 0) {
            countZeros++;
        } else if(counts == 1) {
            countOnes++;
        } else if(counts == 2) {
            countTwos++;
        } else if(counts == 3) {
            countThrees++;
        } else if(counts == 4) {
            countFours++;
        } else if(counts == 5) {
            countFives++;
        } else if(counts > 5) {
            countMore++;
        }

        countSum = countZeros + countOnes + countTwos + countThrees + countFours + countFives + countMore;

    }

    private static void rowCounter(int row) {
        if(row == 1) {
            rowOne++;
        } else if(row == 2) {
            rowTwo++;
        } else if(row == 3) {
            rowThree++;
        } else if(row == 4) {
            rowFour++;
        } else if(row == 5) {
            rowFive++;
        }

        rowSum = rowOne + rowTwo + rowThree + rowFour + rowFive;

    }

    private static void colCounter(int col) {
        if(col == 1) {
            colOne++;
        } else if(col == 2) {
            colTwo++;
        } else if(col == 3) {
            colThree++;
        } else if(col == 4) {
            colFour++;
        } else if(col == 5) {
            colFive++;
        }

        colSum = colOne + colTwo + colThree + colFour + colFive;

    }

    private static void diaCounter(int diaLR, int diaRL) {
        if(diaLR == 1) {
            diaOne++;
        } else if(diaRL == 1) {
            diaTwo++;
        }

        diaSum = diaOne + diaTwo;

    }

    public static List<String[]> designCSVStatics () {
        List<String[]> datalist = new ArrayList<>();

        datalist.add(new String[] {"Simulations", Integer.toString(simuCount)});
        datalist.add(new String[] {"Drawings", Integer.toString(simuDrawings)});
        datalist.add(new String[] {"",""}); // Empty row

        datalist.add(new String[] {"Matches", "Sum"}); // Header
        datalist.add(new String[] {"0", Integer.toString(countZeros)});
        datalist.add(new String[] {"1", Integer.toString(countOnes)});
        datalist.add(new String[] {"2", Integer.toString(countTwos)});
        datalist.add(new String[] {"3", Integer.toString(countThrees)});
        datalist.add(new String[] {"4", Integer.toString(countFours)});
        datalist.add(new String[] {"5", Integer.toString(countFives)});
        datalist.add(new String[] {"5+", Integer.toString(countMore)});
        datalist.add(new String[] {"Sum", Integer.toString(countSum)});

        datalist.add(new String[] {"",""}); // Empty row
        datalist.add(new String[] {"Wins", Integer.toString(wins)});
        datalist.add(new String[] {"Double-Wins", Integer.toString(doublewin)});

        datalist.add(new String[] {"",""}); // Empty row
        datalist.add(new String[] {"Row", "Wins"}); // Header
        datalist.add(new String[] {"1", Integer.toString(rowOne)});
        datalist.add(new String[] {"2", Integer.toString(rowTwo)});
        datalist.add(new String[] {"3", Integer.toString(rowThree)});
        datalist.add(new String[] {"4", Integer.toString(rowFour)});
        datalist.add(new String[] {"5", Integer.toString(rowFive)});
        datalist.add(new String[] {"Sum", Integer.toString(rowSum)});

        datalist.add(new String[] {"",""}); // Empty row
        datalist.add(new String[] {"Col", "Wins"}); // Header
        datalist.add(new String[] {"1", Integer.toString(colOne)});
        datalist.add(new String[] {"2", Integer.toString(colTwo)});
        datalist.add(new String[] {"3", Integer.toString(colThree)});
        datalist.add(new String[] {"4", Integer.toString(colFour)});
        datalist.add(new String[] {"5", Integer.toString(colFive)});
        datalist.add(new String[] {"Sum", Integer.toString(colSum)});

        datalist.add(new String[] {"",""}); // Empty row
        datalist.add(new String[] {"Dia", "Wins"}); // Header
        datalist.add(new String[] {"Left-Top -> Right-Bottom", Integer.toString(diaOne)});
        datalist.add(new String[] {"Right-Top -> Left-Bottom", Integer.toString(diaTwo)});
        datalist.add(new String[] {"Sum", Integer.toString(diaSum)});

        return datalist;
    }

    public static void fileWriter(List<String[]> list, String filename) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        List<String[]> csvData = list;
        try (ICSVWriter writer = new CSVWriterBuilder(
                new FileWriter(filename + "_" + formatter.format(date) + ".csv")).withSeparator(';').build()) {
            writer.writeAll(csvData);
        }
    }

    private static int getCounter() {
        return roundtripcounter;
    }

    private static void setCounter(int counter) {
        roundtripcounter = counter;
    }

    private static void setAllToZero() {
        countZeros = 0;
        countOnes = 0;
        countTwos = 0;
        countThrees = 0;
        countFours = 0;
        countFives = 0;
        countMore = 0;
        countSum = 0;

        rowOne = 0;
        rowTwo = 0;
        rowThree = 0;
        rowFour = 0;
        rowFive = 0;
        rowSum = 0;

        colOne = 0;
        colTwo = 0;
        colThree = 0;
        colFour = 0;
        colFive = 0;
        colSum = 0;

        diaOne = 0;
        diaTwo = 0;
        diaSum = 0;

        wins = 0;
        doublewin = 0;
    }

}