package lib;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Created by JAMES on 11/07/2014.
 */
public class Handle {
    private String name;
    private Map<String, Conversation> conversationsMap = new HashMap<String, Conversation>();
    private List<String> handles = new ArrayList<String>();
    private ApiConnection apiConnection = ApiConnection.getInstance();

    private String conversationsURL = apiConnection.baseUrl + "conversations";
    private String handlesURL = apiConnection.baseUrl + "handle";

    public Handle(){
        loadConversations();
        loadHandles();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map getConversationsMap(){
        return conversationsMap;
    }

    public List<String> getHandles(){
        return handles;
    }

    //todo: For now this will decode the conversations list initialise conversations(ArrayList) with all the conversations tied to this token
    private void loadConversations(){
        try {
            String jsonString = apiConnection.httpGet(conversationsURL);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("conversations");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject tempObject = jsonArray.getJSONObject(i);;
                Conversation tempConversation = new Conversation(tempObject.getString("id"));
                conversationsMap.put(tempObject.getString("id"), tempConversation);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void loadHandles(){
        try {
            String jsonString = apiConnection.httpGet(handlesURL);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("handles");

            for (int i = 0; i < jsonArray.length(); i++) {
                handles.add(i,jsonArray.get(i).toString());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
