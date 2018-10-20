package com.capiyoo.dencables.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.Persistance.SharedPref;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.DenDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewCustomerActivity extends AppCompatActivity {


    EditText customerFirstName;
    EditText customerLastName;
    EditText Address1;
    EditText Address2;
    EditText pincode;
    EditText boxNumber;
    EditText vcNumber;
    EditText date;
    EditText mobile;
    Button submitBtn;

    DatabaseReference customerDatabaseRefrence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_customer);
        View view = findViewById(R.id.toolbar1);
        Toolbar toolbar1 = view.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar1);
        getSupportActionBar().setTitle("Customer Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        customerFirstName = findViewById(R.id.customerFirstName);
        customerLastName = findViewById(R.id.customerLastName);
        Address1 = findViewById(R.id.Address1);
        Address2 = findViewById(R.id.Address2);
        pincode = findViewById(R.id.pincode);
        boxNumber = findViewById(R.id.boxNumber);
        vcNumber = findViewById(R.id.vcNumber);
        date = findViewById(R.id.date);
        mobile = findViewById(R.id.mobile);
        submitBtn = findViewById(R.id.submitBtn);
        SharedPref sharedPref = new SharedPref(this);
        String uid = sharedPref.getFirebaseUid();
        if (uid != null) {
            customerDatabaseRefrence = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(uid);
            customerDatabaseRefrence.keepSynced(true);
        }
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCustomer();
            }
        });
    }


    void registerCustomer() {

        String cFirstName = customerFirstName.getText().toString();
        String cLastName = customerLastName.getText().toString();
        String cMobile = mobile.getText().toString();
        String cAddress1 = Address1.getText().toString();
        String cAddress2 = Address2.getText().toString();
        String cPincode = pincode.getText().toString();
        String cBoxNumber = boxNumber.getText().toString();
        String cVCNumber = vcNumber.getText().toString();
        String cDate = date.getText().toString();


        if (TextUtils.isEmpty(cFirstName) ||
                TextUtils.isEmpty(cLastName) ||
                TextUtils.isEmpty(cMobile) ||
                TextUtils.isEmpty(cAddress1)
                || TextUtils.isEmpty(cAddress2) || TextUtils.isEmpty(cPincode)
                || TextUtils.isEmpty(cBoxNumber) || TextUtils.isEmpty(cVCNumber)
                || TextUtils.isEmpty(cDate)) {
            Toast.makeText(getApplicationContext(), "Please fill all entries", Toast.LENGTH_LONG).show();
            return;
        } else {

            DenDetails denDetails = new DenDetails();
            denDetails.setmVCNo(cVCNumber);
            denDetails.setmCustomerName(cFirstName + " " + cLastName);
            denDetails.setmActivatinDate(cDate);
            denDetails.setmSetupBoxNumber(cBoxNumber);
            denDetails.setmCustomerAddress(cAddress1);
            denDetails.setmAreaDescription(cPincode + " " + cAddress2);
            denDetails.setmMobileNumber(cMobile);
            denDetails.setmVCNo("---");
            denDetails.setmRecAmount("--");
            denDetails.setmRecDate("--");
            denDetails.setmRecNo("--");
            denDetails.setmBoxStatus("--");
            denDetails.setmBalance("--");
            denDetails.setmCCode("--");
            denDetails.setId("--");
            customerDatabaseRefrence.push().setValue(denDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "New Customer Added", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}
