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

public class InfosLieuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView descTextView;
    TextView titreTextView;
    TextView createurTextView;
    TextView nbVuesTextView;
    TextView dateTextView;


    private WeziteBoot mWeziteboot;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mMenu;
    private NavigationView nav;
    private TableLayout infosGeneralesLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infos_lieu);
        infosGeneralesLayout = findViewById(R.id.infos_generales_container);
        infosGeneralesLayout.setVisibility(View.GONE);
        mDrawer = findViewById(R.id.activity_demarrage);

        mWeziteboot = new WeziteBoot();
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

        FirebaseStorage storage = FirebaseStorage.getInstance();


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

    @Override
    protected void onStart() {
        super.onStart();
        mWeziteboot.addDeconnectionListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        if(mMenu.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void afficherCacherDescription(View view) {
        if(descTextView.getVisibility()==View.VISIBLE){
            descTextView.setVisibility(View.GONE);
            infosGeneralesLayout.setVisibility(View.VISIBLE);
        } else{
            descTextView.setVisibility(View.VISIBLE);
            infosGeneralesLayout.setVisibility(View.GONE);
        }

    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
    // TODO : mutualiser
        switch (id) {
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_add_place:
                Toast.makeText(this, "Add Place", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_parcours:
                Toast.makeText(this, "Parcours", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Deconnexion", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
