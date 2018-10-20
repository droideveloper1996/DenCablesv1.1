package com.capiyoo.dencables.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.Persistance.SharedPref;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.LinemanData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LineManActivity extends AppCompatActivity {

    DatabaseReference linemanDatabaseReb;
    ArrayList<LinemanData> linemanData;
    EditText username;
    EditText password;
    Button loginLineman;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_man);
        linemanData = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging in ");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginLineman = findViewById(R.id.loginLineman);
        loginLineman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLineman();
            }
        });
        linemanDatabaseReb = FirebaseDatabase.getInstance().getReference().child(Constants.LINEMAN_).child(new SharedPref(this).getFirebaseUid());

    }


    void loginLineman() {

        final String user = username.getText().toString();
        final String pass = password.getText().toString();

        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(pass)) {
            progressDialog.show();
            linemanDatabaseReb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            LinemanData linemanData = dataSnapshot1.getValue(LinemanData.class);
                            if (linemanData.getLinemanUniqueId().equals(user) && linemanData.getPassword().equals(pass)) {
                                new SharedPref(LineManActivity.this).setLinemanName(linemanData.getLinemanName());
                                new SharedPref(LineManActivity.this).setLinemanId(linemanData.getLinemanUniqueId());
                                new SharedPref(LineManActivity.this).setLinemanKey(dataSnapshot1.getKey());

                                startActivity(new Intent(LineManActivity.this, MainActivity.class));
                                return;
                            }

                        }
                        Toast.makeText(LineManActivity.this, "Wrong UserName or Password", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Empty Fields", Toast.LENGTH_LONG).show();
        }
    }
}
