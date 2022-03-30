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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class profile extends AppCompatActivity {


    EditText firstName,lastName,email,mobile,bio;
    Button updateProfile;
    String emailPattern= "[a-zA-Z0-9._%+-]+@[A-Za-z.-]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    FirebaseUser mUser;
    private FirebaseUser currentUser;
    FirebaseFirestore db;

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

//        bookId = (String) getIntent().getSerializableExtra("docId");

        currentUser = mAuth.getInstance().getCurrentUser();

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfileData();
            }
        });
        getProfileData();
    }

    private void getProfileData() {

//        FirebaseFirestore.getInstance().collection("books-list").document(bookId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    doc = task.getResult();
//                    bookTitle.setText(doc.getString("bookTitle"));
//                    bookSubTitle.setText(doc.getString("bookSubTitle"));
//                    bookAuthor.setText(doc.getString("bookAuthor"));
//                    bookCategory.setText(doc.getString("bookCategory"));
//                    bookPublishedBy.setText(doc.getString("bookPublishedBy"));
//                    bookCoverDetails.setText(doc.getString("bookCoverDetails"));
//                } else {
//                    Toast.makeText(profile.this, "Error getting documents:", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    private void updateProfileData() {


//        Book book = new Book(
//                bookTitle.getText().toString(),
//                bookId,
//                doc.getLong("bookImageId").intValue(),
//                bookCategory.getText().toString(),
//                bookAuthor.getText().toString(),
//                bookSubTitle.getText().toString(),
//                bookCoverDetails.getText().toString(),
//                doc.getLong("bookOverallRating"),
//                doc.getString("bookAddedBy"),
//                bookPublishedBy.getText().toString(),
//                (ArrayList<Rating>) doc.get("bookRatingsAndReviews"),
//                doc.getBoolean("isBlocked")
//        );
//
//        db.collection("books-list").
//                document(bookId).
//                set(book).
//                addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        System.out.println("doc update success--"+bookId);
//                        sendUserToNextActivity();
//                        Toast.makeText(profile.this, "Update Successfull", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(profile.this, "Fail to update the data", Toast.LENGTH_SHORT).show();
//            }
//        });

    }
    private void sendUserToNextActivity() {
        Intent intent= new Intent(profile.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}