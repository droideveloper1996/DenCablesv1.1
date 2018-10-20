package com.capiyoo.dencables.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.capiyoo.dencables.Adapter.ViewPagerAdapter;
import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.PaytmPayments.OperatorsPaymentActivity;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.Activation;
import com.capiyoo.dencables.Utilities.OperatorProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    TabLayout tabLayout;
    NavigationView mNavigationView;
    Toolbar toolbar;
    TextView toolbarTextView;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    boolean isActivated;
    boolean isFirstTimeRegistration;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.USER_REGISTRATION);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        getAuthState();
        if (intent != null && intent.hasExtra(Constants.IS_ACCOUNT_ACTIVATED) && intent.hasExtra(Constants.FIRST_TIME_REGISTRATION)) {

            Log.d("Intent ", "not null");
            isFirstTimeRegistration = intent.getBooleanExtra(Constants.FIRST_TIME_REGISTRATION, true);
            isActivated = intent.getBooleanExtra(Constants.IS_ACCOUNT_ACTIVATED, false);
            Log.d("Intent ", Boolean.toString(isActivated));
            Log.d("Intent ", Boolean.toString(isFirstTimeRegistration));

            if (isFirstTimeRegistration || isActivated) {
                final View v = getLayoutInflater().inflate(R.layout.dialogue_builder, null);
                final EditText activationKey = v.findViewById(R.id.password_otp);
                final AlertDialog.Builder
                        builder = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
                Button cancelBtn = v.findViewById(R.id.cancel);
                Button ok = v.findViewById(R.id.ok);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), activationKey.getText().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setView(v);
                builder.setCancelable(false);
                builder.show();
            }
        } else {
            if (firebaseAuth.getCurrentUser() != null) {
                String currentUser = firebaseAuth.getCurrentUser().getUid();
                databaseReference.child(currentUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot != null) {
                            OperatorProfile operatorProfile = dataSnapshot.getValue(OperatorProfile.class);
                            String activationDate = operatorProfile.getmActivationDate();
                            boolean activeAccount = operatorProfile.ismIsActivated();
                            if (!activeAccount) {
                                /***
                                 *  Check weather the account is active or not;
                                 *  account inactive
                                 */
                                //Toast.makeText(getApplicationContext(), Boolean.toString(activeAccount), Toast.LENGTH_SHORT).show();
                                final View v = getLayoutInflater().inflate(R.layout.dialogue_builder, null);
                                final EditText activationKey = v.findViewById(R.id.password_otp);
                                final AlertDialog.Builder
                                        builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
                                builder.setView(v);
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child(Constants.REEDEMED_BY);
                                        databaseReference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot != null) {

                                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                        Activation activation = dataSnapshot1.getValue(Activation.class);
                                                        String keyOnLineDatabase = activation.getActivationKey();
                                                        Log.d("Database Key", keyOnLineDatabase);
                                                        Log.d("Entered Key", activationKey.getText().toString());

                                                        /***
                                                         * Finding activation Key in database and matching
                                                         * if match found then check if key is already activated or not
                                                         * if it is already activated notify user
                                                         * if not then update details for key...and  also update who has used the key.
                                                         * i.e key is activated and put activation date time detail in database
                                                         */

                                                        if (activationKey.getText().toString().equals(keyOnLineDatabase)) {
                                                            if (!activation.getIsActivated()) {
                                                                Long tsLong = System.currentTimeMillis() / 1000;
                                                                final String ts = tsLong.toString();
                                                                Activation activationStatus = new Activation();
                                                                activationStatus.setIsActivated(true);
                                                                activationStatus.setActivationTime(ts);
                                                                activationStatus.setActivationKey(keyOnLineDatabase);
                                                                activationStatus.setReedemedBy(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                databaseReference1.child(dataSnapshot1.getKey()).setValue(activationStatus).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(getApplicationContext(), "Product Activated", Toast.LENGTH_LONG).show();
                                                                            String currentUser = firebaseAuth.getCurrentUser().getUid();
                                                                            Map map = new HashMap();
                                                                            /**
                                                                             *
                                                                             * Updating the operator Profile
                                                                             * isActivated=true;
                                                                             * putting activation date/time
                                                                             */

                                                                            map.put("mIsActivated", true);
                                                                            map.put("mActivationDate", ts);

                                                                            databaseReference.child(currentUser).updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_LONG).show();

                                                                                    }
                                                                                }
                                                                            })
                                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                                        @Override
                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                                                                    }
                                                                });

                                                                break;

                                                            } else {
                                                                if (activation.getIsActivated()) {
                                                                    Toast.makeText(MainActivity.this, "Key already activated", Toast.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        } else {
                                                            Toast.makeText(MainActivity.this, "Invalid Key Entered", Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                });
                                builder.show();

                            } else {
                                if (activeAccount) {
                                    /***
                                     * account active
                                     *  Check weather the account is active or not;
                                     */
                                    //  Toast.makeText(getApplicationContext(), Boolean.toString(activeAccount), Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "user not logged in", Toast.LENGTH_SHORT).show();

            }
            Log.d("Intent ", " null");
            Log.d("Intent ", Boolean.toString(isActivated));
            Log.d("Intent ", Boolean.toString(isFirstTimeRegistration));


        }
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabs);
        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.True, R.string.False);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        mNavigationView = findViewById(R.id.navigationView);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_add_customer:
                        startActivity(new Intent(MainActivity.this, NewCustomerActivity.class));
                        return true;
                    case R.id.action_logout:
                        if(firebaseAuth.getCurrentUser()!=null) {
                            firebaseAuth.signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                        return true;
                    case R.id.action_make_payment:
                        startActivity(new Intent(MainActivity.this, OperatorsPaymentActivity.class));
                        break;
                    case R.id.action_lineman:
                        startActivity(new Intent(MainActivity.this, LineManActivity.class));
                        break;

                }
                return true;
            }
        });


    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         MenuInflater menuInflater = getMenuInflater();
         menuInflater.inflate(R.menu.main_menu, menu);

         MenuItem search = menu.findItem(R.id.search);
         SearchView searchView = (SearchView) search.getActionView();
         SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
         searchView = (SearchView) menu.findItem(R.id.action_search)
                 .getActionView();
         searchView.setSearchableInfo(searchManager
                 .getSearchableInfo(getComponentName()));
         searchView.setMaxWidth(Integer.MAX_VALUE);

         // listening to search query text change
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
             @Override
             public boolean onQueryTextSubmit(String query) {
                 // filter recycler view when query submitted
                 new SetupBoxRecyclerView().getFilter().filter(query);
                 return false;
             }

             @Override
             public boolean onQueryTextChange(String query) {
                 // filter recycler view when text is changed
                 mAdapter.getFilter().filter(query);
                 return false;
             }
         });

         return super.onCreateOptionsMenu(menu);
     }
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        switch (id) {
            case R.id.action_add_customer:
                startActivity(new Intent(MainActivity.this, NewCustomerActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);

    }



    void getAuthState() {

        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
        }
    }

}
