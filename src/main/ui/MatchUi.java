package ui;

import model.MatchList;
import model.MatchLog;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

// TODO: add specification as well as abstract the data so each method doesn't take up more than 25 lines
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
    private JTextField numLogs;
    private ArrayList<String> stats;
    private Boolean dataSaved;

    public MatchUi() {
        super();
        jsonWriter = new JsonWriter(JSON_DESTINATION);
        jsonReader = new JsonReader(JSON_DESTINATION);
        log = new MatchList("Neo's matches");

        setTitle("Brawl Stats");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        pack();
        centreOnScreen();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        dataSaved = true;

        addWindowListener(createWindowListener());

        setResizable(false);
        add(initCards());
        setVisible(true);
    }

    private JPanel initCards() {
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
        mainPanel.add(statScreen, "StatScreen");

        return mainPanel;
    }

    private WindowListener createWindowListener() {
        return new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!dataSaved) {
                    int choice = JOptionPane.showConfirmDialog(MatchUi.this,
                            "You have unsaved data. Do you want to save before closing?",
                            "Save Reminder", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        saveMatches();
                        dispose();
                    }
                } else {
                    dispose();
                }
            }
        };
    }

    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
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

        JScrollPane scrollPane = new JScrollPane(initLogPanel());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setPreferredSize(new Dimension(WIDTH - 100, 510));

        mainScreenPanel.add(scrollPane, BorderLayout.NORTH);

        createMainPanelButtons(mainScreenPanel);

        mainScreen = mainScreenPanel;
    }

    private JPanel initLogPanel() {
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
                id.setBorder(createStatBorder());
                id.putClientProperty("index", i);
                id.addMouseListener(createLogMouseListener(id, i));
                logsPanel.add(id, gbcTop);
                gbcTop.gridy++;
            }
        } catch (IndexOutOfBound e) {
            System.out.println("An error has occurred");
        }

        return logsPanel;
    }

    private MouseListener createLogMouseListener(final JLabel label, final int index) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selected != null) {
                    selected.setForeground(Color.BLACK);
                }
                selected = label;
                selectedId = index;
                label.setForeground(Color.BLUE);
            }
        };
    }

    private void createMainPanelButtons(JPanel panel) {
        JPanel buttons = createButtonsPanel();
        addButtons(buttons);
        panel.add(buttons, BorderLayout.SOUTH);
    }

    private JPanel createButtonsPanel() {
        JPanel buttons = new JPanel(new GridLayout(6, 1));
        buttons.setVisible(true);
        return buttons;
    }

    private void addButtons(JPanel buttons) {
        GridBagConstraints gbcButtons = new GridBagConstraints();
        gbcButtons.gridx = 0;
        gbcButtons.gridy = 0;
        gbcButtons.anchor = GridBagConstraints.NORTH;
        gbcButtons.gridwidth = GridBagConstraints.REMAINDER;
        gbcButtons.fill = GridBagConstraints.HORIZONTAL;
        gbcButtons.insets = new Insets(0, 10, 0, 10);

        addButton(buttons, "Add log", createAddLogAction(), gbcButtons);
        addButton(buttons, "Remove log", createRemoveLogAction(), gbcButtons);
        addButton(buttons, "Edit log", createEditLogAction(), gbcButtons);
        addButton(buttons, "View stats", createViewStatsAction(), gbcButtons);
        addButton(buttons, "Save", createSaveMatchesAction(), gbcButtons);
        addButton(buttons, "Load", createLoadMatchesAction(), gbcButtons);

        gbcButtons.anchor = GridBagConstraints.SOUTH;
    }

    private void addButton(JPanel panel, String buttonText, ActionListener actionListener, GridBagConstraints gbc) {
        JButton button = makeButton(buttonText, actionListener);
        panel.add(button, gbc);
        gbc.gridy++;
    }

    private JPanel createAddLogScreen() {
        JPanel addLogPanel = new JPanel();
        addLogPanel.setLayout(new BorderLayout());
        addLogPanel.setBackground(beige);

        addLogPanel.add(createTextPanel(), BorderLayout.CENTER);

        addLogPanel.setBorder(createCompoundBorder("Add log"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcBot = new GridBagConstraints();
        gbcBot.gridx = 0;
        gbcBot.gridy = 7;
        gbcBot.insets = new Insets(0, 50, 80, 50);
        gbcBot.anchor = GridBagConstraints.CENTER;

        JButton confirmAddLogButton = makeButton("Add log", finishAddLog());
        buttonPanel.add(confirmAddLogButton, gbcBot);
        gbcBot.gridx++;
        JButton goBackButton = makeButton("Back", goBack());
        buttonPanel.add(goBackButton, gbcBot);
        buttonPanel.setBackground(beige);

        addLogPanel.add(buttonPanel, BorderLayout.SOUTH);
        return addLogPanel;
    }

    private JPanel createTextPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBackground(beige);

        GridBagConstraints gbcTop = createGBC();
        addLabels(textPanel, gbcTop);
        gbcTop.gridx++;
        addFields(textPanel, gbcTop);

        return textPanel;
    }

    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 20, 10);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    private void addLabels(JPanel textPanel, GridBagConstraints gbcTop) {
        String[] labelNames = {"Character Name:", "Damage:", "Kills:", "Deaths:", "Star player:", "Delta trophy:"};
        for (String labelName : labelNames) {
            JLabel label = new JLabel(labelName);
            textPanel.add(label, gbcTop);
            gbcTop.gridy++;
        }
    }

    private void addFields(JPanel textPanel, GridBagConstraints gbcTop) {
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
    }

    private CompoundBorder createCompoundBorder(String s) {
        Border borderLine = BorderFactory.createLineBorder(Color.black);
        EmptyBorder emptyBorder = new EmptyBorder(10, 10, 10, 10);
        TitledBorder tiltedBorder = BorderFactory.createTitledBorder(borderLine, s);

        return BorderFactory.createCompoundBorder(emptyBorder, tiltedBorder);
    }

    private JPanel createEditScreen() {
        JPanel editLogPanel = new JPanel();
        editLogPanel.setLayout(new BorderLayout());
        editLogPanel.setBorder(createCompoundBorder("Edit Log"));
        editLogPanel.setBackground(beige);

        JLabel selectedLog;
        try {
            selectedLog = new JLabel("Selected log: " + log.getLog(selectedId).logToString());
            editLogPanel.add(selectedLog, BorderLayout.NORTH);
        } catch (IndexOutOfBound g) {
            System.out.println("No Matches");
        }

        editLogPanel.add(createComboBoxPanel(), BorderLayout.CENTER);

        editLogPanel.add(createEditButtonPanel(), BorderLayout.SOUTH);

        return editLogPanel;
    }

    private JPanel createComboBoxPanel() {
        String[] items = {"Name", "Damage", "Kills", "Deaths", "Mvp", "Trophy"};

        comboBox = new JComboBox<>(items);

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

        return textPanel;
    }

    private JPanel createEditButtonPanel() {
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

        return buttonPanel;
    }

    private JPanel createStatScreen() {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BorderLayout());
        statPanel.setBorder(createCompoundBorder("Statistics"));
        statPanel.setBackground(beige);

        statPanel.add(createInputPanel(), BorderLayout.NORTH);

        statPanel.add(createLogStatsPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcBot = new GridBagConstraints();
        gbcBot.gridx = 0;
        gbcBot.gridy = 0;
        gbcBot.insets = new Insets(0, 50, 10, 50);
        gbcBot.anchor = GridBagConstraints.CENTER;

        JButton confirmRefreshButton = makeButton("Refresh", refreshStat());
        buttonPanel.add(confirmRefreshButton, gbcBot);
        gbcBot.gridx++;
        JButton goBackButton = makeButton("Back", goBack());
        buttonPanel.add(goBackButton, gbcBot);
        buttonPanel.setBackground(beige);

        statPanel.add(buttonPanel, BorderLayout.SOUTH);

        return statPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(beige);

        GridBagConstraints gbcTop = new GridBagConstraints();
        gbcTop.gridx = 0;
        gbcTop.gridy = 0;
        gbcTop.insets = new Insets(5, 10, 5, 10);
        gbcTop.anchor = GridBagConstraints.WEST;

        JLabel instructionLabel = new JLabel("How many past logs do you want to view: ");
        inputPanel.add(instructionLabel, gbcTop);
        gbcTop.gridx++;

        numLogs = createPlaceholderTextField("1 - " + (log.getSize() - 1));
        numLogs.setPreferredSize(new Dimension(200, 30));

        inputPanel.add(numLogs);

        return inputPanel;
    }

    private JScrollPane createLogStatsPanel() {
        GridBagConstraints gbcCenter = createGBC();

        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(new GridBagLayout());
        logsPanel.setBackground(beige);

        if (stats != null) {
            for (String s : stats) {
                logsPanel.add(createLabelWithBorder(s), gbcCenter);
                gbcCenter.gridy++;
            }
        } else {
            try {
                for (String s : log.characterStatLastFew(log.getSize())) {
                    logsPanel.add(createLabelWithBorder(s), gbcCenter);
                    gbcCenter.gridy++;
                }
            } catch (IndexOutOfBound g) {
                System.out.println("Error has occurred when loading statistics");
            }
        }

        JScrollPane scrollPane = new JScrollPane(logsPanel);
        scrollPane.setPreferredSize(new Dimension(WIDTH - 100, 600));

        return scrollPane;
    }

    private JLabel createLabelWithBorder(String s) {
        JLabel id = new JLabel(s);
        id.setBorder(createStatBorder());
        return id;
    }

    private CompoundBorder createStatBorder() {
        Border raisedBorder = BorderFactory.createRaisedBevelBorder();
        Border lowerBorder = BorderFactory.createLoweredBevelBorder();

        return BorderFactory.createCompoundBorder(raisedBorder, lowerBorder);
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
                    log.addLog(new MatchLog(name, damage, kill, death, mvp, deltaTrophy));
                    mainPanel.remove(mainScreen);
                    updateMainScreen();
                    resetInput();
                    mainPanel.add(mainScreen, "MainScreen");
                    cards.show(mainPanel, "MainScreen");
                    dataSaved = false;
                } catch (Exception g) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Couldn't add log");
                }
            }
        };
    }

    private ActionListener refreshStat() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String numberLogs = numLogs.getText();
                    stats = log.characterStatLastFew(Integer.parseInt(numberLogs));
                    mainPanel.remove(statScreen);
                    statScreen = createStatScreen();
                    mainPanel.add(statScreen, "StatScreen");
                    cards.show(mainPanel, "StatScreen");
                } catch (IndexOutOfBound | NumberFormatException g) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Couldn't view statistics");
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
                    dataSaved = false;
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
                dataSaved = true;
            }
        };
    }

    private ActionListener createLoadMatchesAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMatches();
                mainPanel.remove(mainScreen);
                updateMainScreen();
                mainPanel.add(mainScreen, "MainScreen");
                cards.show(mainPanel, "MainScreen");
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
                dataSaved = false;
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
                mainPanel.remove(statScreen);
                statScreen = createStatScreen();
                mainPanel.add(statScreen, "StatScreen");
                cards.show(mainPanel, "StatScreen");
            }
        };
    }


    public static void main(String[] args) {
        new MatchUi();
    }
}
