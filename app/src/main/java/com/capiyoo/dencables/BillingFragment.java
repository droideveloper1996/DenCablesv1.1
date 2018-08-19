package com.capiyoo.dencables;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BillingFragment extends Fragment {
    long diff = 0l;
    RecyclerView recyclerView;
    DatabaseReference pendingCustomerList;
    ArrayList<DenDetails> pendingCollectionInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // long days = getNumberDays("15-AUG-2018");
        View view = inflater.inflate(R.layout.billing_fragment, container, false);
        recyclerView = view.findViewById(R.id.pendingCollection);
        recyclerView.setHasFixedSize(true);
        SharedPref sharedPref = new SharedPref(getContext());
        pendingCollectionInfo = new ArrayList<>();
        pendingCustomerList = FirebaseDatabase.getInstance().getReference().child(Constants.CUSTOMER_DATA).child(sharedPref.getFirebaseUid());

        getPendingDataFromDatabase();
        return view;
    }


    long getNumberDays(String givenDateString) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date mDate = sdf.parse(givenDateString);
            long timeInMilliseconds = mDate.getTime();
            long currentTime = System.currentTimeMillis();
            diff = (currentTime - timeInMilliseconds) / (1000 * 60 * 60 * 24);
            System.out.println("Date in milli :: " + diff);
            return diff;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return diff;
    }


    void getPendingDataFromDatabase() {
        pendingCustomerList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pendingCollectionInfo.clear();
                if (dataSnapshot != null) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        DenDetails denDetails = dataSnapshot1.getValue(DenDetails.class);
                        String lastPaidDate = denDetails.getmRecDate();
                        int priority = (int) getNumberDays(lastPaidDate);
                        if (priority > 28) {
                            pendingCollectionInfo.add(denDetails);
                        }
                    }

                    BillingRecyclerAdapter billingRecyclerAdapter = new BillingRecyclerAdapter(getContext(), pendingCollectionInfo);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(billingRecyclerAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class BillingRecyclerAdapter extends RecyclerView.Adapter<BillingRecyclerAdapter.BillingRecyclerViewHolder> {

        Context mCtx;
        ArrayList<DenDetails> pendingCustomer;

        public BillingRecyclerAdapter(Context mCtx, ArrayList<DenDetails> denDetails) {
            this.mCtx = mCtx;
            this.pendingCustomer = denDetails;
        }

        @Override
        public BillingRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BillingRecyclerViewHolder(LayoutInflater.from(mCtx).inflate(R.layout.list_item_view,parent,false));
        }

        @Override
        public void onBindViewHolder(BillingRecyclerViewHolder holder, int position) {

            holder.boxNumber.setText(pendingCustomer.get(position).getmVCNo());
            holder.boxStatus.setText(pendingCustomer.get(position).getmBoxStatus());
            holder.customerName.setText(pendingCustomer.get(position).getmCustomerName());
            holder.customerCode.setText(pendingCustomer.get(position).getmCCode());
        }

        @Override
        public int getItemCount() {
            return pendingCollectionInfo.size();
        }

        class BillingRecyclerViewHolder extends RecyclerView.ViewHolder {
            TextView customerName;
            TextView boxNumber;
            TextView boxStatus;
            TextView customerCode;
            public BillingRecyclerViewHolder(View itemView) {
                super(itemView);
                customerCode = itemView.findViewById(R.id.customerCode);
                customerName = itemView.findViewById(R.id.customerName);
                boxStatus = itemView.findViewById(R.id.boxStatus);
                boxNumber = itemView.findViewById(R.id.boxNumber);
            }
        }
    }

}
