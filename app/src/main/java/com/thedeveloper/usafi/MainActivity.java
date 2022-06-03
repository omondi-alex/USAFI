package com.ramgdeveloper.taka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("all_details");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if (mFirebaseUser != null) {

                databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();

                            if (userType.equals("Agent")) {
                                Intent intent = new Intent(MainActivity.this, AllWastesActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (userType.equals("Client")) {
                                Intent intent = new Intent(MainActivity.this, WasteActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error in determining account Type", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(MainActivity.this, "No account Type", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}