package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.security.AuthProvider;
import java.util.concurrent.TimeUnit;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.instagram.R.id.mobile;
import static com.example.instagram.R.id.otp;
import static com.example.instagram.R.id.otp_submit;

public class Login extends AppCompatActivity {
    private String verificationID;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private static final String TAG = "PhoneAuthActivity";
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
    Button submit = (Button)findViewById(R.id.signIn);
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText mobile = (EditText)findViewById(R.id.mobile);
                String mobNo = mobile.getText().toString();
                startVerify(mobNo);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG,"onVerificationCompleted:" + phoneAuthCredential);
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG,"onVerificationFailed",e);
                if(e instanceof FirebaseAuthInvalidCredentialsException){
                    Log.w(TAG,"called invalid");
                    invalid();
                }
                else if(e instanceof FirebaseTooManyRequestsException){
                    sorry();
                }
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(TAG,"onCodeSent:"+ s);
                verificationID = s;
                resendToken = forceResendingToken;
                setContentView(R.layout.login_otp);
                Button submit = (Button)findViewById(R.id.otp_submit);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String code = ((EditText)findViewById(R.id.otp)).getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(s, code);
                        signIn(credential);
                    }
                });

            }
        };
    }
    void startVerify(String mobile)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(

                mobile,        // Phone number to verify

                60,                 // Timeout duration

                TimeUnit.SECONDS,   // Unit of timeout

                this,               // Activity (for callback binding)

                mCallbacks);        // OnVerificationStateChangedCallbacks
    }
    private void signIn(PhoneAuthCredential cred){
        mAuth.signInWithCredential(cred)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Login.this,"Mobile Number verified. Signing in...", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Login.this,MainActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            //sign in failed
                            Log.w(TAG,"signInWithCredential:failure",task.getException());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                //invalid code
                                TextView otpm = (TextView)findViewById(R.id.otp_message);
                                otpm.setText("Invalid OTP. Try again");
                                otpm.setTextColor(Color.RED);
                                Button otps = (Button)findViewById(otp_submit);
                                otps.setText("Try Again");
                                EditText otp = (EditText)findViewById(R.id.otp);
                                otp.setText("");
                                otp.setFocusable(true);
                            }
                            else
                                sorry();
                        }
                }
                });
    }
    private void invalid()
    {
        Toast.makeText(Login.this,"Invalid Credentials",Toast.LENGTH_LONG).show();
    }
    private void sorry()
    {
        Button sign = (Button)findViewById(R.id.signIn);
        Toast.makeText(Login.this,"We are facing issues at our server. Try again Later or contact the developer.",Toast.LENGTH_LONG).show();
        sign.setText("Try Again");
    }
    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
