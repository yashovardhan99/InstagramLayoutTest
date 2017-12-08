package com.example.instagram;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainActivity extends Activity {

    public static final String PREFS_NAME = "MyPreferences";
    public static final String PREFS_THEME = "Theme";

    static boolean flag = false;
    static final int IMAGE_REQ = 1;
    static boolean bp = false;//to store back presses
    String photoPath;
    boolean dark=true;//default as dark theme

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent login = new Intent(this,Login.class);
            startActivity(login);
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        if(prefs.contains(PREFS_THEME))
            dark = prefs.getBoolean(PREFS_THEME,true);
        if(!dark)
            setTheme(R.style.AppTheme_Light);
        else
            setTheme(R.style.AppTheme);
        //Sets theme according to user's preferences
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null)
            flag=savedInstanceState.getBoolean("FLAG");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        requestVisibleBehind(true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//gets current logged in user
        if(user.getDisplayName().isEmpty())//if user has not yet set a name
        {
            Intent intent = new Intent(this,userDetails.class);
            startActivity(intent);//calls user details for the user to set their details
        }
        else if(!flag)//if user name is set
        {
            Toast.makeText(this, "Welcome back, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();//welcomes back the user
            flag=true;//to avoid the toast from re-appearing
        }
        Button profile = (Button)findViewById(R.id.profile);//profile button
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,userDetails.class);
                startActivity(intent);//opens User Details activity
            }
        });

        // Button Name : Image Button camera
        ImageButton cameraButton = (ImageButton) findViewById(R.id.camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int storePerm = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);//checks for permission
                if(storePerm!= PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(camera.resolveActivity(getPackageManager())!=null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();//tries creating image file
                    } catch (IOException ex) {
                        Toast.makeText(MainActivity.this, "An error Occured!", Toast.LENGTH_LONG).show();
                        Log.w("CAMERA",ex);
                    }
                    if (photoFile != null)
                    {
                        Uri photoURI;
                        try {
                            photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.example.android.fileprovider", photoFile);//creates URI
                            camera.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);//adds storage location as the newly created file
                            startActivityForResult(camera, IMAGE_REQ);//starts camera
                        }catch (Exception e){
                            Log.w("URI",e);
                        }
                    }
                }
            }
        });
        // Enabling Messaging Capabilities
        ImageButton msgButton = (ImageButton) findViewById(R.id.message);
        msgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(Intent.ACTION_VIEW);
                message.setData(Uri.parse("sms:"));
                startActivity(message);
            }
        });



        SharedPreferences prefs = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
         SharedPreferences.OnSharedPreferenceChangeListener listener =  new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
            {
                if(key.equals(PREFS_THEME))
                {
                    recreate();
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(listener);

    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("FLAG",flag);
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==IMAGE_REQ && resultCode==RESULT_OK)//image taken successfully
        {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);//scans for image
            File f = new File(photoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            setPic(contentUri);//sets image just taken into the view
        }
        else if(requestCode==IMAGE_REQ && resultCode==RESULT_CANCELED)//if failed
        {
            File f = new File(photoPath);
            if(!f.delete())
                Toast.makeText(this,"An empty image file was created which couldn't be deleted. You may want to delete the file from your pictures directory",Toast.LENGTH_SHORT).show();//deletes the empty file
        }
    }

    private File createImageFile() throws IOException
    {//creates image file using date and time stamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_"+timeStamp+"_";
        File storageDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg",storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    public void setPic(Uri uri)
    {//sets image to the view
        LinearLayout lv = (LinearLayout) findViewById(R.id.mainArea);
        ImageView iv = new ImageView(lv.getContext());
        iv.setImageURI(uri);
        iv.setAdjustViewBounds(true);
        iv.setMaxWidth(lv.getWidth());
        float scale = getResources().getDisplayMetrics().density;
        int dp5 = (int) (scale*5 + 0.5f);
        iv.setPadding(dp5,dp5,dp5,dp5);
        lv.addView(iv);//to add image view
    }

    @Override
    public void onBackPressed() {
        if(bp)
            super.onBackPressed();
        else

            Toast.makeText(this,"Press back once more to exit",Toast.LENGTH_SHORT).show();
        bp=true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bp=false;
            }
        },5000);
    }
}
