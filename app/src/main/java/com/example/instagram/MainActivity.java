package com.example.instagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enabling Media Capabilities
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getDisplayName().isEmpty())
        {
            Intent intent = new Intent(this,userDetails.class);
            startActivity(intent);
        }
        else
            Toast.makeText(this,"Welcome back, "+user.getDisplayName().toString(),Toast.LENGTH_SHORT).show();
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

                Intent camera = new Intent("android.media.action.IMAGE_CAPTURE");
                startActivity(camera);
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
}
