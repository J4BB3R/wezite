package ca.wezite.wezite;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.wezite.wezite.model.PointDinteret;

public class AjoutPointInteretActivity extends MereMapsActivity implements NavigationView.OnNavigationItemSelectedListener {


    private PopupWindow mPopupWindow;
    private View customView;
    private Bitmap selectedImage;
    private String mCurrentPhotoPath;
    private Context ctx;
    private Uri photoURI;

    int MAX_IMAGE_SIZE = 1000 * 1024;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setLayout(R.layout.activity_visite);
        super.onCreate(savedInstanceState);
        ctx=this;
        mDrawer = findViewById(R.id.home);
        mMenu = new ActionBarDrawerToggle(this, mDrawer, R.string.app_name, R.string.app_name);

        ((NavigationView)findViewById(R.id.nav_view)).setNavigationItemSelectedListener(this);
        mDrawer.addDrawerListener(mMenu);
        mMenu.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
       super.onMapReady(googleMap);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(final LatLng latLng) {
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                customView = inflater.inflate(R.layout.popup_creer_point_interet,null);

                mPopupWindow = new PopupWindow(
                        customView,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT
                );
                mPopupWindow.setFocusable(true);
                mPopupWindow.showAtLocation(mDrawer, Gravity.CENTER_HORIZONTAL,0,0);
                customView.findViewById(R.id.lieu_image).setVisibility(View.GONE);

                customView.findViewById(R.id.fermer ).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopupWindow.dismiss();
                        selectedImage=null;
                    }
                });
                customView.findViewById(R.id.input_ajouter_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);
                    }
                });
                customView.findViewById(R.id.input_prendre_photo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                photoURI = FileProvider.getUriForFile(ctx,
                                        "ca.wezite.wezite.utils",
                                        photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, 0);
                            }
                        }

                    }
                });
                ((Button) customView.findViewById(R.id.ajouter)).setEnabled(false);
                customView.findViewById(R.id.ajouter).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customView.invalidate();
                        String titre = ((EditText) customView.findViewById (R.id.text_nom_lieu)).getText().toString();
                        String description = ((EditText) customView.findViewById (R.id.text_description_lieu)).getText().toString();
                        if(titre.isEmpty()){
                            ((EditText) customView.findViewById (R.id.text_nom_lieu)).setError("Valeur obligatoire");
                        }
                        if(description.isEmpty()){
                            ((EditText) customView.findViewById (R.id.text_description_lieu)).setError("Valeur obligatoire");
                        }

                        if(!titre.isEmpty()&&!description.isEmpty()){
                            ((Button) customView.findViewById(R.id.ajouter)).setEnabled(false);
                            final PointDinteret p = new PointDinteret();
                            p.setDateCréation(new Date());
                            p.setNom(titre);
                            p.setDescription(description);
                            final String key = pointsDInteretsCloudEndPoint.push().getKey();
                            p.setId(key);
                            p.setxCoord(String.valueOf(latLng.latitude));
                            p.setyCoord(String.valueOf(latLng.longitude));
                            p.setNbVues(1);
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            p.setAuteur(user.getDisplayName());
                            p.setUserId(user.getUid());
                            final String imgPath = p.getUserId() + "-" +new Date();
                            StorageReference imgRef = storageReference.child("images/"+ imgPath);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                            UploadTask uploadTask = imgRef.putBytes(baos.toByteArray());
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // TODO : handle onFailure
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    p.setImgPath(imgPath);
                                    pointsDInteretsCloudEndPoint.child(key).setValue(p);
                                    MereActivity.user.getListeIdPointsAjoutes().add(p.getId());
                                    mDatabase.child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(MereActivity.user);
                                    selectedImage=null;

                                    mPopupWindow.dismiss();
                                    isListening=false;
                                    pointAPromite=p;

                                    afficherPlus(null);
                                }
                            });

                        }
                    }
                });
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){

                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ((ImageView) customView.findViewById(R.id.lieu_image)).setImageBitmap(selectedImage);
                    customView.findViewById(R.id.lieu_image).setVisibility(View.VISIBLE);
                    ((Button) customView.findViewById(R.id.ajouter)).setEnabled(true);

                }

                break;
            case 1:
                if(resultCode == RESULT_OK){

                    try {
                        selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageReturnedIntent.getData());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ((ImageView) customView.findViewById(R.id.lieu_image)).setImageBitmap(selectedImage);
                    customView.findViewById(R.id.lieu_image).setVisibility(View.VISIBLE);
                    ((Button) customView.findViewById(R.id.ajouter)).setEnabled(true);


                }
                break;
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

}

