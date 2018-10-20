package com.capiyoo.dencables.PaytmPayments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.capiyoo.dencables.Adapter.PricingAdapter;
import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.Persistance.SharedPref;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.PlansPricing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OperatorsPaymentActivity extends AppCompatActivity implements PricingAdapter.PriceClickListener, PaytmPaymentTransactionCallback {
    public static String CUST_ID;
    public static String ORDER_ID;
    String checkSumGenerated;
    ArrayList<PlansPricing> plansPricings;
    DatabaseReference firebaseDatabaseReference;
    String transactionAmount = "";
    String PLAN_NAME = "";
    ProgressDialog progressDialog;
    SharedPref sharedPref;
    private RecyclerView plansRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators_payment);
        sharedPref = new SharedPref(this);
        if (ContextCompat.checkSelfPermission(OperatorsPaymentActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OperatorsPaymentActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(OperatorsPaymentActivity.this);
        progressDialog.setMessage("Processing...Taking you to merchant Gateway");
        progressDialog.setTitle("Please wait...");

        plansPricings = new ArrayList<>();
        plansRecyclerView = findViewById(R.id.plansRecyclerView);
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PLAN);
        PricingAdapter pricingAdapter = new PricingAdapter(this, this, plansPricings);
        plansRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        plansRecyclerView.setLayoutManager(linearLayoutManager);
        plansRecyclerView.setAdapter(pricingAdapter);
        getPlanPricingFirebase();
    }


    public void generateCheksum() {

        Log.d("ORDER_ID", ORDER_ID);
        HashMap<String, String> paramMap = new HashMap<>();
        String URL = "http://www.capiyoo.com/admin/gateway/payment/paytm/checksum.php";

        paramMap.put("CALLBACK_URL", Constants.CALLBACK_URL + "?" + "ORDER_ID=" + ORDER_ID);
        paramMap.put("CHANNEL_ID", Constants.CHANNEL_ID);
        paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", ORDER_ID);
        paramMap.put("TXN_AMOUNT", "1");
        paramMap.put("WEBSITE", Constants.WEBSITE);
        paramMap.put("MOBILE_NO", "7777777777");

        JSONObject jsonObject = new JSONObject(paramMap);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                checkSumGenerated = response.optString("CHECKSUMHASH");
                if (!TextUtils.isEmpty(checkSumGenerated) && checkSumGenerated.trim().length() != 0) {

                    onStartTransaction(checkSumGenerated);
                }
                Log.d("VolleyResponse", checkSumGenerated);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyResponse", error.toString());
            }
        });

        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }


    @Override
    protected void onStart() {
        super.onStart();
        //initOrderId();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    String generateUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");

    }

    public void onStartTransaction(String checkSumGenerated) {
        PaytmPGService Service = PaytmPGService.getProductionService();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("CALLBACK_URL", Constants.CALLBACK_URL + "?ORDER_ID=" + ORDER_ID);
        paramMap.put("CHANNEL_ID", Constants.CHANNEL_ID);
        paramMap.put("CHECKSUMHASH", checkSumGenerated);
        paramMap.put("CUST_ID", CUST_ID);
        paramMap.put("INDUSTRY_TYPE_ID", Constants.INDUSTRY_TYPE_ID);
        paramMap.put("MID", Constants.M_ID);
        paramMap.put("ORDER_ID", ORDER_ID);
        paramMap.put("TXN_AMOUNT", "1");
        paramMap.put("WEBSITE", Constants.WEBSITE);
        paramMap.put("MOBILE_NO", "7777777777");
        PaytmOrder Order = new PaytmOrder(paramMap);
        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                this);
    }

    @Override
    public void onPriceChoosen(int position) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);


        Calendar cal = Calendar.getInstance();
        DateFormat time = new SimpleDateFormat("hh:mm:ss");
        String _time = time.format(cal.getTime());


        progressDialog.show();
        transactionAmount = plansPricings.get(position).getmPlanBasePrice();
        PLAN_NAME = plansPricings.get(position).getmPlanName();

        CUST_ID = generateUUID();
        ORDER_ID = PLAN_NAME + generateUUID();

        sharedPref.setOrderId(ORDER_ID);
        sharedPref.setPackageName(plansPricings.get(position).getmPlanName());
        sharedPref.setPackagePrice(plansPricings.get(position).getmPlanBasePrice());
        sharedPref.setOrderDate(formattedDate);
        sharedPref.setOrderTIme(_time);


        generateCheksum();
    }


    void getPlanPricingFirebase() {

        firebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    plansPricings.clear();
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        PlansPricing plansPricing = dataSnapshot1.getValue(PlansPricing.class);
                        plansPricings.add(plansPricing);
                    }
                    PricingAdapter pricingAdapter = new PricingAdapter(OperatorsPaymentActivity.this, OperatorsPaymentActivity.this, plansPricings);
                    plansRecyclerView.setHasFixedSize(true);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(OperatorsPaymentActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    plansRecyclerView.setLayoutManager(linearLayoutManager);
                    plansRecyclerView.setAdapter(pricingAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    void updateOperatorPayment() {

        Map<String, String> paymentHash = new HashMap<>();
        paymentHash.put("PLAN_NAME", sharedPref.getPackageName());
        paymentHash.put("PLAN_PRICE", sharedPref.getPackagePrice());
        paymentHash.put("ORDER_ID", sharedPref.getOrderId());
        paymentHash.put("ORDER_DATE", sharedPref.getOrderDate());
        paymentHash.put("ORDER_TIME", sharedPref.getOrderTime());

        if (FirebaseAuth.getInstance().getUid() != null) {
            DatabaseReference Operatorpayment = FirebaseDatabase.getInstance().getReference().child(Constants.OPERATOR_PAYMENT).child(FirebaseAuth.getInstance().getUid());
            Operatorpayment.keepSynced(true);
            Operatorpayment.push().setValue(paymentHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        Toast.makeText(getApplicationContext(), "Payment Received.Thanks for choosing us", Toast.LENGTH_LONG).show();
                        /**
                         * UPDATE USER PROFILE ACCOUNT STATUS
                         */

                        progressDialog.dismiss();

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


    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {

        Log.d("LOG", "Payment Transaction is successful " + inResponse);
        updateOperatorPayment();
        Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void networkNotAvailable() { // If network is not
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Network Failure", Toast.LENGTH_LONG).show();

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Client Authentication Failed", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode,
                                      String inErrorMessage, String inFailingUrl) {

        Toast.makeText(getApplicationContext(), inErrorMessage, Toast.LENGTH_LONG).show();


    }

    @Override
    public void onBackPressedCancelTransaction() {
        progressDialog.dismiss();
        Toast.makeText(OperatorsPaymentActivity.this, "Back pressed. Transaction cancelled", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
        Log.d("ERROR", inErrorMessage);
        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();


    }
}
