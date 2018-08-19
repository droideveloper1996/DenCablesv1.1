package com.capiyoo.dencables;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
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
import com.hoin.btsdk.BluetoothService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CustomerProfileActivity extends AppCompatActivity {
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_ENABLE_BT = 2;

    private static final int REQUEST_CONNECT_DEVICE = 1;  //Get device message
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
    String customerLastPayDate;
    Button printReciept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        printReciept=findViewById(R.id.actionPrintReciept);
        SharedPref sharedPref = new SharedPref(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(sharedPref.getFirebaseUid());
        toolbarView = findViewById(R.id.yo_toolbar);
        databaseReference.keepSynced(true);
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

        mService = new BluetoothService(CustomerProfileActivity.this, mHandler);
        //Bluetooth is not available to exit the program
        if (!mService.isAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

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
                                customerLastPayDate = denDetails.getmRecDate();
                                outstandingAmt = monthlyPakage + balance;
                                customerPreviousBalance.setText("Previous Balance: " + "₹" + denDetails.getmBalance() + ".00");
                                customerMonthlyPackage.setText("Monthly Charge : " + "₹" + denDetails.getmMonthlyCharge());
                                customerPendingAmount.setText("Outstanding Due: " + "₹" + outstandingAmt);
                                customerLastPaymentDate.setText("Last Paid: " + denDetails.getmRecDate());
                                customerLastPaymentReciept.setText("Last Receipt No: " + denDetails.getmRecNo());
                                int noOfdays = (int) getNumberDays(customerLastPayDate);
                                if (noOfdays < 28) {
                                    actionPrintReport.setEnabled(false);
                                } else {

                                }

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
                printReciept.setVisibility(View.VISIBLE);
            }
        });

        printReciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String lang = getString(R.string.bluetooth_strLang);
                SharedPref sharedPref=new SharedPref(CustomerProfileActivity.this);
                byte[] cmd = new byte[3];
                cmd[0] = 0x1b;
                cmd[1] = 0x21;
                if ((lang.compareTo("en")) == 0) {
                    cmd[2] |= 0x10;
                    mService.write(cmd);
                    mService.sendMessage(formatString(sharedPref.getFirmName()), "GBK");
                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                    mService.sendMessage(formatString(sharedPref.getFirmAddress1() + " " + sharedPref.getFirmAddress2() + " " + sharedPref.getCity()
                            ) + '\n' + arrangeEndToEnd("Contact No.", sharedPref.getFirmContact()) + '\n' +
                                    arrangeEndToEnd("Person", sharedPref.getFirmAuthority())
                            , "GBK");

                    cmd[2] &= 0xEF;
                    mService.write(cmd);
                    //   mService.sendMessage(), "GBK");

                    /**
                     * Bill Format
                     *
                     * 			CapiYoo Infotech Pvt Ltd.
                     115/8 AwasVikas Sector -J
                     Keshavpuram Kalyanpur Kanpur
                     Uttar Pradesh

                     Invoice
                     Crew: XXXX					Id:XXXX
                     ----------------------------------------------------------
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     *
                     */
                    String msg =
                            " 115/8 AwasVikas Sector -J \n"
                                    + "  Keshavpuram Kalyanpur Kanpur\n"
                                    + "     Uttar Pradesh\n\n"
                                    + "GSTIN-            ABCDEFG123456\n\n"
                                    + "         INVOICE\n\n"
                                    + " Crew: XXXX          Id:XXXX \n"
                                    + "--------------------------------"
                                    + "BillNO:		 234XXXXXX\n"
                                    + "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456 ";
                    //   mService.sendMessage(msg, "GBK");
                }
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
            actionPrintReport.setVisibility(View.GONE);
            paynow.setVisibility(View.GONE);
            printReciept.setVisibility(View.VISIBLE);
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
            databaseReference1.keepSynced(true);
            Map map = new HashMap<>();
            map.put("mRecAmount", paynow.getText().toString());
            map.put("mRecDate", formattedDate);
            map.put("mRecNo", "R-2049392");
            map.put("mBalance", Float.toString(outstandingAmount));

            databaseReference1.child(key).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        connectPrinter();
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

        actionPrintReport.setEnabled(false);
    }


    long getNumberDays(String givenDateString) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            long currentTime = System.currentTimeMillis();
            long diff = (currentTime - timeInMilliseconds) / (1000 * 60 * 60 * 24);
            System.out.println("Date in milli :: " + diff);
            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mService.isAvailable()) {
            if (!mService.isBTopen()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            Toast.makeText(CustomerProfileActivity.this, "Connect successful",
                                    Toast.LENGTH_SHORT).show();
                          //  btnClose.setEnabled(true);
                           // btnSend.setEnabled(true);
                           // qrCodeBtnSend.setEnabled(true);
                          //  btnSendDraw.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:  //ÕýÔÚÁ¬½Ó
                            Log.d("À¶ÑÀµ÷ÊÔ", "ÕýÔÚÁ¬½Ó.....");
                            break;
                        case BluetoothService.STATE_LISTEN:     //¼àÌýÁ¬½ÓµÄµ½À´
                        case BluetoothService.STATE_NONE:
                            Log.d("À¶ÑÀµ÷ÊÔ", "µÈ´ýÁ¬½Ó.....");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:    //À¶ÑÀÒÑ¶Ï¿ªÁ¬½Ó
                    Toast.makeText(CustomerProfileActivity.this, "Device connection was lost",
                            Toast.LENGTH_SHORT).show();
                   // btnClose.setEnabled(false);
                 //   btnSend.setEnabled(false);
                 //   qrCodeBtnSend.setEnabled(false);
                 //   btnSendDraw.setEnabled(false);
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:     //ÎÞ·¨Á¬½ÓÉè±¸
                    Toast.makeText(CustomerProfileActivity.this, "Unable to connect device",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                //Request to turn on Bluetooth
                if (resultCode == Activity.RESULT_OK) {
                    //Bluetooth is turned on
                    Toast.makeText(CustomerProfileActivity.this, "Bluetooth open successful", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CONNECT_DEVICE:     //Request to connect to a Bluetooth device
                if (resultCode == Activity.RESULT_OK) {
                    //	A device item in the search list has been clicked

                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);  //Get the mac address of the device in the list item
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                }
                break;
        }
}

    String formatString(String str) {

        String spacedCha = "";
        int length = str.length();
        int maxSpaces = 32;
        int leftSpaces = maxSpaces - length;
        int remainingSpaces = leftSpaces / 2;

        for (int i = 0; i < remainingSpaces; i++) {
            spacedCha += " ";
        }
        spacedCha += str;
        return spacedCha;

    }

    String arrangeEndToEnd(String str, String lead) {
        String spacedCha = "";
        String myString = "";
        int length1 = str.trim().length();
        int length2 = lead.trim().length();
        int maxSpaces = 32;
        int rem1 = maxSpaces - length1;
        int rem2 = rem1 - length2;
        for (int i = 0; i < rem2; i++) {
            myString += " ";
        }
        spacedCha = str + myString + lead;
        return spacedCha;
    }

    void connectPrinter()
    {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);      //ÔËÐÐÁíÍâÒ»¸öÀàµÄ»î¶¯
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }
}
