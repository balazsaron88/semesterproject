package com.semesterproject.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListingActivity extends AppCompatActivity {

    TextView ename,eemail,eaddress;
    Button save,view, btnProfile;
    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Listdata> list;
    RecyclerView recyclerview;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(ListingActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        ename = (TextView) findViewById(R.id.etname);
        eemail = (TextView) findViewById(R.id.eemail);
        eaddress = (TextView) findViewById(R.id.eaddress);
        save = (Button) findViewById(R.id.save);
        view = (Button) findViewById(R.id.view);
        btnProfile = (Button) findViewById(R.id.profile);
        recyclerview = (RecyclerView) findViewById(R.id.rview);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("message");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String name =  ename.getText().toString();
                String email =  eemail.getText().toString();
                String address =  eaddress.getText().toString();


                String key =myRef.push().getKey();
                Userdetails userdetails = new Userdetails();

                userdetails.setName(name);
                userdetails.setEmail(email);
                userdetails.setAddress(address);

                myRef.child(key).setValue(userdetails);
                ename.setText("");
                eemail.setText("");
                eaddress.setText("");

            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListingActivity.this, MainActivity.class));
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        list = new ArrayList<>();
                       // StringBuffer stringbuffer = new StringBuffer();
                        for(DataSnapshot dataSnapshot1 :dataSnapshot.getChildren()){

                            Userdetails userdetails = dataSnapshot1.getValue(Userdetails.class);
                               Listdata listdata = new Listdata();
                               String name=userdetails.getName();
                               String email=userdetails.getEmail();
                               String address=userdetails.getAddress();
                              listdata.setName(name);
                             listdata.setEmail(email);
                              listdata.setAddress(address);
                              list.add(listdata);
                           // Toast.makeText(ListingActivity.this,""+name,Toast.LENGTH_LONG).show();

                        }

                        RecyclerviewAdapter recycler = new RecyclerviewAdapter(list);
                        RecyclerView.LayoutManager layoutmanager = new LinearLayoutManager(ListingActivity.this);
                        recyclerview.setLayoutManager(layoutmanager);
                        recyclerview.setItemAnimator( new DefaultItemAnimator());
                        recyclerview.setAdapter(recycler);

                  }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //  Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }
}
