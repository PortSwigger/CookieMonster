package ui.panels;

import burp.api.montoya.logging.Logging;
import controllers.TableController;
import network.Downloader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.concurrent.*;

import static network.Downloader.DEFAULT_FILE;

public class SettingsPanel extends JPanel {

    private final TableController tableController;

    private final Downloader downloader;

    private final Logging logging;

    private final JTextField textField;

    private final JButton downloadButton;

    private final JButton selectButton;

    private final JButton exportButton;

    private final JButton resetButton;

    public SettingsPanel(TableController tableController, Logging logging, Downloader downloader) {
        this.tableController = tableController;
        this.downloader = downloader;
        this.logging = logging;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        // Text Field
        textField = new JTextField(20);
        textField.setText(DEFAULT_FILE);
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height)); // Ensure maximum width

        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files (*.csv)", "csv");

        // Select Button
        selectButton = new JButton("Load File");
        selectButton.addActionListener(e -> {
            setComponentState(false);

            // if table is not empty
            if (!tableController.getTableModel().getData().isEmpty()) {

                // verify with the user they are ok overwriting their config
                if (openConfirmMenu() == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileFilter(filter);

                    int returnValue = fileChooser.showOpenDialog(SettingsPanel.this);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        loadFromFile(fileChooser.getSelectedFile());
                    }
                }

            } else {
                // table is empty
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(filter);

                int returnValue = fileChooser.showOpenDialog(SettingsPanel.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    loadFromFile(fileChooser.getSelectedFile());
                }
            }

            setComponentState(true);

        });

        // Download Button
        downloadButton = new JButton("Download");
        downloadButton.addActionListener(e -> {
            setComponentState(false);

            // table is not empty
            if (!tableController.getTableModel().getData().isEmpty()) {
                if (openConfirmMenu() == JOptionPane.YES_OPTION) {
                    loadFromURL(textField.getText());
                }
            } else {
                loadFromURL(textField.getText());
            }

            setComponentState(true);
        });

        // Export Button
        exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            setComponentState(false);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(filter);

            int returnValue = fileChooser.showOpenDialog(SettingsPanel.this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String exportFilePath = selectedFile.getAbsolutePath() + ".csv";

                exportToFile(exportFilePath);
            }

            setComponentState(true);
        });

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            setComponentState(false);

            if (!tableController.getTableModel().getData().isEmpty()) {
                if (openConfirmMenu() == JOptionPane.YES_OPTION) {
                    loadDefaultFile();
                    textField.setText(DEFAULT_FILE);
                }
            } else {
                loadDefaultFile();
                textField.setText(DEFAULT_FILE);
            }

            setComponentState(true);
        });

        JLabel titleLabel = new JLabel("Configuration");
        titleLabel.setFont(new Font(
                titleLabel.getFont().getFontName(),
                Font.BOLD,
                titleLabel.getFont().getSize()
        ));

        JLabel bottomLabel = new JLabel(
                "Note: Your .CSV file should have these columns: " +
                        "Enabled, Platform, Category, Cookie / Data Key name, Wildcard match"
        );

        // Panel for buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        buttonsPanel.add(downloadButton);
        buttonsPanel.add(selectButton);
        buttonsPanel.add(exportButton);
        buttonsPanel.add(resetButton);

        // Panel for search field and buttons
        JPanel searchAndButtonsPanel = new JPanel();
        searchAndButtonsPanel.setLayout(new BoxLayout(searchAndButtonsPanel, BoxLayout.LINE_AXIS));
        searchAndButtonsPanel.add(textField);
        searchAndButtonsPanel.add(Box.createHorizontalStrut(10)); // Add space between search field and buttons
        searchAndButtonsPanel.add(buttonsPanel);

        // Panel for settings
        JPanel settingsPanel = new JPanel(new BorderLayout());
        settingsPanel.add(titleLabel, BorderLayout.NORTH);
        settingsPanel.add(searchAndButtonsPanel, BorderLayout.CENTER);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(settingsPanel, BorderLayout.NORTH);
        mainPanel.add(bottomLabel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.NORTH);
    }

    private int openConfirmMenu() {
        return JOptionPane.showConfirmDialog(
                this,
                "This will delete any previous configurations.\nWould you like to continue?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION
        );
    }


    /**
     * Used to enable / disable components
     * @param state - the state of the component
     */
    private void setComponentState(boolean state) {
        downloadButton.setEnabled(state);
        selectButton.setEnabled(state);
        exportButton.setEnabled(state);
        resetButton.setEnabled(state);
        textField.setEnabled(state);
    }

    private void submitRunnable(Runnable runnable) {
        Executors.newCachedThreadPool().execute(runnable);
    }

    /**
     * Load table data via local file.
     */
    private void loadFromFile(File file) {
        textField.setText(file.getAbsolutePath());

        logging.logToOutput("Loading file: " + file.getAbsolutePath());

        submitRunnable(() -> {
            tableController.loadFile(file);
        });
    }

    /**
     * Load table data from a URL.
     */
    private void loadFromURL(String url) {
        logging.logToOutput("Submitting download task.");

        submitRunnable(() -> {
            byte[] data = downloader.getData(url);
            if (data != null) {
                tableController.loadByteArray(data);
            }

            logging.logToOutput("Finished download task.");
        });
    }

    /**
     * Load table data via embedded file.
     */
    private void loadDefaultFile() {
        byte[] defaultData = tableController.getDefaultData();

        if (defaultData != null) {
            submitRunnable(() -> {
                tableController.loadByteArray(defaultData);

                logging.logToOutput("Finished reset task.");
            });
        }
    }

    /**
     * Export table data to a .CSV file.
     *
     * @param path - file name to save to.
     */
    private void exportToFile(String path) {
        logging.logToOutput("Exporting to: " + path);

        submitRunnable(() -> {
            tableController.exportAsCSV(path);

            logging.logToOutput("Finished export task.");
        });
    }
}
