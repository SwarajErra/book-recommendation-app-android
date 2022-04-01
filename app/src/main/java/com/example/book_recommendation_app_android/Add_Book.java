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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

public class Add_Book extends AppCompatActivity {
    EditText bookTitle,bookSubTitle,bookAuthor,bookCategory,bookPublishedBy,bookCoverDetails;
    Button addBook;

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);


        bookTitle=findViewById(R.id.bookTitle);
        bookSubTitle=findViewById(R.id.bookSubTitle);
        bookAuthor=findViewById(R.id.bookAuthor);
        bookCategory=findViewById(R.id.bookCategory);
        bookPublishedBy=findViewById(R.id.bookPublishedBy);
        bookCoverDetails=findViewById(R.id.bookCoverDetails);

        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUser = mAuth.getInstance().getCurrentUser();

        addBook = findViewById(R.id.addBook);
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBookData();

            }
        });

    }

    private void sendBookData() {
        CollectionReference addBook = db.collection("books-list");


        Book book = new Book(
                bookTitle.getText().toString(),
                "",
                new Random().nextInt(10) + 1,
                bookCategory.getText().toString(),
                bookAuthor.getText().toString(),
                bookSubTitle.getText().toString(),
                bookCoverDetails.getText().toString(),
                0,
                currentUser.getEmail().toString(),
                bookPublishedBy.getText().toString(),
                new ArrayList<>(),
                false
        );

        addBook.add(book).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                sendUserToNextActivity();
                Toast.makeText(Add_Book.this, "Book added Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Add_Book.this, "Book adding failed", Toast.LENGTH_SHORT).show();
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
                Intent intentProfile= new Intent(Add_Book.this,profile.class);
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentProfile);
                return true;
            case R.id.home:
                Intent intentHome= new Intent(Add_Book.this,home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                return true;
            case R.id.aboutUs:
                Intent intentAboutUs= new Intent(Add_Book.this,AboutUsActivity.class);
                intentAboutUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAboutUs);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(Add_Book.this,LogoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(Add_Book.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendUserToNextActivity() {
        Intent intent= new Intent(Add_Book.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent= new Intent(Add_Book.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}