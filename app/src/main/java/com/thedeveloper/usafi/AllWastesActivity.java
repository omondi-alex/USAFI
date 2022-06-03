package com.ramgdeveloper.taka;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ramgdeveloper.taka.databinding.ActivityAllWastesBinding;

import java.util.Objects;

public class AllWastesActivity extends AppCompatActivity {

    ActivityAllWastesBinding binding;
    AllWastesRecyclerAdapter adapter;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllWastesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Usafi Agent");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        key = databaseReference.getKey();

        initializeRecycler();
    }

    private void initializeRecycler() {

        Query query = databaseReference.child("all_wastes");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    binding.progressBar.setVisibility(View.INVISIBLE);
                } else {
                    binding.imageViewNoData.setVisibility(View.VISIBLE);
                    binding.textViewNoData.setVisibility(View.VISIBLE);
                    binding.progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseRecyclerOptions<Waste> options = new FirebaseRecyclerOptions.Builder<Waste>()
                .setQuery(query, Waste.class)
                .build();

        adapter = new AllWastesRecyclerAdapter(options);
        binding.allWasteRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(AllWastesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}