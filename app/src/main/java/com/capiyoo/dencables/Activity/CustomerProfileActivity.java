package com.capiyoo.dencables.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.Fragments.CustomerBilling;
import com.capiyoo.dencables.Persistance.SharedPref;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.DenDetails;
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
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerProfileActivity extends AppCompatActivity {
    static final String month[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:   //ÒÑÁ¬½Ó
                            connectPrinter.setVisibility(View.GONE);
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
                    connectPrinter.setVisibility(View.VISIBLE);
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
    BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_ENABLE_BT = 2;
    String formattedDate = "";
    private static final int REQUEST_CONNECT_DEVICE = 1;  //Get device message
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference linemanDatabaseReb;
    DatabaseReference databaseReference;
    View toolbarView;
    Button connectPrinter;
    TextView customerNameData;
    TextView customerAddress;
    TextView customerMobile;
    TextView customerNumber;
    TextView customerBoxNumber;
    TextView customerPlan;
    TextView customerLastPayment;
    TextView customerPendingAmount;
    EditText paynow;
    DatabaseReference dailyPaymentDatabaseref;
    TextView customerLastPaymentReciept;
    TextView customerLastPaymentDate;
    Button actionPrintReport;
    RecyclerView paymentHistory;
    float outstandingAmount;
    SharedPref sharedPref;
    float balanceAmount;
    float paidAmount;
    TextView customerMonthlyPackage;
    TextView customerPreviousBalance;
    float monthlyPakage;
    float outstandingAmt;
    float previousBalanceCopy;
    String key;
    String customerLastPayDate;
    Button printReciept;
    Boolean isPreviousBalance = true;
    int monthDifference = 1;
    private String receiptNumberGenerated;
    private ImageView imageVector;
    DenDetails denDetails;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        connectPrinter = findViewById(R.id.actionConnectPrinter);
        printReciept = findViewById(R.id.actionPrintReciept);
        sharedPref = new SharedPref(this);
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(sharedPref.getFirebaseUid());
        toolbarView = findViewById(R.id.yo_toolbar);
        databaseReference.keepSynced(true);
        Toolbar toolbar = toolbarView.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CustomerProfile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        outstandingAmount = 0.0f;
        balanceAmount = 0.0f;
        imageVector = findViewById(R.id.imageVector);
        Picasso.with(this).load(R.drawable.flik).into(imageVector);
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
                            denDetails = dataSnapshot.getValue(DenDetails.class);

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
                                monthDifference = getMonthPosition(getCurrentMonth()) - getMonthPosition(getmRecMonth(customerLastPayDate));
                                Toast.makeText(getApplicationContext(), Integer.toString(monthDifference), Toast.LENGTH_LONG).show();
                                if (isPreviousBalance) {
                                    previousBalanceCopy = balance;
                                    if (monthDifference > 1) {
                                        outstandingAmt = monthlyPakage + previousBalanceCopy;
                                        Toast.makeText(getApplicationContext(), Float.toString(outstandingAmt), Toast.LENGTH_LONG).show();
                                    } else {
                                        outstandingAmt = monthlyPakage + previousBalanceCopy;
                                    }
                                    //   Toast.makeText(getApplicationContext(), Float.toString(previousBalanceCopy), Toast.LENGTH_LONG).show();
                                    isPreviousBalance = false;
                                } else {

                                    if (monthDifference > 1) {
                                        outstandingAmt = monthlyPakage + balance;
                                    } else {
                                        outstandingAmt = monthlyPakage + balance;
                                    }

                                }
                                customerPreviousBalance.setText("Previous Balance: " + "₹" + denDetails.getmBalance());
                                customerMonthlyPackage.setText("Monthly Charge : " + "₹" + denDetails.getmMonthlyCharge());
                                /***
                                 *
                                 *                                 Add Next Due Date

                                 */
                                customerPendingAmount.setText("Outstanding Due: " + "₹" + outstandingAmt);

                                customerLastPaymentDate.setText("Last Paid: " + denDetails.getmRecDate());
                                customerLastPaymentReciept.setText("Last Receipt No: " + denDetails.getmRecNo());


                                int noOfdays = (int) getNumberDays(customerLastPayDate);
                                if (noOfdays < 5) {
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
        // TODO:
        /**
         *
         * Make outstanding amount to be 0 or show Next Month
         */

        actionPrintReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                receiptNumberGenerated = sharedPref.getFirmName().substring(0, 2) + "-" + Long.toString(System.currentTimeMillis() / 1000);
                updateBalance();
                updateBillingReport();
                updateIndividualCustomerBill();

                //  printReciept.setVisibility(View.VISIBLE);
            }
        });


        //connect printer button
        connectPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    connectPrinter();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        //Button for printing Reciept;
        printReciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                DateFormat df = new SimpleDateFormat("hh:mm:ss");
                String date_str = df.format(cal.getTime());


                try {
                    String lang = getString(R.string.bluetooth_strLang);
                    SharedPref sharedPref = new SharedPref(CustomerProfileActivity.this);
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
                        mService.sendMessage(arrangeEndToEnd("Lineman.", sharedPref.getLinemanName()), "GBK");

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("--------------------------------"), "GBK");

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Receipt"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Receipt No.", receiptNumberGenerated), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Receipt Date.", formattedDate), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Receipt Time", date_str), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Customer Name", denDetails.getmCustomerName()), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Customer Address", denDetails.getmCustomerAddress()), "GBK");

