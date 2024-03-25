package ui.models;

import controllers.TableController;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CookieTableModel extends AbstractTableModel {

    private final String[] columnNames = {
            "Enabled?", // 0
            "Platform", // 1
            "Category", // 2
            "Cookie",   // 3
            "Wildcard"  // 4
    };

    private final TableController tableController;

    private List<CookieModel> data = new ArrayList<>();

    public CookieTableModel(TableController tableController) {
        this.tableController = tableController;
    }

    public List<CookieModel> getData() {
        return data;
    }

    public void addRow(CookieModel model) {
        for (CookieModel cookieModel : data) {
            if (model.equals(cookieModel)) {
                return;
            }
        }

        data.add(model);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void emptyRows() {
        this.data = new ArrayList<>();
        this.fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Boolean.class; // Set first column to display checkboxes
        }
        return super.getColumnClass(columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0; // Allow editing only in the first column
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        CookieModel model = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> model.isSelected();
            case 1 -> model.getPlatform();
            case 2 -> model.getCategory();
            case 3 -> model.getCookie();
            case 4 -> model.getWildcard();
            default -> null;
        };
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        CookieModel model = data.get(rowIndex);
        switch (columnIndex) {
            case 0 -> {
                if((boolean) value) {
                    tableController.track(model);
                } else {
                    tableController.remove(model);
                }
                model.setSelected((boolean) value);
            }
            case 1 -> model.setPlatform((String) value);
            case 2 -> model.setCategory((String) value);
            case 3 -> model.setCookie((String) value);
            case 4 -> model.setWildcard((int) value);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}