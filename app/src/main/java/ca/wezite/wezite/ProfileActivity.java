package ca.wezite.wezite;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import ca.wezite.wezite.async.DownloadImageTask;
import ca.wezite.wezite.model.User;


public class ProfileActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    private LinearLayout listProfil;
    private User profil;
    private TabLayout tabProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDrawer = findViewById(R.id.profile);
        mWeziteboot.checkFirebaseAuth(this, mDrawer);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listProfil = findViewById(R.id.listProfil);
        final TextView nom = findViewById(R.id.nomProfil);
        final TextView mail = findViewById(R.id.mailProfil);
        final ImageView img = findViewById(R.id.photoProfile);
        tabProfil = findViewById(R.id.tabProfil);

        String id_profil = getIntent().getStringExtra("id_profil");

        if (id_profil == null) {
            profil = user;
            nom.setText(profil.getName());
            mail.setText(profil.getEmail());
            Uri st = Uri.parse(profil.getPhoto());
            Picasso.get()
                    .load(st)
                    .into(img);
            onAddField(0);
        } else {
            mDatabase.child("users/").child(id_profil).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    profil = dataSnapshot.getValue(User.class);
                    nom.setText(profil.getName());
                    mail.setText(profil.getEmail());
                    try{
                        Uri st = Uri.parse(profil.getPhoto());
                        Picasso.get()
                                .load(st)
                                .into(img);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    onAddField(0);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }

            });

        }

        tabProfil.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clearView(listProfil);
                onAddField(tab.getPosition());

            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }



    public void onAddField(final int tab) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        List<String> affichageList = null;
        String type="";
        switch(tab) {
            case 0:
                affichageList = profil.getUserP();
                type = "parcours";
                break;
            case 1:
                affichageList = profil.getListIdsParcoursAjoutes();
                type = "parcours";
                break;
            case 2:
                affichageList = profil.getListeIdPointsAjoutes();
                type = "pointDInterets";
                break;
        }
        if(affichageList!=null) {
            for (final String id : affichageList) {
                View rowView = inflater.inflate(R.layout.list_profile, null);
                // Add the new row before the add field button.
                final TextView toto = rowView.findViewById(R.id.titleListProfil);
                final TextView tata = rowView.findViewById(R.id.descListProfil);
                final TextView titi = rowView.findViewById(R.id.typeListProfil);
                final ImageView tutu = rowView.findViewById(R.id.imageListProfil);
                mDatabase.child(type).child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(tab == 2) {
                            toto.setText(dataSnapshot.child("nom").getValue(String.class));
                            tutu.setImageDrawable(getResources().getDrawable(R.drawable.pi));
                        } else {
                            toto.setText(dataSnapshot.child("name").getValue(String.class));
                            titi.setText(dataSnapshot.child("type").getValue(String.class));
                            try {
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(dataSnapshot.child("imgPath").getValue(String.class));
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        new DownloadImageTask(tutu)
                                                .execute(uri.toString());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            } catch(Exception e){
                                Log.e("ImageAsyncTask","ImgPath equals Null.");
                            }
                        }
                        tata.setText(dataSnapshot.child("description").getValue(String.class));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                if(tab!=2){
                    rowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), InfosParcoursActivity.class);
                            intent.putExtra("id_parcours", id);
                            getApplicationContext().startActivity(intent);
                        }
                    });
                }
                listProfil.addView(rowView, listProfil.getChildCount() - 1);
            }
        }
    }

    public void clearView(LinearLayout v) {
        v.removeAllViews();
    }


}
