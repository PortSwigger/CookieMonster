import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.persistence.PersistedObject;
import contextmenus.CookieContextMenuProvider;
import controllers.TableController;
import handlers.RequestHandler;
import persistence.ProjectData;
import ui.CookieMonsterTab;

import static persistence.ProjectData.PROJECT_DATA_KEY;

@SuppressWarnings("unused")
public class CookieMonsterExtension implements BurpExtension {

    private static final String VERSION = "1.0.0";

    private static final String EXTENSION_NAME = "CookieMonster";

    private static final String CREATOR = "[@] baegmon@gmail.com";

    private static final String SOURCE = "[@] https://github.com/baegmon/CookieMonster";

    @Override
    public void initialize(MontoyaApi api) {
        api.extension().setName(EXTENSION_NAME);

        api.logging().logToOutput(String.format("%s v%s", EXTENSION_NAME, VERSION));
        api.logging().logToOutput(CREATOR);
        api.logging().logToOutput(SOURCE);
        api.logging().logToOutput("");

        PersistedObject persistedObject = api.persistence().extensionData();

        ProjectData projectData = new ProjectData(persistedObject);
        TableController tableController = new TableController(api.logging(), projectData);

        api.proxy().registerRequestHandler(new RequestHandler(tableController));
        api.userInterface().registerSuiteTab(EXTENSION_NAME, new CookieMonsterTab(api, tableController));

        api.userInterface().registerContextMenuItemsProvider(
                new CookieContextMenuProvider(tableController, api.userInterface().swingUtils().suiteFrame())
        );

        api.extension().registerUnloadingHandler(() -> {
            api.logging().logToOutput("Unloading CookieMonster");

            byte[] data = tableController.serializeTableData();
            projectData.saveByteArray(PROJECT_DATA_KEY, data);
        });
    }
}