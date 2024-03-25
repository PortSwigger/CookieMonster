package ui.tables;

import burp.api.montoya.core.ByteArray;
import controllers.TableController;
import ui.models.CookieTableModel;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static persistence.ProjectData.PROJECT_DATA_KEY;

public class CookiesTable extends JTable {

    private final TableController tableController;

    public CookiesTable(TableController tableController) {
        super(new CookieTableModel(tableController));

        this.tableController = tableController;

        setup();
    }

    private void setup() {
        getColumnModel().getColumn(0).setMaxWidth(200); // Set checkbox column width
        getColumnModel().getColumn(0).setCellRenderer(new CheckBoxRenderer());

        tableController.setTableModel((CookieTableModel) getModel());

        ByteArray saved = tableController.getProjectData().getByteArrayByKey(PROJECT_DATA_KEY);
        if (saved == null) {
            // import default data from resources
            tableController.loadByteArray(tableController.getDefaultData());
        } else {
            // import any data saved for the project.
            tableController.importData(saved.getBytes());
        }
    }

    static class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            setSelected((value != null && (Boolean) value));
            return this;
        }
    }
}
