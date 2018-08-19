package com.capiyoo.dencables;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener, SetupBoxRecyclerView.CustomerClickListner {
    @Nullable
    RecyclerView recyclerView;
    ArrayList<DenDetails> denDetailsArrayList;
    ArrayList<String> denCustomerKey;
    SetupBoxRecyclerView setupBoxRecyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference customerDatabasereference;
    ProgressBar progressBar;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        denDetailsArrayList = new ArrayList<>();

        denCustomerKey = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupBoxRecyclerView = new SetupBoxRecyclerView(getContext(), denDetailsArrayList, denCustomerKey, this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressBar = view.findViewById(R.id.progressBar);


        String uid = new SharedPref(getContext()).getFirebaseUid();
        if (uid != null) {
            customerDatabasereference = firebaseDatabase.getReference().child(Constants.CUSTOMER_DATA).child(uid);
            customerDatabasereference.keepSynced(true);
           } else {
            Toast.makeText(getContext(), "Authentication Error", Toast.LENGTH_LONG).show();
        }
        getCustomerData();

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Toast.makeText(getContext(), "Clicked Me", Toast.LENGTH_SHORT).show();
        String userInput = newText.toLowerCase();
        ArrayList<DenDetails> denDetailsArrayList2 = new ArrayList<>();
        for (DenDetails details : denDetailsArrayList) {
            String customerNam = details.getmBalance();
            if (customerNam.toLowerCase().contains(userInput)) {
                DenDetails denDetails = new DenDetails();
                denDetails.setmCustomerName(customerNam);
                denDetailsArrayList2.add(denDetails);
            }
        }
        setupBoxRecyclerView.updateList(denDetailsArrayList2);


        return false;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.main_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(this);
    }


    void getCustomerData() {

        customerDatabasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    denDetailsArrayList.clear();
                    denCustomerKey.clear();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        denCustomerKey.add(childDataSnapshot.getKey());
                        DenDetails denDetails = childDataSnapshot.getValue(DenDetails.class);
                        denDetailsArrayList.add(denDetails);

                    }
                    setupBoxRecyclerView = new SetupBoxRecyclerView(getContext(), denDetailsArrayList, denCustomerKey, HomeFragment.this);
                    recyclerView.setAdapter(setupBoxRecyclerView);
                    if (setupBoxRecyclerView.getItemCount() > 0) {
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCustomerClick(int position) {
        Intent intent = new Intent(getContext(), CustomerProfileActivity.class);
        intent.putExtra("CustomerKey", denCustomerKey.get(position));
        startActivity(intent);
    }
}
