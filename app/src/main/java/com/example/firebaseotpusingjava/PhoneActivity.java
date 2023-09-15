package com.example.firebaseotpusingjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class PhoneActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sentOtp_Btn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        countryCodePicker = findViewById(R.id.countryCode);
        phoneInput = findViewById(R.id.phoneNumber);
        sentOtp_Btn = findViewById(R.id.sendBtn);
        progressBar = findViewById(R.id.progress_bar);

        progressBar.setVisibility(View.GONE);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sentOtp_Btn.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Kindly provide complete 10 digit Mobile Number");
                return;
            }
            Intent intent = new Intent(PhoneActivity.this, OtpActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);
        });

    }
}