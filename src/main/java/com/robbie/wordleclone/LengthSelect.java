package com.robbie.wordleclone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LengthSelect implements ActionListener {

    private JLabel feedback;
    private final JLabel label = new JLabel("How many letters?");
    private JFrame frame = new JFrame();
    private JTextField userInput = new JTextField();
    static boolean validDesiredLength = false;
    private int desiredLength;

    public LengthSelect() {

        // the panel with the button and text
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(label);
        
        
        // text input field
        userInput.setBounds(10, 20, 165, 25);
        panel.add(userInput);
        
        // the clickable button
        JButton button = new JButton("OK");
        button.addActionListener(this);
        panel.add(button);

        feedback = new JLabel("");
        feedback.setBounds(10, 110, 300, 25);
        panel.add(feedback);


        // set up the frame and display it
        frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Wordle Clone");
        frame.pack();
        frame.setVisible(true);
    }

    public static int getWordLength() {
        return 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String desiredLengthString = userInput.getText();
        userInput.setText(""); // Clear text input
        feedback.setText("");

        try {
            desiredLength = validateDesiredLength(desiredLengthString);
        } catch (NumberFormatException ex) {
            validDesiredLength = false;
            feedback.setText("Please enter an integer");
        }catch (Exception ex) {
            validDesiredLength = false;
            feedback.setText("Please enter an integer between 3 and 10");
        }

        if (validDesiredLength) {
            frame.dispose();
        }
    }

    private int validateDesiredLength(String desiredLengthString) throws Exception {

        desiredLength = Integer.parseInt(desiredLengthString);

        if (desiredLength < 3 || desiredLength > 10) {
            throw new Exception();
        }

        validDesiredLength = true;
        return desiredLength;
    }
}
