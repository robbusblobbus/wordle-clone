package com.robbie.wordleclone;

/* Need:
 - GUI - in progress
 - Guess tracker - done
    - Max guesses (6)
 - Word generator - could be fixed to start - done
        - Then move to totally random from dictionary - done
        - Verify guess is in dictionary - done -- check against bigger dictionary
 - Input checker
    (1) Verify valid input - done
    (2) Assess valid input - done
 - Way to display guesses and correctness - done
 - Winning/Losing text - done

 Maybe:
 - Select word length
    - More guesses for longer words
 - Select word(?)
 - Store and display previous scores (maybe a new window)
 - Show which letters are tried/right/wrong
 - Start new game (change button to "restart" and clear everything)
 */

import org.apache.commons.lang.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class WordleClone implements ActionListener {

    private int guessCount = 0;
    private JPanel panel;
    private final JLabel guessCountLabel;
    private final JLabel feedback;
    private static JLabel[] guessDisplay;
    private final JTextField userGuess;
    private static String guessSimilarityDisplay = "";
    private boolean validGuess;
    private static int wordLength;
    private static String word;
    private static int[] howManyLetter;
    private boolean isCorrect = false;
    private static final int maxGuesses = 6; // Maybe changeable
    private static ArrayList<ArrayList<String>> wordListGroupByLength = new ArrayList<>(20);

    // This constructs the whole class (effectively just a GUI)
    public WordleClone() {

        // All the below is standard setup for a GUI using swing

        // Create frame - this holds the GUI itself
        JFrame frame = new JFrame(); // This is the window

        // Create panel - this holds all the nonsense within the GUI
        panel = new JPanel();
        // Sets border and gives dimensions from BorderFactory
        panel.setBorder(BorderFactory.createEmptyBorder(150, 150, 50, 150));
        // Sets layout with row and column grid
        panel.setLayout(new GridLayout(0, 1));

        // Add guess prompt - parts of the panel seem to be added top to bottom(?)
        JLabel prompt = new JLabel("Input a " + wordLength + " letter word");
        prompt.setBounds(10, 10, 80, 25); // setBounds sets size AND location
        panel.add(prompt);

        //Add guess text input field
        userGuess = new JTextField(5);
        userGuess.setBounds(10, 20, 165, 25);
        panel.add(userGuess);

        // Add guess button
        JButton button = new JButton("Guess");
        button.setPreferredSize(new Dimension(200, 20));
        // The action listener looks for a method called "actionPerformed"
        button.addActionListener(this);
        panel.add(button);

        // Get word length

//        boolean wordLengthSelected = false;
//        while(!wordLengthSelected) {
//            //do
//        }

        // Add guess counter
        guessCountLabel = new JLabel("Number of attempts: 0/" + maxGuesses);
        panel.add(guessCountLabel);

        // Create placeholders for guess display
        for (int i = 0; i < guessDisplay.length; i++) {
            guessDisplay[i] = new JLabel((i + 1) + ":");
            guessDisplay[i].setBounds(10, 110, 300, 25);
            panel.add(guessDisplay[i]);
        }

        // Create placeholder for feedback display
        feedback = new JLabel("");
        feedback.setBounds(10, 110, 300, 25);
        panel.add(feedback);

        // Sets parameters for the frame e.g. close ops, title
        frame.add(panel, BorderLayout.CENTER); // Where does contents of frame go?
        frame.setDefaultCloseOperation((JFrame.EXIT_ON_CLOSE));
        frame.setTitle("Wordle Clone");
        frame.pack(); // Auto-sets size of frame
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        LengthSelect lengthSelect = new LengthSelect();

        while (lengthSelect.validDesiredLength = false) {
            continue;
        }

        wordLength = LengthSelect.getWordLength();

        word = selectWordFromList(analyseWordList().get(4)); // Will be able to preselect word length
        analyseWord(word);
        wordLength = word.length(); // Will add ability to select word length later
        guessDisplay = new JLabel[maxGuesses];

        new WordleClone();
    }

    private static String selectWordFromList(ArrayList<String> wordListSameLength) {

        return wordListSameLength.get(new Random().nextInt(wordListSameLength.size()));
    }

    private static ArrayList<ArrayList<String>> analyseWordList() {

        // Assumes max word length is 20, not ideal

        for (int i = 0; i < 20; i++) {
            wordListGroupByLength.add(new ArrayList<>());
        }

        // Read WordList and append each word to ArrayList (index currentWordLength - 1)
        // Aim to have set of ArrayLists organised by word length

        try {
            FileInputStream fis = new FileInputStream("WordList.txt");
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                String currentWord = sc.nextLine();
                int currentWordLength = currentWord.length();
                wordListGroupByLength.get(currentWordLength-1).add(currentWord.toUpperCase());
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return wordListGroupByLength;
    }

    private static void analyseWord(String word) {

        // Counts frequency of each letter (needed for ? notation in guess feedback)
        // Have to do with ArrayList as opposed to array because "frequency" needs an ArrayList

        ArrayList<Character> wordArrayList = new ArrayList<>();
        for (char letter : word.toCharArray()) {
            wordArrayList.add(letter);
        }

        howManyLetter = new int[wordArrayList.size()];

        for (int i = 0; i < wordArrayList.size(); i++) {
            howManyLetter[i] = Collections.frequency(wordArrayList, wordArrayList.get(i));
        }
    }

    // Below is method that happens when the button is pressed with ActionListener
    @Override
    public void actionPerformed(ActionEvent e) {

        if (guessCount < maxGuesses && !isCorrect) {

            String guess = userGuess.getText().toUpperCase();
            userGuess.setText(""); // Clear text input
            feedback.setText(""); // Clear feedback text

            try {
                validateGuess(guess);
            } catch (NotEnglishException ex) {
                validGuess = false;
                feedback.setText("Please enter a valid English word");
            }
            catch (Exception ex) {
                validGuess = false;
                feedback.setText("Please enter valid guess");
            }

            if (validGuess) {

                guessSimilarityDisplay = analyseGuess(guess);
                guessDisplay[guessCount].setText(++guessCount + ": " + guess + "    " + guessSimilarityDisplay);
                guessCountLabel.setText("Number of attempts: " + guessCount + "/" + maxGuesses);
            }

            if (isCorrect) {
                feedback.setText("Congratulations! You got it right");
            } else if (guessCount >= maxGuesses) {
                feedback.setText("Sorry, you lost! Answer: " + word);
            }
        }
    }

    private void validateGuess(String guess) throws Exception {

        if (guess.length() != wordLength) {
            throw new Exception();
        }

        for (int i = 0; i < wordLength; i++) {
            if (!Character.isAlphabetic(guess.charAt(i))) {
                throw new Exception();
            }
        }

        if (!wordListGroupByLength.get(wordLength-1).contains(guess)) {
            throw new NotEnglishException("Not English word");
        }

        validGuess = true;
    }

    private String analyseGuess(String guess) {

        char[] wordArray = word.toCharArray();
        char[] guessArray = guess.toCharArray();
        char[] guessSimilarityArray = new char[wordLength];
        int[] howManyLetterFound = new int[wordLength];
        int j;

        // First iterate and assign ticks, increment howManyLetterFound

        for (int i = 0; i < wordLength; i++) {
            if (wordArray[i] == guessArray[i]) {
                guessSimilarityArray[i] = '✅';
                for (j = 0; j < wordLength; j++) {
                    if (wordArray[j] == guessArray[i]) {
                        howManyLetterFound[j]++;
                    }
                }
            }
        }

        // Then iterate, check if the letter exists and if it is not already assigned a tick
        // Then check if howManyLetterFound has reached howManyLetter, if not assign ? and increment howManyLetterFound

        for (int i = 0; i < wordLength; i++) {
            if (ArrayUtils.contains(wordArray, guessArray[i]) && guessSimilarityArray[i] != '✅') {
                for (j = 0; guessArray[i] != wordArray[j]; j++) {}
                if (howManyLetterFound[j] < howManyLetter[j]) {
                    guessSimilarityArray[i] = '⍰';
                    howManyLetterFound[j]++;
                }
            }
        }

        // Then assigns Xs to all remaining

        for (int i = 0; i < wordLength; i++) {
            if (guessSimilarityArray[i] != '⍰' && guessSimilarityArray[i] != '✅') {
                guessSimilarityArray[i] = '❌';
            }
        }

        isCorrect = Arrays.equals(howManyLetterFound, howManyLetter);

        guessSimilarityDisplay = String.valueOf(guessSimilarityArray);
        return guessSimilarityDisplay;
    }
}

