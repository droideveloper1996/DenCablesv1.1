package com.capiyoo.dencables;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements HomeAdapter.HomeCustomerClickListner {
    @Nullable
    private RecyclerView recyclerView;
    private ArrayList<DenDetails> denDetailsArrayList;
    private ArrayList<String> denCustomerKey;
    private HomeAdapter setupBoxRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference customerDatabasereference;
    private ProgressBar progressBar;
    private SearchView searchView;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        denDetailsArrayList = new ArrayList<>();
        setHasOptionsMenu(true);
        denCustomerKey = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupBoxRecyclerView = new HomeAdapter(getContext(), denDetailsArrayList, denCustomerKey, this);
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
                        denDetails.setmCustomerKey(childDataSnapshot.getKey());
                        denDetailsArrayList.add(denDetails);

                    }
                    setupBoxRecyclerView = new HomeAdapter(getContext(), denDetailsArrayList, denCustomerKey, HomeFragment.this);
                    recyclerView.setAdapter(setupBoxRecyclerView);
                    setupBoxRecyclerView.notifyDataSetChanged();
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
                setupBoxRecyclerView.getFilter().filter(query);
                setupBoxRecyclerView.notifyDataSetChanged();
                //denDetailsArrayList.clear();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                //Toast.makeText(getContext(),"Clicked me",Toast.LENGTH_LONG).show();

                setupBoxRecyclerView.getFilter().filter(query);
                setupBoxRecyclerView.notifyDataSetChanged();
                // denDetailsArrayList.clear();
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
