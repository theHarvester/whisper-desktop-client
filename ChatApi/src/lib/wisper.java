package lib;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;

/**
 * Created by MaGraham on 10/07/2014.
 */
public class wisper {
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

    private static ChatController mainChatController;
    private final static String newline = "\n";
    private Timer timer;

    public wisper(){
        mainChatController = new ChatController(usernameField.getText(), passwordField.getText());
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
                    if(currentConversationId != conversationsList.getSelectedValue().toString()) {
                        currentConversationId = conversationsList.getSelectedValue().toString();
                        updateConversation(currentConversationId);
                    }
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

    private void sendMessage(String message){
        mainChatController.sendPost(message, currentConversationId);
    }

    private void updateConversation(String id){
        chatArea.setText(null);
        conversation = mainChatController.fetchConversation(id);
        chatArea.setText(null);
        for (int i = 0; i < conversation.size(); i++) {
            chatArea.append(conversation.get(i) + newline);
        }
    }

    private String connectToChat(){

        try {
            //mainChatController = new ChatController(usernameField.getText(), passwordField.getText());

            conversations = mainChatController.fetchConversations();
            conversation = mainChatController.fetchConversation(conversations.get(0));
            currentConversationId = conversations.get(0);
            handles = mainChatController.fetchHandles();


            timer = new Timer();

            for (int i = 0; i < conversation.size(); i++) {
                chatArea.append(conversation.get(i) + newline);
            }

            handlesList.setListData(handles.toArray());

            conversationsList.setListData(conversations.toArray());

            lastDisplayedMessageTimestamp = mainChatController.getLastTimeStamp();
            timer.scheduleAtFixedRate(new updateFeed(), 2, 1 * 1000);
        }
        catch (Exception e){
            System.out.println(e);
            return null;
        }
        return null;
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
            conversation = mainChatController.fetchNewMessages(currentConversationId, lastDisplayedMessageTimestamp);

            for (int i = 0; i < conversation.size(); i++){
                chatArea.append(conversation.get(i) + newline);
            }
            lastDisplayedMessageTimestamp = mainChatController.getLastTimeStamp();
        }
    }
}