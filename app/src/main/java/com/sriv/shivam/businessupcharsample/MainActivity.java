package com.sriv.shivam.businessupcharsample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sriv.shivam.businessupcharsample.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    FirebaseAuth mAuth;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.buttonSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.editTextPhone.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter mobile details", Toast.LENGTH_SHORT).show();
                }
                else {
                    String phone = "+91" + binding.editTextPhone.getText().toString();
                    startPhoneAuth(phone);
                }
            }
        });
    }

    private void startPhoneAuth(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(MainActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();

                                binding.buttonSendOtp.setVisibility(View.VISIBLE);
                                binding.editTextPhone.setVisibility(View.VISIBLE);
                                binding.editTextNumber.setVisibility(View.GONE);
                                binding.buttonVerifyOtp.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = s;

                                binding.buttonSendOtp.setVisibility(View.GONE);
                                binding.editTextPhone.setVisibility(View.GONE);
                                binding.editTextNumber.setVisibility(View.VISIBLE);
                                binding.buttonVerifyOtp.setVisibility(View.VISIBLE);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        binding.buttonVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.editTextNumber.getText().toString().trim().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                }
                else {
                    String otp = binding.editTextNumber.getText().toString();

                    if(verificationId != null) {
                        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                                verificationId,
                                otp
                        );

                        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        binding.buttonSendOtp.setVisibility(View.VISIBLE);
                                        binding.editTextPhone.setVisibility(View.VISIBLE);
                                        binding.editTextNumber.setVisibility(View.GONE);
                                        binding.buttonVerifyOtp.setVisibility(View.GONE);

                                        if(task.isSuccessful()) {
                                            binding.textView.setText("Authenticated");
                                        }
                                        else {
                                            Toast.makeText(MainActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }
}
