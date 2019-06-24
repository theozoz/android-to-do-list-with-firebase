package com.example.dodo;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;


public class MainActivity extends AppCompatActivity {

    private ImageView mLogout,mDoes,Rehber,mUsers,Topluluk,Ayalar,Mesaj,Takvim;
    private TextView mUsername,mUserStatus;
    private Uri imageUri=null;
    private CircularImageView mainProfileImage;

    private FirebaseAuth mAuth;
    private FirebaseUser currenUser;
    private DatabaseReference RootRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currenUser=mAuth.getCurrentUser();
        currentUserID=currenUser.getUid();
        RootRef=FirebaseDatabase.getInstance().getReference();

        InitializeFields();
        RetrieveUserInfo();

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { FirebaseAuth.getInstance().signOut();SendUserToLoginActivity();return; }});

        mDoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToDoesActivity();
            }
        });
        Takvim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToTakvimActivity();
            }
        });
        Mesaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToMesajActivity();
            }
        });
        mUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToUsersActivity();
            }
        });
        Rehber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRehberActivity();
            }
        });
        Topluluk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToToplulukActivity();
            }
        });
        Ayalar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToAyarlarActivity();
            }
        });

        getPermissions();
    }

    private void InitializeFields()
    {
        mLogout=findViewById(R.id.logout);
        mDoes=findViewById(R.id.gorevler);
        Rehber=findViewById(R.id.rehber);
        mUsers=findViewById(R.id.kisiler);
        Topluluk=findViewById(R.id.toluluk);
        Ayalar=findViewById(R.id.ayarlar);
        Mesaj=findViewById(R.id.mesajlar);
        Takvim=findViewById(R.id.takvim);
        mainProfileImage=findViewById(R.id.main_profile_image);

        mUsername=findViewById(R.id.muserName);
        mUserStatus=findViewById(R.id.muserStatus);
    }

    private void getPermissions()
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_CONTACTS},1);
        }
    }

    private void SendUserToDoesActivity()
    {
        Intent DoesIntent=new Intent(MainActivity.this,DoesActivity.class);
        startActivity(DoesIntent);

    }
    private void SendUserToTakvimActivity()
    {
        Intent TakvimIntent=new Intent(MainActivity.this,TakvimActivity.class);
        startActivity(TakvimIntent);

    }
    private void SendUserToMesajActivity()
    {
        Intent MesajIntent=new Intent(MainActivity.this,MesajActivity.class);
        startActivity(MesajIntent);

    }
    private void SendUserToUsersActivity()
    {
        Intent UsersIntent=new Intent(MainActivity.this,KisilerActivity.class);
        startActivity(UsersIntent);

    }
    private void SendUserToRehberActivity()
    {
        Intent RehberIntent=new Intent(MainActivity.this,RehberActivity.class);
        startActivity(RehberIntent);

    }
    private void SendUserToToplulukActivity()
    {
        Intent TolulukIntent=new Intent(MainActivity.this,ToplulukActivity.class);
        startActivity(TolulukIntent);

    }
    private void SendUserToAyarlarActivity()
    {
        Intent AyarlarIntent=new Intent(MainActivity.this,AyarlarActivity.class);
        startActivity(AyarlarIntent);

    }
    private void SendUserToLoginActivity()
    {
        Intent LoginIntent=new Intent(MainActivity.this,LoginActivity.class);
        LoginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(LoginIntent);
        finish();
    }


    private void RetrieveUserInfo()
    {
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))&& (dataSnapshot.hasChild("status")) && (dataSnapshot.hasChild("image")))
                {
                    String RetrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String RetrieveStatus=dataSnapshot.child("status").getValue().toString();
                    String RetrieveImage=dataSnapshot.child("image").getValue().toString();
                    imageUri=Uri.parse(RetrieveImage);

                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_profile);

                    mUsername.setText(RetrieveUserName);
                    mUserStatus.setText(RetrieveStatus);
                    Glide.with(MainActivity.this).setDefaultRequestOptions(requestOptions).load(imageUri).into(mainProfileImage);
                }
                 else if ((dataSnapshot.exists())&& (dataSnapshot.hasChild("status")) && (dataSnapshot.hasChild("name")))
                {
                    String RetrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String RetrieveStatus=dataSnapshot.child("status").getValue().toString();

                    mUsername.setText(RetrieveUserName);
                    mUserStatus.setText(RetrieveStatus);
                }



                else
                {
                    //userName.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "İsim ve Durum alınamıyoumıyor...Ayarlara gidin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
