package ca.wezite.wezite.model;

import java.util.Date;
import java.util.List;

public class PointDinteret {

    private String id;

    private String nom;

    private String description;

    private  String xCoord;

    private String yCoord;

    private String imgPath;

    private int nbVues;

    private String auteur;

    private Date dateCréation;

    public PointDinteret() {
    }

    public PointDinteret(String id, String nom, String description, String xCoord, String yCoord) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getxCoord() {
        return xCoord;
    }

    public void setxCoord(String xCoord) {
        this.xCoord = xCoord;
    }

    public String getyCoord() {
        return yCoord;
    }

    public void setyCoord(String yCoord) {
        this.yCoord = yCoord;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getNbVues() {
        return nbVues;
    }

    public void setNbVues(int nbVues) {
        this.nbVues = nbVues;
    }

    public Date getDateCréation() {
        return dateCréation;
    }

    public void setDateCréation(Date dateCréation) {
        this.dateCréation = dateCréation;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    @Override
    public String toString() {
        return "PointDinteret{" +
                "id='" + id + '\'' +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", xCoord='" + xCoord + '\'' +
                ", yCoord='" + yCoord + '\'' +
                '}';
    }
}
