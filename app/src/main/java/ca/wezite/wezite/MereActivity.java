package ca.wezite.wezite;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import ca.wezite.wezite.R;
import ca.wezite.wezite.model.User;
import ca.wezite.wezite.service.MenuService;
import ca.wezite.wezite.utils.WeziteBoot;

public class MereActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected WeziteBoot mWeziteboot;
    protected DrawerLayout mDrawer;
    protected ActionBarDrawerToggle mMenu;
    protected NavigationView nav;
    protected DatabaseReference mDatabase;
    protected FirebaseStorage mStorage;
    protected StorageReference storageReference;
    protected static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase =  FirebaseDatabase.getInstance().getReference();
        mWeziteboot = new WeziteBoot();
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();

        mDatabase.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                user = data.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        serviceIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Comes with android:launchMode="singleTask" and it prevent app to create new activities when navigate
        startService(serviceIntent);
        mDrawer.closeDrawers();
        return true;
    }
    
}

