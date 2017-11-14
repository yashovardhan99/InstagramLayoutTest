package com.example.instagram;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import static com.example.instagram.R.id.mobile;

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
        Button submit = (Button)findViewById(R.id.signIn);

        mAuth = FirebaseAuth.getInstance();
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

            /*@Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(TAG,"onCodeSent:"+ s);
                verificationID = s;
                resendToken = forceResendingToken;

            }*/
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
        Intent intent = new Intent(this,MainActivity.class);
        Toast.makeText(Login.this,"Signing In",Toast.LENGTH_SHORT).show();
        startActivity(intent);
    };
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
}
