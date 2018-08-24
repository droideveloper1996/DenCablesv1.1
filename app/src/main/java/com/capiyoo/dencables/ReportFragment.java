package com.capiyoo.dencables;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReportFragment extends Fragment {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference firebaseDatabaseReference;
    private Button dailyReport;
    private Button flexiReport;
    private Button crewReport;

    ///TODO : Update Data in CustomerData--->Uid---->Billing ---->Individual customer Report
    ///TODO : Update Data in Billing-Info---->uid---->daily---->Report;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return Daily Summary Download
     * <p>
     * Billing-Info
     * uid
     * Date
     * pushId
     * crew:value
     * collectedFrom:value
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report_fragment, container, false);
        dailyReport = view.findViewById(R.id.generateDailyReport);
        flexiReport = view.findViewById(R.id.customReport);
        crewReport = view.findViewById(R.id.crewCollection);
        SharedPref sharedPref = new SharedPref(getContext());
        String uis = sharedPref.getFirebaseUid();

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(uis);
        dailyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateDailyReport();
            }
        });

        flexiReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                generateCustomReport();
            }
        });

        crewReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                generateCrewReport();
            }
        });

        return view;
    }


    void generateDailyReport() {
        firebaseDatabaseReference.child(Constants.CUSTOMER_BILLING_INFO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    void generateCustomReport() {

    }


    void generateCrewReport() {
    }
}
