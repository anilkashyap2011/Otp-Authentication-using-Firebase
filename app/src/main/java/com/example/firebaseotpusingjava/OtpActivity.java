package com.example.firebaseotpusingjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    String phoneNumber;
    EditText otpInput;
    Button nextBtn;
    ProgressBar progressBar;
    TextView resendOtpTextView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static final long TIMER_INTERVAL_MS = 1000; // Timer interval in milliseconds
    private long timeOutInSeconds = 60L; // Initial timeout value
    private final Handler handler = new Handler();

    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendingTokenForcely;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phoneNumber = getIntent().getStringExtra("phone");

        otpInput = findViewById(R.id.otp_number);
        nextBtn = findViewById(R.id.nextBtn);
        progressBar = findViewById(R.id.progress_bar1);

        resendOtpTextView = findViewById(R.id.resend_otp_textview);

        sendOtp(phoneNumber, false);

        nextBtn.setOnClickListener(view -> {
            String enteredOtp = otpInput.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
            setInProgress(true);
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phoneNumber, true);
        });
    }

    void sendOtp(String phoneNumber, boolean isResend){
        setInProgress(true);
//        startResendTimer();
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(timeOutInSeconds, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toast.makeText(getApplicationContext(), "Otp verification failed", Toast.LENGTH_LONG)
                                .show();
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        forceResendingToken = resendingTokenForcely;
                        Toast.makeText(getApplicationContext(), "Otp OTP sent successfully", Toast.LENGTH_LONG)
                                .show();
                        setInProgress(false);
                    }
                });
        if (isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingTokenForcely).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }


    }// end of sendOTP () method

    // same code as below but using lambda expressions
    void signIn(PhoneAuthCredential phoneAuthCredential){
        //Login and go to next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Intent intent = new Intent(OtpActivity.this, UsernameActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Otp verification failed", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }// End of signIn() method

// same code as above for signIn() method but not using lambda expressions
//     void signIn(PhoneAuthCredential phoneAuthCredential){
//        setInProgress(true);
//        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if (task.isSuccessful()){
//                    Intent intent = new Intent(OtpActivity.this, UsernameActivity.class);
//                    intent.putExtra("phone", phoneNumber);
//                    startActivity(intent);
//                }else{
//                    Toast.makeText(getApplicationContext(), "Otp verification failed", Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        });
//    }// End of signIn() method

    /*Code for Progress bar*/
    void setInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.GONE);
        } else{
            progressBar.setVisibility(View.VISIBLE);
        }
    }// End of setInProgress()

    /* Code for resending an Otp*/

    private void startResendTimer() {
        resendOtpTextView.setEnabled(false);

        final Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                timeOutInSeconds--;
                if (timeOutInSeconds > 0) {
                    resendOtpTextView.setText("Resend Otp in " + timeOutInSeconds + " seconds");
                    handler.postDelayed(this, TIMER_INTERVAL_MS);
                } else {
                    timeOutInSeconds = 60L;
                    resendOtpTextView.setEnabled(true);
                }
            }
        };

        handler.post(timerRunnable);
    }
/*Old code for startResendTimer() method above one is the new code
    private void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeOutInSeconds--;
                resendOtpTextView.setText("Resend Otp in"+timeOutInSeconds+" seconds");
                if (timeOutInSeconds<=0){
                    timeOutInSeconds = 60L;
                    timer.cancel();83
                    runOnUiThread(() -> {
                        resendOtpTextView.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }
//////////////////////////////////////
 *///Comment

}