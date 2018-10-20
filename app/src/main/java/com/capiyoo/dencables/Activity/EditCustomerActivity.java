package com.capiyoo.dencables.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.Persistance.SharedPref;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.DenDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class EditCustomerActivity extends AppCompatActivity {

    private static String KEY;
    private static Boolean FROM_CUSTOMER_ACTVITY;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private SharedPref sharedPref;
    private DenDetails customerDetails;
    private Button update;
    private EditText id;
    private EditText activation_date;
    private EditText areaDescription;
    private EditText mBalance;
    private EditText mBoxStatus;
    private EditText mCCode;
    private EditText mCustomerAddress;
    private EditText mCustomerName;
    private EditText mMonthlyCharge;
    private EditText mPackageName;
    private EditText mRecAmount;
    private EditText mRecDate;
    private EditText mRecNo;
    private EditText mSetupBoxNumber;
    private EditText mVCNo;
    private EditText mMobileNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);
        id = findViewById(R.id.id);
        update = findViewById(R.id.update);
        activation_date = findViewById(R.id.activation_date);
        areaDescription = findViewById(R.id.areaDescription);
        mBalance = findViewById(R.id.mBalance);
        mBoxStatus = findViewById(R.id.mBoxStatus);
        mCCode = findViewById(R.id.mCCode);
        mCustomerAddress = findViewById(R.id.mCustomerAddress);
        mCustomerName = findViewById(R.id.mCustomerName);
        mMonthlyCharge = findViewById(R.id.mMonthlyCharge);
        mPackageName = findViewById(R.id.mPackageName);
        mRecAmount = findViewById(R.id.mRecAmount);
        mRecDate = findViewById(R.id.mRecDate);
        mRecNo = findViewById(R.id.mRecNo);
        mSetupBoxNumber = findViewById(R.id.mSetupBoxNumber);
        mVCNo = findViewById(R.id.mVCNo);
        mMobileNumber = findViewById(R.id.mMobileNumber);
        customerDetails = new DenDetails();
        sharedPref = new SharedPref(EditCustomerActivity.this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(sharedPref.getFirebaseUid());
        Intent intent = getIntent();
        if (intent != null) {
            KEY = intent.getStringExtra("CustomerKey");
            FROM_CUSTOMER_ACTVITY = intent.getBooleanExtra("FromCustomerProfileActivity", false);

            if (FROM_CUSTOMER_ACTVITY) {

                updateCustomerData();
                //  Toast.makeText(getApplicationContext(), KEY, Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(getApplicationContext(), "There is some problem", Toast.LENGTH_LONG).show();
        }

        databaseReference.keepSynced(true);
    }

    void updateCustomerData() {

        databaseReference.child(KEY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    customerDetails = dataSnapshot.getValue(DenDetails.class);
                    try {

                        id.setText(customerDetails.getId());
                        activation_date.setText(customerDetails.getmActivatinDate());
                        areaDescription.setText(customerDetails.getmAreaDescription());
                        mBalance.setText(customerDetails.getmBalance());
                        mBoxStatus.setText(customerDetails.getmBoxStatus());
                        mCCode.setText(customerDetails.getmCCode());
                        mCustomerAddress.setText(customerDetails.getmCustomerAddress());
                        mCustomerName.setText(customerDetails.getmCustomerName());
                        mMonthlyCharge.setText(customerDetails.getmMonthlyCharge());
                        mPackageName.setText(customerDetails.getmPackageName());
                        mRecAmount.setText(customerDetails.getmRecAmount());
                        mRecDate.setText(customerDetails.getmRecDate());
                        mRecNo.setText(customerDetails.getmRecNo());
                        mSetupBoxNumber.setText(customerDetails.getmSetupBoxNumber());
                        mVCNo.setText(customerDetails.getmVCNo());
                        mMobileNumber.setText(customerDetails.getmMobileNumber());

                        update.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Map<String, String> map = new HashMap<>();
                                map.put("id", customerDetails.getId());
                                map.put("activation_date", customerDetails.getmActivatinDate());
                                map.put("areaDescription", customerDetails.getmAreaDescription());
                                map.put("mBalance", customerDetails.getmBalance());
                                map.put("mBoxStatus", customerDetails.getmBoxStatus());
                                map.put("mCCode", customerDetails.getmCCode());
                                map.put("mCustomerAddress", customerDetails.getmCustomerAddress());
                                map.put("mCustomerName", mCustomerName.getText().toString());
                                map.put("mMonthlyCharge", customerDetails.getmMonthlyCharge());
                                map.put("mPackageName", customerDetails.getmPackageName());
                                map.put("mRecAmount", customerDetails.getmRecAmount());
                                map.put("mRecDate", customerDetails.getmRecDate());
                                map.put("mRecNo", customerDetails.getmRecNo());
                                map.put("mSetupBoxNumber", customerDetails.getmSetupBoxNumber());
                                map.put("mVCNo", customerDetails.getmVCNo());
                                map.put("mMobileNumber", mMobileNumber.getText().toString());

                                databaseReference.child(KEY).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
