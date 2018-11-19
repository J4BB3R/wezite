package ca.wezite.wezite;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfosParcoursActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String par = "histoire";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_parcours);

        mDrawer = findViewById(R.id.infoP);
        mWeziteboot.checkFirebaseAuth(this,mDrawer);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readOnDB();
    }

    public void readOnDB(){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                TextView txt = findViewById(R.id.titreP);
                TextView resum = findViewById(R.id.resumeP);
                txt.setText(data.child("parcours").child(par).child("name").getValue(String.class));
                resum.setText("Type : " + data.child("parcours").child(par).child("type").getValue(String.class) + "\nDurée : " + data.child("parcours").child(par).child("duree").getValue(Double.class) + "\n");
                for(DataSnapshot ds : data.child("parcours").child(par).child("listIdPointsInterets").getChildren()){
                    resum.setText(resum.getText() + data.child("pointDInterets").child(ds.getValue(String.class)).child("nom").getValue(String.class) + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
