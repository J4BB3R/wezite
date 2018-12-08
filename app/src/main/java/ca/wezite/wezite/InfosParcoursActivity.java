package ca.wezite.wezite;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.wezite.wezite.model.Parcours;
import ca.wezite.wezite.model.User;
import ca.wezite.wezite.utils.Constantes;

public class InfosParcoursActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, LocationListener {

    protected GoogleMap mMap;
    protected View mMapFragment;
    private LatLng coord;

    protected LocationManager locationManager;
    private Parcours parcours;
    private String par;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_parcours);

        mMapFragment = findViewById(R.id.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);

        mDrawer = findViewById(R.id.infoP);
        mWeziteboot.checkFirebaseAuth(this,mDrawer);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        par = getIntent().getStringExtra("id_parcours");
        readOnDB();

        final Switch sw = findViewById(R.id.switchB);

        if(user.getUserP() != null && user.getUserP().contains(par)){
            sw.setChecked(true);
        }

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    user.addUserP(par);
                } else {
                    user.getUserP().remove(par);
                }
                mDatabase.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);

            }
        });
        FloatingActionButton btn = findViewById(R.id.floating);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent parc = new Intent( InfosParcoursActivity.this, VisiteActivity.class);
                parc.putExtra("id_parcours", par);
                startActivity(parc);
            }
        });
    }
    public String timeStoHMS(double num){

        int hours = (int) num / 3600;
        int minutes = (int) (num % 3600) / 60;

        return String.format("%02d h %02d min", hours, minutes);
    }


    public void readOnDB(){

        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                TextView txt = findViewById(R.id.titreP);
                TextView resum = findViewById(R.id.resumeP);
                TextView type = findViewById(R.id.typeP);
                TextView duree = findViewById(R.id.dureeP);
                TextView lis = findViewById(R.id.listPI);
                TextView ptsRem = findViewById(R.id.ptsRemarq);
                TextView desc = findViewById(R.id.descParcour);
                Switch sw = findViewById(R.id.switchB);
                TextView auteur = findViewById(R.id.auteurText);

                parcours = data.child("parcours/"+par).getValue(Parcours.class);

                coord = new LatLng(Double.valueOf(parcours.getListePoints().get(0).getxCoord()), Double.valueOf(parcours.getListePoints().get(0).getyCoord()));
                mMap.addMarker(new MarkerOptions().position(coord).title(parcours.getName()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coord, 13));

                txt.setText(parcours.getName().toUpperCase());
                String time = timeStoHMS(parcours.getDuree());
                resum.setText(parcours.getDescription());

                type.setText(parcours.getType().toUpperCase());
                duree.setText(time);


                auteur.setText(parcours.getNomCreateur());
                findViewById(R.id.auteurClick).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("id_profil", parcours.getUid());
                        getApplicationContext().startActivity(intent);
                    }
                });

                //Liste Point Interet
                for(String ds : parcours.getListIdPointsInterets()){
                    lis.setText(lis.getText() + "•  " + data.child("pointDInterets").child(ds).child("nom").getValue(String.class) + "\n");
                }

                ptsRem.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    Constantes.LOCATION_PERMISSION_REQUEST_CODE);

        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                        .zoom(13)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setCompassEnabled(true);
        enableMyLocationIfPermitted();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Afficher message d'erreur
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            mMapFragment.setVisibility(View.GONE);
            findViewById(R.id.imageP).setVisibility(View.GONE);
        } else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mMapFragment.setVisibility(View.VISIBLE);
            findViewById(R.id.imageP).setVisibility(View.VISIBLE);
        }
    }
}
