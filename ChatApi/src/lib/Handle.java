package lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JAMES on 11/07/2014.
 */
public class Handle {
    private String name;
    private List<Conversation> conversations = new ArrayList<Conversation>();
    private ApiConnection apiConnection = ApiConnection.getInstance();
    private String jsonString;
    private String conversationsURL = "http://chat.fifty2project.com/api/conversations";

    public Handle(){
        loadConversations();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Conversation> getConversations(){
        return  conversations;
    }

    //todo: Find a better way to look for conversation pertaining to a specific handle

    private void loadConversations(){
        try{
            jsonString = apiConnection.httpGet(conversationsURL);
            decodeJsonString();
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

    //todo: For now this will decode the conversations list initialse conversations(ArrayList) with all the conversations tied to this token
    private void decodeJsonString(){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("conversations");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);
                //JSONArray tempJsonArray = jsonObject.getJSONArray("handles");
                //name = tempJsonArray.getString(0);
                Conversation tempConversation = new Conversation(tempObject.getString("id"));
                conversations.add(i, tempConversation);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }

    }
}
