package ca.wezite.wezite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ca.wezite.wezite.utils.WeziteBoot;

public class ParcoursListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WeziteBoot mWeziteboot;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mMenu;
    private NavigationView nav;

    private LinearLayout mListParcour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours_list);

        mWeziteboot = new WeziteBoot();
        mWeziteboot.checkFirebaseAuth(this,mDrawer); // DO NOT FORGET PLZZZ

        SeekBar simpleSeek = (SeekBar)findViewById(R.id.distance);
        final TextView simpleSeekValue = (TextView) findViewById(R.id.distanceValueTxt);

        simpleSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress==0){
                    simpleSeekValue.setText("<1");
                } else {
                    simpleSeekValue.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // TODO Change with database array
        Spinner spin = (Spinner)findViewById(R.id.typeSpinner);
        String[] arraySpinner = new String[] {
                "Nature",
                "Culture",
                "Art",
                "Histoire",
                "Photographie",
                "Tous"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spin.setAdapter(adapter);


        mDrawer = findViewById(R.id.parcourList);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);

        nav.setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mWeziteboot.addDeconnectionListener();

        mListParcour = (LinearLayout) findViewById(R.id.listParcour);

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
        int id = menuItem.getItemId();

        switch (id) {
            case R.id.nav_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_parcours_list);
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
