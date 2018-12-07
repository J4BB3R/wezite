package ca.wezite.wezite.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ca.wezite.wezite.AjoutParcoursActivity;
import ca.wezite.wezite.AjoutPointInteretActivity;
import ca.wezite.wezite.HomeActivity;
import ca.wezite.wezite.ParcoursListActivity;
import ca.wezite.wezite.ProfileActivity;
import ca.wezite.wezite.R;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class MenuService extends Service {
    public MenuService() {
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int id = intent.getIntExtra("id", 0);

        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(this, HomeActivity.class));
                break;
            case R.id.nav_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.nav_add_place:
                startActivity(new Intent(this, AjoutPointInteretActivity.class));
                break;
            case R.id.nav_parcours:
                startActivity(new Intent(this, ParcoursListActivity.class));
                break;
            case R.id.nav_add_parcours:
                startActivity(new Intent(this, AjoutParcoursActivity.class));
                break;
            case R.id.nav_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                break;
        }
        return flags;
    }
        @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
