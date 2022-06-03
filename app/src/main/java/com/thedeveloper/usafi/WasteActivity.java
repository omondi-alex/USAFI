package com.ramgdeveloper.taka;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.androidstudy.daraja.Daraja;
import com.androidstudy.daraja.DarajaListener;
import com.androidstudy.daraja.model.AccessToken;
import com.androidstudy.daraja.model.LNMExpress;
import com.androidstudy.daraja.model.LNMResult;
import com.androidstudy.daraja.util.TransactionType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ramgdeveloper.taka.databinding.ActivityWasteBinding;

import java.util.Calendar;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class WasteActivity extends AppCompatActivity {

    ActivityWasteBinding binding;
    String wasteType;
    String paymentType = "Cash";
    private static final String TAG = "WasteActivity";
    Daraja daraja;
    DatabaseReference databaseReference;
    DatabaseReference wastesDatabaseReference;
    FirebaseAuth firebaseAuth;
    String phoneNum, phone;
    String location;
    String clientName;
    SweetAlertDialog loading;
    SweetAlertDialog success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWasteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Usafi Client");

        loading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        loading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        loading.setTitleText("Loading...");
        loading.setCancelable(false);

        success = new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE);
        success.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        success.setTitleText("Successfully requested for garbage collection");
        success.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                success.dismiss();
            }
        });
        success.setCancelable(false);

        if (paymentType.equals("Cash")) {
            binding.phoneNumEdtx.setVisibility(View.GONE);
        }

        // Firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("all_details");
        getCurrentUserDetails();

        //Init Daraja for M-pesa payments
        daraja = Daraja.with("fkgASeWSL1NP3aEj07CLuAjuP9TQez1S", "c6RtBlH0cHiwImCD", new DarajaListener<AccessToken>() {
            @Override
            public void onResult(@NonNull AccessToken accessToken) {
                //Log.i(WasteActivity.this.getClass().getSimpleName(), accessToken.getAccess_token());
                Toast.makeText(WasteActivity.this, "TOKEN : " + accessToken.getAccess_token(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Log.e(WasteActivity.this.getClass().getSimpleName(), error);
                Toast.makeText(WasteActivity.this, "Error ", Toast.LENGTH_SHORT).show();
            }
        });

        binding.wasteType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int radioButtonID = binding.wasteType.getCheckedRadioButtonId();
                View radioButton = binding.wasteType.findViewById(radioButtonID);
                int idx = binding.wasteType.indexOfChild(radioButton);
                RadioButton r = (RadioButton) binding.wasteType.getChildAt(idx);
                wasteType = r.getText().toString();
            }
        });

        binding.paymentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int radioButtonID = binding.paymentType.getCheckedRadioButtonId();
                View radioButton = binding.paymentType.findViewById(radioButtonID);
                int idx = binding.paymentType.indexOfChild(radioButton);
                RadioButton r = (RadioButton) binding.paymentType.getChildAt(idx);
                paymentType = r.getText().toString();

                if (paymentType.equals("M-Pesa")) {
                    binding.phoneNumEdtx.setVisibility(View.VISIBLE);
                } else if (paymentType.equals("Cash")) {
                    binding.phoneNumEdtx.setVisibility(View.GONE);
                }
            }
        });

        binding.buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(WasteActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure you want to request garbage collection?")
                        .setCancelText("No")
                        .setConfirmText("Yes")
                        .showCancelButton(true)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                loading.show();

                                if (paymentType.equals("Cash")) {
                                    pushWasteDetailsToFirebase(false, phone);
                                } else if (paymentType.equals("M-Pesa")) {
                                    loading.show();
                                    if (wasteType == null) {
                                        Toast.makeText(WasteActivity.this, "Please select Waste Type", Toast.LENGTH_SHORT).show();
                                    } else if (paymentType == null) {
                                        Toast.makeText(WasteActivity.this, "Payment Type is compulsory", Toast.LENGTH_SHORT).show();
                                    }

                                    phoneNum = binding.phoneNumEdtx.getText().toString();
                                    if (binding.phoneNumEdtx.getText().toString().isEmpty()) {
                                        binding.phoneNumEdtx.setError("Phone Number is required");
                                        return;
                                    }
                                    LNMExpress lnmExpress = new LNMExpress(
                                            "174379",
                                            "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919",  //https://developer.safaricom.co.ke/test_credentials
                                            TransactionType.CustomerPayBillOnline,
                                            "1",
                                            "254742002867",
                                            "174379",
                                            phoneNum,
                                            "https://usafi-48370-default-rtdb.firebaseio.com/",
                                            "001ABC",
                                            "Paybill option"
                                    );

                                    daraja.requestMPESAExpress(lnmExpress,
                                            new DarajaListener<LNMResult>() {
                                                @Override
                                                public void onResult(@NonNull LNMResult lnmResult) {
                                                    Log.i(WasteActivity.this.getClass().getSimpleName(), lnmResult.ResponseDescription);
                                                    pushWasteDetailsToFirebase(true, phoneNum);
                                                    success.dismissWithAnimation();
                                                    loading.dismissWithAnimation();
                                                    binding.phoneNumEdtx.setText("");
                                                    binding.phoneNumEdtx.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Log.i(WasteActivity.this.getClass().getSimpleName(), error);
                                                }
                                            }
                                    );
                                }
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    private void getCurrentUserDetails() {
        databaseReference.child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    phone = Objects.requireNonNull(snapshot.child("phoneNumber").getValue()).toString();
                    clientName = Objects.requireNonNull(snapshot.child("fullName").getValue()).toString();
                    location = Objects.requireNonNull(snapshot.child("location").getValue()).toString();
                    Log.d(TAG, "onDataChange: " + clientName);

                } else {
                    Toast.makeText(WasteActivity.this, "It seems we dont have your phone number", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void pushWasteDetailsToFirebase(Boolean payed, String phonr) {
        String date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        Waste waste = new Waste(wasteType, date, clientName, location, phonr, payed);
        wastesDatabaseReference = FirebaseDatabase.getInstance().getReference("all_wastes");
        wastesDatabaseReference.push().setValue(waste);
        loading.dismiss();
        success.show();
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
            Intent intent = new Intent(WasteActivity.this, LoginActivity.class);
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