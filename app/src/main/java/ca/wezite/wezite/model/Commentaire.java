package ca.wezite.wezite.model;

public class Commentaire {

    private String commentaire;

    private String auteur;

    public Commentaire() {
    }

    public Commentaire(String commentaire, String auteur) {
        this.commentaire = commentaire;
        this.auteur = auteur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
}
