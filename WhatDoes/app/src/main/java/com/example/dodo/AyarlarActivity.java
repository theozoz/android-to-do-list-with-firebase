package com.example.dodo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class AyarlarActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userName,userStatus;
    private Button settingSavebtn;
    private CircularImageView SetProfileImage;
    private Uri ImageUri=null;
    private Button setting_add_btn;
    private Animation sAnimation,sbackanim;
    private int i=0;
    private static final int GalleryPick=1;

    private DatabaseReference RootRef,mRootRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private FirebaseUser currentUser;
    private StorageReference UserProfileImageRef;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayarlar);

        InitializeFields();
        PropertiesImageView();
        mToolbar=findViewById(R.id.ayarlar_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Ayarlar");

        //---------------------------------------
        RootRef=FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();
        currentUserID=currentUser.getUid();
        UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");
        mDialog=new ProgressDialog(this);
        //-------------------------------------

        SetProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i%2==0)
                {
                    getAnimation();
                }
                else
                {
                      getBackAnimation();
                }
                i++;
                if
                (i==8)
                {
                    i=0;
                }
            }
        });


        settingSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDataToFireBase();
            }
        });

        setting_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GalleryPick);
            }
        });

        RetrieveUserInfo();

    }

    private void PropertiesImageView()
    {
        SetProfileImage= findViewById(R.id.s_profile_image);
        SetProfileImage.setBorderColor(getResources().getColor(R.color.GrayLight));
        SetProfileImage.setBorderWidth(10);
// Add Shadow with default param
        SetProfileImage.addShadow();
// or with custom param
        SetProfileImage.setShadowRadius(15);
        SetProfileImage.setShadowColor(Color.RED);
        SetProfileImage.setBackgroundColor(Color.RED);
        SetProfileImage.setShadowGravity(CircularImageView.ShadowGravity.CENTER);
    }
    private void getBackAnimation()
    {
        setting_add_btn.setAlpha(1);
        setting_add_btn.startAnimation(sbackanim);
        setting_add_btn.setVisibility(View.INVISIBLE);
    }
    private void getAnimation()
    {
        setting_add_btn.setAlpha(1);
        setting_add_btn.startAnimation(sAnimation);
        setting_add_btn.setVisibility(View.VISIBLE);
    }
    private void InitializeFields()
    {
        userName=findViewById(R.id.setting_name);
        userStatus=findViewById(R.id.setting_status);
        settingSavebtn=findViewById(R.id.setting_save);

        setting_add_btn=findViewById(R.id.setting_add_image);

        sAnimation= AnimationUtils.loadAnimation(this,R.anim.setting_anim);
        sbackanim= AnimationUtils.loadAnimation(this,R.anim.setting_anim_back);
        setting_add_btn.setAlpha(0);
    }
    private void SendUserToMainActivity()
    {
        Intent AyarlarToMainIntent=new Intent(AyarlarActivity.this, MainActivity.class);
        AyarlarToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(AyarlarToMainIntent);
        finish();
    }
    private void RetrieveUserInfo()
    {

        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("status"))) {

                    String RetrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String RetrieveStatus = dataSnapshot.child("status").getValue().toString();

                    userName.setText(RetrieveUserName);
                    userStatus.setText(RetrieveStatus);
                }
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("status")) && (dataSnapshot.hasChild("image"))) {

                    String RetrieveUserName = dataSnapshot.child("name").getValue().toString();
                    String RetrieveStatus = dataSnapshot.child("status").getValue().toString();
                    String RetrieveImage = dataSnapshot.child("image").getValue().toString();

                    ImageUri = Uri.parse(RetrieveImage);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_profile);

                    Glide.with(AyarlarActivity.this).setDefaultRequestOptions(requestOptions).load(ImageUri).into(SetProfileImage);

                    userName.setText(RetrieveUserName);
                    userStatus.setText(RetrieveStatus);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void setDataToFireBase()
    {
        String setUserName=userName.getText().toString();
        String setStatus=userStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "İsim alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(setStatus))
        {
            Toast.makeText(this, "Durum alanı boş bırakılamaz", Toast.LENGTH_SHORT).show();
        }
        else
        {

            HashMap SettingProfileMap=new HashMap<>();
            SettingProfileMap.put("uid",currentUserID);
            SettingProfileMap.put("name",setUserName);
            SettingProfileMap.put("status",setStatus);



            RootRef.child("Users").child(currentUserID).updateChildren(SettingProfileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if (task.isSuccessful())
                    {
                        SendUserToMainActivity();
                        Toast.makeText(AyarlarActivity.this, "Profil kaydı başarılı", Toast.LENGTH_SHORT).show();

                    }
                    else
                    {
                        String eror=task.getException().toString();
                        Toast.makeText(AyarlarActivity.this, "HATA: "+ eror, Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick && resultCode==RESULT_OK && data!=null)
        {
            Uri ImageUri =data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mDialog.setTitle("Resim Güncelleniyor");
                mDialog.setMessage("Lürfen Bekleyiniz...");
                mDialog.setCanceledOnTouchOutside(true);
                mDialog.show();

                Uri resultUri = result.getUri();
                StorageReference filepath=UserProfileImageRef.child(currentUserID+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful())
                        {
                            final String downloadUrl= task.getResult().getDownloadUrl().toString();

                            RootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(AyarlarActivity.this, "Resim Güncellendi", Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }
                                    else
                                    {
                                        String hata=task.getException().toString();
                                        Toast.makeText(AyarlarActivity.this, "Hata: "+hata , Toast.LENGTH_SHORT).show();
                                        mDialog.dismiss();
                                    }

                                }
                            });
                        }
                        else
                        {
                            String hata=task.getException().toString();
                            Toast.makeText(AyarlarActivity.this, "Hata: "+hata , Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                    }

                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
