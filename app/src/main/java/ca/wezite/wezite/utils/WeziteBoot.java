package ca.wezite.wezite.utils;


import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import ca.wezite.wezite.ConnectActivity;
import ca.wezite.wezite.R;

public class WeziteBoot {

    public FirebaseAuth mAuth;
    public FirebaseAuth.AuthStateListener mAuthListener;

    private static boolean conn=false;

    public WeziteBoot(){
        mAuth = FirebaseAuth.getInstance();
    }

    public void checkFirebaseAuth(final Activity context, final View view){ // In onCreate()
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    conn=false;
                    context.startActivity(new Intent(context,ConnectActivity.class));
                } else {
                    if(!conn){
                        // Just some UI porn
                        conn=true;
                        Snackbar snack = Snackbar.make(view, "Authentication Succeed.", Snackbar.LENGTH_SHORT);
                        snack.getView().setBackgroundColor(context.getResources().getColor(R.color.successColor));
                        snack.setActionTextColor(context.getResources().getColor(R.color.textLight));
                        snack.show();
                    }
                }
            }
        };
    }

    public void addDeconnectionListener(){ // In onStart()
        mAuth.addAuthStateListener(mAuthListener);
    }
}
