package ca.wezite.wezite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import ca.wezite.wezite.model.Parcours;
import ca.wezite.wezite.model.PointDinteret;
import ca.wezite.wezite.utils.WeziteBoot;
import ca.wezite.wezite.view.ParcourListAdaptor;

public class ParcoursListActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Spinner spin;

    private List<Parcours> parcourListBuff = new ArrayList<>();
    private List<Parcours> parcourList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours_list);

        mDrawer = findViewById(R.id.parcourList);

        mWeziteboot.checkFirebaseAuth(this,mDrawer); // DO NOT FORGET PLZZZ

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


        SeekBar simpleSeek = (SeekBar)findViewById(R.id.distance);
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
        Spinner spin = (Spinner)findViewById(R.id.typeSpinner);
        String[] arraySpinner = new String[] {
                "Tous",
                "Nature",
                "Culture",
                "Art",
                "Histoire",
                "Photographie"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, arraySpinner);
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
        for(Parcours par: parcourListBuff){
            parcourList.add(par);
        }
        mAdapter.notifyDataSetChanged();
    }
}
