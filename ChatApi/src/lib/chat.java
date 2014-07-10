package lib;


import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.net.URLEncoder;
import java.util.Map;

import org.json.*;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * Created by JAMES DA FEG on 7/07/2014.
 */
public class chat {
    private String username;
    private String password;
    private String baseUrl;
    private String handleUrl;
    private String conversationUrl;
    private String messageUrl;
    private int lastTimeStamp;

    public chat(String username, String password){
        this.username = username;
        this.password = password;
        this.baseUrl = "http://chat.fifty2project.com/api/";
        this.handleUrl = this.baseUrl + "handle";
        this.conversationUrl = this.baseUrl + "conversation";
        this.messageUrl = this.baseUrl + "message";
    }

    public int getLastTimeStamp(){
        return lastTimeStamp;
    }

    private String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // todo: workout way of storing session so credentials aren't as easily sniffed
        String credentials = this.username + ":" + this.password;
        String encoded = Base64.encode(credentials.getBytes());
        conn.setRequestProperty("Authorization", "Basic "+encoded);

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }

    public void sendPost(String msg, String convoId){
        try{
            httpPost(messageUrl, convoId, msg);
        }
        catch (IOException e){
            System.out.println(e);
        }
    }

    private void httpPost(String urlStr, String convId, String message) throws IOException{

        URL obj = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header

        // todo: workout way of storing session so credentials aren't as easily sniffed
        String credentials = this.username + ":" + this.password;
        String encoded = Base64.encode(credentials.getBytes());
        con.setRequestProperty("Authorization", "Basic "+encoded);
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "conversation_id=" + convId + "&message=" + URLEncoder.encode(message, "UTF-8");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + urlStr);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    public List<String> fetchConversation(String convId){

        String convUrl = conversationUrl + "/" + convId;
        String jsonString = "";
        List<String> conversation = new ArrayList<String>();
        List<JSONObject> messages = new ArrayList<JSONObject>();

        try{
            jsonString = httpGet(convUrl);
        }
        catch (IOException e){
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                messages.add((JSONObject) jsonArray.get(i));
            }
            //todo: use a better data type to store messages, handles and ids
            for(int i =0; i < messages.size(); i++) {
                conversation.add(i, messages.get(i).getString("handle") + " : " + messages.get(i).getString("message"));
                //get timestamp of current message, make it last known timestamp
                lastTimeStamp = messages.get(i).getInt("ts");
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
        return conversation;
    }

    public List<String> fetchNewMessages(String convId, int timestamp){
        String convUrlFromTS = conversationUrl + "/" + convId + "/from/" + timestamp;
        String jsonString = "";
        List<String> conversation = new ArrayList<String>();
        List<JSONObject> messages = new ArrayList<JSONObject>();

        try{
            jsonString = httpGet(convUrlFromTS);
        }
        catch (IOException e){
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                messages.add((JSONObject) jsonArray.get(i));
            }
            //todo: use a better data type to store messages, handles and ids
            for(int i =0; i < messages.size(); i++) {
                conversation.add(i, messages.get(i).getString("handle") + " : " + messages.get(i).getString("message"));
                //get timestamp of current message, make it last known timestamp
                lastTimeStamp = messages.get(i).getInt("ts");
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
        return conversation;
    }

    public List<String> fetchHandles(){
        String jsonString = "";
        List<String> handles = new ArrayList<String>();

        try{
            jsonString = httpGet(handleUrl);
        }
        catch (IOException e){
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("handles");

            // List<String> handles = new ArrayList<String>();  //Now a Member of class to allow for greater scope
            for (int i = 0; i < jsonArray.length(); i++) {
                handles.add(jsonArray.get(i).toString());
            }

        } catch (JSONException e) {
            System.out.println(e);
        }
        return handles;
    }

    public List<String> fetchConversations(){
        String convUrl = conversationUrl;
        String jsonString = "";
        List<JSONObject> conversations = new ArrayList<JSONObject>();
        List<String> conversationList = new ArrayList<String>();

        try{
            jsonString = httpGet(convUrl);
        }
        catch (IOException e){
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("conversations");

            for (int i = 0; i < jsonArray.length(); i++) {
                conversations.add((JSONObject) jsonArray.get(i));
            }
            for (int i = 0; i < conversations.size(); i++){
                conversationList.add(i,conversations.get(i).getString("id"));
            }

            System.out.println(conversations.toString());
        } catch (JSONException e) {
            System.out.println(e);
        }
        return conversationList;
    }
}
