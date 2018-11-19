package ca.wezite.wezite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import ca.wezite.wezite.R;
import ca.wezite.wezite.service.MenuService;
import ca.wezite.wezite.utils.WeziteBoot;

public class MereActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected WeziteBoot mWeziteboot;
    protected DrawerLayout mDrawer;
    protected ActionBarDrawerToggle mMenu;
    protected NavigationView nav;
    protected DatabaseReference mDatabase;
    protected FirebaseStorage mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mWeziteboot = new WeziteBoot();
        mStorage = FirebaseStorage.getInstance();


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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent serviceIntent = new Intent(this, MenuService.class);
        serviceIntent.putExtra("id", menuItem.getItemId());
        startService(serviceIntent);
        return true;
    }
    
}

