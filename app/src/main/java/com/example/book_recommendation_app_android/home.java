package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

class Rating {
    double bookRating;
    String bookReview;
    String reviewerName;
    String reviewerId;

    public Rating(
            double bookRating,
            String bookReview,
            String reviewerName,
            String reviewerId
    ){
        this.bookRating = bookRating;
        this.bookReview =  bookReview;
        this.reviewerName =  reviewerName;
        this.reviewerId =  reviewerId;
    }
}

class Book {
     String bookTitle;
     int bookId;
     int bookImageId;
     String bookCategory;
     String bookSubTitle;
     String bookCoverDetails;
     double bookOverallRating;
     String bookAddedBy;
     String bookPublishedBy;
     ArrayList<Rating> bookRatingsAndReviews;
     boolean isBlocked;

    public Book(String bookTitle,
            int bookId,
            int bookImageId,
            String bookCategory,
            String bookSubTitle,
            String bookCoverDetails,
            double bookOverallRating,
            String bookAddedBy,
            String bookPublishedBy,
            ArrayList<Rating> bookRatingsAndReviews,
            boolean isBlocked

    ){
        this.bookTitle = bookTitle;
        this.bookId =  bookId;
        this.bookImageId =  bookImageId;
        this.bookCategory =  bookCategory;
        this.bookSubTitle =  bookSubTitle;
        this.bookCoverDetails = bookCoverDetails;
        this.bookOverallRating =  bookOverallRating;
        this.bookAddedBy =  bookAddedBy;
        this.bookPublishedBy =  bookPublishedBy;
        this.bookRatingsAndReviews =  bookRatingsAndReviews;
        this.isBlocked =  isBlocked;
    }
}

public class home extends AppCompatActivity {

    ArrayList<Book> arrayJobs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getPostedBooks();
    }

    private void getPostedBooks() {

        FirebaseFirestore.getInstance().collection("books-list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot jobs = task.getResult();

                    for(int i=0; i<jobs.getDocuments().size(); i++) {
                        DocumentSnapshot doc = jobs.getDocuments().get(i);
                        System.out.println(doc.getString("bookAddedBy"));
//                        arrayJobs.add();
                    }

//                    buildCard(arrayJobs);

                } else {
//                    Toast.makeText(getJobActivity.this, "Error getting documents:", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}