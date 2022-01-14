package com.ramgdeveloper.taka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramgdeveloper.taka.databinding.ActivityLoginBinding;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("all_details");

        binding.signinButton.setOnClickListener(v -> {
            String mail = Objects.requireNonNull(binding.emailEditText.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(binding.passwordEditText.getEditText()).getText().toString().trim();

            if (TextUtils.isEmpty(binding.emailEditText.getEditText().getText().toString())) {
                binding.emailEditText.setError("Field can't be empty!!");
                return;
            } else if (TextUtils.isEmpty(binding.passwordEditText.getEditText().getText().toString())) {
                binding.passwordEditText.setError("Field can't be empty!!");
                return;
            } else if (!(CheckInternet.isConnected(getApplicationContext()))) {
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else {
                binding.loginProgressBar.setVisibility(View.VISIBLE);
                binding.emailEditText.setEnabled(false);
                binding.passwordEditText.setEnabled(false);
                binding.signinButton.setEnabled(false);
            }
            userLogin(mail, password);
        });

        binding.dontHaveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        binding.forgotPass.setOnClickListener(v -> {
            ForgotPasswordDialog dialog = new ForgotPasswordDialog();
            dialog.show(getSupportFragmentManager(), "string");
        });
    }

    private void userLogin(String mail, String password) {
        firebaseAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {

                    databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                String userType = Objects.requireNonNull(snapshot.child("accountType").getValue()).toString();

                                if (userType.equals("Agent")) {
                                    Intent intent = new Intent(LoginActivity.this, AllWastesActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if (userType.equals("Client")) {
                                    Intent intent = new Intent(LoginActivity.this, WasteActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error in determining account Type", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "No account Type", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                    Toast.makeText(getApplicationContext(), "Welcome to Usafi", Toast.LENGTH_SHORT).show();
                    binding.loginProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    binding.loginProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getApplicationContext(), "Verify you email first", Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(binding.emailEditText.getEditText()).setText("");
                    Objects.requireNonNull(binding.passwordEditText.getEditText()).setText("");
                    binding.emailEditText.setEnabled(true);
                    binding.passwordEditText.setEnabled(true);
                    binding.signinButton.setEnabled(true);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Something Went Wrong.\n please check your details and try again", Toast.LENGTH_SHORT).show();
                binding.loginProgressBar.setVisibility(View.INVISIBLE);
                binding.emailEditText.setEnabled(true);
                binding.passwordEditText.setEnabled(true);
                binding.signinButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}