package ca.wezite.wezite.dev;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import ca.wezite.wezite.model.PointDinteret;


public class InitData {


    public static List<PointDinteret> initPointsDinterets(){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        DatabaseReference pointsDinteretCloudEndPoint = mDatabase.child("pointDInterets");
        pointsDinteretCloudEndPoint.removeValue();

        String key = pointsDinteretCloudEndPoint.push().getKey();
        List<PointDinteret> listPointsDinterets = new ArrayList<>();
        PointDinteret p = new PointDinteret(key, "Le bassin", "En juillet 1996, la rivière Chicoutimi, gonflée par des pluies torrentielles, atteint en quelques heures onze fois son débit normal. Des dizaines de maisons sont emportées par les flots. Le monde entier peut voir les images de la désormais célèbre « petite maison blanche » résistant à l’une des pires catastrophes naturelles ayant touché le Québec. Au lendemain du déluge, le site était dévasté. Il fait place aujourd’hui à un parc urbain très fréquenté, autant des résidents que des touristes. Le secteur a été constitué en site du patrimoine par la Ville de Saguenay en 2001.", "48.425745","-71.072532");
        p.setAuteur("Barack O");
        p.setImgPath("280px-Chicou4.jpg");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        listPointsDinterets.add(p);

        key = pointsDinteretCloudEndPoint.push().getKey();
        p = new PointDinteret(key, "La centrale hydro-éléctrique", "Observez au loin la centrale hydro-éléctrique Price, construite en 1923. Cette centrale n'est plus en activité aujourd'hui, mais il s'agit d'une des premières centrales du Québec", "48.426016","-71.075702");
        p.setAuteur("John D");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        listPointsDinterets.add(p);

        key = pointsDinteretCloudEndPoint.push().getKey();
        p = new PointDinteret(key, "Le monument du Coteau-du-portage", "Érigé en 1937 par la Société historique du Saguenay et " +
                "la Société Saint-Jean-Baptiste, le monument Coteau du-portage " +
                "rend hommage à ceux qui ont visité la " +
                "région avant la colonisation", "48.427655"," -71.076813");
        p.setAuteur("Donal T");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        listPointsDinterets.add(p);

        key = pointsDinteretCloudEndPoint.push().getKey();
        p = new PointDinteret(key, "La cote saint Ange", "Les maisons de ce quartier témoignent du fort passé ouvrier. Remarquablement conservées, les maisons en bois ont été construites par les employés de la compagnie de pulpe de Chicoutimi", "48.424942","-71.081232");
        p.setAuteur("Emmanuel M");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        listPointsDinterets.add(p);

        key = pointsDinteretCloudEndPoint.push().getKey();
        p = new PointDinteret(key, "La pulperie", "Découvrez les cathédrales industrielles datant du siècle dernier qui abritent La Pulperie de Chicoutimi. Si vous avez le temps, baladez vous dans les magnifiques jardins et découvrez les vestiges de l'exploitation de la pulpe en ces lieux", "48.423299","-71.080037");
        p.setAuteur("François H");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        listPointsDinterets.add(p);

        key = pointsDinteretCloudEndPoint.push().getKey();
        p = new PointDinteret(key, "L'église du Sacré-Coeurs", "L'église du Sacré-Coeur est un lieu de culte de tradition catholique érigé de 1903 à 1905. L'édifice en pierre d'inspiration néogothique présente un plan en croix latine composé d'une nef à trois vaisseaux, d'un transept et d'un choeur en saillie terminé par une abside à pans coupés. Sa façade est encadrée de contreforts surmontés de pinacles. Elle est dotée d'une imposante tour-clocher demi-hors-oeuvre au bas de laquelle est aménagé un imposant portail à arc brisé en pierre de taille. Des portails latéraux sont disposés de part et d'autre. Construite en 1919 et 1920, la sacristie en pierre est greffée à l'abside dans le prolongement du choeur et reprend le plan de l'église de façon simplifiée. L'église du Sacré-Coeur est située dans le quartier du Bassin, un ancien secteur ouvrier, dans l'arrondissement municipal de Chicoutimi de la ville de Saguenay. Elle est implantée sur un promontoire rocheux, à proximité de la rivière Chicoutimi.",
                "48.424331","-71.076443");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        p.setAuteur("Angela M");
        listPointsDinterets.add(p);

        key = pointsDinteretCloudEndPoint.push().getKey();
        p = new PointDinteret(key, "La maison Price", "La maison Price est une demeure bourgeoise en bois de plan rectangulaire, à un étage et demi, coiffée d'un toit à deux versants retroussés aux larmiers saillants. Elle a été érigée vers 1870 et se situe dans le quartier du Bassin, dans le secteur historique de l'arrondissement Chicoutimi de la ville de Saguenay.",
                "48.425176","-71.072291");
        p.setAuteur("Georges B");
        pointsDinteretCloudEndPoint.child(key).setValue(p);
        listPointsDinterets.add(p);


        return listPointsDinterets;
    }
}
