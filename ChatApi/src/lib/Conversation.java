package lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JAMES on 11/07/2014.
 */
public class Conversation {

    private ApiConnection apiConnection = ApiConnection.getInstance();
    private List<Message> conversation = new ArrayList<Message>();
    private List<Message> messageBuffer = new ArrayList<Message>();
    private String conversationID;
    private String conversationURL;
    private int lastLoadedMessageTimeStamp;

    public Conversation(String id){
        this.conversationID = id;
        this.conversationURL = apiConnection.baseUrl + "conversation/" + id;
        loadMessages(); //May not want to do this here
    }

    public Conversation(){

    }

    public List<Message> getMessages(){
        return conversation;
    }
    public List<Message> getNewMessages(){
        return messageBuffer;
    }

    private void loadMessages(){
        try {
            String jsonString = apiConnection.httpGet(conversationURL);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);
                Message tempMessage = new Message(tempObject.getString("handle"), tempObject.getString("message"), tempObject.getInt("ts"));
                setLastLoadedMessageTimeStamp(tempObject.getInt("ts"));
                this.conversation.add(i, tempMessage);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void loadMessagesFromTimeStamp(int ts){
        String convUrlFromTS = conversationURL + "/from/" + ts;
        try {
            String jsonString = apiConnection.httpGet(convUrlFromTS);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);
                Message tempMessage = new Message(tempObject.getString("handle"), tempObject.getString("message"), tempObject.getInt("ts"));
                setLastLoadedMessageTimeStamp(tempObject.getInt("ts"));
                this.messageBuffer.add(i, tempMessage);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        mergeMessageBuffer();
    }

    private void mergeMessageBuffer(){
        conversation.addAll(messageBuffer);
    }

    public void clearMessageBuffer(){
        messageBuffer.clear();
    }


    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public int getLastLoadedMessageTimeStamp() {
        return lastLoadedMessageTimeStamp;
    }

    public void setLastLoadedMessageTimeStamp(int lastLoadedMessageTimeStamp) {
        this.lastLoadedMessageTimeStamp = lastLoadedMessageTimeStamp;
    }
}

