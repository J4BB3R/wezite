package ca.wezite.wezite;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.View;
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

        FloatingActionButton btn = findViewById(R.id.floating);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent parc = new Intent( InfosParcoursActivity.this, VisiteActivity.class);
                parc.putExtra("key", par);
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

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                TextView txt = findViewById(R.id.titreP);
                TextView resum = findViewById(R.id.resumeP);
                TextView imp = findViewById(R.id.importantP);
                TextView lis = findViewById(R.id.listPI);
                txt.setText(data.child("parcours").child(par).child("name").getValue(String.class));
                String time = timeStoHMS(data.child("parcours").child(par).child("duree").getValue(Double.class));
                resum.setText(resum.getText() + data.child("parcours").child(par).child("description").getValue(String.class));
                imp.setText("Type : " + data.child("parcours").child(par).child("type").getValue(String.class) + "\nDurée : " + time + "\n");

                //Liste Point Interet
                for(DataSnapshot ds : data.child("parcours").child(par).child("listIdPointsInterets").getChildren()){
                    lis.setText(lis.getText() + data.child("pointDInterets").child(ds.getValue(String.class)).child("nom").getValue(String.class) + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
