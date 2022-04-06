package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;

public class bookDesc extends AppCompatActivity {

    public  String  bookId;
    public ArrayList<Rating> ratings = new ArrayList<>();
    RatingBar bookDescRating,userRating,specificUserRating;
    TextView bookDescTitle,bookDescAuthour,bookCategory,bookPublishedBy,bookCoverDetails,userComments;
    TextView descBook;
    Button submmitRating,bookDescShare;
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    private FirebaseUser currentUser;
    DocumentSnapshot doc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_desc);

        bookDescTitle=findViewById(R.id.bookDescTitle);
        bookDescAuthour=findViewById(R.id.bookDescAuthour);
        bookDescRating = findViewById(R.id.bookDescRatingBar);

        descBook=findViewById(R.id.descBook);


        userRating=findViewById(R.id.userRating);
        userComments=findViewById(R.id.userComments);

        bookDescShare = findViewById(R.id.bookDescShare);

        mAuth= FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        bookId = (String) getIntent().getSerializableExtra("docId");

        currentUser = mAuth.getInstance().getCurrentUser();

        submmitRating = findViewById(R.id.submitRating);
        submmitRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submmitRating();
            }
        });

        getBookData();

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
                Intent intentProfile= new Intent(bookDesc.this,profile.class);
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentProfile);
                return true;
            case R.id.home:
                Intent intentHome= new Intent(bookDesc.this,home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                return true;
            case R.id.aboutUs:
                Intent intentAboutUs= new Intent(bookDesc.this,AboutUsActivity.class);
                intentAboutUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAboutUs);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(bookDesc.this,LogoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(bookDesc.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submmitRating() {

        Rating newRating = new Rating(
                userRating.getRating(),
                userComments.getText().toString(),
                currentUser.getEmail(),
                currentUser.getUid()
        );
        ratings.add(newRating);

        db.collection("books-list").
                document(bookId).
                update("bookRatingsAndReviews",ratings).
                addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(bookDesc.this, "Rated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(bookDesc.this, "Fail to Rate the Book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void buildCommentsCard(Map<String, Object> rating){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.commentsLayout);
        LayoutInflater li =  (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View tempView = li.inflate(R.layout.comments, null);
            TextView userLebel = (TextView) tempView.findViewById(R.id.userLebel);
            TextView userComments = (TextView) tempView.findViewById(R.id.userComments);
            RatingBar specificUserRating = (RatingBar) tempView.findViewById(R.id.specificUserRating);

                userLebel.setText(Html.fromHtml((String) rating.get("reviewerName")));
                userComments.setText(Html.fromHtml((String) rating.get("bookReview")));
                Float rat = ((Double) rating.get("bookRating")).floatValue();
                specificUserRating.setRating((Float) rat );

            mainLayout.addView(tempView);


    }

    private Float setOverallRating(ArrayList<Rating> ratings){
        float sum = 0,avg=0;
        for (int i = 0; i < ratings.size();  i++) {
            Map<String, Object> temp = (Map<String, Object>) ratings.get(i);
            sum = sum + ((Double) temp.get("bookRating")).floatValue();
        }
        avg = sum/ratings.size();
        return avg;
    }

    private void getBookData() {

        FirebaseFirestore.getInstance().collection("books-list").document(bookId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    doc = task.getResult();
                    bookDescTitle.setText(doc.getString("bookTitle"));
                    bookDescAuthour.setText(doc.getString("bookAuthor"));
                    descBook.setText(doc.getString("bookCoverDetails"));
//                    bookDescRating.setRating(doc.getLong("bookOverallRating"));
                    ratings = (ArrayList<Rating>)  doc.get("bookRatingsAndReviews");
                    bookDescRating.setRating(setOverallRating(ratings));
                    bookDescShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT,"Check Out this Book : ");
                            intent.putExtra(Intent.EXTRA_TEXT,doc.getString("bookTitle"));
                            startActivity(Intent.createChooser(intent,"Share it through :  "));
                        }
                    });

                    for (int i = 0; i < ratings.size();  i++) {
                        buildCommentsCard((Map<String, Object>) ratings.get(i));
                    }

                } else {
                    Toast.makeText(bookDesc.this, "Error getting documents:", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent= new Intent(bookDesc.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}