package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Update_Book extends AppCompatActivity {

    EditText bookTitle,bookSubTitle,bookAuthor,bookCategory,bookPublishedBy,bookCoverDetails;
    Button updateBook;
    public  String  bookId;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private FirebaseUser currentUser;
    DocumentSnapshot doc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        bookTitle=findViewById(R.id.bookTitle);
        bookSubTitle=findViewById(R.id.bookSubTitle);
        bookAuthor=findViewById(R.id.bookAuthor);
        bookCategory=findViewById(R.id.bookCategory);
        bookPublishedBy=findViewById(R.id.bookPublishedBy);
        bookCoverDetails=findViewById(R.id.bookCoverDetails);

        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bookId = (String) getIntent().getSerializableExtra("docId");

        currentUser = mAuth.getInstance().getCurrentUser();

        updateBook = findViewById(R.id.updateBook);
        updateBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBookData();
            }
        });
        getBookData();
    }

    private void getBookData() {

        FirebaseFirestore.getInstance().collection("books-list").document(bookId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    doc = task.getResult();
                    bookTitle.setText(doc.getString("bookTitle"));
                    bookSubTitle.setText(doc.getString("bookSubTitle"));
                    bookAuthor.setText(doc.getString("bookAuthor"));
                    bookCategory.setText(doc.getString("bookCategory"));
                    bookPublishedBy.setText(doc.getString("bookPublishedBy"));
                    bookCoverDetails.setText(doc.getString("bookCoverDetails"));
                } else {
                    Toast.makeText(Update_Book.this, "Error getting documents:", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateBookData() {


        Book book = new Book(
                bookTitle.getText().toString(),
                bookId,
                doc.getLong("bookImageId").intValue(),
                bookCategory.getText().toString(),
                bookAuthor.getText().toString(),
                bookSubTitle.getText().toString(),
                bookCoverDetails.getText().toString(),
                doc.getLong("bookOverallRating"),
                doc.getString("bookAddedBy"),
                bookPublishedBy.getText().toString(),
                (ArrayList<Rating>) doc.get("bookRatingsAndReviews"),
                doc.getBoolean("isBlocked")
        );

        db.collection("books-list").
                        document(bookId).
                        set(book).
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        System.out.println("doc update success--"+bookId);
                        sendUserToNextActivity();
                        Toast.makeText(Update_Book.this, "Update Successfull", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Update_Book.this, "Fail to update the data", Toast.LENGTH_SHORT).show();
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
                Intent intentProfile= new Intent(Update_Book.this,profile.class);
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentProfile);
                return true;
            case R.id.home:
                Intent intentHome= new Intent(Update_Book.this,home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                return true;
            case R.id.aboutUs:
                Intent intentAboutUs= new Intent(Update_Book.this,AboutUsActivity.class);
                intentAboutUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAboutUs);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(Update_Book.this,LogoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(Update_Book.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendUserToNextActivity() {
        Intent intent= new Intent(Update_Book.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent= new Intent(Update_Book.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}