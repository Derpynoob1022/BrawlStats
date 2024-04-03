package ui;

import model.EventLog;
import model.Event;
import model.MatchList;
import model.MatchLog;
import model.exception.IllegalValueException;
import model.exception.IndexOutOfBound;
import model.exception.NoMatchingFields;
import persistence.JsonReader;
import persistence.JsonWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

// the graphical user interface for the application
class MatchUi extends JFrame {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 700;
    private CardLayout cards;
    private MatchList log;
    private static JPanel mainPanel;
    private JPanel startScreen;
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
    private int selectedId = -1;
    private JComboBox<String> comboBox;
    private JTextField numLogs;
    private ArrayList<String> stats;
    private Boolean dataSaved;
    private Boolean overrideWarning;

    // EFFECTS: constructor that creates a new window with buttons on the user's screen
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
        overrideWarning = false;

        addWindowListener(createWindowListener());

        setResizable(false);
        add(initCards());
        setVisible(true);
    }

    // MODIFIES: this
    // EFFECTS: initiate the cards in the card-layout
    private JPanel initCards() {
        mainPanel = new JPanel();
        cards = new CardLayout();
        mainPanel.setLayout(cards);

        startScreen = createStartScreen();
        updateMainScreen();
        editScreen = createEditScreen();
        addLogScreen = createAddLogScreen();
        statScreen =  createStatScreen();

        mainPanel.add(startScreen, "StartScreen");
        mainPanel.add(mainScreen, "MainScreen");
        mainPanel.add(addLogScreen, "AddLogScreen");
        mainPanel.add(editScreen, "EditScreen");
        mainPanel.add(statScreen, "StatScreen");

        return mainPanel;
    }

    // EFFECTS: creates a new window listener that opens a warning message when there is unsaved changes
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
                printLog();
            }
        };
    }

    // EFFECTS: centers the window in the middle of the screen
    private void centreOnScreen() {
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        setLocation((width - getWidth()) / 2, (height - getHeight()) / 2);
    }

    // MODIFIES: this
    // EFFECTS: saves the current match list to the file at destination
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
    // EFFECTS: loads the saved match list from the file at destination
    private void loadMatches() {
        try {
            log = jsonReader.read();
            System.out.println("Loaded " + log.getName() + " from " + JSON_DESTINATION);
        } catch (IllegalValueException | IOException e) {
            System.out.println("Unable to read from file: " + JSON_DESTINATION);
        }
    }

    // EFFECTS: creates a JButton
    private static JButton makeButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.addActionListener(listener);
        return button;
    }

    // MODIFIES: this
    // EFFECTS: creates the startup screen
    public JPanel createStartScreen() {
        try {
            BufferedImage background = ImageIO.read(new File("./data/background.jpg"));
            Image scaledBackground = background.getScaledInstance(2160, 1212, Image.SCALE_DEFAULT);
            JPanel startScreen = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(scaledBackground, 0, 0, null);
                }
            };
            startScreen.setLayout(new BorderLayout());
            BufferedImage myPicture = ImageIO.read(new File("./data/trophy.png"));
            JLabel picLabel = new JLabel(new ImageIcon(myPicture));
            startScreen.add(picLabel, BorderLayout.CENTER);

            JButton start = makeButton("Start", startApp());
            start.setPreferredSize(new Dimension(200, 100));
            startScreen.add(start, BorderLayout.SOUTH);
            return startScreen;
        } catch (IOException e) {
            System.exit(0);
        }
        return null;
    }


    // MODIFIES: this
    // EFFECTS: creates/updates the main screen with its various GUI components
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

    // EFFECTS: creates the JPanel for the logs to be displayed
    private JPanel initLogPanel() {
        JPanel logsPanel = new JPanel();
        logsPanel.setLayout(new GridBagLayout());
        logsPanel.setBackground(beige);
        GridBagConstraints gbcTop = createGBC(5, 10, 5, 10);

        int startNum = Integer.max(0, log.getSize() - 30);

        if (log.getSize() == 0) {
            return logsPanel;
        } else {
            try {
                for (int i = log.getSize() - 1; i >= startNum; i--) {
                    MatchLog l = log.getLog(i);
                    JLabel id = new JLabel(l.logToString());
                    id.setBorder(createColorBorder(evalColor(l)));
                    id.putClientProperty("index", i);
                    id.addMouseListener(createLogMouseListener(id, i));
                    logsPanel.add(id, gbcTop);
                    gbcTop.gridy++;
                }
            } catch (IndexOutOfBound e) {
                System.out.println("An error has occurred");
            }
        }
        return logsPanel;
    }

    // EFFECTS: evaluate which color the border of the log should be
    public Color evalColor(MatchLog l) {
        if (l.getDeltaTrophy() > 0) {
            return Color.GREEN;
        } else if (l.getDeltaTrophy() < 0) {
            return Color.RED;
        } else {
            return Color.BLACK;
        }
    }

    // EFFECTS: creates a mouse listener for the Jlabel at a certain index
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

    // MODIFIES: this
    // EFFECTS: adds the created button panel to the main panel
    private void createMainPanelButtons(JPanel panel) {
        JPanel buttons = createButtonsPanel();
        addButtons(buttons);
        panel.add(buttons, BorderLayout.SOUTH);
    }

    // EFFECTS: creates the button panel with buttons inside
    private JPanel createButtonsPanel() {
        JPanel buttons = new JPanel(new GridLayout(6, 1));
        buttons.setVisible(true);
        return buttons;
    }

    // MODIFIES: this
    // EFFECTS: Adds buttons to the specified panel with the given constraints.
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

    // MODIFIES: this
    // EFFECTS: makes the button while adding to the gridbaglayout
    private void addButton(JPanel panel, String buttonText, ActionListener actionListener, GridBagConstraints gbc) {
        JButton button = makeButton(buttonText, actionListener);
        panel.add(button, gbc);
        gbc.gridy++;
    }

    // EFFECTS: creates a new panel for the addLog card
    private JPanel createAddLogScreen() {
        JPanel addLogPanel = new JPanel();
        addLogPanel.setLayout(new BorderLayout());
        addLogPanel.setBackground(beige);

        addLogPanel.add(createTextPanel(), BorderLayout.CENTER);

        addLogPanel.setBorder(createCompoundTitleBorder("Add log"));

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

    // EFFECTS: creates the text and input panel
    private JPanel createTextPanel() {
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBackground(beige);

        GridBagConstraints gbcTop = createGBC(20, 10, 20, 10);
        addLabels(textPanel, gbcTop);
        gbcTop.gridx++;
        addFields(textPanel, gbcTop);

        return textPanel;
    }

    // EFFECTS: creates a new GridBagConstraints for the given numbers as the inserts
    private GridBagConstraints createGBC(int top, int left, int bottom, int right) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(top, left, bottom, right);
        gbc.anchor = GridBagConstraints.WEST;
        return gbc;
    }

    // MODIFIES: this
    // EFFECTS: adds the labels to the panel
    private void addLabels(JPanel textPanel, GridBagConstraints gbcTop) {
        String[] labels = {"Character Name: ", "Damage: ", "Kills: ", "Deaths: ", "Star player: ", "Delta trophy: "};
        for (String labelName : labels) {
            JLabel label = new JLabel(labelName);
            textPanel.add(label, gbcTop);
            gbcTop.gridy++;
        }
    }

    // MODIFIES: this
    // EFFECTS: adds the text boxes to the panel
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

    // EFFECTS: creates a compound border with a title
    private CompoundBorder createCompoundTitleBorder(String s) {
        Border borderLine = BorderFactory.createLineBorder(Color.black);
        EmptyBorder emptyBorder = new EmptyBorder(10, 10, 10, 10);
        TitledBorder tiltedBorder = BorderFactory.createTitledBorder(borderLine, s);

        return BorderFactory.createCompoundBorder(emptyBorder, tiltedBorder);
    }

    // EFFECTS: creates a compound border with a title
    private Border createColorBorder(Color color) {
        return BorderFactory.createLineBorder(color);
    }

    // EFFECTS: creates a new panel for the editLog card
    private JPanel createEditScreen() {
        JPanel editLogPanel = new JPanel();
        editLogPanel.setLayout(new BorderLayout());
        editLogPanel.setBorder(createCompoundTitleBorder("Edit Log"));
        editLogPanel.setBackground(beige);

        JLabel selectedLog;
        try {
            if (selectedId == -1) {
                selectedLog = new JLabel("Selected log: none");
            } else {
                selectedLog = new JLabel("Selected log: " + log.getLog(selectedId).logToString());
            }
            editLogPanel.add(selectedLog, BorderLayout.NORTH);
        } catch (IndexOutOfBound g) {
            System.out.println("An error has occured");
        }

        editLogPanel.add(createComboBoxPanel(), BorderLayout.CENTER);

        editLogPanel.add(createEditButtonPanel(), BorderLayout.SOUTH);

        return editLogPanel;
    }

    // EFFECTS: creates a new dropdown box
    private JPanel createComboBoxPanel() {
        String[] items = {"Name", "Damage", "Kills", "Deaths", "Mvp", "Trophy"};

        comboBox = new JComboBox<>(items);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBackground(beige);

        GridBagConstraints gbcTop = createGBC(20, 10, 20, 10);
        gbcTop.fill = GridBagConstraints.HORIZONTAL;

        textPanel.add(comboBox, gbcTop);

        gbcTop.weightx = 1;
        gbcTop.gridx++;

        replaceField = createPlaceholderTextField("Replacement");

        textPanel.add(replaceField, gbcTop);

        return textPanel;
    }

    // EFFECTS: creates a new button panel for the editPanel
    private JPanel createEditButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcBot = createGBC(0, 50, 10, 50);

        JButton confirmAddLogButton = makeButton("Edit log", finishEditLog());
        buttonPanel.add(confirmAddLogButton, gbcBot);
        gbcBot.gridx++;
        JButton goBackButton = makeButton("Back", goBack());
        buttonPanel.add(goBackButton, gbcBot);
        buttonPanel.setBackground(beige);

        return buttonPanel;
    }

    // EFFECTS: creates a panel for the statScreen card
    private JPanel createStatScreen() {
        JPanel statPanel = new JPanel();
        statPanel.setLayout(new BorderLayout());
        statPanel.setBorder(createCompoundTitleBorder("Statistics"));
        statPanel.setBackground(beige);

        statPanel.add(createInputPanel(), BorderLayout.NORTH);

        statPanel.add(createLogStatsPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbcBot = createGBC(0, 50, 10, 50);

        JButton confirmRefreshButton = makeButton("Refresh", refreshStat());
        buttonPanel.add(confirmRefreshButton, gbcBot);
        gbcBot.gridx++;
        JButton goBackButton = makeButton("Back", goBack());
        buttonPanel.add(goBackButton, gbcBot);
        buttonPanel.setBackground(beige);

        statPanel.add(buttonPanel, BorderLayout.SOUTH);

        return statPanel;
    }

    // EFFECTS: creates the text and input panel
    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBackground(beige);

        GridBagConstraints gbcTop = createGBC(5, 10, 5, 10);

        JLabel instructionLabel = new JLabel("How many past logs do you want to view: ");
        inputPanel.add(instructionLabel, gbcTop);
        gbcTop.gridx++;

        String text = Integer.min(1, log.getSize()) + " - " + Integer.max(0, log.getSize());
        numLogs = createPlaceholderTextField(text);
        numLogs.setPreferredSize(new Dimension(200, 30));

        inputPanel.add(numLogs);

        return inputPanel;
    }

    // EFFECTS: creates the scroll panel that includes all the statistic entries
    private JScrollPane createLogStatsPanel() {
        GridBagConstraints gbcCenter = createGBC(5, 10, 5, 10);

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

    // EFFECTS: creates a label with the given string
    private JLabel createLabelWithBorder(String s) {
        JLabel id = new JLabel(s);
        id.setBorder(createStatBorder());
        return id;
    }

    // EFFECTS: creates a special border for statistics entries
    private Border createStatBorder() {
        Border raisedBorder = BorderFactory.createRaisedBevelBorder();
        Border lowerBorder = BorderFactory.createLoweredBevelBorder();

        return BorderFactory.createCompoundBorder(raisedBorder, lowerBorder);
    }

    // MODIFIES: this
    // EFFECTS: adds a button that switches the screen to the MainScreen
    private ActionListener startApp() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.show(mainPanel, "MainScreen");
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: takes the values from the addLogScreen and creates a new log. After that repaint the
    // components to display the changes
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
                    overrideWarning = true;
                } catch (Exception g) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Couldn't add log");
                }
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: refreshes the display when user requests to view the stats for another set of logs
    private ActionListener refreshStat() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String numberLogs = numLogs.getText();
                    stats = log.characterStatLastFew(Math.min(log.getSize(), Integer.parseInt(numberLogs)));
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

    // Modifies: this
    // EFFECTS: takes the values from the editLogScreen and edits an existing log. After that repaint the
    // components to display the changes
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
                    overrideWarning = true;
                } catch (IndexOutOfBound g) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Index out of bounds!");
                } catch (NoMatchingFields | NumberFormatException h) {
                    JOptionPane.showMessageDialog(MatchUi.this, "Invalid data entered!");
                }
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: reset all the previous inputs in the text boxes
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

    // EFFECTS: creates a text box where when nothing is inputted, it shows what belongs there
    private JTextField createPlaceholderTextField(String placeholder) {
        JTextField textField = new JTextField(placeholder);
        textField.setBackground(beige);

        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
        return textField;
    }

    // MODIFIES: this
    // EFFECTS: goes back to the mainPanel
    private ActionListener goBack() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.show(mainPanel, "MainScreen");
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: switches to the addLogScreen
    private ActionListener createAddLogAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cards.show(mainPanel, "AddLogScreen");
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: saves the matches
    private ActionListener createSaveMatchesAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMatches();
                dataSaved = true;
            }
        };
    }

    // MODIFIES: this
    // EFFECTS: loads in the matches from the JSON file. Displays a warning when data is overridden. Also refresh screen
    private ActionListener createLoadMatchesAction() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!overrideWarning) {
                    executeLoadMatches();
                } else {
                    int choice = JOptionPane.showConfirmDialog(MatchUi.this,
                            "Are you sure you want to Override existing data?",
                            "Load Reminder", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        executeLoadMatches();
                    }
                }
            }
        };
    }

    // EFFECTS: execute and load the matches
    private void executeLoadMatches() {
        dataSaved = true;
        loadMatches();
        mainPanel.remove(mainScreen);
        updateMainScreen();
        mainPanel.add(mainScreen, "MainScreen");
        cards.show(mainPanel, "MainScreen");
    }

    // MODIFIES: this
    // EFFECTS: removes the selected log
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

    // MODIFIES: this
    // EFFECTS: switches to the editLogScreen
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

    // MODIFIES: this
    // EFFECTS: switches to the viewStatsScreen
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

    private void printLog() {
        for (Event next : EventLog.getInstance()) {
            System.out.println(next.toString() + "\n");
        }
    }

    public static void main(String[] args) {
        new MatchUi();
    }
}
