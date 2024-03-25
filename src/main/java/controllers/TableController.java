package controllers;

import burp.api.montoya.logging.Logging;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import csv.CookieFileBean;
import org.apache.commons.lang3.SerializationUtils;
import persistence.ProjectData;
import ui.models.CookieModel;
import ui.models.CookieTableModel;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TableController {

    private static final String DEFAULT_FILE_NAME = "open-cookie-database.csv";

    private final Logging logging;

    private final ProjectData projectData;

    private CookieTableModel tableModel;

    private JTextField searchField;

    private final HashSet<CookieModel> enabled = new HashSet<>();

    public TableController(Logging logging, ProjectData projectData) {
        this.logging = logging;
        this.projectData = projectData;
    }

    public ProjectData getProjectData() {
        return projectData;
    }

    public CookieTableModel getTableModel() {
        return tableModel;
    }

    public void setTableModel(CookieTableModel tableModel) {
        this.tableModel = tableModel;
    }

    public void setSearchField(JTextField searchField) {
        this.searchField = searchField;
    }

    /*
     * Helper functions used to populate JTable data.
     * */

    private void populateTable(CSVReader reader) {
        tableModel.emptyRows();
        searchField.setText("");

        List<CookieFileBean> beans = new CsvToBeanBuilder<CookieFileBean>(reader)
                .withType(CookieFileBean.class)
                .build()
                .parse();

        for (CookieFileBean bean : beans) {
            if (!bean.getCategory().equals("Functional")) {
                addRow(
                        new CookieModel(
                                bean.isSelected() == 1,
                                bean.getPlatform(),
                                bean.getCategory(),
                                bean.getCookie(),
                                bean.getWildcard()
                        )
                );
            }
        }
    }

    public void addRow(CookieModel cookie) {
        // Add any selected items to a HashSet first,
        // performance becomes O(n) vs O(n^2).

        if (cookie.isSelected()) {
            enabled.add(cookie);
        }
        tableModel.addRow(cookie);
    }

    /*
     * Helper functions used to import data into a CSVReader.
     * */

    public void loadFile(File file) {
        try (CSVReader csvReader = new CSVReader(new FileReader(file))) {
            populateTable(csvReader);
        } catch (IOException e) {
            logging.logToError(e);
        }
    }

    public void loadByteArray(byte[] data) {
        // Convert byte array to string
        String csvContent = new String(data);

        // Create a ByteArrayInputStream from the string
        try (ByteArrayInputStream stream = new ByteArrayInputStream(csvContent.getBytes()); BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
            CSVReader csvReader = new CSVReader(br);

            populateTable(csvReader);
        } catch (IOException e) {
            logging.logToError(e);
        }
    }

    public byte[] getDefaultData() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(DEFAULT_FILE_NAME)) {
            if (inputStream != null) {
                return inputStream.readAllBytes();
            } else {
                logging.logToError(String.format("Could not load resource '%s'", DEFAULT_FILE_NAME));
                return null;
            }
        } catch (IOException e) {
            logging.logToError(e);
            return null;
        }
    }

    /*
     * Helper functions used to import / export serialized data.
     * */

    public byte[] serializeTableData() {
        return SerializationUtils.serialize((ArrayList<CookieModel>) tableModel.getData());
    }

    public void importData(byte[] data) {
        List<CookieModel> imported = SerializationUtils.deserialize(data);

        for (CookieModel model : imported) {
            addRow(model);
        }
    }

    /*
    * Helper functions used to export data to a file.
    * */

    public void exportAsCSV(String path) {
        try (CSVWriter writer = new CSVWriter(
                new FileWriter(path),
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END)) {

            String[] header = {
                    "Enabled",
                    "Platform",
                    "Category",
                    "Cookie / Data Key name",
                    "Wildcard match"
            };
            writer.writeNext(header);

            for (CookieModel model : tableModel.getData()) {
                String[] line = {
                        model.isSelected() ? "1" : "0",
                        model.getPlatform(),
                        model.getCategory(),
                        model.getCookie(),
                        String.valueOf(model.getWildcard())
                };
                writer.writeNext(line);
            }

        } catch (IOException e) {
            logging.logToError(e);
        }
    }

    /*
    * Helper functions to track selected cookie filters
    * */

    public void track(CookieModel cookie) {
        enabled.add(cookie);
    }

    public void remove(CookieModel cookie) {
        enabled.remove(cookie);
    }

    public boolean check(String cookie) {
        for (CookieModel model : enabled) {
            // no need to check model.isSelected() since these are only selected items
            String rowCookie = model.getCookie();

            if (model.getWildcard() == 1 && cookie.startsWith(rowCookie)) {
                return true;
            }

            // intentionally not doing insensitivity checks
            if (rowCookie.equals(cookie)) {
                // only return true if selected.
                return true;
            }
        }
        return false;
    }
}
