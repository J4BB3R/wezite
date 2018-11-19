package ca.wezite.wezite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.wezite.wezite.async.DownloadImageTask;
import ca.wezite.wezite.utils.WeziteBoot;

public class InfosLieuActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView descTextView;
    TextView titreTextView;
    TextView createurTextView;
    TextView nbVuesTextView;
    TextView dateTextView;

    private TableLayout infosGeneralesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_lieu);
        infosGeneralesLayout = findViewById(R.id.infos_generales_container);
        infosGeneralesLayout.setVisibility(View.GONE);
        mDrawer = findViewById(R.id.activity_demarrage);

        mWeziteboot.checkFirebaseAuth(this,mDrawer); // DO NOT FORGET PLZZZ

        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);

        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String description = intent.getStringExtra("description");
        descTextView = (TextView) findViewById(R.id.description_content);
        descTextView.setText(description);
        titreTextView = (TextView) findViewById(R.id.title);
        titreTextView.setText(intent.getStringExtra("titre"));
        createurTextView = findViewById(R.id.auteur_text);
        createurTextView.setText(intent.getStringExtra("auteur"));
        dateTextView = findViewById(R.id.date_creation_text);
        dateTextView.setText(intent.getStringExtra("dateCreation"));
        nbVuesTextView = findViewById(R.id.nb_vues_text);
        nbVuesTextView.setText(intent.getStringExtra("nbVues"));


        String photo = intent.getStringExtra("photo");
        if(photo!=null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images").child(photo);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    new DownloadImageTask((ImageView) findViewById(R.id.first_image))
                            .execute(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });


        }


    }

    public void afficherCacherDescription(View view) {
        if(infosGeneralesLayout.getVisibility()==View.GONE){
            infosGeneralesLayout.setVisibility(View.VISIBLE);
        } else{
            infosGeneralesLayout.setVisibility(View.GONE);
        }

    }


}
