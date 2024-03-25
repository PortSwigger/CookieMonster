package ui.panels;

import controllers.TableController;
import ui.inputs.JPlaceholderTextField;
import ui.models.CookieTableModel;
import ui.tables.CookiesTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.regex.PatternSyntaxException;

public class CookiesPanel extends JPanel {

    private final CookiesTable cookiesTable;

    private final JTextField searchField;

    public CookiesPanel(TableController tableController) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the panel

        // Search Box
        searchField = new JPlaceholderTextField("Search by cookie");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // Add space to the right of the search field
        tableController.setSearchField(searchField);

        // Buttons
        JButton enableAllButton = new JButton("Enable All");
        enableAllButton.addActionListener(e -> enableAll());

        JButton disableAllButton = new JButton("Disable All");
        disableAllButton.addActionListener(e -> disableAll());

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.LINE_AXIS));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(5)); // Add space between search field and buttons
        searchPanel.add(enableAllButton);
        searchPanel.add(Box.createHorizontalStrut(5)); // Add space between buttons
        searchPanel.add(disableAllButton);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0)); // Add space below the search panel

        // Cookies Table
        cookiesTable = new CookiesTable(tableController);

        JScrollPane scrollPane = new JScrollPane(cookiesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); // Add space above the table

        // Add components to the main panel
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void filterTable() {
        String searchText = searchField.getText();
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(cookiesTable.getModel());
        cookiesTable.setRowSorter(sorter);
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 3)); // 3 is the index of the Cookie column
            } catch (PatternSyntaxException e) {
                sorter.setRowFilter(null);
            }
        }
    }

    private void enableAll() {
        CookieTableModel model = (CookieTableModel) cookiesTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(true, i, 0);
        }
    }

    private void disableAll() {
        CookieTableModel model = (CookieTableModel) cookiesTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(false, i, 0);
        }
    }
}

