package ca.wezite.wezite;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ca.wezite.wezite.model.Parcours;
import ca.wezite.wezite.utils.Constantes;
import ca.wezite.wezite.view.ParcourListAdaptor;

public class ParcoursListActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner spin;
    private LocationManager locationManager;
    private Location location;
    private SeekBar simpleSeek;
    private TextView resulTxt;

    private List<Parcours> parcourListBuff = new ArrayList<>();
    private List<Parcours> parcourList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours_list);

        mDrawer = findViewById(R.id.parcourList);

        mWeziteboot.checkFirebaseAuth(this, mDrawer); // DO NOT FORGET PLZZZ

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        }

        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        ((NavigationView)findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.parcour_list);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ParcourListAdaptor(this,parcourList);
        mRecyclerView.setAdapter(mAdapter);

        resulTxt = (TextView) findViewById(R.id.resultTxt);

        /*
        for(int i=0;i<12;i++){
            Parcours p = new Parcours();
            p.setType("Culture");
            p.setName("Csdfsdfsdf");
            p.setDescription("dfgdsfgfdsgfdsgsdfgsdfgsdfgsdfgsdfgsdfgsdfgsdfgsdfgsdfgsdf");
            p.setDistance(2600);
            parcourList.add(p);
        }
        */


        simpleSeek = (SeekBar)findViewById(R.id.distance);
        final TextView simpleSeekValue = (TextView) findViewById(R.id.distanceValueTxt);

        simpleSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0){
                    simpleSeekValue.setText("<1");
                } else {
                    simpleSeekValue.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                prepareDatas();
            }
        });

        // TODO Change with database array
        spin = (Spinner)findViewById(R.id.typeSpinner);

        spin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prepareDatas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, Constantes.arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);

        mDatabase.child("parcours").addValueEventListener(new ValueEventListener() { //TODO Change filter
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    parcourListBuff.add(noteSnapshot.getValue(Parcours.class));
                }
                prepareDatas();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        prepareDatas();
    }

    public void prepareDatas(){
        parcourList.clear();
        String spinnerValue = Constantes.arraySpinner[spin.getSelectedItemPosition()];
        boolean check = Constantes.arraySpinner[0].equals(spinnerValue);

        for(Parcours par: parcourListBuff){
            if(spinnerValue.equals(par.getType())||check){
                float[] distance = new float[1];
                Location.distanceBetween(location.getLatitude(),
                        location.getLongitude(), Double.parseDouble(par.getListePoints().get(0).getxCoord()),
                        Double.parseDouble(par.getListePoints().get(0).getyCoord()), distance);
                par.setDistance(distance[0]);
                if(distance[0]<=simpleSeek.getProgress()*1000){
                    parcourList.add(par); //TODO trie par choix
                    resulTxt.setText(mAdapter.getItemCount()+" Résultats"); //TODO Fix Page scroll
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }
}
