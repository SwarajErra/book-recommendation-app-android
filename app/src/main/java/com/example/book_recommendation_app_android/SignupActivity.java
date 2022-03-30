package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


class User {
    public String bio;
    public String email;
    public String firstName;
    public String lastName;
    public Boolean isAdmin;
    public String mobile;
    public String uid;


    public User(
            String bio,
            String email,
            String firstName,
            String lastName,
            Boolean isAdmin,
            String mobile,
            String uid
    ){
        this.bio =  bio;
        this.email =  email;
        this.firstName =  firstName;
        this.lastName =lastName;
        this.isAdmin =  isAdmin;
        this.mobile = mobile;
        this.uid = uid;
    }
}
public class SignupActivity extends AppCompatActivity {

    EditText firstName,lastName,email,mobile,bio,password,confirmPassword;
    Button signUp;
    String emailPattern= "[a-zA-Z0-9._%+-]+@[A-Za-z.-]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUp = (Button) findViewById(R.id.signUp);
        firstName=findViewById(R.id.firstName);
        lastName=findViewById(R.id.lastName);
        email=findViewById(R.id.email);
        mobile=findViewById(R.id.mobile);
        bio=findViewById(R.id.bio);
        password=findViewById(R.id.password);
        confirmPassword=findViewById(R.id.confirmPassword);

        progressDialog = new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser=mAuth.getCurrentUser();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAndProfile();

            }
        });


    }

    private void signUpAndProfile() {
        String emailValue=email.getText().toString();
        String passwordValue=password.getText().toString();
        String confirmPasswordValue=confirmPassword.getText().toString();

        if (!emailValue.matches(emailPattern)){
            email.setError("Enter Valid Email Address");
            email.requestFocus();
        }else if (passwordValue.isEmpty()||passwordValue.length()<8) {
            password.setError("Enter atleast 8 Characters password");
        }else if (!passwordValue.equals(confirmPasswordValue)){
            confirmPassword.setError("Passwords does not Match");
        }else{
            progressDialog.setMessage("Please Wait While Registration...");
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        FirebaseUser userInfo = mAuth.getCurrentUser();
                        progressDialog.dismiss();
                        sendUserData(userInfo);
//                        Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(SignupActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void sendUserToNextActivity() {
        Intent intent= new Intent(SignupActivity.this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void sendUserData(FirebaseUser userInfo) {
        CollectionReference adduser = db.collection("profile-info");

        User user = new User(
                bio.getText().toString(),
                email.getText().toString(),
                firstName.getText().toString(),
                lastName.getText().toString(),
                false,
                mobile.getText().toString(),
                userInfo.getUid()
        );

        adduser.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                sendUserToNextActivity();
                Toast.makeText(SignupActivity.this, "User added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignupActivity.this, "User adding failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}