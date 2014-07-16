package lib;

import javax.swing.*;
import java.awt.event.*;
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
    private Handle handle; //in future this will come from the user class
    private String currentConversationId;
    private static boolean tokenFailed;

    private static ApiConnection apiConnection = ApiConnection.getInstance();
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
                    //load the conversation that has just been clicked
                    loadConversationByIndex(conversationsList.getSelectedValue().toString());
                }
            }
        });
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode()== KeyEvent.VK_ENTER){
                    //send the message that has been entered
                    String messageToSend = messageField.getText();
                    messageField.setText(null);
                    apiConnection.sendMessage(currentConversationId, messageToSend);
                }
            }
        });
    }

    private String connectToChat(){

        try {
            if(tokenFailed) {
                apiConnection.getConnectionInfo(usernameField.getText(), passwordField.getText());
            }
            else{
                loadChatGUIInformation();
            }
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

        if(!apiConnection.startup())
        {
            tokenFailed = true;//send apiConnection our credentials
        }
    }

    private void loadChatGUIInformation(){
        this.handle = new Handle(); //builds all the data from handle downwards

        //conversationsList.setListData(handle.getConversations().toArray());
        conversationsList.setListData(handle.getConversationsMap().keySet().toArray());
        handlesList.setListData(handle.getHandles().toArray());
    }

    private void loadConversationByIndex(String convoId){
        chatArea.setText(null);
        currentConversationId = convoId;
        Conversation convoToLoad = (Conversation)handle.getConversationsMap().get(convoId);

        for(int i = 0; i < convoToLoad.getMessages().size(); i++){
            chatArea.append(convoToLoad.getMessages().get(i).getHandle() + " : " + convoToLoad.getMessages().get(i).getMessage() + newline);
        }
        timer = new Timer();

        timer.scheduleAtFixedRate(new updateFeed(), 2, 1 * 1000);
    }

    private class updateFeed extends TimerTask {

        updateFeed() {
        }

        public void run() {
            Conversation convoToLoad = (Conversation)handle.getConversationsMap().get(currentConversationId);
            convoToLoad.loadMessagesFromTimeStamp(convoToLoad.getLastLoadedMessageTimeStamp());
            for(int i = 0; i < convoToLoad.getNewMessages().size(); i++){
                chatArea.append(convoToLoad.getNewMessages().get(i).getHandle() + " : " + convoToLoad.getNewMessages().get(i).getMessage() + newline);
            }
            convoToLoad.clearMessageBuffer();
        }
    }
}