package com.capiyoo.dencables;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BillingFragment extends Fragment implements SetupBoxRecyclerView.CustomerClickListner {
    long diff = 0l;
    RecyclerView recyclerView;
    SetupBoxRecyclerView billingRecyclerAdapter;
    private DatabaseReference pendingCustomerList;
    private ArrayList<DenDetails> pendingCollectionInfo;
    private ArrayList<String> CustomerKey;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // long days = getNumberDays("15-AUG-2018");
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.billing_fragment, container, false);
        recyclerView = view.findViewById(R.id.pendingCollection);
        recyclerView.setHasFixedSize(true);
        CustomerKey = new ArrayList<>();
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
                            //  CustomerKey.add(dataSnapshot1.getKey());
                            denDetails.setmCustomerKey(dataSnapshot1.getKey());
                            pendingCollectionInfo.add(denDetails);
                        }
                    }

                    billingRecyclerAdapter = new SetupBoxRecyclerView(getContext(), pendingCollectionInfo, CustomerKey, BillingFragment.this);
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

    @Override
    public void onCustomerClick(DenDetails denDetails) {

        Intent intent = new Intent(getContext(), CustomerProfileActivity.class);
        intent.putExtra("CustomerKey", denDetails.getmCustomerKey());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) item.getActionView();

        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                Toast.makeText(getContext(), "Clicked me", Toast.LENGTH_LONG).show();
                billingRecyclerAdapter.getFilter().filter(query);
                billingRecyclerAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                //Toast.makeText(getContext(),"Clicked me",Toast.LENGTH_LONG).show();

                billingRecyclerAdapter.getFilter().filter(query);
                billingRecyclerAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return true;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
