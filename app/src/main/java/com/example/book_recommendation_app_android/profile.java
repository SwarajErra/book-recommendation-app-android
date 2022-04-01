package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class profile extends AppCompatActivity {


    EditText firstName,lastName,email,mobile,bio;
    Button updateProfile;
    String emailPattern= "[a-zA-Z0-9._%+-]+@[A-Za-z.-]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    public  String  profileId;
    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FirebaseUser currentUser;
    FirebaseFirestore db;
    FirebaseUser user,mCurrentUser;
    public String currentUserEmail;
    public String uid,docId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        updateProfile = (Button) findViewById(R.id.updateProfile);
        firstName=findViewById(R.id.firstName);
        lastName=findViewById(R.id.lastName);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        bio=findViewById(R.id.bio);

        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mCurrentUser = mAuth.getInstance().getCurrentUser();
        if(mCurrentUser != null){
            currentUserEmail =  mCurrentUser.getEmail();
        }

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileData();
            }
        });
        getProfileData();
    }

    private void getProfileData() {

        FirebaseFirestore.getInstance().collection("profile-info").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot jobs = task.getResult();

                    for(int i=0; i<jobs.getDocuments().size(); i++) {
                        DocumentSnapshot doc = jobs.getDocuments().get(i);

                        if (doc.getString("email").equalsIgnoreCase(currentUserEmail)){
                            firstName.setText(doc.getString("firstName"));
                            lastName.setText(doc.getString("lastName"));
                            email.setText(doc.getString("email"));
                            mobile.setText(doc.getString("mobile"));
                            bio.setText(doc.getString("bio"));
                            uid = doc.getString("uid");
                            docId = doc.getId();
                        }
                    }

                } else {
                    Toast.makeText(profile.this, "Error getting documents:", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfileData() {


        User user = new User(
                bio.getText().toString(),
                email.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                false,
                mobile.getText().toString(),
                uid
        );

        db.collection("profile-info").
                document(docId).
                set(user).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("doc update success--"+docId);
                        sendUserToNextActivity();
                        Toast.makeText(profile.this, "Updated Profile Successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(profile.this, "Fail to update the data", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blurb_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent intentProfile= new Intent(profile.this,profile.class);
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentProfile);
                return true;
            case R.id.home:
                Intent intentHome= new Intent(profile.this,home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                return true;
            case R.id.aboutUs:
                Intent intentAboutUs= new Intent(profile.this,AboutUsActivity.class);
                intentAboutUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAboutUs);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(profile.this,LogoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(profile.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendUserToNextActivity() {
        Intent intent= new Intent(profile.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent= new Intent(profile.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}