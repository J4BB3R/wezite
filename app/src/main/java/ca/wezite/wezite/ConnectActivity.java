package ca.wezite.wezite;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.wezite.wezite.model.User;

public class ConnectActivity extends Activity {

    RelativeLayout bar;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        findViewById(R.id.googleBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        bar = (RelativeLayout) findViewById(R.id.loadingBar);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    bar.setVisibility(View.GONE);

                    final DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReference();
                    FirebaseUser cuser = firebaseAuth.getCurrentUser();
                    final User user = new User();
                    user.setId(cuser.getUid());
                    user.setName(cuser.getDisplayName());
                    user.setEmail(cuser.getEmail());
                    user.setPhoto(cuser.getPhotoUrl().toString());
                    user.setNotif(true);
                    user.setName(firebaseAuth.getCurrentUser().getDisplayName());
                    user.setPhoto(""+ firebaseAuth.getCurrentUser().getPhotoUrl());
                    user.setUserP(null);
                    mDatabase.addListenerForSingleValueEvent( new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (!snapshot.hasChild("users/" + user.getId())) {
                                mDatabase.child("users").child(user.getId()).setValue(user);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    startActivity(new Intent(ConnectActivity.this, HomeActivity.class));
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void signIn() {
        bar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Just some UI porn
                bar.setVisibility(View.GONE);
                Snackbar snack = Snackbar.make(findViewById(R.id.connect_frame), "Google Sign-In Failed.", Snackbar.LENGTH_SHORT);
                snack.getView().setBackgroundColor(getResources().getColor(R.color.errorColor));
                snack.setActionTextColor(getResources().getColor(R.color.textLight));
                snack.show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("OAUTH2", "signInWithCredential:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("OAUTH2", "signInWithCredential:failure", task.getException());
                            // Just some UI porn
                            bar.setVisibility(View.GONE);
                            Snackbar snack = Snackbar.make(findViewById(R.id.connect_frame), "Authentication Failed.", Snackbar.LENGTH_SHORT);
                            snack.getView().setBackgroundColor(getResources().getColor(R.color.errorColor));
                            snack.setActionTextColor(getResources().getColor(R.color.textLight));
                            snack.show();
                        }
                    }
                });
    }
}