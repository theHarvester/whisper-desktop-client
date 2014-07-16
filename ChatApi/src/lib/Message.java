package lib;

import org.json.JSONObject;

/*
 * Created by JAMES on 11/07/2014.
 */
public class Message{
    private String handle;
    private String message;
    private int timeStamp;

    public Message(String h, String m, int ts){
        this.handle = h;
        this.message = m;
        this.timeStamp = ts;
    }

    public Message(){

    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
}
