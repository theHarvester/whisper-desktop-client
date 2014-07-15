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
    private String conversationID;
    private String conversationURL;
    private String baseUrl;
    private String jsonString;

    public Conversation(String id){
        baseUrl = "http://chat.fifty2project.com/api/";
        this.conversationID = id;
        this.conversationURL = this.baseUrl + "conversation/" + id;
        loadMessages(); //May not want to do this here
    }

    public Conversation(){

    }

    public List<Message> getConversation(){
        return conversation;
    }

    private void loadMessages(){
        try{
            jsonString = apiConnection.httpGet(conversationURL);
            decodeJsonString();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    private void decodeJsonString(){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);
                Message tempMessage = new Message(tempObject.getString("handle"), tempObject.getString("message"), tempObject.getInt("ts"));
                conversation.add(i, tempMessage);
            }

        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }
}

