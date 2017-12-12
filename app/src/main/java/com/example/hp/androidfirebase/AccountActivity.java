package com.example.hp.androidfirebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class AccountActivity extends AppCompatActivity {

    private Button btnLogout, btnUpdate;
    private EditText etName, etSurname;
    private TextView textView0;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String everyoneName = " ";
    private String uid;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        textView0 = (TextView)findViewById(R.id.textView0);
        etName = (EditText)findViewById(R.id.etName);
        etSurname = (EditText)findViewById(R.id.etSurname);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Profile");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid().toString();

        //------------------------------------------//

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    startActivity(new Intent(AccountActivity.this, MainActivity.class));
                }
            }
        };

        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etName.getText().toString();
                String surname = etSurname.getText().toString();
                myRef.child(uid).child("Name").setValue(name);
                myRef.child(uid).child("Surname").setValue(surname);
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                everyoneName = " ";
                for(DataSnapshot childData : dataSnapshot.getChildren()){
                    everyoneName += childData.child("Name").getValue().toString() + " " + childData.child("Surname").getValue().toString() + "\n";
                }
                textView0.setText(everyoneName);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("B","Failed to read value.", error.toException());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
}
