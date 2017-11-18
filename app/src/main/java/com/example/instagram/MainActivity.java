package com.example.instagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    static boolean flag = false;
    static final int IMAGE_REQ = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null)
            flag=savedInstanceState.getBoolean("FLAG");
    }

    @Override
    protected void onResume() {
        super.onResume();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getDisplayName().isEmpty())
        {
            Intent intent = new Intent(this,userDetails.class);
            startActivity(intent);
        }
        else if(!flag)
        {
            Toast.makeText(this, "Welcome back, " + user.getDisplayName().toString(), Toast.LENGTH_SHORT).show();
            flag=true;
        }
        Button profile = (Button)findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,userDetails.class);
                startActivity(intent);
            }
        });

        // Button Name : Image Button camera
        ImageButton cameraButton = (ImageButton) findViewById(R.id.camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(camera.resolveActivity(getPackageManager())!=null)
                startActivityForResult(camera,IMAGE_REQ);
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

    }
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("FLAG",flag);
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(requestCode==IMAGE_REQ && resultCode==RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap)extras.get("data");
            ((ImageView)findViewById(R.id.image)).setImageBitmap(imageBitmap);
        }
    }
}
