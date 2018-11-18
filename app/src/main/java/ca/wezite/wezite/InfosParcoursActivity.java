package ca.wezite.wezite;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfosParcoursActivity extends AppCompatActivity {

    private String par = "histoire";
    private TextView txt = findViewById(R.id.titreP);
    private TextView resum = findViewById(R.id.resumeP);
    private TextView desc = findViewById(R.id.descriptionP);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_parcours);

        readOnDB();
    }

    public void readOnDB(){

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot data) {

                txt.setText(data.child("parcours").child(par).child("name").getValue(String.class));
                resum.setText("Type : " + data.child("parcours").child(par).child("type").getValue(String.class) + "\nDurée : " + data.child("parcours").child(par).child("duree").getValue(Double.class));
                for(DataSnapshot ds : data.child("parcours").child(par).child("listIdPointsInterets").getChildren()){
                    desc.setText(desc.getText() + data.child("pointDInterets").child(ds.getValue(String.class)).child("nom").getValue(String.class) + "\n");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