//
//cmd
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Card Number", denDetails.getmVCNo()), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Previous Balance.", Float.toString(previousBalanceCopy)), "GBK");


                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Monthly Charge", denDetails.getmMonthlyCharge()), "GBK");


                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("--------------------------------"), "GBK");

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Total Amount Due", Float.toString(paidAmount + outstandingAmount)), "GBK");


                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(arrangeEndToEnd("Paid Amount", Float.toString(paidAmount)), "GBK");

                        if (paidAmount > outstandingAmt) {
                            cmd[2] |= 0x10;
                            mService.write(cmd);
                            mService.sendMessage(arrangeEndToEnd("Advance Amount Paid.", Float.toString(outstandingAmount * -1)), "GBK");
                        } else {
                            cmd[2] |= 0x10;
                            mService.write(cmd);
                            mService.sendMessage(arrangeEndToEnd("Remaining Balance", Float.toString(outstandingAmount)), "GBK");
                        }

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("--------------------------------"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Receipt Amount is Inclusive of   all taxes."), "GBK");

                        cmd[2] |= 0x10;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Thank You For Choosing " + '\n' + sharedPref.getFirmName()), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("Have a great Day ahead :)\n"), "GBK");

                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("CapiYoo Infotech Pvt Ltd."), "GBK");
                        cmd[2] &= 0xEF;
                        mService.write(cmd);
                        mService.sendMessage(formatString("droid.developer1996@gmail.com\n\n"), "GBK");


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
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

    void updateBalance() {

        if (!TextUtils.isEmpty(paynow.getText().toString())) {
            //  actionPrintReport.setVisibility(View.GONE);
            paynow.setVisibility(View.GONE);
            //printReciept.setVisibility(View.VISIBLE);
            paidAmount = Float.parseFloat(paynow.getText().toString());
            //outstandingAmount is the Balance amount after the client has paid ;
            outstandingAmount = outstandingAmt - paidAmount;
//            if (outstandingAmount < 0) {
//                outstandingAmount *= -1;
//            }
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            formattedDate = df.format(c);
            // Toast.makeText(getApplicationContext(), Float.toString(outstandingAmount) + "     " + formattedDate, Toast.LENGTH_LONG).show();
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(new SharedPref(CustomerProfileActivity.this).getFirebaseUid());
            databaseReference1.keepSynced(true);
            Map map = new HashMap<>();
            map.put("mRecAmount", paynow.getText().toString());
            map.put("mRecDate", formattedDate);
            map.put("mRecNo", receiptNumberGenerated);
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

            //Hide my Pay now button
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    actionPrintReport.setVisibility(View.GONE);
                    printReciept.setVisibility(View.VISIBLE);
                    connectPrinter.setVisibility(View.VISIBLE);
                }
            }, 100);

            /**
             *
             * Updating daily payment report
             */

            updateDailypayment();
            updateLinemanCollection();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    customerPendingAmount.setText("Due Next Month: " + "₹" + outstandingAmt);
                    customerPendingAmount.setTextColor(getResources().getColor(R.color.positive));
                }
            }, 2000);

        } else {
            Toast.makeText(getApplicationContext(), "Invalid Amount", Toast.LENGTH_LONG).show();
        }

        actionPrintReport.setEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stop();
        mService = null;
    }


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

    void connectPrinter() {
        Intent serverIntent = new Intent(this, DeviceListActivity.class);      //ÔËÐÐÁíÍâÒ»¸öÀàµÄ»î¶¯
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    }

    void updateBillingReport() {

        DatabaseReference childBillingreference = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_BILLING_INFO).child(new SharedPref(getApplicationContext()).getFirebaseUid());
        CustomerBilling customerBilling = new CustomerBilling();
        customerBilling.setmBalanceAmount(Float.toString(paidAmount + outstandingAmount));
        customerBilling.setmCrewName("CREW-NAME");
        customerBilling.setmPaidAmount(Float.toString(paidAmount));
        customerBilling.setmTimeStamp(formattedDate);
        customerBilling.setMupdatedBalance(Float.toString(outstandingAmount));
        customerBilling.setmRecieptNumber("Random-receipt");
        childBillingreference.child(key).push().setValue(customerBilling).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        childBillingreference.keepSynced(true);

    }


    void updateIndividualCustomerBill() {

    }

    void updateDailypayment() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        dailyPaymentDatabaseref = FirebaseDatabase.getInstance().getReference().child("DailyCollection")
                .child(new SharedPref(CustomerProfileActivity.this).getFirebaseUid()).child(formattedDate);

        Map<String, String> map = new HashMap<>();
        //Customer id
        map.put("PAID_AMOUNT", Float.toString(paidAmount));
        map.put("CUSTOMER_TOKEN", key);
        map.put("CUSTOMER_NAME", customerNameData.getText().toString());
        map.put("CUSTOMER_ADDR", customerAddress.getText().toString());

        dailyPaymentDatabaseref.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(CustomerProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dailyPaymentDatabaseref.keepSynced(true);

    }

    void updateLinemanCollection() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        linemanDatabaseReb = FirebaseDatabase.getInstance().getReference().child("LinemanDailyCollection").child(new SharedPref(this).getFirebaseUid());
        Map<String, String> map = new HashMap<>();

        map.put("COLLECTED_AMOUNT", Float.toString(paidAmount));
        map.put("CUSTOMER_TOKEN", key);
        map.put("CUSTOMER_NAME", customerNameData.getText().toString());
        map.put("CUSTOMER_ADDR", customerAddress.getText().toString());

        linemanDatabaseReb.child(new SharedPref(getApplicationContext()).getLinemanKey()).child(formattedDate).push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        linemanDatabaseReb.keepSynced(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_profile, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_edit_customer:
                Intent intent = new Intent(CustomerProfileActivity.this, EditCustomerActivity.class);
                intent.putExtra("CustomerKey", key);
                intent.putExtra("FromCustomerProfileActivity", true);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }


    int getMonthPosition(String mnth) {
        int monthPosition = -1;
        for (int i = 0; i < 12; i++) {
            if (mnth.equals(month[i])) {
                monthPosition = i;
                break;
            }
        }
        return monthPosition;
    }

    String getCurrentMonth() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int mon = c.get(Calendar.MONTH);
        return month[mon];
    }

    String getmRecMonth(String givenDateString) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date mDate = sdf.parse(givenDateString);
            return month[mDate.getMonth()];

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    void updateBalanceMonthly() {

    }
}
