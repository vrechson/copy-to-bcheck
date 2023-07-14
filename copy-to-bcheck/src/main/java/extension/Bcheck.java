package extension;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;

public class Bcheck implements ActionListener, ClipboardOwner, Runnable {

    private MontoyaApi api = null;
    private HttpRequestResponse request = null;
    private String mode = "";
    private String args = "";


    public Bcheck(MontoyaApi api, HttpRequestResponse request, String mode, String args) {

        this.api = api;
        this.request = request;
        this.mode = mode;
        this.args = args;

        formatBcheck();
    }

    public void setRequestResponse (HttpRequestResponse request) {
        this.request = request;
    }

    public void setArgs (String args) {
        this.args = args;
    }

    public void formatBcheck() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        panel.add(new JLabel("Bcheck name: "));
        JTextField bname = new JTextField("");
        panel.add(bname);

        panel.add(new JLabel("Description: "));
        JTextField desc = new JTextField("");
        panel.add(desc);

        panel.add(new JLabel("Author: "));
        JTextField author = new JTextField("");
        panel.add(author);

        JComboBox successList = null, parameterList = null, severityList = null, confidenceList = null;
        JTextField sink = null, regex = null;


        switch (this.mode) {
            case "host":

                String[] successStrings = { "matches", "differs", "in", "is"};
                panel.add(new JLabel("Success if: "));
                successList = new JComboBox(successStrings);
                panel.add(successList);

                String[] parameterStrings = { "status code", "body", "headers", "response", "collaborator"};
                panel.add(new JLabel("Parameter: "));
                parameterList = new JComboBox(parameterStrings);
                panel.add(parameterList);

                panel.add(new JLabel("Value: "));
                sink = new JTextField(args);
                panel.add(sink);

                break;

            case "passive":

                panel.add(new JLabel("Regex: "));
                regex = new JTextField("");
                regex = new JTextField(this.args);
                panel.add(regex);

                break;
        }

        String[] severityStrings = { "Info", "Low", "Medium", "High"};
        panel.add(new JLabel("Severity: "));
        severityList = new JComboBox();
        severityList = new JComboBox(severityStrings);
        panel.add(severityList);

        String[] confidenceStrings = { "Tentative", "Certain", "Firm"};
        panel.add(new JLabel("Confidence: "));
        confidenceList = new JComboBox();
        confidenceList = new JComboBox(confidenceStrings);
        panel.add(confidenceList);

        int result = JOptionPane.showConfirmDialog(
                this.api.userInterface().swingUtils().suiteFrame(),
                panel,
                "Template Config",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );
        if (result == JOptionPane.OK_OPTION) {

            String template =
                    "metadata:\n" +
                            "  language: v1-beta\n" +
                            "  name: \""+bname.getText()+"\"\n" +
                            "  description: \""+desc.getText()+"\"\n" +
                            "  author: \""+author.getText()+"\"\n";


            switch (this.mode) {

                case "host":
                    String method = this.request.request().method().toString();
                    String path = this.request.request().path().toString();
                    String param = this.request.request().parameters().toString();
                    String body = this.request.request().bodyToString();

                    template +=
                            "\nrun for each:\n" +
                            "    potential_path =\n" +
                            "        \""+ path +"\"\n\n" +
                            "given host then\n" +
                            "    send request called check:\n" +
                            "        method: \""+ method +"\"\n" +
                            "        path: {potential_path}\n" +
                            "        headers: \n";

                            for (HttpHeader h : this.request.request().headers()) {
                                if (h.name().equals("Host")) {
                                    continue;
                                }
                                template +=
                                        "           \""+ h.name().toString().replaceAll("\"", "\\\\\\\"") +"\": `";
                                if (parameterList.getSelectedItem().toString() == "collaborator" && h.value().toString().contains(sink.getText().toString())) {
                                    api.logging().logToOutput(parameterList.getSelectedItem().toString());
                                    String s = h.value().toString().replaceAll(sink.getText().toString(), "{generate_collaborator_address()}");
                                    s = s.replaceAll("`", "\\\\`") + "`\n";
                                    template += s;
                                } else {
                                    String s = h.value().toString().replaceAll("`", "\\\\`") + "`,\n";
                                    template += s;
                                }
                            }

                            if (method == "PUT" || method == "POST" || method == "PATCH") {
                                if (parameterList.getSelectedItem().toString().equals("collaborator")) {
                                    body = body.replaceAll(sink.getText().toString(), "{generate_collaborator_address()}");
                                }

                                template +=
                                "        body: \n`" + body.replaceAll("`", "\\\\`") + "`\n";
                            }

                    template += "\n";
                            if (parameterList.getSelectedItem() == "collaborator") {
                                template += "    if any interactions then\n";
                            } else {
                                template += "    if \""+ sink.getText() +"\"";
                            }
                            if (successList.getSelectedItem().toString().equals("matches")) {
                                template += " matches";
                            } else if (successList.getSelectedItem().toString().equals("is")) {
                                template += " is";
                            } else if (successList.getSelectedItem().toString().equals("differs")) {
                                template += " differs";
                            } else if (successList.getSelectedItem().toString().equals("in")) {
                                template += " in";
                            }
                            if (parameterList.getSelectedItem().equals("status code")) {
                                template += " {check.response.status_code} then\n";
                            } else if (parameterList.getSelectedItem().equals("body")) {
                                template += " {check.response.body} then\n";
                            } else if (parameterList.getSelectedItem().equals("headers")) {
                                template += " {check.response.headers} then\n";
                            }  else if (parameterList.getSelectedItem().equals("response")) {
                                template += " {check.response} then\n";
                            }

                template +=
                            "        report issue:\n" +
                            "            severity: High\n" +
                            "            confidence: certain\n" +
                            "            detail: `"+bname.getText()+" found at {potential_path}.`\n" +
                            "            remediation: \"tbd.\"\n" +
                            "    end if";
                    break;

                case "passive":
                    String r = regex.getText();
                    template +=
                            "tags: \"passive\"\n\n" +
                            "given response then\n" +
                            "    if {latest.response} matches \""+ r.replaceAll("\"", "\\\\\\\"") +"\" then\n" +
                            "        report issue:\n" +
                            "            severity: "+ severityList.getSelectedItem().toString() +"\n" +
                            "            confidence: "+ confidenceList.getSelectedItem().toString() +"\n" +
                            "            detail: \""+"aaaaaaaa"+"\"\n" +
                            "            remediation: \""+"bbbbbbb"+"\"\n" +
                            "    end if";
                    break;
            }

            if (!template.equals("")) {
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(template), this);
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void lostOwnership(Clipboard aClipboard, Transferable aContents) {}

    @Override
    public void run() {

    }
}