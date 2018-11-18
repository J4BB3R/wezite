package ca.wezite.wezite.model;

import android.graphics.Point;

import org.w3c.dom.Comment;

import java.util.List;

public class Parcours {



    private String id;

    public Parcours() {
    }

    private double duree;

    private String nomCreateur;

    private List<Commentaire> commentaires;

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

    public double getDuree() {
        return duree;
    }

    public void setDuree(double duree) {
        this.duree = duree;
    }

    public String getNomCreateur() {
        return nomCreateur;
    }

    public void setNomCreateur(String nomCreateur) {
        this.nomCreateur = nomCreateur;
    }

    public List<Commentaire> getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(List<Commentaire> commentaires) {
        this.commentaires = commentaires;
    }

    public void setListIdPointsInterets(List<String> listIdPointsInterets) {
        this.listIdPointsInterets = listIdPointsInterets;
    }


}
