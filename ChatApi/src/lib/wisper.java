package lib;

import javafx.scene.input.KeyCode;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Timer;

/**
 * Created by MaGraham on 10/07/2014.
 */
public class wisper {
    private JPasswordField passwordField1;
    private JTextField usernameField;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton connectButton;
    private JPanel wisperJPanel;
    private JTextField passwordField;
    private JTabbedPane tabbedPane1;
    private JList handlesList;
    private JList conversationsList;
    private int lastDisplayedMessageTimestamp;
    private List<String> conversation;
    private List<String> handles;
    private List<String> conversations;
    private String currentConversationId;

    private static chat mainChat;
    private final static String newline = "\n";
    private Timer timer;

    public wisper(){
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToChat();
            }
        });
        conversationsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2 && !e.isConsumed()) {
                    e.consume();
                    currentConversationId = conversationsList.getSelectedValue().toString();
                    updateConversation(conversationsList.getSelectedValue().toString());
                }
            }
        });
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode()== KeyEvent.VK_ENTER){
                    String msg = messageField.getText();
                    messageField.setText(null);
                    sendMessage(msg);
                }
            }
        });
    }

    public void sendMessage(String message){
        mainChat.sendPost(message, currentConversationId);
    }

    public void updateConversation(String id){
        conversation = mainChat.fetchConversation(id);
        chatArea.setText(null);
        for (int i = 0; i < conversation.size(); i++) {
            chatArea.append(conversation.get(i) + newline);
        }
    }

    public void connectToChat(){
        mainChat = new chat(usernameField.getText(), passwordField.getText());
        conversation = mainChat.fetchConversation("Jj");
        currentConversationId = "Jj";
        handles = mainChat.fetchHandles();
        conversations = mainChat.fetchConversations();
        timer = new Timer();

        for (int i = 0; i < conversation.size(); i++) {
            chatArea.append(conversation.get(i) + newline);
        }

        handlesList.setListData(handles.toArray());

        conversationsList.setListData(conversations.toArray());

        lastDisplayedMessageTimestamp = mainChat.getLastTimeStamp();
        timer.scheduleAtFixedRate(new updateFeed(), 2, 1 * 1000);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("wisper");
        frame.setContentPane(new wisper().wisperJPanel);
        frame.getContentPane().setSize(800,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private class updateFeed extends TimerTask {

        updateFeed() {
        }

        public void run() {
            conversation = mainChat.fetchNewMessages(currentConversationId, lastDisplayedMessageTimestamp);

            for (int i = 0; i < conversation.size(); i++){
                chatArea.append(conversation.get(i) + newline);
            }
            lastDisplayedMessageTimestamp = mainChat.getLastTimeStamp();
        }
    }
}