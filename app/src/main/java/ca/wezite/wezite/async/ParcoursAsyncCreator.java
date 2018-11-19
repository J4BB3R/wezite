package ca.wezite.wezite.async;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ca.wezite.wezite.dev.InitData;
import ca.wezite.wezite.model.Parcours;
import ca.wezite.wezite.model.PointDinteret;
import ca.wezite.wezite.model.PointParcours;
import ca.wezite.wezite.utils.HttpUtils;

public class ParcoursAsyncCreator extends AsyncTask<String, Void, Boolean> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    private List<PointDinteret> listPointsDinterets;

    private Parcours p;

    public  void initParcours(){

        p = new Parcours();
        String key = "histoire";
        p.setNomCreateur("Baptiste C");
        p.setId(key);
        p.setName("Aux origines historiques du Saguenay");
        p.setDescription("Découvrez les monuments historiques du Saguenay, en vous promenant sur les rives de la rivière mythique ! Voyagez dans le temps, et retracez le parcours des premiers colons, qui ont bati cette ville à la sueur de leur front");
        p.setType("Culture");
        p.setImgPath("barrage-3.jpg");

        p.setListIdPointsInterets(new ArrayList<String>());
        listPointsDinterets = InitData.initPointsDinterets();
        for(PointDinteret point : listPointsDinterets){
            p.getListIdPointsInterets().add(point.getId());
        }

        p.setListePoints(new ArrayList<PointParcours>());
    }

    @Override
    protected Boolean doInBackground(String... params) {

        initParcours();
        for(int i=1;i<listPointsDinterets.size();i++){

            PointDinteret pointDepart = listPointsDinterets.get(i-1);
            PointDinteret pointArrivee = listPointsDinterets.get(i);
            String url="https://api.openrouteservice.org/directions?api_key=5b3ce3597851110001cf6248a8274cc6c9994e8aaaa2480ae9cd9da5&coordinates="+pointDepart.getyCoord()+","+pointDepart.getxCoord()+"%7C"+pointArrivee.getyCoord()+","+pointArrivee.getxCoord()+"&profile=foot-walking&preference=recommended&geometry_format=polyline";
            p.getListePoints().addAll(recupererPoints(url));
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        DatabaseReference parcoursCloudEndPoint = mDatabase.child("parcours");
        parcoursCloudEndPoint.removeValue();
        parcoursCloudEndPoint.child(p.getId()).setValue(p);
       return true;
    }

    protected List<PointParcours> recupererPoints(String url) {



        try {
            String data = HttpUtils.downloadUrl(url);
            JSONObject jsono = new JSONObject(data);
            JSONArray routes = jsono.getJSONArray("routes");
            JSONArray geometry = routes.getJSONObject(0).getJSONArray("geometry");

            double duree = routes.getJSONObject(0).getJSONObject("summary").getDouble("duration");
            p.setDuree(p.getDuree()+duree);
            final List<PointParcours> listePointsParcours = new ArrayList<>();
            for(int i=0;i<geometry.length();i++){
                JSONArray coordonnees = geometry.getJSONArray(i);
                double yCoord = coordonnees.getDouble(0);
                double xCoord = coordonnees.getDouble(1);
                PointParcours p = new PointParcours(String.valueOf(xCoord), String.valueOf(yCoord));
                listePointsParcours.add(p);
            }

            return listePointsParcours;


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return new ArrayList<>();
    }

    protected void onPostExecute(Boolean result) {

    }

}