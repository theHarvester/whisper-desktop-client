package lib;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.List;

/**
 * Created by MaGraham on 14/07/2014.
 */
public class ApiConnection {

    private String username;
    private String password;
    private String baseUrl;
    private String handleUrl;
    private String conversationUrl;
    private String messageUrl;
    private String authUrl;
    HttpCookie cookie;
    CookieManager manager;

    public final static ApiConnection INSTANCE = new ApiConnection();

    private ApiConnection() {
        // Exists only to defeat instantiation.
        manager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(manager);
        this.baseUrl = "http://chat.fifty2project.com/api/";
        this.handleUrl = this.baseUrl + "handle";
        this.conversationUrl = this.baseUrl + "conversation";
        this.messageUrl = this.baseUrl + "message";
        this.authUrl = this.baseUrl + "auth";
    }

    public static ApiConnection getInstance() {
        return INSTANCE;
    }

    public void getConnectionInfo(String username, String password){
        this.username = username;
        this.password = password;
        getSession();
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

}
