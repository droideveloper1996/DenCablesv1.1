package com.capiyoo.dencables;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.sql.Timestamp;

import static com.capiyoo.dencables.Constants.IS_ACCOUNT_ACTIVATED;

public class SignupActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText company_name;
    EditText person_name;
    EditText address1;
    EditText addline2;
    EditText city;
    EditText gstnumber;
    EditText state;
    EditText password;
    EditText mobile;
    EditText email;

    LinearLayout getStarted;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.USER_REGISTRATION);
        progressDialog = new ProgressDialog(this);
        company_name = findViewById(R.id.company_name);
        person_name = findViewById(R.id.person_name);
        address1 = findViewById(R.id.address1);
        addline2 = findViewById(R.id.addline2);
        city = findViewById(R.id.city);
        gstnumber = findViewById(R.id.gstnumber);
        state = findViewById(R.id.state);
        password = findViewById(R.id.password);
        mobile = findViewById(R.id.mobile);
        getStarted = findViewById(R.id.getStarted);
        email = findViewById(R.id.email);
        progressDialog.setMessage("Please wait...");
        progressDialog.setTitle("Registring");
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerOperator();
            }
        });


    }

    public void registerOperator() {

        final String mCompanyName = company_name.getText().toString();
        final String mPersonName = person_name.getText().toString();
        final String mAddress1 = address1.getText().toString();
        final String mAddress2 = addline2.getText().toString();
        final String mCity = city.getText().toString();
        final String mGstNumber = gstnumber.getText().toString();
        final String mState = state.getText().toString();
        final String mPassword = password.getText().toString();
        final String mMobile = mobile.getText().toString();
        final String mEmail = email.getText().toString();


        if (TextUtils.isEmpty(mAddress1) ||
                TextUtils.isEmpty(mCity) ||
                TextUtils.isEmpty(mCompanyName)
                || TextUtils.isEmpty(mPersonName)
                || TextUtils.isEmpty(mAddress2)
                || TextUtils.isEmpty(mGstNumber)
                || TextUtils.isEmpty(mState)
                || TextUtils.isEmpty(mPassword)
                || TextUtils.isEmpty(mMobile)
                || TextUtils.isEmpty(mEmail)) {

            Toast.makeText(getApplicationContext(), "Fields are missing", Toast.LENGTH_SHORT).show();
            return;
        } else {

            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    int time = (int) (System.currentTimeMillis());
                    Timestamp tsTemp = new Timestamp(time);
                    String ts = tsTemp.toString();

                    final OperatorProfile operatorProfile = new OperatorProfile();
                    operatorProfile.setmAddress1(mAddress1);
                    operatorProfile.setmAddress2(mAddress2);
                    operatorProfile.setmCity(mCity);
                    operatorProfile.setmGstNumber(mGstNumber);
                    operatorProfile.setmPersonName(mPersonName);
                    operatorProfile.setmState(mState);
                    operatorProfile.setmCompanyName(mCompanyName);
                    operatorProfile.setmEmail(mEmail);
                    operatorProfile.setmMobile(mMobile);
                    operatorProfile.setmPassword(mPassword);
                    operatorProfile.setmIsActivated(false);
                    operatorProfile.setmActivationDate(ts);
                    firebaseDatabase.child(firebaseAuth.getCurrentUser().getUid()).setValue(operatorProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SharedPref sharedPref = new SharedPref(SignupActivity.this);
                                if (firebaseAuth.getCurrentUser() != null) {
                                    String uid = firebaseAuth.getCurrentUser().getUid();
                                    sharedPref.putMyUid(uid);

                                    sharedPref.setFirmName(operatorProfile.getmCompanyName());
                                    sharedPref.setFirmContact(operatorProfile.getmMobile());
                                    sharedPref.setFirmAuthority(operatorProfile.getmPersonName());
                                    sharedPref.putFirmAddress1(operatorProfile.getmAddress1());
                                    sharedPref.putFirmAddress2(operatorProfile.getmAddress2());
                                    sharedPref.setState(operatorProfile.getmState());
                                    sharedPref.setCity(operatorProfile.getmState());

                                } else {
                                    sharedPref.putMyUid("null");
                                }
                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.putExtra(Constants.FIRST_TIME_REGISTRATION, true);
                                intent.putExtra(Constants.IS_ACCOUNT_ACTIVATED, false);
                                startActivity(intent);
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        //

    }
}
