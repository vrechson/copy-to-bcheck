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
    public Bcheck(MontoyaApi api, HttpRequestResponse request, String style) {

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

        panel.add(new JLabel("Vulnerability indicator: "));
        JTextField sink = new JTextField("");
        panel.add(sink);

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
            switch (style) {
                case "host":
                    String method = request.request().method().toString();
                    String path = request.request().path().toString();
                    String param = request.request().parameters().toString();
                    String body = request.request().bodyToString();

                    String template =
                  "metadata:\n" +
                            "  language: v1-beta\n" +
                            "  name: \""+bname.getText()+"\"\n" +
                            "  description: \""+desc.getText()+"\"\n" +
                            "  author: \""+author.getText()+"\"\n\n" +
                    "run for each:\n" +
                            "    potential_path =\n" +
                            "        \""+ path + param +"\",\n" +
                            "\n" +
                            "given host then\n" +
                            "    send request called check:\n" +
                            "        method: \""+ method +"\"\n" +
                            "        path: {potential_path}\n" +
                            "\n" +
                            "    if \""+ sink.getText() +"\" in {check.response.body} then\n" +
                            "        report issue:\n" +
                            "            severity: High\n" +
                            "            confidence: certain\n" +
                            "            detail: `"+bname.getText()+" found at {potential_path}.`\n" +
                            "            remediation: \"tbd.\"\n" +
                            "    end if";
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

}