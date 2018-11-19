package ca.wezite.wezite.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ca.wezite.wezite.HomeActivity;
import ca.wezite.wezite.ParcoursListActivity;
import ca.wezite.wezite.R;
import ca.wezite.wezite.service.MenuService;

public class CommonUtils {

    public static void gererAuthentification(WeziteBoot weziteBoot, View view, Activity activity){
        weziteBoot = new WeziteBoot();
        weziteBoot.checkFirebaseAuth(activity, view); // DO NOT FORGET PLZZZ
    }



}
