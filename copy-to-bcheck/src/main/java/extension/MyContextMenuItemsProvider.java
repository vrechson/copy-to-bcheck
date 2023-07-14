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
    private final JMenuItem hostBcheck;
    private final JMenuItem passiveBcheck;

    public MyContextMenuItemsProvider(MontoyaApi api)
    {
        this.api = api;
        hostBcheck = new JMenuItem("Copy host bcheck");
        passiveBcheck = new JMenuItem("Copy passive bcheck");

        //api.logging().logToOutput("hi");
    }

    @Override
    public List<Component> provideMenuItems(ContextMenuEvent event)
    {
        if (event.isFromTool(ToolType.PROXY, ToolType.TARGET, ToolType.LOGGER, ToolType.REPEATER))
        {
            List<Component> menuItemList = new ArrayList<>();

            HttpRequestResponse requestResponse = event.messageEditorRequestResponse().isPresent() ? event.messageEditorRequestResponse().get().requestResponse() : event.selectedRequestResponses().get(0);


            hostBcheck.addActionListener(l -> new Bcheck(api, requestResponse, "host" , ""));
            menuItemList.add(hostBcheck);

            String args = "";

            if (event.messageEditorRequestResponse().get().selectionContext().toString() == "RESPONSE") {
                args = event.messageEditorRequestResponse().get().requestResponse().response().toString().substring(event.messageEditorRequestResponse().get().selectionOffsets().get().startIndexInclusive(), event.messageEditorRequestResponse().get().selectionOffsets().get().endIndexExclusive());
            } else if (event.messageEditorRequestResponse().get().selectionContext().toString() == "REQUEST") {
                args = event.messageEditorRequestResponse().get().requestResponse().request().toString().substring(event.messageEditorRequestResponse().get().selectionOffsets().get().startIndexInclusive(), event.messageEditorRequestResponse().get().selectionOffsets().get().endIndexExclusive());
            }

            String finalArgs = args;
            passiveBcheck.addActionListener(l -> new Bcheck(api, requestResponse, "passive", finalArgs));
            menuItemList.add(passiveBcheck);

            return menuItemList;
        }

        return null;
    }
}
