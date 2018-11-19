package ca.wezite.wezite;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


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

        TextView nom = findViewById(R.id.nomProfil);
        TextView mail = findViewById(R.id.mailProfil);
        ImageView img = findViewById(R.id.photoProfile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        nom.setText(user.getDisplayName());
        mail.setText(user.getEmail());
        Uri st = user.getPhotoUrl();
        Picasso.get()
                .load(st)
                .into(img);
    }


}
