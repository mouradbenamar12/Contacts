package com.example.mourad.projetandroid.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mourad.projetandroid.Classes.User;
import com.example.mourad.projetandroid.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView ImgUser;
    static int PReqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;

    private Button registerButton,btnSigninOr;
    private EditText userName,userEmail,userPhone,userAdresse,userPassword,userPassword2;
    private ProgressBar loadingProgress;
    Intent homeActivity;

    private DatabaseReference databaseUsers;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseUsers = FirebaseDatabase.getInstance().getReference();


        userName = findViewById(R.id.regName);
        userEmail = findViewById(R.id.regMail);
        userPhone = findViewById(R.id.regPhone);
        userAdresse = findViewById(R.id.regAdresse);
        userPassword = findViewById(R.id.regPassword);
        userPassword2 = findViewById(R.id.regConfirmPassword);
        registerButton = findViewById(R.id.regButton);
        loadingProgress = findViewById(R.id.progressBar);
        btnSigninOr = findViewById(R.id.signinOr);

        homeActivity = new Intent(getApplicationContext(),Home.class);

        loadingProgress.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();



        btnSigninOr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent LoginActivity = new Intent(RegisterActivity.this,com.example.mourad.projetandroid.Activities.LoginActivity.class);
                startActivity(LoginActivity);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerButton.setVisibility(View.INVISIBLE);
                loadingProgress.setVisibility(View.VISIBLE);
                final String name = userName.getText().toString();
                final String email = userEmail.getText().toString();
                final String phone = userPhone.getText().toString();
                final String adresse = userAdresse.getText().toString();
                final String password = userPassword.getText().toString();
                final String password2 = userPassword2.getText().toString();

                if (email.isEmpty() || name.isEmpty() || phone.isEmpty() || adresse.isEmpty() || password.isEmpty() || !password.equals(password2)){

                    showMessage("Please Verify all fields");
                    registerButton.setVisibility(View.VISIBLE);
                    loadingProgress.setVisibility(View.INVISIBLE);

                } else {

                    createUserAccount(name,email,phone,adresse,password);

                }



            }
        });


        ImgUser = findViewById(R.id.regImg);
        ImgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22){
                    checkAndRequestForPermission();
                }
                else {
                    openGallery();
                }

            }
        });

    }


    private void createUserAccount(final String name, final String email, final String phone, final String adresse, String password) {

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            showMessage("Account created successfully");
                            addUser(name,email,phone,adresse);
                            updateUserInfo(name,pickedImgUri,mAuth.getCurrentUser());

                        }else {

                            showMessage("Account creation failed"+ task.getException().getMessage());
                            registerButton.setVisibility(View.VISIBLE);
                            loadingProgress.setVisibility(View.INVISIBLE);

                        }
                    }
                });





    }

    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {


        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        UserProfileChangeRequest profileUpdate = new  UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                            showMessage("Register Complete");

                                            updateUI();
                                        }

                                    }
                                });

                    }
                });

            }
        });
    }


    private void addUser(String name,String email,String phone,String adresse){


            String id = databaseUsers.push().getKey();
            User user = new User(id,name,email,phone,adresse);
            databaseUsers.child("users").push().setValue(user);


    }

    private void updateUI() {

        startActivity(homeActivity);
        finish();
    }


    private void showMessage(String message) {

        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){

                Toast.makeText(RegisterActivity.this,"Please accept for required permission",Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else {
            openGallery();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESCODE  && data != null){


            pickedImgUri = data.getData();
            ImgUser.setImageURI(pickedImgUri);


        }
    }
}
