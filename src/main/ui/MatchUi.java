package ui;

import model.MatchList;
import model.MatchLog;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

class MatchUi extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 700;
    private CardLayout cards;
    private MatchList log;
    private static JPanel mainPanel;
    private JPanel addLogScreen;
    private JPanel mainScreen;
    private JPanel editScreen;
    private JPanel statScreen;
    private JTextField nameField;
    private JTextField damageField;
    private JTextField killField;
    private JTextField deathField;
    private JCheckBox mvpField;
    private JTextField deltaTrophyField;
    private JTextField replaceField;
    private Color beige = new Color(220, 220, 190);
    private static final String JSON_DESTINATION = "./data/saved.json";
    private JsonWriter jsonWriter;   // writing the json file
    private JsonReader jsonReader;   // reading the json file
    private JLabel selected;
    private int selectedId;
    private JComboBox<String> comboBox;

    public MatchUi() {
        super();
        jsonWriter = new JsonWriter(JSON_DESTINATION);
        jsonReader = new JsonReader(JSON_DESTINATION);
        log = new MatchList("Neo's matches");
        loadMatches();

        setTitle("BrawlStats");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        mainPanel = new JPanel();
        cards = new CardLayout();
        mainPanel.setLayout(cards);

        updateMainScreen();
        editScreen = createEditScreen();
        addLogScreen = createAddLogScreen();
        statScreen =  createStatScreen();

        mainPanel.add(mainScreen, "MainScreen");
        mainPanel.add(addLogScreen, "AddLogScreen");
        mainPanel.add(editScreen, "EditScreen");
        // mainPanel.add(statScreen, "StatScreen");

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void saveMatches() {
        try {
            jsonWriter.open();
            jsonWriter.write(log);
            jsonWriter.close();
            System.out.println("Saved " + log.getName() + " to " + JSON_DESTINATION);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_DESTINATION);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads the saved match list from file
    private void loadMatches() {
        try {
            log = jsonReader.read();
            System.out.println("Loaded " + log.getName() + " from " + JSON_DESTINATION);
        } catch (IllegalValueException | IOException e) {
            System.out.println("Unable to read from file: " + JSON_DESTINATION);
        }
    }

    private static JButton makeButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }


    private void updateMainScreen() {
        JPanel mainScreenPanel = new JPanel();
        mainScreenPanel.setLayout(new BorderLayout());


        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(new GridBagLayout());
        logsPanel.setBackground(beige);
        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.insets = new Insets(5, 10, 5, 10);
        gbcTop.anchor = GridBagConstraints.WEST;

        int startNum = Integer.max(0,log.getSize() - 30);

        try {
            for (int i = log.getSize() - 1; i >= startNum; i--) {
                JLabel id = new JLabel(log.getLog(i).logToString());
                id.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder()));
                id.putClientProperty("index", i);
                id.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Perform actions when the label is clicked (selected)
                        int index = (int) id.getClientProperty("index");
                        if (selected != null) {
                            selected.setForeground(Color.BLACK);
                        }
                        selected = id;
                        selectedId = index;
                        id.setForeground(Color.BLUE); // Change text color
                    }
                });
                logsPanel.add(id, gbcTop);
                gbcTop.gridy++;
            }
        } catch (IndexOutOfBound e) {
            System.out.println("An error has occurred");
        }

        JScrollPane scrollPane = new JScrollPane(logsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(WIDTH - 100, 510));

        mainScreenPanel.add(scrollPane, BorderLayout.NORTH);

        createMainPanelButtons(mainScreenPanel);

        mainScreen = mainScreenPanel;
    }

    private void createMainPanelButtons(JPanel panel) {
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridLayout(6, 1));
        setVisible(true);

        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0;
        gbcButtons.gridy = 0;
        gbcButtons.anchor = GridBagConstraints.NORTH;
        gbcButtons.gridwidth = GridBagConstraints.REMAINDER;
        gbcButtons.fill = GridBagConstraints.HORIZONTAL;
        gbcButtons.insets = new Insets(0, 10, 0, 10);

        JButton button1 = makeButton("Add log", createAddLogAction());
        buttons.add(button1, gbcButtons);
        gbcButtons.gridy++;

        JButton button2 = makeButton("Remove log", createRemoveLogAction());
        buttons.add(button2,gbcButtons);
        gbcButtons.gridy++;

        JButton button3 = makeButton("Edit log", createEditLogAction());
        buttons.add(button3,gbcButtons);
        gbcButtons.gridy++;

        JButton button4 = makeButton("View stats", createViewStatsAction());
        buttons.add(button4,gbcButtons);
        gbcButtons.gridy++;

        JButton button5 = makeButton("Save", createSaveMatchesAction());
        buttons.add(button5, gbcButtons);
        gbcButtons.gridy++;

        JButton button6 = makeButton("Load", createLoadMatchesAction());
        buttons.add(button6, gbcButtons);
        gbcButtons.gridy++;

        gbcButtons.anchor = GridBagConstraints.SOUTH;

        panel.add(buttons, BorderLayout.SOUTH);
    }

    private JPanel createAddLogScreen() {
        JPanel addLogPanel = new JPanel();
        addLogPanel.setLayout(new GridBagLayout());
        addLogPanel.setBackground(beige);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBackground(beige);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.insets = new Insets(20, 10, 20, 10);
        gbcTop.anchor = GridBagConstraints.WEST;

        textPanel.add(new JLabel("Character Name: "), gbcTop);
        gbcTop.gridy++;
        textPanel.add(new JLabel("Damage: "), gbcTop);
        gbcTop.gridy++;
        textPanel.add(new JLabel("Kills: "), gbcTop);
        gbcTop.gridy++;
        textPanel.add(new JLabel("Deaths: "), gbcTop);
        gbcTop.gridy++;
        textPanel.add(new JLabel("Star player: "), gbcTop);
        gbcTop.gridy++;
        textPanel.add(new JLabel("Delta trophy: "), gbcTop);
        gbcTop.gridx++;

        gbcTop.gridy = 0;
        gbcTop.weightx = 1;
        gbcTop.fill = GridBagConstraints.HORIZONTAL;

        nameField = createPlaceholderTextField("String");
        damageField = createPlaceholderTextField("Integer");
        killField = createPlaceholderTextField("Integer");
        deathField = createPlaceholderTextField("Integer");
        mvpField = new JCheckBox("Is star player?", true);
        mvpField.setBackground(beige);
        deltaTrophyField = createPlaceholderTextField("Integer");

        textPanel.add(nameField, gbcTop);
        gbcTop.gridy++;
        textPanel.add(damageField, gbcTop);
        gbcTop.gridy++;
        textPanel.add(killField, gbcTop);
        gbcTop.gridy++;
        textPanel.add(deathField, gbcTop);
        gbcTop.gridy++;
        textPanel.add(mvpField, gbcTop);
        gbcTop.gridy++;
        textPanel.add(deltaTrophyField, gbcTop);

        addLogPanel.add(textPanel, gbcTop);

        addLogPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Add Log")));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcBot = new GridBagConstraints();
        gbcBot.gridx = 0;
        gbcBot.gridy = 7;
        gbcBot.insets = new Insets(0, 50, 10, 50);
        gbcBot.anchor = GridBagConstraints.CENTER;

        JButton confirmAddLogButton = makeButton("Add log", finishAddLog());
        buttonPanel.add(confirmAddLogButton, gbcBot);
        gbcBot.gridx++;
        JButton goBackButton = makeButton("Back", goBack());
        buttonPanel.add(goBackButton, gbcBot);
        buttonPanel.setBackground(beige);

        addLogPanel.add(buttonPanel, gbcBot);
        return addLogPanel;
    }

    private JPanel createEditScreen() {
        JPanel editLogPanel = new JPanel();
        editLogPanel.setLayout(new BorderLayout());
        editLogPanel.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Edit Log")));
        editLogPanel.setBackground(beige);

        JLabel selectedLog = null;
        try {
            selectedLog = new JLabel("Selected log: " + log.getLog(selectedId).logToString());
        } catch (IndexOutOfBound g) {
            System.out.println("Error");
        }

        editLogPanel.add(selectedLog, BorderLayout.NORTH);

        // Create a list of items for the dropdown
        String[] items = {"Name", "Damage", "Kill", "Death", "Mvp", "Delta Trophy"};

        // Create the JComboBox with the list of items
        comboBox = new JComboBox<>(items);

        // Add an ActionListener to the JComboBox
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the selected item
                String selectedOption = (String) comboBox.getSelectedItem();
            }
        });

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBackground(beige);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.insets = new Insets(20, 10, 20, 10);
        gbcTop.anchor = GridBagConstraints.WEST;
        gbcTop.fill = GridBagConstraints.HORIZONTAL;

        textPanel.add(comboBox, gbcTop);

        gbcTop.weightx = 1;
        gbcTop.gridx++;

        replaceField = createPlaceholderTextField("Replacement");

        textPanel.add(replaceField, gbcTop);

        editLogPanel.add(textPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcBot = new GridBagConstraints();
        gbcBot.gridx = 0;
        gbcBot.gridy = 0;
        gbcBot.insets = new Insets(0, 50, 10, 50);
        gbcBot.anchor = GridBagConstraints.CENTER;

        JButton confirmAddLogButton = makeButton("Edit log", finishEditLog());
        buttonPanel.add(confirmAddLogButton, gbcBot);
        gbcBot.gridx++;
        JButton goBackButton = makeButton("Back", goBack());
        buttonPanel.add(goBackButton, gbcBot);
        buttonPanel.setBackground(beige);

        editLogPanel.add(buttonPanel, BorderLayout.SOUTH);

        return editLogPanel;
    }

    private JPanel createStatScreen() {

        return null;
    }


    private ActionListener finishAddLog() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String name = nameField.getText();
                    int damage = Integer.parseInt(damageField.getText());
                    int kill = Integer.parseInt(killField.getText());
                    int death = Integer.parseInt(deathField.getText());
                    Boolean mvp = mvpField.isSelected();
                    int deltaTrophy = Integer.parseInt(deltaTrophyField.getText());
                    deltaTrophyField.getText();
                    log.addLog(new MatchLog(name, damage, kill, death, mvp, deltaTrophy));
                    mainPanel.remove(mainScreen);
                    updateMainScreen();
                    resetInput();
                    mainPanel.add(mainScreen, "MainScreen");
                    cards.show(mainPanel, "MainScreen");
                } catch (Exception g) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Couldn't add log");
                }
            }
        };
    }

    private ActionListener finishEditLog() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String replacement = replaceField.getText();
                    String type = comboBox.getSelectedItem().toString().toLowerCase();
                    log.editList(selectedId, type, replacement);
                    mainPanel.remove(mainScreen);
                    updateMainScreen();
                    mainPanel.add(mainScreen, "MainScreen");
                    cards.show(mainPanel, "MainScreen");
                } catch (IndexOutOfBound | NoMatchingFields | NumberFormatException g) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Couldn't edit log");
                }
            }
        };
    }

    private void resetInput() {
        nameField.setText("String");
        damageField.setText("Integer");
        killField.setText("Integer");
        deathField.setText("Integer");
        mvpField.setText("Boolean");
        deltaTrophyField.setText("Integer");
        nameField.setForeground(Color.GRAY);
        damageField.setForeground(Color.GRAY);
        killField.setForeground(Color.GRAY);
        deathField.setForeground(Color.GRAY);
        mvpField.setForeground(Color.GRAY);
        deltaTrophyField.setForeground(Color.GRAY);
    }

    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setBackground(beige);

        textField.setForeground(Color.GRAY); // Set text color to gray
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText(""); // Clear text only if it's the placeholder
                    textField.setForeground(Color.BLACK); // Change text color to black
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder); // Restore placeholder if field is empty
                    textField.setForeground(Color.GRAY); // Change text color back to gray
                }
            }
        });

        return textField;
    }


    private ActionListener goBack() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.show(mainPanel, "MainScreen");
            }
        };
    }

    private ActionListener createAddLogAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                cards.show(mainPanel, "AddLogScreen");
            }
        };
    }

    private ActionListener createSaveMatchesAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMatches();
            }
        };
    }

    private ActionListener createLoadMatchesAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMatches();
            }
        };
    }

    private ActionListener createRemoveLogAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selected = null;
                try {
                    log.deleteLog(selectedId);
                } catch (Exception g) {
                    return;
                }
                selectedId = -1;
                mainPanel.remove(mainScreen);
                updateMainScreen();
                resetInput();
                mainPanel.add(mainScreen, "MainScreen");
                cards.show(mainPanel, "MainScreen");
            }
        };
    }

    private ActionListener createEditLogAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selected == null) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Need to select a log");
                } else {
                    mainPanel.remove(editScreen);
                    editScreen = createEditScreen();
                    mainPanel.add(editScreen, "EditScreen");
                    cards.show(mainPanel, "EditScreen");
                }
            }
        };
    }

    private ActionListener createViewStatsAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.show(mainPanel, "StatScreen");
            }
        };
    }


    public static void main(String[] args) {
        new MatchUi();
    }
}
