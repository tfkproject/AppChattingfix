package com.example.zart.appchattingfix;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zart.appchattingfix.helper.StaticConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

import java.util.concurrent.TimeUnit;


public class Login extends AppCompatActivity implements View.OnClickListener{

    //variabel komponen yg diperlukan
    private EditText noTelpon, setkode;
    private Button masuk, verifikasi, resend;
    private TextView mundur;

    //variabel hitung mundur
    private CountDownTimer countDownTimer;
    private long maxstime = 60000; //1 menit


    public  void startTimer(){
        countDownTimer = new CountDownTimer(maxstime,1000) {
            @Override
            public void onTick(long l) {
                maxstime = l;
                updateTimer();
            }

            @Override
            public void onFinish() {
                mundur.setText("");
            }
        }.start();
    }

    public void stopTimer(){
        countDownTimer.cancel();
        countDownTimer.onFinish();
    }

    public void updateTimer(){
        int detik = (int) maxstime % 60000 / 1000;

        String timeleftText = "";

        if (detik <10) timeleftText += "0";
        timeleftText += detik;
        mundur.setText(timeleftText);
    }


    //variabel untuk autentikasi
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener stateListener;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private DatabaseReference mDatabase;
    private String VerifikasiID;
    private String No_Telepon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mundur = (TextView)findViewById(R.id.mundur);
        noTelpon = (EditText)findViewById(R.id.phone);
        setkode = (EditText)findViewById(R.id.ed_verify);
        masuk = (Button)findViewById(R.id.login);
        masuk.setOnClickListener(this);
        verifikasi = (Button)findViewById(R.id.verifi);
        verifikasi.setOnClickListener(this);
        resend = (Button)findViewById(R.id.resend);
        resend.setOnClickListener(this);
        resend.setEnabled(false);

        //menghubungkan project dgn firebase autentikasi
        auth = FirebaseAuth.getInstance();
        stateListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.e("User",""+user);

                if (user != null){

                    // User is signed in
                    StaticConfig.UID = user.getUid();
                    Log.d("Login", "onAuthStateChanged:signed_in:" + user.getUid());
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (stateListener != null){

            auth.removeAuthStateListener(stateListener);
        }
    }


    private void setupVerificationCallback(){
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Log.e("Token",""+verificationId + token);
                VerifikasiID = verificationId;
                resendingToken = token;
                resend.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Mendapatkan Kode Verifikasi", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(getApplicationContext(),"Verifikasi Selesai",Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
                stopTimer();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(getApplicationContext(), "Verifikasi Gagal, Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();
                Log.e("Error",""+e);
                stopTimer();
            }
        };
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential){
        Log.e("Credensial",""+ credential);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new  OnCompleteListener<AuthResult>() {
                    @Override
                    public  void onComplete(@NonNull Task<AuthResult> task){
                        if (task.isSuccessful()){
                            //sign in berhasil
                            String no = "+62"+noTelpon.getText().toString();

                            Bundle bundle = new Bundle();
                            bundle.putString("nohp",no);
                            Intent intent = new Intent(Login.this, Profile.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }else{
                            //sign in gagal
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                //kode tdak falid
                                Toast.makeText(getApplicationContext(), "Kode Yang Dimasukan Tidak valid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(),Login.class));
            finish();
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                No_Telepon = "+62"+noTelpon.getText().toString();
                startTimer();
                setupVerificationCallback();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        No_Telepon,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        callbacks);
                Toast.makeText(getApplicationContext(), "Memverifikasi, Mohon Tunggu", Toast.LENGTH_SHORT).show();
                break;

            case R.id.verifi:
                String verifiCode = setkode.getText().toString();
                if (TextUtils.isEmpty(verifiCode)) {
                    //memberi pesan kode verifikasi tidak boleh kosong
                    Toast.makeText(getApplicationContext(), "Masukan Kode Verifikasi", Toast.LENGTH_SHORT).show();
                }else{
                    //memverifikasi no telepon
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerifikasiID, verifiCode);
                    signInWithPhoneAuthCredential(credential);
                    Toast.makeText(getApplicationContext(), "Sedang Memverifikasi", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.resend:
                No_Telepon ="+62"+noTelpon.getText().toString();
                setupVerificationCallback();

                countDownTimer = new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long l) {
                        maxstime = l;
                        updateTimer();
                    }

                    @Override
                    public void onFinish() {
                        mundur.setText("");
                    }
                }.start();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        No_Telepon, //no telp di verifikasi
                        60, //durasi waktu
                        TimeUnit.SECONDS, //unit timeout
                        this, //activity
                        callbacks, //OnverificationStateChangedCallbacks
                        resendingToken); //mengir ulang kode verifikasi
                Toast.makeText(getApplicationContext(), "Mengiring Ulang kode Verifikasi", Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
