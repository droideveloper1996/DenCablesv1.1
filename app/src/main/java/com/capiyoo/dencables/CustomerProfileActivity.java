package com.capiyoo.dencables;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomerProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    View toolbarView;

    TextView customerNameData;
    TextView customerAddress;
    TextView customerMobile;
    TextView customerNumber;
    TextView customerBoxNumber;
    TextView customerPlan;
    TextView customerLastPayment;
    TextView customerPendingAmount;
    EditText paynow;
    TextView customerLastPaymentReciept;
    TextView customerLastPaymentDate;
    Button actionPrintReport;
    RecyclerView paymentHistory;
    float outstandingAmount;
    float balanceAmount;
    float paidAmount;
    TextView customerMonthlyPackage;
    TextView customerPreviousBalance;
    float monthlyPakage;
    float outstandingAmt;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        SharedPref sharedPref = new SharedPref(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(sharedPref.getFirebaseUid());
        toolbarView = findViewById(R.id.yo_toolbar);
        Toolbar toolbar = toolbarView.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CustomerProfile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outstandingAmount = 0.0f;
        balanceAmount = 0.0f;
        customerNameData = findViewById(R.id.customerNameData);
        customerAddress = findViewById(R.id.customerAddress);
        customerMobile = findViewById(R.id.customerMobile);
        customerNumber = findViewById(R.id.customerNumber);
        customerBoxNumber = findViewById(R.id.customerBoxNumber);
        customerPlan = findViewById(R.id.customerPlan);
        customerLastPayment = findViewById(R.id.customerLastPayment);
        customerPendingAmount = findViewById(R.id.customerPendingAmount);
        customerLastPaymentDate = findViewById(R.id.customerLastPaymentDate);
        customerLastPaymentReciept = findViewById(R.id.customerLastPaymentReciept);
        customerPreviousBalance = findViewById(R.id.customerPreviousBalance);
        customerMonthlyPackage = findViewById(R.id.customerMonthlyPackage);
        paynow = findViewById(R.id.paynow);
        actionPrintReport = findViewById(R.id.actionPrintReport);

        paymentHistory = findViewById(R.id.paymentHistory);
        paymentHistory.setHasFixedSize(true);
        paymentHistory.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("CustomerKey")) {
                key = getIntent().getStringExtra("CustomerKey");
                databaseReference.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            //  Toast.makeText(getApplicationContext(), dataSnapshot.toString(), Toast.LENGTH_LONG).show();
                            DenDetails denDetails = dataSnapshot.getValue(DenDetails.class);

                            try {
                                customerNameData.setText(denDetails.getmCustomerName());
                                customerAddress.setText(denDetails.getmCustomerAddress());
                                customerBoxNumber.setText("Box No.: " + denDetails.getmSetupBoxNumber());
                                customerMobile.setText("Mobile: " + denDetails.getmMobileNumber());
                                customerPlan.setText("PKG Name: " + denDetails.getmPackageName());
                                customerNumber.setText("VC No.: " + denDetails.getmVCNo());
                                customerLastPayment.setText("Last Paid Amount: " + "₹" + denDetails.getmRecAmount());
                                monthlyPakage = Float.parseFloat(denDetails.getmMonthlyCharge());
                                float balance = Float.parseFloat(denDetails.getmBalance());
                                outstandingAmt = monthlyPakage + balance;
                                customerPreviousBalance.setText("Previous Balance: " + "₹" + denDetails.getmBalance() + ".00");
                                customerMonthlyPackage.setText("Monthly Charge : " + "₹" + denDetails.getmMonthlyCharge());
                                customerPendingAmount.setText("Outstanding Due: " + "₹" + outstandingAmt);
                                customerLastPaymentDate.setText("Last Paid: " + denDetails.getmRecDate());
                                customerLastPaymentReciept.setText("Last Receipt No: " + denDetails.getmRecNo());

                                balanceAmount = Float.parseFloat(denDetails.getmBalance());
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
        hideKeyboard(this);

        actionPrintReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBalance();
            }
        });
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    void updateBalance() {
        if (!TextUtils.isEmpty(paynow.getText().toString())) {


            paidAmount = Float.parseFloat(paynow.getText().toString());
            outstandingAmount = outstandingAmt - paidAmount;
            if (outstandingAmount < 0) {
                outstandingAmount *= -1;
            }


            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);

            Toast.makeText(getApplicationContext(), Float.toString(outstandingAmount) + "     " + formattedDate, Toast.LENGTH_LONG).show();

            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(new SharedPref(CustomerProfileActivity.this).getFirebaseUid());
            Map map = new HashMap<>();
            map.put("mRecAmount", paynow.getText().toString());
            map.put("mRecDate", formattedDate);
            map.put("mRecNo", "R-2049392");
            map.put("mBalance", Float.toString(outstandingAmount));


            databaseReference1.child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Balance Updated", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Invalid Amount", Toast.LENGTH_LONG).show();
        }

    }
}
