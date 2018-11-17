package ca.wezite.wezite;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class parcoursListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mMenu;
    private NavigationView nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcours_list);

        mDrawer = findViewById(R.id.list);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);
        nav = findViewById(R.id.nav_view);

            nav.setNavigationItemSelectedListener(this);
            mDrawer.addDrawerListener(mMenu);
            mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
