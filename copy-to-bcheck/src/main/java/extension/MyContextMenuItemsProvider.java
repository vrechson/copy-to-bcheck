package extension;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyContextMenuItemsProvider implements ContextMenuItemsProvider
{

    private final MontoyaApi api;
    private final JMenuItem copyRequestToBcheck;

    public MyContextMenuItemsProvider(MontoyaApi api)
    {
        this.api = api;
        copyRequestToBcheck = new JMenuItem("Copy bcheck per host");

        //api.logging().logToOutput("hi");
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER, ToolType.REPEATER))
        {
            List<Component> menuItemList = new ArrayList<>();

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);

            copyRequestToBcheck.addActionListener(l -> new Bcheck(api, requestResponse, "host"));
            menuItemList.add(copyRequestToBcheck);

            return menuItemList;
        }

        return null;
    }
}
