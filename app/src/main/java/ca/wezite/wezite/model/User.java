package ca.wezite.wezite.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    private String id;

    private String email;

    private List<String> userP;

    public boolean isNotif() {
        return notif;
    }

    public void setNotif(boolean notif) {
        this.notif = notif;
    }

    private boolean notif;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getUserP() {
        return userP;
    }

    public void setUserP(List<String> userP) {
        this.userP = userP;
    }

    public void addUserP(String p){
        if(userP==null){
            userP=new ArrayList<>();
        }
        userP.add(p);
    }

    public void removeUserP(String p){
        userP.remove(p);
    }

}
