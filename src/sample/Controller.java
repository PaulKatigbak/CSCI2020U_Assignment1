package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class Controller {

    //The following variables are used to count the files and words from the chosen directories
    private final TreeMap<String, Double> HamFreq = new TreeMap<String, Double>();
    private final TreeMap<String, Integer> HamWordCount = new TreeMap<String, Integer>();
    private final TreeMap<String, Double> SpamFreq = new TreeMap<String, Double>();
    private final TreeMap<String, Integer> SpamWordCount = new TreeMap<String, Integer>();
    private final TreeMap<String, Double> SpamWord = new TreeMap<String, Double>();



    //The following @fxml variables are to initialize the GUI
    @FXML
    private TableView tableview;
    @FXML
    private TableColumn<TestFile, String> File;
    @FXML
    private TableColumn<TestFile, String> ActualClass;
    @FXML
    private TableColumn<TestFile, Double> Probability;
    @FXML
    private TextField accuracyBox;
    @FXML
    private TextField precisionBox;
    @FXML
    private TextField trainPath;
    @FXML
    private TextField testPath;

    //Function taken from WordCounter program
    private boolean isValidWord(String word) {
        String allLetters = "^[a-zA-Z]+$";
        // returns true if the word is composed by only letters otherwise returns false;
        return word.matches(allLetters);

    }

    /*
    PROBABILITY IMPLEMENTATIONS
     */

    //Probability of ham files containing word, Pr(W|H)
    public void trainHam(File file) throws IOException {
        File[] filesinDir = file.listFiles();
        System.out.println("Training in process...");
        System.out.println("# of files: " + filesinDir.length);

        for (int i = 0; i < filesinDir.length; i++) {

            //Create temporary map
            TreeMap<String, Integer> temp = new TreeMap<String, Integer>();

            //Collect the words and put it in the temp map
            Scanner scanner = new Scanner(filesinDir[i]);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isValidWord(word)) {
                    if (!temp.containsKey(word)) {
                        temp.put(word, 1);
                    }
                }
            }

            //Iterate through temp map and insert word in HamWordCount
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
                if (HamWordCount.containsKey(entry.getKey())) {
                    int oldCount = HamWordCount.get(entry.getKey());
                    HamWordCount.put(entry.getKey(), oldCount+1);
                } else {
                    HamWordCount.put(entry.getKey(), 1);
                }
            }

            temp.clear();

            //Get num of words in ham and put in map
            for (Map.Entry<String, Integer> entry : HamWordCount.entrySet()) {
                double probabilityHamWord = (double)entry.getValue() / (double)filesinDir.length;
                HamFreq.put(entry.getKey(), probabilityHamWord);
            }
        }
    }


    //Probability of spam files containing word, Pr(W|S)
    public void trainSpam(File file) throws IOException {
        File[] filesinDir = file.listFiles();
        System.out.println("Training in process...");
        System.out.println("# of files: " + filesinDir.length);

        for (int i = 0; i < filesinDir.length; i++) {

            //Create temporary map
            TreeMap<String, Integer> temp = new TreeMap<String, Integer>();

            //Collect the words and put it in the temp map
            Scanner scanner = new Scanner(filesinDir[i]);
            while (scanner.hasNext()) {
                String word = scanner.next();
                if (isValidWord(word)) {
                    if (!temp.containsKey(word)) {
                        temp.put(word, 1);
                    }
                }
            }

            //Iterate through temp map and insert word in SpamWordCount
            for (Map.Entry<String, Integer> entry : temp.entrySet()) {
                if (SpamWordCount.containsKey(entry.getKey())) {
                    int oldCount = SpamWordCount.get(entry.getKey());
                    SpamWordCount.put(entry.getKey(), oldCount+1);
                } else {
                    SpamWordCount.put(entry.getKey(), 1);
                }
            }

            temp.clear();

            //Get num of words in spam and put in map
            for (Map.Entry<String, Integer> entry : SpamWordCount.entrySet()) {
                double probabilitySpamWord = (double)entry.getValue() / (double)filesinDir.length;
                SpamFreq.put(entry.getKey(), probabilitySpamWord);
            }
        }
    }

    //Probability of spam files containing word, Pr(S|W)
    public void trainSpamWord() {
        //Get num of words in Spam and put in map
        for (Map.Entry<String, Double> entry : SpamFreq.entrySet()) {
            if (HamFreq.containsKey(entry.getKey())) {
                double probabilitySpamWords = entry.getValue() / (entry.getValue() + HamFreq.get(entry.getKey()));
                SpamWord.put(entry.getKey(), probabilitySpamWords);
            }
        }
    }

    //Variables for getting accuracy and precision
    public double numTruePositives = 0;
    public double numFalsePositives = 0;
    public double numTrueNegatives = 0;
    public double accuracy;
    public double precision;
    public double numTestFiles;

    //Pr(S|F)
    public double testProb(File file) throws FileNotFoundException {
        double probSpamFreq;
        double n = 0.0;
        double cap = 0.5;

        Scanner scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String word = scanner.next();
            if (isValidWord(word)) {
                if (SpamWord.containsKey(word)) {
                    n += Math.log( 1 - SpamWord.get(word)) - Math.log(SpamWord.get(word));
                }
            }
        }
        probSpamFreq = 1 / (1 + Math.pow(Math.E, n));

        //Gets accuracy and precision
        if (file.getParent().contains("spam") && probSpamFreq > cap) {
            this.numTruePositives ++;

        }

        if (file.getParent().contains("ham") && probSpamFreq > cap) {
            this.numFalsePositives ++;
        }

        if (file.getParent().contains("ham") && probSpamFreq < cap) {
            this.numTrueNegatives ++;
        }
        this.numTestFiles ++;
        return probSpamFreq;
    }


    /*
    /TRAINING PHASE SECTION
     */

    //Button handler
    public void TrainButton(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        java.io.File mainDirectory = directoryChooser.showDialog(null);

        if (mainDirectory != null) {
            String path = mainDirectory.getAbsolutePath();
            trainPath.setText(path);
            processTrain(mainDirectory);

            trainSpamWord();
        }
        else {
            System.out.println("Directory not valid!");
        }
    }

    // process spam and ham folders for training directory
    public void processTrain(File file) {

        //Directory names are hardcoded to reflect actual directory name
        if (file.isDirectory()) {
            if (file.getName().equals("ham")) {
                try {
                    trainHam(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("DONE Folder: ../ham");
            } else if (file.getName().equals("spam")) {
                try {
                    trainSpam(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("DONE Folder: ../spam");
            } else if (file.getName().equals("ham2")) {
                try {
                    trainHam(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("DONE Folder: ../ham2");
            } else {
                File[] filesInDir = file.listFiles();
                for (int i = 0; i < filesInDir.length; i++) {
                    processTrain(filesInDir[i]);
                }
            }
        }
    }

    /*
    TESTING PHASE SECTION
     */

    //Button handler
    public void TestButtonAction(ActionEvent event) {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));
        File mainDirectory = directoryChooser.showDialog(null);

        if (mainDirectory != null) {
            String path = mainDirectory.getAbsolutePath();
            testPath.setText(path);
            processTest(mainDirectory);
            System.out.println("Number of files: "+numTestFiles);
            System.out.println("Number of TruePositives: "+numTruePositives);
            System.out.println("Number of FalsePositives: "+numFalsePositives);
            System.out.println("Number of TrueNegatives: "+numTrueNegatives);

            // calculate and format accuracy and precision
            DecimalFormat df = new DecimalFormat("0.00000");
            accuracy = (numTruePositives + numTrueNegatives) / numTestFiles;
            accuracyBox.setText(df.format(accuracy));

            precision = numTruePositives / (numFalsePositives + numTruePositives);
            precisionBox.setText(df.format(precision));

            // add values to table columns
            File.setCellValueFactory(new PropertyValueFactory<TestFile, String>("filename"));
            ActualClass.setCellValueFactory(new PropertyValueFactory<TestFile, String>("actualClass"));
            Probability.setCellValueFactory(new PropertyValueFactory<TestFile, Double>("spamProbability"));
        } else {
            System.out.println("Directory not valid");
        }
    }

    public void processTest(File file) {


        if (file.isDirectory()) {

            File[] filesInDir = file.listFiles();
            for (int i = 0; i < filesInDir.length; i++) {
                processTest(filesInDir[i]);
            }
        } else if (file.exists()) {
            double spamProbability = 0.0;
            //Get spam probability
            try {
                spamProbability = testProb(file);

            } catch (IOException e) {
                e.printStackTrace();
            }

            // format data and add to table data
            DecimalFormat df = new DecimalFormat("0.00000");

            if (file.getParent().contains("ham")) {
                tableview.getItems().add(new TestFile(file.getName(), df.format(spamProbability), "Ham"));
            } else if (file.getParent().contains("spam")) {
                tableview.getItems().add(new TestFile(file.getName(), df.format(spamProbability), "Spam"));
            }
        }
    }
}
