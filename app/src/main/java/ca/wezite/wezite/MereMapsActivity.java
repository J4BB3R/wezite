package ca.wezite.wezite;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
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

public class MereMapsActivity extends MereActivity implements OnMapReadyCallback, LocationListener, NavigationView.OnNavigationItemSelectedListener {


    protected GoogleMap mMap;
    protected DatabaseReference pointsDInteretsCloudEndPoint;

    protected FloatingActionButton playButton;

    protected LocationManager locationManager;

    protected List<PointDinteret> pointDinteretList = new ArrayList<>();
    protected PointDinteret pointAPromite;
    protected LocationListener altListener;

    protected String channelId = "wezite-position-tracker";
    protected NotificationManager notificationManager;
    protected NotificationChannel notificationChannel;
    protected static boolean notified=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visite);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        playButton = (FloatingActionButton) findViewById(R.id.afficher_details);
        playButton.hide();

        mWeziteboot.checkFirebaseAuth(this, findViewById(R.id.map)); // DO NOT FORGET PLZZZ

         pointsDInteretsCloudEndPoint = mDatabase.child("pointDInterets");

        pointsDInteretsCloudEndPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    PointDinteret pointDInteret = noteSnapshot.getValue(PointDinteret.class);
                    LatLng point1 = new LatLng(Double.valueOf(pointDInteret.getxCoord()), Double.valueOf(pointDInteret.getyCoord()));
                    if (mMap != null && pointDInteret != null)
                        mMap.addMarker(new MarkerOptions().position(point1).title(pointDInteret.getNom()));
                    pointDinteretList.add(pointDInteret);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = notificationManager.getNotificationChannel(channelId);
        if (notificationChannel == null) {
            notificationChannel = new NotificationChannel(channelId, "wezite channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLightColor(Color.YELLOW); //Set if it is necesssary
            notificationChannel.enableVibration(true); //Set if it is necesssary
            notificationManager.createNotificationChannel(notificationChannel);
        }
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);
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

            LatLng pos;
            if(getIntent().getStringExtra("x")==null && getIntent().getStringExtra("y")==null) {
                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(new Criteria(), false));
                pos = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                pos = new LatLng(new Double(getIntent().getStringExtra("x")),new Double(getIntent().getStringExtra("y")));
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pos)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        boolean isPointAPromite = false;

        if(pointDinteretList!=null){
            for(PointDinteret pointDinteret : pointDinteretList){
                float[] distance= new float[1];
                Location.distanceBetween(location.getLatitude(),
                        location.getLongitude(), Double.parseDouble(pointDinteret.getxCoord()),
                        Double.parseDouble(pointDinteret.getyCoord()), distance);
                if(distance[0]<20){
                    pointAPromite=pointDinteret;
                    isPointAPromite=true;
                }
            }
        }
        if(!isPointAPromite){
            playButton.hide();
        }
        else{
            playButton.show();
        }
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

    public void afficherPlus(View view) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        pointsDInteretsCloudEndPoint.child(pointAPromite.getId()+"/"+"nbVues").setValue(pointAPromite.getNbVues()+1);

        Intent intent = new Intent(MereMapsActivity.this, InfosLieuActivity.class);
        intent.putExtra("titre", pointAPromite.getNom());
        intent.putExtra("description", pointAPromite.getDescription());
        intent.putExtra("photo", pointAPromite.getImgPath());
        intent.putExtra("auteur", pointAPromite.getAuteur());
        intent.putExtra("nbVues", pointAPromite.getNbVues()+"");
        intent.putExtra("dateCreation", df.format("dd/MM/yyyy", pointAPromite.getDateCréation()));
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onPause();
        locationManager.removeUpdates(this);
        altListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(pointDinteretList!=null){
                    int count = 0;
                    for(PointDinteret pointDinteret : pointDinteretList){
                        float[] distance= new float[1];
                        Location.distanceBetween(location.getLatitude(),
                                location.getLongitude(), Double.parseDouble(pointDinteret.getxCoord()),
                                Double.parseDouble(pointDinteret.getyCoord()), distance);
                        if(distance[0]<20){
                            count++;
                            if(!notified){
                                Intent resultIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                resultIntent.putExtra("x", pointDinteret.getxCoord());
                                resultIntent.putExtra("y", pointDinteret.getyCoord());
                                resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
                                Notification notif = new Notification.Builder(getApplicationContext(),channelId)
                                        .setSmallIcon(R.drawable.logo)
                                        .setContentTitle("Cliquez pour ouvrir la carte")
                                        .setContentText("Le point '"+pointDinteret.getNom()+"' est proche de vous !")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setContentIntent(contentIntent)
                                        .build();
                                notificationManager.notify(0, notif);
                                notified=true;
                            }
                        }
                    }
                    if(count==0 && notified) {
                        notificationManager.cancel(0);
                        notified=false;
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5, altListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        locationManager.removeUpdates(altListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, this);
        Log.i("Cul",getIntent().getStringExtra("x")+"");
        if(getIntent().getStringExtra("x")!=null && getIntent().getStringExtra("y")!=null) {
            LatLng pos = new LatLng(new Double(getIntent().getStringExtra("x")), new Double(getIntent().getStringExtra("y")));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pos)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}