package extension;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpRequestResponse;

public class Bcheck implements ActionListener, ClipboardOwner {
    public Bcheck(MontoyaApi api, HttpRequestResponse request, String mode, String args) {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        HashMap<String, Object> configured = new HashMap<>();
        panel.add(new JLabel("Bcheck name: "));
        JTextField bname = new JTextField("");
        panel.add(bname);

        panel.add(new JLabel("Description: "));
        JTextField desc = new JTextField("");
        panel.add(desc);

        panel.add(new JLabel("Author: "));
        JTextField author = new JTextField("");
        panel.add(author);

        JTextField sink = new JTextField("");
        JComboBox confidenceList = new JComboBox();
        JComboBox severityList = new JComboBox();
        JTextField regex = new JTextField("");

        switch (mode) {
            case "host":
                panel.add(new JLabel("Vulnerability indicator: "));
                sink = new JTextField("");
                panel.add(sink);

            case "passive":
                String[] severityStrings = { "Info", "Low", "Medium", "High"};
                panel.add(new JLabel("Severity: "));
                severityList = new JComboBox(severityStrings);
                panel.add(severityList);

                String[] confidenceStrings = { "Tentative", "Certain", "Firm"};
                panel.add(new JLabel("Confidence: "));
                confidenceList = new JComboBox(confidenceStrings);
                panel.add(confidenceList);

                panel.add(new JLabel("Regex: "));
                regex = new JTextField(args);
                panel.add(regex);
        }

//        panel.add(new JLabel("tags (comma separated): "));
//        JTextField tags = new JTextField("");
//        panel.add(tags);

//        panel.add(new JLabel("Request: "));
//        JTextArea area = new JTextArea(request);
//        panel.add(area);
//
//        configured.put("name", box);

        int result = JOptionPane.showConfirmDialog(api.userInterface().swingUtils().suiteFrame(), panel, "Template Config", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            //api.logging().logToOutput("ihuuuuu");


            String template =
                    "metadata:\n" +
                            "  language: v1-beta\n" +
                            "  name: \""+bname.getText()+"\"\n" +
                            "  description: \""+desc.getText()+"\"\n" +
                            "  author: \""+author.getText()+"\"\n";


            switch (mode) {

                case "host":
                    String method = request.request().method().toString();
                    String path = request.request().path().toString();
                    String param = request.request().parameters().toString();
                    String body = request.request().bodyToString();

                    template = template +
                            "\nrun for each:\n" +
                            "    potential_path =\n" +
                            "        \""+ path + param +"\",\n\n" +
                            "given host then\n" +
                            "    send request called check:\n" +
                            "        method: \""+ method +"\"\n" +
                            "        path: {potential_path}\n\n" +
                            "    if \""+ sink.getText() +"\" in {check.response.body} then\n" +
                            "        report issue:\n" +
                            "            severity: High\n" +
                            "            confidence: certain\n" +
                            "            detail: `"+bname.getText()+" found at {potential_path}.`\n" +
                            "            remediation: \"tbd.\"\n" +
                            "    end if";
                    Toolkit.getDefaultToolkit().getSystemClipboard()
                            .setContents(new StringSelection(template), this);

                case "passive":
                    template = template +
                            "tags: \"passive\"\n\n" +
                            "given response then\n" +
                            "    if {latest.response} matches \""+ regex.toString() +"\" then\n" +
                            "        report issue:\n" +
                            "            severity: "+ severityList.getSelectedItem().toString() +"\n" +
                            "            confidence: "+ confidenceList.getSelectedItem().toString() +"\n" +
                            "            detail: \""+"aaaaaaaa"+"\"\n" +
                            "            remediation: \""+"bbbbbbb"+"\"\n" +
                            "    end if";
            }

        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void lostOwnership(Clipboard aClipboard, Transferable aContents) {}

}