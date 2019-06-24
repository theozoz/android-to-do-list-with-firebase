package com.example.dodo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText mPhoneNumber,mCode;
    private Button mSend,btn_setSignWithEmail,LoginWithEmail_btn,loginWithPhoneBtn,register_btn;
    ProgressDialog mloading;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String mVerificationId;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        userIsLoggedIn();


        mPhoneNumber=findViewById(R.id.phoneNumber);
        mCode=findViewById(R.id.code);
        mSend=findViewById(R.id.send_btn);
        btn_setSignWithEmail=findViewById(R.id.setSignignWithEmail);
        loginWithPhoneBtn=findViewById(R.id.loginWithPhone);
        register_btn=findViewById(R.id.logint_to_register_btn);
        LoginWithEmail_btn=findViewById(R.id.SignWithEmail);
        mloading=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        RootRef=FirebaseDatabase.getInstance().getReference();

        final String phonem=mPhoneNumber.getText().toString();

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    if (mVerificationId!=null)
                    {
                        verifiPhoneNumberWithCode();
                    }
                    else
                    {
                        startPhoneNumberVerification();
                    }
            }
        });



        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
            {
                singInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Toast.makeText(LoginActivity.this, "Geçersiz Numara. Lütfen Numaranızı kontrol ediniz", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId=verificationId;
                mSend.setText("kodu doğrula");
            }
        };

        btn_setSignWithEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { EmailSetLogin(); }});

        LoginWithEmail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { LoginWithEmail(); }});

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { SendUserToRegisterActivity(); }});


        loginWithPhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhoneNumber.setHint("Telefon numarası");
                mPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
                mPhoneNumber.setText("");
                mCode.setHint("Kodu gir");
                mCode.setText("");
                mCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
                mSend.setVisibility(View.INVISIBLE);
                loginWithPhoneBtn.setVisibility(View.INVISIBLE);
                mSend.setVisibility(View.VISIBLE);
                LoginWithEmail_btn.setVisibility(View.INVISIBLE);
                btn_setSignWithEmail.setVisibility(View.VISIBLE);
            }
        });
    }


    private void LoginWithEmail()
    {
        String email=mPhoneNumber.getText().toString();
        String password=mCode.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Lütfen Email alanını doldurunuz", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Lütfen Şifre alanını doldurunuz", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mloading.setTitle("Kod Doğrulaması");
            mloading.setMessage("Lürfen bekleyiniz, Kod doğrulaması yapılıyor....");
            mloading.setCanceledOnTouchOutside(false);
            mloading.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();

                    }
                    else
                    {
                        String hata=task.getException().toString();
                        Toast.makeText(LoginActivity.this, "Hata: "+hata, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    private void SendUserToMainActivity()
    {
        Intent LoginToMainIntent=new Intent(LoginActivity.this,MainActivity.class);
        LoginToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginToMainIntent);
        finish();
    }

private void SendUserToRegisterActivity()
    {
        Intent RegisterIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        RegisterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(RegisterIntent);
        finish();
    }

    private void EmailSetLogin()
    {
        mPhoneNumber.setHint("Email Adresi...");
        mPhoneNumber.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        mPhoneNumber.setText("");
        mCode.setText("");
        mCode.setHint("Şifre....");
        mCode.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mSend.setVisibility(View.INVISIBLE);
        loginWithPhoneBtn.setVisibility(View.VISIBLE);
        btn_setSignWithEmail.setVisibility(View.INVISIBLE);
        LoginWithEmail_btn.setVisibility( View.VISIBLE);
    }

    private void verifiPhoneNumberWithCode()
    {
        mloading.setTitle("Kod Doğrulaması");
        mloading.setMessage("Lürfen bekleyiniz, Kod doğrulaması yapılıyor....");
        mloading.setCanceledOnTouchOutside(false);
        mloading.show();
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,mCode.getText().toString());
        singInWithPhoneAuthCredential(credential);
    }

    private void singInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential)
    {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {
                    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                    if (user!=null)
                    {
                        final DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("Phone_Users").child(user.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists())
                                {
                                    Map<String,Object> userMap=new HashMap<>();
                                    userMap.put("phone",user.getPhoneNumber());
                                    userMap.put("name",user.getDisplayName());
                                    mUserDB.updateChildren(userMap);

                                    final DatabaseReference RootRef=FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
                                    RootRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(LoginActivity.this, user.getDisplayName()+" Hoşgeldin", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }
                               userIsLoggedIn();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }


            }
        });
    }

    private void userIsLoggedIn()
    {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mPhoneNumber.getText().toString(),
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }
}
