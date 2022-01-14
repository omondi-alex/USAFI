package com.ramgdeveloper.taka;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordDialog extends DialogFragment {
    private static final String TAG = "PasswordResetDialog";
    private EditText mEmail;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_resetpassword, container, false);
        mEmail = view.findViewById(R.id.email_password_reset);
        mContext = getActivity();
        TextView confirmDialog = view.findViewById(R.id.dialogConfirm);

        confirmDialog.setOnClickListener(v -> {
            if (!isEmpty(mEmail.getText().toString())) {
                Log.d(TAG, "onClick: attempting to send reset link to: " + mEmail.getText().toString());
                sendPasswordResetEmail(mEmail.getText().toString());
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });
        return view;
    }

    public void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnCompleteListener(v -> {
                    if (v.isSuccessful()) {
                        Log.d(TAG, "onComplete: Password Reset Email sent.");
                        Toast.makeText(mContext, "Password Reset Link Sent to Email", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onComplete: No user associated with that email.");
                        Toast.makeText(mContext, "No User is Associated with that Email", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isEmpty(String string) {
        return string.equals("");
    }
}
