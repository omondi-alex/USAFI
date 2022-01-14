package com.ramgdeveloper.taka;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ramgdeveloper.taka.databinding.ActivityRegisterBinding;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String userID;
    ActivityRegisterBinding binding;
    String regType = "Client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        firebaseAuth = FirebaseAuth.getInstance();

        if (regType.equals("Client")) {
            binding.agentId.setVisibility(View.GONE);
        }

        binding.registerType.setOnCheckedChangeListener((group, checkedId) -> {

            int radioButtonID = binding.registerType.getCheckedRadioButtonId();
            View radioButton = binding.registerType.findViewById(radioButtonID);
            int idx = binding.registerType.indexOfChild(radioButton);
            RadioButton r = (RadioButton) binding.registerType.getChildAt(idx);
            regType = r.getText().toString();

            if (regType.equals("Client")) {
                binding.agentId.setVisibility(View.GONE);
            } else if (regType.equals("Agent")) {
                binding.agentId.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "onCheckedChanged: " + regType);
        });

        binding.registerButton.setOnClickListener(v -> {

            Log.d(TAG, "onClick: " + regType);

            String mail = Objects.requireNonNull(binding.enterEmail.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(binding.enterPassword.getEditText()).getText().toString().trim();
            String agentId = Objects.requireNonNull(binding.agentId.getEditText()).getText().toString().trim();
            String fullNames = Objects.requireNonNull(binding.fullName.getEditText()).getText().toString().trim();
            String phoneNum = Objects.requireNonNull(binding.phoneNumber.getEditText()).getText().toString().trim();
            String location = Objects.requireNonNull(binding.location.getEditText()).getText().toString().trim();


            if (TextUtils.isEmpty(binding.enterEmail.getEditText().getText().toString())) {
                binding.enterEmail.setError("Field can't be empty!!");
            } else if (TextUtils.isEmpty(binding.enterPassword.getEditText().getText().toString())) {
                binding.enterPassword.setError("Field can't be empty!!");
            } else if (TextUtils.isEmpty(binding.fullName.getEditText().getText().toString())) {
                binding.fullName.setError("Field can't be empty!!");
            } else if (TextUtils.isEmpty(binding.location.getEditText().getText().toString())) {
                binding.location.setError("Field can't be empty!!");
            } else if (TextUtils.isEmpty(binding.phoneNumber.getEditText().getText().toString())) {
                binding.phoneNumber.setError("Field can't be empty!!");
            } else if (!isvalidemail(binding.enterEmail.getEditText().getText().toString())) {
                binding.enterEmail.setError("Invalid Email!");
            } else if (!(CheckInternet.isConnected(getApplicationContext()))) {
                Toast.makeText(RegisterActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            } else {
                createUserAccount(agentId, mail, password, fullNames, location, phoneNum);
            }
        });

        binding.haveAnAccount.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void createUserAccount(String agentId, String email, String password, String names, String location, String phone) {
        binding.registerProgressBar.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //sending verification email
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                firebaseUser.sendEmailVerification().addOnSuccessListener(aVoid -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Check email for verification link", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show());
                //
                userID = firebaseAuth.getUid();

                if (regType.equals("Client")) {
                    saveClientDetails(email, names, location, phone, regType);

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    binding.registerProgressBar.setVisibility(View.INVISIBLE);

                } else if (regType.equals("Agent")) {
                    saveAgentDetails(agentId, email, names, location, phone, regType);

                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    binding.registerProgressBar.setVisibility(View.INVISIBLE);

                }


            } else {
                if (!(CheckInternet.isConnected(getApplicationContext()))) {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                binding.registerProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private boolean isvalidemail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void saveClientDetails(String email, String names, String location, String phone, String accountType) {
        databaseReference = FirebaseDatabase.getInstance().getReference("all_details");
        User user = new User(email, names, location, phone, accountType);
        databaseReference.child(userID).setValue(user);
    }

    private void saveAgentDetails(String agentNum, String email, String names, String location, String phone, String accountType) {
        databaseReference = FirebaseDatabase.getInstance().getReference("all_details");
        User user = new User(agentNum, email, names, location, phone, accountType);
        databaseReference.child(userID).setValue(user);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}