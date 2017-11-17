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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Pattern;

public class userDetails extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser current = mAuth.getCurrentUser();
        Button submit = (Button)findViewById(R.id.update);
        final EditText name = (EditText)findViewById(R.id.name);
        final EditText email = (EditText)findViewById(R.id.email);
        final EditText mob = (EditText)findViewById(R.id.mobileNo);
        final TextView resend = (TextView)findViewById(R.id.resend);
        mob.setText("");
        email.setText("");
        name.setText("");
        if(current==null)
        {
            revert();
        }
        if(current.getPhoneNumber().isEmpty())
            revert();
        else
            mob.setText(current.getPhoneNumber());
        if(!current.getDisplayName().isEmpty())
        {
            TextView welcome = (TextView)findViewById(R.id.welcome);
            welcome.setText("Welcome back "+current.getDisplayName()+"!");
            name.setText(current.getDisplayName());
            submit.setText("Update");
        }
        if(!current.getEmail().isEmpty())
            email.setText(current.getEmail());
        final Button app = (Button)findViewById(R.id.back);
        app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.getDisplayName().isEmpty())
                    Toast.makeText(userDetails.this,"Please update your name first",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(userDetails.this,"Successfully Logged out!",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(userDetails.this,"Your profile has been updated!",Toast.LENGTH_SHORT).show();
                    if(Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches() && !current.getEmail().equals(email.getText()))
                    {
                        current.updateEmail(email.getText().toString());
                        if(!current.isEmailVerified())
                        {
                            current.sendEmailVerification();
                            Toast.makeText(userDetails.this, "A verification email has been sent", Toast.LENGTH_SHORT).show();
                            resend.setText("Verification Email sent");
                            resend.setTextColor(Color.GREEN);
                            resend.setClickable(false);
                        }
                    }
                    else
                        if(email.getText().length()>0 && email.getText().length()<=5)
                            Toast.makeText(userDetails.this,"Email Too short",Toast.LENGTH_SHORT).show();
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.isEmailVerified()) {
                    resend.setText("Email already verified");
                    resend.setTextColor(Color.DKGRAY);
                    resend.setClickable(false);
                }
                else {
                    if (current.getEmail().isEmpty()) {
                        resend.setText("Please enter an Email ID first.");
                        resend.setTextColor(Color.RED);
                        resend.setClickable(false);
                    } else {
                        current.sendEmailVerification();
                        resend.setText("Verification Email Sent");
                        resend.setClickable(false);
                    }
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
