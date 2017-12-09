package main.java;

import main.java.Mongo;
import main.java.Player;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This is a Beat Box used to generate Midi sounds of various Drum Kits
 *
 * @version 0.2
 * @since 0.1
 */
public class BeatBox {
    private final int[] instruments;
    private final String[] instrumentNames;
    private JFrame mainFrame;
    private JButton playBTN;
    private JButton stopBTN;
    private JButton resetBTN;
    private JButton playFromSavedBTN;
    private JButton saveBTN;
    private JButton tempoUpBTN;
    private JButton tempoDownBTN;
    private JPanel mainPanel;
    private ArrayList<JCheckBox> checkboxList;
    private StringBuilder title;
    private Player player;
    private Sequencer sequencer;

    public static void main(String[] args) {
        new BeatBox().run();
    }

    public BeatBox() {
        instruments = new int[]{
                35, 42, 46, 38,
                49, 39, 50, 60,
                70, 72, 64, 56,
                58, 47, 67, 63
        };
        instrumentNames = new String[]{
                "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
                "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
                "Maracas", "Whistle", "Low Conga", "Cowbell",
                "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"
        };
    }

    public void run() {
        // setup application
        setupGUI();
        player = new Player(checkboxList,instrumentNames,instruments,mainFrame,title);
        player.setupPlayer();

        // run/generate application
        createGUI();
        createListeners();

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
        mainFrame.requestFocus();
    }


    private void setupGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        title = new StringBuilder("BeatBox");
        mainFrame = new JFrame(title.toString());

        mainPanel = new JPanel(new GridLayout(16, 17, 1, 2));

        // List of checkboxes in array
        checkboxList = new ArrayList<JCheckBox>(256);

        // buttons config.
        playBTN = new JButton("Play");
        stopBTN = new JButton("Stop");
        resetBTN = new JButton("Reset");
        tempoUpBTN = new JButton(">");
        tempoDownBTN = new JButton("<");
        saveBTN = new JButton("Save tune.");
        playFromSavedBTN = new JButton("Play From Saved");
    }


    private void createGUI() {
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JPanel instrumentsPanel = new JPanel(new GridLayout(16, 1, 1, 2));
        for (int i = 0; i < 16; i++) {
            // create JLabel of Instruments & add tof
            // WEST box
            JLabel instrumentName = new JLabel(instrumentNames[i], SwingConstants.LEFT);
            instrumentName.setFont(new Font("Georgia", Font.PLAIN, 15));
            instrumentsPanel.add(instrumentName);

            // create unselected checkboxes of corresponding instruments
            for (int j = 0; j < 16; j++) {
                // add checkboxes
                JCheckBox cb = new JCheckBox();
                cb.setSelected(false);
                checkboxList.add(cb);
                mainPanel.add(cb);
            }
        }

        // add buttons to buttonsPanel
        Box buttonsPanel = new Box(BoxLayout.Y_AXIS);
        // @FIxMe Align buttons to right
        playBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.add(playBTN);
        stopBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.add(stopBTN);
        resetBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.add(resetBTN);
        saveBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.add(saveBTN);
        playFromSavedBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        buttonsPanel.add(playFromSavedBTN);

        // add tempoButton to tempoPanel
        Box tempoPanel = new Box(BoxLayout.X_AXIS);
        // @FixMe Align buttons to center
        tempoDownBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tempoPanel.add(tempoDownBTN);
        tempoUpBTN.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tempoPanel.add(tempoUpBTN);

        // add padding to mainFrame
        JPanel backgroundPanel = new JPanel(new BorderLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // add instrumentsPanel to WEST of backgroundPanel
        backgroundPanel.add(instrumentsPanel, BorderLayout.WEST);

        // add buttonsPanel to rightPanel
        rightPanel.add(buttonsPanel);
        rightPanel.add(tempoPanel);
        // add rightPanel to EAST of backgroundPanel
        backgroundPanel.add(rightPanel, BorderLayout.EAST);

        // add mainPanel to CENTER of backgroundPanel
        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        // add backgroundPanel to mainFrame
        mainFrame.getContentPane().add(backgroundPanel);
    }



    private void createListeners() {
        playBTN.addActionListener(e -> player.startPlayer());
        stopBTN.addActionListener(e -> player.stopPlayer());
        resetBTN.addActionListener(e -> player.resetPlayer());
        tempoUpBTN.addActionListener(e -> player.tempoUpPlayer());
        tempoDownBTN.addActionListener(e -> player.tempoDownPlayer());
        saveBTN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = getChecked();
                Mongo.addDocument(selected);

            }
        });
        playFromSavedBTN.addActionListener(e -> player.playSaved());
        }

    private String getChecked() {
        int flag = 0;
        String selected = "";
        for (int i = 0; i < 16; i++) { // row - instrument [0 - 15] or channel
            for (int j = 0; j < 16; j++) { // column - beat [0 - 15]
                if (checkboxList.get(j + i * 16).isSelected()) {
                    selected += "1";
                    flag = 1;
                }
                else{
                    selected += "0";
                }
            }
        }
            if (flag == 0) {
                JPanel jPanel = new JPanel();
                JOptionPane.showMessageDialog(jPanel, "Please select atleast one check box to continue", "Warning", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return selected;
    }

    public void updateGUI() {
    }
}