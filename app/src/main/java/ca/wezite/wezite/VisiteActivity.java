package ca.wezite.wezite;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.wezite.wezite.model.Parcours;
import ca.wezite.wezite.model.PointDinteret;
import ca.wezite.wezite.model.PointParcours;
import ca.wezite.wezite.utils.Constantes;

public class VisiteActivity extends MereMapsActivity implements OnMapReadyCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference parcoursCloudEndPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parcoursCloudEndPoint = mDatabase.child("parcours/"+getIntent().getStringExtra("id_parcours"));

        parcoursCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Parcours parcours = dataSnapshot.getValue(Parcours.class);
                drawPrimaryLinePath(parcours.getListePoints());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        mDrawer = findViewById(R.id.home);
        mWeziteboot.checkFirebaseAuth(this,mDrawer); // DO NOT FORGET PLZZZ
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void drawPrimaryLinePath( List<PointParcours> listLocsToDraw ) {
        if ( mMap == null ) {
            return;
        }
        if (listLocsToDraw.size() < 2) {
            return;
        }

        PolylineOptions options = new PolylineOptions();
        options.color(Color.parseColor("#CC0000FF"));
        options.width(12);
        options.visible(true);

        for (PointParcours pointParcours : listLocsToDraw) {
            options.add(new LatLng(Double.parseDouble(pointParcours.getxCoord()),
                    Double.parseDouble(pointParcours.getyCoord())));
        }
        mMap.addPolyline(options);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
       super.onMapReady(googleMap);
    }
}