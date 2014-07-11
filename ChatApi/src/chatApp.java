import jdk.nashorn.internal.runtime.Debug;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.TimerTask;
import java.util.Timer;
import java.util.ArrayList;
import java.util.List;

import lib.*;
/*
 * Created by sasquatch on 7/7/14. brilliant, can you see this?
 */
public class chatApp {

    protected static JTextArea textArea;
    protected JTextField textField;
    private final static String newline = "\n";
    private static int lastDisplayedMessageTimestamp;
    private static List<String> conversation;

    public chatApp() {
/*        super(new GridBagLayout());

        textField = new JTextField(20);
        textField.addActionListener(this);
        textArea = new JTextArea(5, 20);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(textField, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        add(scrollPane, c);*/

    }

   /* public void actionPerformed(ActionEvent evt) {
        String text = textField.getText();
        ChatController myChat = new ChatController("sussmonkey_69@hotmail.com", "123456789");
        //textArea.append(text + newline);
        myChat.sendPost(text);
        textField.selectAll();
        textArea.setCaretPosition(textArea.getDocument().getLength());

    }*/

    /*private static void CreateGUI() {
        JFrame frame = new JFrame("Secret Chat");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.add(new chatApp());

        frame.pack();
        frame.setVisible(true);

    }*/

    //public static void main(String[] args) {
        /*javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CreateGUI();
            }
        });*/

       /* Timer timer = new Timer();

        ChatController myChat = new ChatController("sussmonkey_69@hotmail.com", "123456789");
        conversation = myChat.fetchConversation("Jj");

        for (int i = 0; i < conversation.size(); i++) {
                textArea.append(conversation.get(i) + newline);
        }
        lastDisplayedMessageTimestamp = myChat.getLastTimeStamp();
        timer.scheduleAtFixedRate(new updateFeed(), 2, 1 * 1000);*/
    //}


    /*private static class updateFeed extends TimerTask {
        ChatController mainChat;

        updateFeed() {
            mainChat = new ChatController("sussmonkey_69@hotmail.com", "123456789");
        }

        public void run() {
            conversation = mainChat.fetchNewMessages("Jj", lastDisplayedMessageTimestamp);

            for (int i = 0; i < conversation.size(); i++){
                textArea.append(conversation.get(i) + newline);
            }
            lastDisplayedMessageTimestamp = mainChat.getLastTimeStamp();
        }
    }*/
}
