package ca.wezite.wezite;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ca.wezite.wezite.async.ParcoursAsyncCreator;
import ca.wezite.wezite.model.Parcours;
import ca.wezite.wezite.model.PointDinteret;
import ca.wezite.wezite.utils.Constantes;

public class AjoutParcoursActivity extends MereMapsActivity implements NavigationView.OnNavigationItemSelectedListener, OnMarkerClickListener {


    private List<Marker> listPointsNouveauParcours = new ArrayList<>();
    private View customView;
    private Spinner spin;

    private PopupWindow mPopupWindow;
    private Bitmap selectedImage;

    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setLayout(R.layout.activity_ajouter_parcours);
        ctx=this;

        super.onCreate(savedInstanceState);
        mDrawer = findViewById(R.id.home);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);

        ((NavigationView)findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       super.onMapReady(googleMap);
       mMap.setOnMarkerClickListener(this);

    }




    @Override
    public boolean onMarkerClick(Marker marker) {
        PointDinteret p = (PointDinteret) marker.getTag();
        if(listPointsNouveauParcours.contains(marker)){
            return true;
        }

        listPointsNouveauParcours.add(marker);
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        marker.setTitle(listPointsNouveauParcours.size() +"");
        marker.setSnippet(p.getNom());
        marker.showInfoWindow();



        return true;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void supprimerDernierPoint(View view) {
        if(listPointsNouveauParcours.size()>0){
            Marker marker = listPointsNouveauParcours.get(listPointsNouveauParcours.size()-1);
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            marker.hideInfoWindow();
            listPointsNouveauParcours.remove(marker);
        }
    }

    public void ajouterPoint(View view) {
        if(listPointsNouveauParcours.size()>1) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            customView = inflater.inflate(R.layout.popup_creer_parcours, null);

            mPopupWindow = new PopupWindow(
                    customView,
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
            );
            mPopupWindow.setFocusable(true);
            mPopupWindow.showAtLocation(mDrawer, Gravity.CENTER_HORIZONTAL, 0, 0);
            spin = (Spinner)customView.findViewById(R.id.typeSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item_primary, Constantes.TYPE_SPINNER);
            adapter.setDropDownViewResource(R.layout.spinner_item_primary);
            spin.setAdapter(adapter);

            customView.findViewById(R.id.lieu_image).setVisibility(View.GONE);
            customView.findViewById(R.id.fermer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                    selectedImage = null;
                }
            });
            customView.findViewById(R.id.input_ajouter_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                }
            });
            customView.findViewById(R.id.ajouter).setEnabled(false);
            customView.findViewById(R.id.input_prendre_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                }
            });
            customView.findViewById(R.id.ajouter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Button) customView.findViewById(R.id.ajouter)).setEnabled(false);

                    List<PointDinteret> pointDinteretList = new ArrayList<>();
                    for (Marker m : listPointsNouveauParcours) {
                        PointDinteret pointDinteret = (PointDinteret) m.getTag();
                        pointDinteretList.add(pointDinteret);
                    }
                    String titre = ((EditText) customView.findViewById(R.id.text_nom_lieu)).getText().toString();
                    String description = ((EditText) customView.findViewById(R.id.text_description_lieu)).getText().toString();
                    if(titre.isEmpty()){
                        ((EditText) customView.findViewById (R.id.text_nom_lieu)).setError("Valeur obligatoire");
                    }
                    if(description.isEmpty()){
                        ((EditText) customView.findViewById (R.id.text_description_lieu)).setError("Valeur obligatoire");
                    }
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Parcours parcours = new Parcours();
                    parcours.setNomCreateur(user.getDisplayName());
                    parcours.setUid(user.getUid());
                    parcours.setName(titre);
                    parcours.setDescription(description);
                    parcours.setType(Constantes.TYPE_SPINNER[spin.getSelectedItemPosition()]);
                    final ParcoursAsyncCreator directionsReader = new ParcoursAsyncCreator();
                    directionsReader.setListPointsDinterets(pointDinteretList);
                    directionsReader.setParcours(parcours);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    String imgPath = parcours.getUid()+new Date();
                    parcours.setImgPath(imgPath);
                    StorageReference imgRef = storageReference.child("images/"+ imgPath);
                    UploadTask uploadTask = imgRef.putBytes(baos.toByteArray());
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // TODO : handle onFailure
                            Toast.makeText(ctx, "Erreur de création", Toast.LENGTH_LONG).show();
                            ((Button) customView.findViewById(R.id.ajouter)).setEnabled(true);


                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            directionsReader.execute();
                            mPopupWindow.dismiss();
                            Toast.makeText(ctx, "Parcours en cours de création", Toast.LENGTH_LONG).show();
                            ((Button) customView.findViewById(R.id.ajouter)).setEnabled(true);

                        }
                    });


                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    selectedImage =  (Bitmap) imageReturnedIntent.getExtras().get("data");
                    ((ImageView) customView.findViewById(R.id.lieu_image)).setImageBitmap(selectedImage);
                    customView.findViewById(R.id.lieu_image).setVisibility(View.VISIBLE);
                    ((Button) customView.findViewById(R.id.ajouter)).setEnabled(true);


                }

                break;
            case 1:
                if(resultCode == RESULT_OK){

                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageReturnedIntent.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ((ImageView) customView.findViewById(R.id.lieu_image)).setImageBitmap(selectedImage);
                    customView.findViewById(R.id.lieu_image).setVisibility(View.VISIBLE);
                    ((Button) customView.findViewById(R.id.ajouter)).setEnabled(true);


                }
                break;
        }

    }


}

