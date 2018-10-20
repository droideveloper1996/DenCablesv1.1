package com.capiyoo.dencables.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capiyoo.dencables.Constants.Constants;
import com.capiyoo.dencables.Persistance.SharedPref;
import com.capiyoo.dencables.R;
import com.capiyoo.dencables.Utilities.LinemanData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Lineman extends Fragment {

    TextView LinemanName;
    TextView LinemanCId;
    TextView LinemanContact;
    TextView LinemanAddress;
    SharedPref sharedPref;
    DatabaseReference linemanDbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_detail_fragment, container, false);
        LinemanName = view.findViewById(R.id.linemanActive);
        sharedPref = new SharedPref(getContext());
        LinemanCId = view.findViewById(R.id.linemanId);
        LinemanContact = view.findViewById(R.id.linemanContact);
        LinemanAddress = view.findViewById(R.id.linemanAddress);
        linemanDbRef = FirebaseDatabase.getInstance().getReference().child(Constants.LINEMAN_).child(sharedPref.getFirebaseUid());
        getLinemanDetail();

        return view;
    }


    void getLinemanDetail() {

        if (sharedPref.getLinemanKey() != null) {
            linemanDbRef.child(sharedPref.getLinemanKey()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {

                        LinemanData linemanData = dataSnapshot.getValue(LinemanData.class);

                        try {
                            LinemanName.setText(linemanData.getLinemanName());
                            LinemanCId.setText(linemanData.getLinemanUniqueId());
                            LinemanContact.setText(linemanData.getLinemanContact());
                            LinemanAddress.setText(linemanData.getLinemanAddress());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            //Lineman Not Logged In
            LinemanName.setText("Lineman Not Logged in");
        }
    }

}
