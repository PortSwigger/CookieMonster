package ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;
import controllers.TableController;
import network.Downloader;
import ui.panels.CookiesPanel;
import ui.panels.SettingsPanel;

import javax.swing.*;
import java.awt.*;

public class CookieMonsterTab extends JComponent {

    private final TableController tableController;

    private final JTabbedPane tabbedPane;

    private final Downloader downloader;

    private final Logging logging;

    public CookieMonsterTab(MontoyaApi api, TableController tableController) {
        this.logging = api.logging();
        this.tableController = tableController;

        this.downloader = new Downloader(api);
        this.tabbedPane = new JTabbedPane();

        setup();
    }

    private void setup() {
        addCookiesTab();
        addSettingsTab();

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void addCookiesTab() {
        CookiesPanel cookiesPanel = new CookiesPanel(tableController);
        tabbedPane.addTab("Cookies", cookiesPanel);
    }

    private void addSettingsTab() {
        SettingsPanel settingsPanel = new SettingsPanel(tableController, logging, downloader);
        tabbedPane.addTab("Settings", settingsPanel);
    }
}
