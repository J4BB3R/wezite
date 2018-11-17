package ca.wezite.wezite.model;

import android.graphics.Point;

import java.util.List;

public class Parcours {



    private String id;

    public Parcours() {
    }

    private String name;

    private List<PointParcours> listePoints;

    private List<String> listIdPointsInterets;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PointParcours> getListePoints() {
        return listePoints;
    }

    public void setListePoints(List<PointParcours> listePoints) {
        this.listePoints = listePoints;
    }

    public List<String> getListIdPointsInterets() {
        return listIdPointsInterets;
    }

    public void setListIdPointsInterets(List<String> listIdPointsInterets) {
        this.listIdPointsInterets = listIdPointsInterets;
    }
}
