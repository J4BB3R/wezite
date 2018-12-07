package ca.wezite.wezite.async;

import android.os.AsyncTask;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.wezite.wezite.MereActivity;
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

    private Parcours parcours;


    @Override
    protected Boolean doInBackground(String... params) {
        parcours.setListePoints(new ArrayList<PointParcours>());
        parcours.setId(parcours.getUid() + new Date());
        parcours.setListIdPointsInterets(new ArrayList<String>());
        for(PointDinteret pointDinteret : listPointsDinterets){
            parcours.getListIdPointsInterets().add(pointDinteret.getId());
        }
        for(int i=1;i<listPointsDinterets.size();i++){

            PointDinteret pointDepart = listPointsDinterets.get(i-1);
            PointDinteret pointArrivee = listPointsDinterets.get(i);
            String url="https://api.openrouteservice.org/directions?api_key=5b3ce3597851110001cf6248a8274cc6c9994e8aaaa2480ae9cd9da5&coordinates="+pointDepart.getyCoord()+","+pointDepart.getxCoord()+"%7C"+pointArrivee.getyCoord()+","+pointArrivee.getxCoord()+"&profile=foot-walking&preference=recommended&geometry_format=polyline";
            parcours.getListePoints().addAll(recupererPoints(url));
        }
        DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
        DatabaseReference parcoursCloudEndPoint = mDatabase.child("parcours");
        parcoursCloudEndPoint.child(parcours.getId()).setValue(parcours);
        MereActivity.user.getListIdsParcoursAjoutes().add(parcours.getId());
        mDatabase.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(MereActivity.user);
       return true;
    }

    protected List<PointParcours> recupererPoints(String url) {



        try {
            String data = HttpUtils.downloadUrl(url);
            JSONObject jsono = new JSONObject(data);
            JSONArray routes = jsono.getJSONArray("routes");
            JSONArray geometry = routes.getJSONObject(0).getJSONArray("geometry");

            double duree = routes.getJSONObject(0).getJSONObject("summary").getDouble("duration");
            parcours.setDuree(parcours.getDuree()+duree);
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

    public List<PointDinteret> getListPointsDinterets() {
        return listPointsDinterets;
    }

    public void setListPointsDinterets(List<PointDinteret> listPointsDinterets) {
        this.listPointsDinterets = listPointsDinterets;
    }

    public Parcours getParcours() {
        return parcours;
    }

    public void setParcours(Parcours parcours) {
        this.parcours = parcours;
    }
}