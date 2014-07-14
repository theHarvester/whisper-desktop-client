package lib;


import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import org.json.JSONArray;
import org.json.JSONObject;

/*
 * Created by JAMES DA FEG on 7/07/2014.
 */
public class ChatController {
    private String username;
    private String password;
    private String baseUrl;
    private String handleUrl;
    private String conversationUrl;
    private String messageUrl;
    private String authUrl;
    private int lastTimeStamp;
    HttpCookie cookie;
    CookieManager manager;

    public ChatController(String username, String password) {
        manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        this.username = username;
        this.password = password;
        this.baseUrl = "http://chat.fifty2project.com/api/";
        this.handleUrl = this.baseUrl + "handle";
        this.conversationUrl = this.baseUrl + "conversation";
        this.messageUrl = this.baseUrl + "message";
        this.authUrl = this.baseUrl + "auth";
    }

    public ChatController() {
        manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        this.baseUrl = "http://chat.fifty2project.com/api/";
        this.handleUrl = this.baseUrl + "handle";
        this.conversationUrl = this.baseUrl + "conversation";
        this.messageUrl = this.baseUrl + "message";
        this.authUrl = this.baseUrl + "auth";
    }

    public int getLastTimeStamp() {
        return lastTimeStamp;
    }

    private String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (cookie == null) {
            System.out.println("Cookie was null, getting session");
            getSession(authUrl);
        }

        // todo: workout way of storing session so credentials aren't as easily sniffed
        conn.setRequestProperty("Cookie", cookie.toString());
        System.out.println("Has Session and is GETing");
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

    public void sendPost(String msg, String convoId) {
        try {
            httpPost(messageUrl, convoId, msg);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void httpPost(String urlStr, String convId, String message) throws IOException {

        URL obj = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        if (cookie == null) {
            System.out.println("Cookie was null, getting session");
            getSession(authUrl);
        }

        // todo: workout way of storing session so credentials aren't as easily sniffed
        con.setRequestProperty("Cookie", cookie.toString());
        System.out.println("Has Session and is POSTing");
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "conversation_id=" + convId + "&message=" + URLEncoder.encode(message, "UTF-8");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }

    public List<String> fetchConversation(String convId) {

        String convUrl = conversationUrl + "/" + convId;
        String jsonString = "";
        List<String> conversation = new ArrayList<String>();
        List<JSONObject> messages = new ArrayList<JSONObject>();

        try {
            jsonString = httpGet(convUrl);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                messages.add((JSONObject) jsonArray.get(i));
            }
            //todo: use a better data type to store messages, handles and ids
            for (int i = 0; i < messages.size(); i++) {
                conversation.add(i, messages.get(i).getString("handle") + " : " + messages.get(i).getString("message"));
                //get timestamp of current message, make it last known timestamp
                lastTimeStamp = messages.get(i).getInt("ts");
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
        return conversation;
    }

    public List<String> fetchNewMessages(String convId, int timestamp) {
        String convUrlFromTS = conversationUrl + "/" + convId + "/from/" + timestamp;
        String jsonString = "";
        List<String> conversation = new ArrayList<String>();
        List<JSONObject> messages = new ArrayList<JSONObject>();

        try {

            jsonString = httpGet(convUrlFromTS);

        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("messages");

            for (int i = 0; i < jsonArray.length(); i++) {
                messages.add((JSONObject) jsonArray.get(i));
            }
            //todo: use a better data type to store messages, handles and ids
            for (int i = 0; i < messages.size(); i++) {
                conversation.add(i, messages.get(i).getString("handle") + " : " + messages.get(i).getString("message"));
                //get timestamp of current message, make it last known timestamp
                lastTimeStamp = messages.get(i).getInt("ts");
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
        return conversation;
    }

    public List<String> fetchHandles() {
        String jsonString = "";
        List<String> handles = new ArrayList<String>();

        try {
            jsonString = httpGet(handleUrl);
        } catch (IOException e) {
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

    public List<String> fetchConversations() {
        String convUrl = conversationUrl;
        String jsonString = "";
        List<JSONObject> conversations = new ArrayList<JSONObject>();
        List<String> conversationList = new ArrayList<String>();

        try {
            jsonString = httpGet(convUrl);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("conversations");

            for (int i = 0; i < jsonArray.length(); i++) {
                conversations.add((JSONObject) jsonArray.get(i));
            }
            for (int i = 0; i < conversations.size(); i++) {
                conversationList.add(i, conversations.get(i).getString("id"));
            }

        } catch (JSONException e) {
            System.out.println(e);
        }
        return conversationList;
    }

    private void getSession(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String credentials = this.username + ":" + this.password;
            String encoded = Base64.encode(credentials.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoded);

            InputStream in = conn.getInputStream();

            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();
            for (HttpCookie ckie : cookies) {
                cookie = ckie;
            }
            in.close();
            saveTokenToFile();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void saveTokenToFile(){
        try {
            PrintWriter out = new PrintWriter("token.txt");
            out.println(cookie.getValue()); //todo: Use the token from the server rather than a cookie
            out.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    private boolean readTokenFromFile(){
        try {
            BufferedReader br = new BufferedReader(new FileReader("token.txt"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            cookie = new HttpCookie("laravel_session",sb.toString());
            br.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
    public void startUpFunc(){

        if(readTokenFromFile()) {
            System.out.println("Token was found");
        }
    }
}
