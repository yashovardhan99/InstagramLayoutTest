package com.example.instagram;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.PatternMatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class userDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser current = mAuth.getCurrentUser();
        if(current==null)
        {
            revert();
        }
        final EditText mob = (EditText)findViewById(R.id.mobileNo);
        if(current.getPhoneNumber().isEmpty())
            revert();
        else
            mob.setText(current.getPhoneNumber());
        Button submit = (Button)findViewById(R.id.update);
        final EditText name = (EditText)findViewById(R.id.name);
        final EditText email = (EditText)findViewById(R.id.email);
        if(!current.getDisplayName().isEmpty())
            name.setText(current.getDisplayName());
        if(!current.getEmail().isEmpty())
            email.setText(current.getEmail());
        Button app = (Button)findViewById(R.id.back);
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.getDisplayName().isEmpty())
                    Toast.makeText(userDetails.this,"Please provide your name first",Toast.LENGTH_SHORT).show();
                else
                {
                    Intent intent = new Intent(userDetails.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(userDetails.this,Login.class);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(userDetails.this,"Successfully Logged out!",Toast.LENGTH_SHORT);
                startActivity(logout);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().length()<2)
                {
                    name.setHint("Name should be atleast 2 characters long");
                    Toast.makeText(userDetails.this,"Name Too short",Toast.LENGTH_LONG).show();
                    name.setHintTextColor(Color.RED);
                    name.setFocusable(true);
                }
                else
                {
                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name.getText().toString())
                            .build();
                    current.updateProfile(update);
                    if(email.getText().length()>5)
                    {
                        current.updateEmail(email.getText().toString());
                        if(!current.isEmailVerified())
                        {
                            current.sendEmailVerification();
                            Toast.makeText(userDetails.this, "A verification email has been sent", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                        if(email.getText().length()>0)
                            Toast.makeText(userDetails.this,"Email Too short",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void revert()
    {
        Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
        Intent back = new Intent(this,Login.class);
        startActivity(back);
    }
}
