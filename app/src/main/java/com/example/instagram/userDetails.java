package com.example.instagram;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.PatternMatcher;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import static android.widget.Toast.LENGTH_SHORT;

public class userDetails extends Activity
{
    String themeFile = "Theme_Prefs";
    boolean dark=true;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser current = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try {
            FileInputStream fis = openFileInput(themeFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String str = br.readLine();
            dark = Boolean.valueOf(str);
            fis.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        if(!dark)
            setTheme(R.style.Dialog_Light);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final Button submit = (Button)findViewById(R.id.update);
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

        Button logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logout = new Intent(userDetails.this,Login.class);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(userDetails.this,"Successfully Logged out!", LENGTH_SHORT).show();
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
                    Toast.makeText(userDetails.this,"Your profile has been updated!", LENGTH_SHORT).show();
                    if(Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches() && !current.getEmail().equals(email.getText()))
                    {
                        current.updateEmail(email.getText().toString());
                        if(!current.isEmailVerified())
                        {
                            current.sendEmailVerification();
                            Toast.makeText(userDetails.this, "A verification email has been sent", LENGTH_SHORT).show();
                            resend.setText("Verification Email sent");
                            resend.setTextColor(Color.GREEN);
                            resend.setClickable(false);
                        }
                    }
                    else
                        if(email.getText().length()>0 && email.getText().length()<=5)
                            Toast.makeText(userDetails.this,"Email Too short", LENGTH_SHORT).show();
                }
            }
        });
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current.isEmailVerified()) {
                    resend.setText("Email already verified");
                    resend.setTextColor(Color.CYAN);
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
        final Switch themeSel = (Switch)findViewById(R.id.theme);
        if(!dark)
        {
            themeSel.setChecked(true);
            themeSel.setText("Light Mode\t");
        }
        themeSel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dark=!isChecked;
                Resources.Theme theme = userDetails.super.getTheme();
                if(dark)
                {
                    themeSel.setText("Dark Mode\t");
                    setTheme(R.style.Dialog);
                }
                else
                {
                    themeSel.setText("Light Mode\t");
                    setTheme(R.style.Dialog_Light);
                }
                String string = Boolean.toString(dark);
                try {
                    FileOutputStream fos = openFileOutput(themeFile, Context.MODE_PRIVATE);
                    fos.write(string.getBytes());
                    fos.close();
                } catch (java.io.IOException e) {
                    Toast.makeText(userDetails.this,"There was some error saving your preferences. Theme will revert back when app is closed",LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                recreate();
            }
        });

    }
    private void revert()
    {
        Toast.makeText(this,"Something went wrong!",Toast.LENGTH_LONG).show();
        Intent back = new Intent(this,Login.class);
        startActivity(back);
    }

    @Override
    public void onBackPressed() {
        if(current.getDisplayName().isEmpty())
            Toast.makeText(userDetails.this,"Please update your name first", LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }
}
