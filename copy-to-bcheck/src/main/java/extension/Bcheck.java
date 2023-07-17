package extension;

import javax.swing.*;
import javax.swing.border.Border;
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
        panel.setLayout(new GridLayout(0, 1));

        JLabel label = null;

        JPanel row1 = new JPanel(new BorderLayout());
        label = new JLabel("Bcheck Name: ");
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        //panel.add(label);
        row1.add(label, BorderLayout.WEST);
        JTextField bname = new JTextField("");
        bname.setColumns(20);
        row1.add(bname, BorderLayout.CENTER);
        panel.add(row1);

        JPanel row2 = new JPanel(new BorderLayout());
        label = new JLabel("Description: ");
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        row2.add(label, BorderLayout.WEST);
        //panel.add(label);
        JTextField desc = new JTextField("");
        desc.setColumns(20);
        row2.add(desc, BorderLayout.CENTER);
        panel.add(row2);

        JPanel row3 = new JPanel(new BorderLayout());
        label = new JLabel("Author: ");
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        row3.add(label, BorderLayout.WEST);
        JTextField author = new JTextField("");
        author.setColumns(20);
        row3.add(author, BorderLayout.CENTER);
        panel.add(row3);

        JComboBox successList = null, parameterList = null, severityList = null, confidenceList = null;
        JTextField sink = null, regex = null, insertion = null;
        String[] parameterStrings = {"status code", "body", "headers", "response", "collaborator"};

        JPanel row4 = new JPanel(new BorderLayout());
        String[] successStrings = {"matches", "differs", "in", "is"};
        JLabel successLabel = new JLabel("Success if: ");
        successLabel.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        row4.add(successLabel, BorderLayout.WEST);
        successList = new JComboBox(successStrings);
        row4.add(successList, BorderLayout.CENTER);
        panel.add(row4);

        String windowTitle = "Template Configuration";

        switch (this.mode) {
            case "host":

                JPanel row5 = new JPanel(new BorderLayout());
                label = new JLabel("Parameter: ");
                label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
                row5.add(label, BorderLayout.WEST);
                //panel.add(label);
                parameterList = new JComboBox(parameterStrings);
                row5.add(parameterList, BorderLayout.CENTER);
                panel.add(row5);

                JPanel row6 = new JPanel(new BorderLayout());
                label = new JLabel("Value: ");
                label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
                row6.add(label, BorderLayout.WEST);
                sink = new JTextField(args);
                sink.setColumns(20);
                row6.add(sink, BorderLayout.CENTER);
                panel.add(row6);

                windowTitle = "Host based Check Configuration";

                break;

            case "passive":

                JPanel row7 = new JPanel(new BorderLayout());
                label = new JLabel("Regex: ");
                label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
                row7.add(label, BorderLayout.WEST);
                regex = new JTextField(this.args);
                regex.setColumns(20);
                row7.add(regex, BorderLayout.CENTER);
                panel.add(row7);

                windowTitle = "Passive based Check Configuration";

                break;

            case "insertion":

                JPanel row8 = new JPanel(new BorderLayout());
                parameterStrings = new String[]{"response", "collaborator"};
                label = new JLabel("Parameter: ");
                label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
                row8.add(label, BorderLayout.WEST);
                //panel.add(label);
                parameterList = new JComboBox(parameterStrings);
                row8.add(parameterList, BorderLayout.CENTER);
                panel.add(row8);

                JPanel row9 = new JPanel(new BorderLayout());
                label = new JLabel("Insertion Vector: ");
                label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
                row9.add(label, BorderLayout.WEST);
                //panel.add(label);
                insertion = new JTextField(this.args);
                insertion.setColumns(20);
                row9.add(insertion, BorderLayout.CENTER);
                panel.add(row9);

                JPanel row10 = new JPanel(new BorderLayout());
                label = new JLabel("Success Regex: ");
                label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
                row10.add(label, BorderLayout.WEST);
                regex = new JTextField("");
                regex.setColumns(20);
                row10.add(regex, BorderLayout.CENTER);
                panel.add(row10);

                panel.remove(row4);

                windowTitle = "Insertion based Check Configuration";

                break;
        }

        JPanel row11 = new JPanel(new BorderLayout());
        String[] severityStrings = { "Info", "Low", "Medium", "High"};
        JLabel severityLabel = new JLabel("Severity: ");
        severityLabel.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        row11.add(severityLabel, BorderLayout.WEST);
        //panel.add(severityLabel);
        severityList = new JComboBox(severityStrings);
        row11.add(severityList, BorderLayout.CENTER);
        panel.add(row11);

        JPanel row12 = new JPanel(new BorderLayout());
        String[] confidenceStrings = { "Tentative", "Certain", "Firm"};
        label = new JLabel("Confidence: ");
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        row12.add(label, BorderLayout.WEST);
        //panel.add(label);
        confidenceList = new JComboBox(confidenceStrings);
        row12.add(confidenceList, BorderLayout.CENTER);
        panel.add(row12);

        JPanel row13 = new JPanel(new BorderLayout());
        label = new JLabel("Remediation: ");
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        row13.add(label, BorderLayout.WEST);
        JTextField remediation = new JTextField("");
        remediation.setColumns(20);
        row13.add(remediation, BorderLayout.CENTER);
        panel.add(row13);

        int result = JOptionPane.showConfirmDialog(
                this.api.userInterface().swingUtils().suiteFrame(),
                panel,
                windowTitle,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {

            // Write template
            String template =
                    "metadata:\n" +
                            "  language: v1-beta\n" +
                            "  name: \""+bname.getText()+"\"\n" +
                            "  description: \""+desc.getText()+"\"\n" +
                            "  author: \""+author.getText()+"\"\n";

            switch (this.mode) {

                case "host":
                    String method = this.request.request().method();
                    String path = this.request.request().path();
                    String param = this.request.request().parameters().toString();
                    String body = this.request.request().bodyToString();

                    template +=
                            "tags: \"host-level\"\n\n" +
                            "\nrun for each:\n" +
                            "    potential_path =\n" +
                            "        \""+ path +"\"\n\n" +
                            "given host then\n" +
                            "    send request called check:\n" +
                            "        method: \""+ method +"\"\n" +
                            "        path: {potential_path}\n" +
                            "        headers: \n";

                            for (HttpHeader h : this.request.request().headers()) {
                                String strEnd = (this.request.request().headers().indexOf(h) == (this.request.request().headers().size() - 1)) ? "`\n" :  "`,\n";

                                if (h.name().equals("Host")) {
                                    continue;
                                }
                                template +=
                                        "           \""+ h.name().replaceAll("\"", "\\\\\\\"") +"\": `";
                                if (parameterList.getSelectedItem().toString() == "collaborator" && h.value().contains(sink.getText())) {
                                    api.logging().logToOutput(parameterList.getSelectedItem().toString());
                                    String s = h.value().replaceAll(sink.getText(), "{generate_collaborator_address()}");
                                    s = s.replaceAll("`", "\\\\`") + strEnd;
                                    template += s;
                                } else {
                                    String s = h.value().replaceAll("`", "\\\\`") + strEnd;
                                    template += s;
                                }
                            }

                            if (method == "PUT" || method == "POST" || method == "PATCH") {
                                if (parameterList.getSelectedItem().toString().equals("collaborator")) {
                                    body = body.replaceAll(sink.getText(), "{generate_collaborator_address()}");
                                }

                                template +=
                                "        body: \n`" + body.replaceAll("`", "\\\\`") + "`\n";
                            }

                    template += "\n";
                            if (parameterList.getSelectedItem() == "collaborator") {
                                template += "    if any interactions then\n";
                            } else {
                                template += "    if";

                                if (parameterList.getSelectedItem().equals("status code")) {
                                    template += " {check.response.status_code}";
                                } else if (parameterList.getSelectedItem().equals("body")) {
                                    template += " {check.response.body}";
                                } else if (parameterList.getSelectedItem().equals("headers")) {
                                    template += " {check.response.headers}";
                                }  else if (parameterList.getSelectedItem().equals("response")) {
                                    template += " {check.response}";
                                }

                                if (successList.getSelectedItem().equals("matches")) {
                                    template += " matches";
                                } else if (successList.getSelectedItem().toString().equals("is")) {
                                    template += " is";
                                } else if (successList.getSelectedItem().toString().equals("differs")) {
                                    template += " differs";
                                } else if (successList.getSelectedItem().toString().equals("in")) {
                                    template += " in";
                                }

                                template += " \"" + sink.getText() + "\" then\n";
                            }

                    template +=
                            "        report issue:\n" +
                            "            severity: "+severityList.getSelectedItem().toString().toLowerCase()+"\n" +
                            "            confidence: "+confidenceList.getSelectedItem().toString().toLowerCase()+"\n" +
                            "            detail: `"+bname.getText()+" found at {potential_path}.`\n" +
                            "            remediation: \""+remediation.getText()+"\"\n" +
                            "    end if";
                    break;

                case "passive":
                    template +=
                            "tags: \"passive\"\n\n" +
                            "given response then\n" +
                            "    if {latest.response}";

                            if (successList.getSelectedItem().equals("matches")) {
                                template += " matches";
                            } else if (successList.getSelectedItem().equals("is")) {
                                template += " is";
                            } else if (successList.getSelectedItem().equals("differs")) {
                                template += " differs";
                            } else if (successList.getSelectedItem().equals("in")) {
                                template += " in";
                            }

                    template += " \""+ regex.getText().replaceAll("\"", "\\\\\\\"") + "\" or {latest.request}";

                            if (successList.getSelectedItem().equals("matches")) {
                                template += " matches";
                            } else if (successList.getSelectedItem().equals("is")) {
                                template += " is";
                            } else if (successList.getSelectedItem().equals("differs")) {
                                template += " differs";
                            } else if (successList.getSelectedItem().equals("in")) {
                                template += " in";
                            }

                    template += " \""+ regex.getText().replaceAll("\"", "\\\\\\\"") + "\" then\n" +
                            "        report issue:\n" +
                            "            severity: "+ severityList.getSelectedItem().toString().toLowerCase() +"\n" +
                            "            confidence: "+ confidenceList.getSelectedItem().toString().toLowerCase() +"\n" +
                            "            detail: `"+bname.getText()+" found.`\n" +
                            "            remediation: \""+remediation.getText()+"\"\n" +
                            "    end if";
                    break;

                case "insertion":
                    template +=
                            "tags: \"insertion-point-level\"\n\n" +
                            "define:\n" +
                            "    insertion=\""+insertion.getText()+"\"\n" +
                            "    answer=\""+regex.getText()+"\"\n" +
                            "\n" +
                            "given insertion point then\n" +
                            "\n" +
                            "    if not({answer} in {base.response}) then\n" +
                            "        send payload:\n" +
                            "            appending: {insertion}\n" +
                            "\n";

                    if (parameterList.getSelectedItem().equals("collaborator")) {
                        template += "        if any interaction then\n";
                    } else if (parameterList.getSelectedItem().equals("response")){
                        template += "        if {answer} in {latest.response} then\n";
                    }

                    template +=
                            "            report issue:\n" +
                            "                severity: "+severityList.getSelectedItem().toString().toLowerCase()+"\n" +
                            "                confidence: "+confidenceList.getSelectedItem().toString().toLowerCase()+"\n" +
                            "                detail: `"+bname.getText()+" found.`\n"+
                            "                remediation: `"+remediation.getText()+"`\n" +
                            "        end if\n" +
                            "    end if\n";
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