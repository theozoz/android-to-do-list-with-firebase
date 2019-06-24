package com.example.dodo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button toLogin,msubmit;
    private EditText  UserMail,UserPassword,UserName;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,sRootRef;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toLogin=findViewById(R.id.toLogin);
        msubmit=findViewById(R.id.register_btn);
        UserMail=findViewById(R.id.register_Email);
        UserPassword=findViewById(R.id.register_password);
        UserName=findViewById(R.id.register_userName);

        mDialog=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();

        msubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAcount();
            }
        });


        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    private void CreateAcount() {


        final String name=UserName.getText().toString();
        final String email=UserMail.getText().toString();
        final String password=UserPassword.getText().toString();
        int uzunluk=password.length();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Lütfen gerkli alanı doldurunuz", Toast.LENGTH_SHORT).show();
        }
        if (uzunluk<6)
        {
            Toast.makeText(this, "Şifre en az 6 haneli olmalı", Toast.LENGTH_SHORT).show();
        }
            else
        {
            mDialog.setTitle("Kayıt yapılıyor");
            mDialog.setMessage("Lütfen Bekleyiniz");
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
            //mAuth.getCurrentUser().sendEmailVerification();
            //if(!mAuth.getCurrentUser().isEmailVerified()) bir tane daha send; else continue
            //mAuth.sendPasswordResetEmail(email)

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful())
                    {
                        HashMap<String,String> RegisterMap=new HashMap<>();
                        RegisterMap.put("name",name);
                        RegisterMap.put("email",email);
                        RegisterMap.put("password",password);
                        RegisterMap.put("status","");

                        String currentUserID=mAuth.getCurrentUser().getUid();
                        sRootRef=RootRef.child("Users").child(currentUserID);
                        sRootRef.setValue(RegisterMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(RegisterActivity.this, name+ " Kaydın başarılı", Toast.LENGTH_SHORT).show();
                            }
                        });
                        SendUserToAyarlarActivity();
                        mDialog.dismiss();
                    }
                    else
                    {
                        String hata=task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Hata: "+hata, Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }

                }
            });

        }

    }

    private void SendUserToMainActivity() {

        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    private void SendUserToLoginActivity() {

        Intent LoginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }

    private void SendUserToAyarlarActivity()
    {
        Intent AyarlarIntent=new Intent(RegisterActivity.this,AyarlarActivity.class);
        AyarlarIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(AyarlarIntent);
        finish();

    }

}
