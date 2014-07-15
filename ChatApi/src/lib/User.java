package lib;

import java.util.ArrayList;

/**
 * Created by JAMES on 11/07/2014.
 */
public class User {
    private ArrayList<Handle> Handles;
    private ApiConnection apiConnection = ApiConnection.getInstance();

    public User(){

    }

    public ArrayList<Handle> getHandleList(){
        return this.Handles;
    }

    public void setHandleList(ArrayList<Handle> Handles){
        this.Handles = Handles;
    }

    public void addHandle(Handle newHandle){
        // todo: append to Handles list, I cant be bothered to google it
    }

    public void removeHandle(){
        // todo: remove from Handle list, don't know what to pass into this function to know what to delete
    }

    private void loadHandles(){

    }
}
