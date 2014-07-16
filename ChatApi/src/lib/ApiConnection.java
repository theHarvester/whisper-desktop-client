package lib;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Created by MaGraham on 14/07/2014.
 */
public class ApiConnection {

    private String username;
    private String password;
    protected String baseUrl;
    private String authUrl;
    private String messageUrl;
    private HttpCookie cookie;
    private CookieManager manager;

    public final static ApiConnection INSTANCE = new ApiConnection();

    private ApiConnection() {
        // Exists only to defeat instantiation.
        this.manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        this.baseUrl = "http://chat.fifty2project.com/api/";
        this.authUrl = this.baseUrl + "auth";
        this.messageUrl = this.baseUrl + "message";
    }

    public static ApiConnection getInstance() {
        return INSTANCE;
    }

    public void getConnectionInfo(String username, String password){
        this.username = username;
        this.password = password;
        getSession();
    }

    protected String httpGet(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // todo: workout way of storing session so credentials aren't as easily sniffed
        conn.setRequestProperty("Cookie", cookie.toString());

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }

    public void sendMessage(String convId, String message) {
        try {
            String urlParameters = "conversation_id=" + convId + "&message=" + URLEncoder.encode(message, "UTF-8");
            httpPost(messageUrl, urlParameters);
        }
        catch (Exception e){
            System.out.println(e);
        }
    }
    //todo:HTTP POST
    private void httpPost(String urlStr, String urlParameters) throws IOException {

        URL obj = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // todo: workout way of storing session so credentials aren't as easily sniffed
        con.setRequestProperty("Cookie", cookie.toString());
        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
    }


    public boolean startup(){
        if(!readTokenFromFile()) { //check if file exists, if not get credentials from user/gui
            return false;
        }
        else{
            return true;
        }
    }

    private void getSession() { //used to get the auth token for this app instance
        try {
            URL url = new URL(authUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String credentials = this.username + ":" + this.password;
            String encoded = Base64.encode(credentials.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoded);

            InputStream in = conn.getInputStream();

            CookieStore cookieJar = manager.getCookieStore();
            List<HttpCookie> cookies = cookieJar.getCookies();
            for (HttpCookie ckie : cookies) {
                this.cookie = ckie;
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
            this.cookie = new HttpCookie("laravel_session",sb.toString());
            br.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

}
