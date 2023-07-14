package extension;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.logging.Logging;

public class CopyToBcheck implements BurpExtension
{
    @Override
    public void initialize(MontoyaApi api)
    {
        // set extension name
        api.extension().setName("Copy to bcheck extension");

        Logging logging = api.logging();

        // write a message to our output stream
        logging.logToOutput("Loading extension.");

        // register menu entry
        api.userInterface().registerContextMenuItemsProvider(new MyContextMenuItemsProvider(api));

        // throw an exception that will appear in our error stream
        //throw new RuntimeException("Hello exception.");
    }
}