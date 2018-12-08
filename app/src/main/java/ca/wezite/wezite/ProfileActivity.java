package ca.wezite.wezite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;

import ca.wezite.wezite.async.DownloadImageTask;
import ca.wezite.wezite.model.User;


public class ProfileActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDrawer = findViewById(R.id.profile);
        mWeziteboot.checkFirebaseAuth(this,mDrawer);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView nom = findViewById(R.id.nomProfil);
        final TextView mail = findViewById(R.id.mailProfil);
        final ImageView img = findViewById(R.id.photoProfile);

        String id_profil = getIntent().getStringExtra("id_profil");

        if(id_profil==null){

            nom.setText(user.getName());
            mail.setText(user.getEmail());
            Uri st = Uri.parse(user.getPhoto());
            Picasso.get()
                    .load(st)
                    .into(img);
        } else {
            mDatabase.child("users/").child(id_profil).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user1 = dataSnapshot.getValue(User.class);
                    nom.setText(user1.getName());
                    mail.setText(user1.getEmail());
                    Picasso.get()
                            .load(user1.getPhoto())
                            .into(img);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


}
