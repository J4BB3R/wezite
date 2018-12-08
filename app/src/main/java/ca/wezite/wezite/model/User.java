package ca.wezite.wezite.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class User {

    private String id;

    private String email;

    private List<String> userP;

    private boolean notif;

    private String photo;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean isNotif() {
        return notif;
    }

    public void setNotif(boolean notif) {
        this.notif = notif;
    }

    private List<String> listIdsParcoursAjoutes;

    private List<String> listeIdPointsAjoutes;

    public List<String> getListeIdPointsAjoutes() {
        if (listeIdPointsAjoutes==null)
            listeIdPointsAjoutes=new ArrayList<>();
        return listeIdPointsAjoutes;
    }

    public void setListeIdPointsAjoutes(List<String> listeIdPointsAjoutes) {
        this.listeIdPointsAjoutes = listeIdPointsAjoutes;
    }

    public List<String> getListIdsParcoursAjoutes() {
        if(listIdsParcoursAjoutes==null)
            listIdsParcoursAjoutes=new ArrayList<>();
        return listIdsParcoursAjoutes;
    }

    public void setListIdsParcoursAjoutes(List<String> listIdsParcoursAjoutes) {
        this.listIdsParcoursAjoutes = listIdsParcoursAjoutes;
    }

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
