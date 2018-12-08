package ca.wezite.wezite;

import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.beans.PropertyChangeListener;

public class ParamsActivity extends MereActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Thread userHook;
    private boolean notifStatus=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);

        mDrawer = findViewById(R.id.params);
        mWeziteboot.checkFirebaseAuth(this,mDrawer);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Switch notifSwitch = (Switch)findViewById(R.id.switchNotif);

        userHook = new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        synchronized (userMutex){
                            try {
                                userMutex.wait();
                                notifStatus=user.isNotif();
                                notifSwitch.setChecked(notifStatus);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        userHook.start();

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(notifStatus != notifSwitch.isChecked()){
                    mDatabase.child("users").child(user.getId()).child("notif").setValue(notifSwitch.isChecked());
                }
            }
        });
    }
}
