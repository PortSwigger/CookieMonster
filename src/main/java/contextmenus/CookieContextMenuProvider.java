package contextmenus;

import burp.api.montoya.core.Range;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import burp.api.montoya.ui.contextmenu.MessageEditorHttpRequestResponse;
import controllers.TableController;
import ui.models.CookieModel;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CookieContextMenuProvider implements ContextMenuItemsProvider {

    private final TableController tableController;

    private final Frame frame;

    public CookieContextMenuProvider(TableController tableController, Frame frame) {
        this.tableController = tableController;
        this.frame = frame;
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event) {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER)) {
            List<Component> menuItemList = new ArrayList<>();

            JMenuItem filterCookieItem = new JMenuItem("Filter Cookie");

            MessageEditorHttpRequestResponse requestResponse = event.messageEditorRequestResponse().orElseThrow();

            if (requestResponse.selectionOffsets().isPresent()) {
                menuItemList.add(filterCookieItem);
            }

            // When selecting the menu item
            filterCookieItem.addActionListener(l -> {
                byte[] selectedBytes;

                Range selectedRange = requestResponse.selectionOffsets().orElseThrow();

                // Get the text from the selected range
                if (requestResponse.selectionContext() == MessageEditorHttpRequestResponse.SelectionContext.REQUEST) {
                    HttpRequest request = requestResponse.requestResponse().request();

                    selectedBytes = Arrays.copyOfRange(request.toByteArray().getBytes(), selectedRange.startIndexInclusive(),
                            selectedRange.endIndexExclusive());

                } else {
                    HttpResponse response = requestResponse.requestResponse().response();

                    selectedBytes = Arrays.copyOfRange(response.toByteArray().getBytes(), selectedRange.startIndexInclusive(),
                            selectedRange.endIndexExclusive());

                }

                String selected = new String(selectedBytes, StandardCharsets.UTF_8);
                String host = requestResponse.requestResponse().httpService().host();

                // Open a context menu to add data
                open(selected, host);
            });

            return menuItemList;
        }

        return null;
    }

    private void open(String cookie, String host) {
        // User submittable components
        JLabel cookieLabel = new JLabel("Cookie");
        JTextField cookieTextField = new JTextField(cookie, 20);
        JCheckBox checkBox = new JCheckBox("Is Wildcard");

        // Create a panel to hold the components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 10); // Add spacing between label and text field
        panel.add(cookieLabel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(cookieTextField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(checkBox, gbc);

        int option = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Filter Cookie",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option == JOptionPane.OK_OPTION) {
            String result = cookieTextField.getText();
            String category = "Custom";
            int wildcard = checkBox.isSelected() ? 1 : 0;

            if (!result.isEmpty()) {
                tableController.addRow(
                        new CookieModel(
                                true,
                                host,
                                category,
                                result,
                                wildcard
                        )
                );
            }
        }
    }
}
