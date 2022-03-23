package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
     String bookAuthor;
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
            String bookAuthor,
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
        this.bookAuthor =bookAuthor;
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

    ArrayList<Book> arrayBooks = new ArrayList<>();
    Button Button,searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getPostedBooks();
        searchButton();
    }

    void buildCard(ArrayList<Book> newArray){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.homeCardLayout);

        LayoutInflater li =  (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < newArray.size();  i++){
            View tempView = li.inflate(R.layout.book_card, null);

            TextView title = (TextView) tempView.findViewById(R.id.title);
            TextView authour = (TextView) tempView.findViewById(R.id.authour);
            TextView rating = (TextView) tempView.findViewById(R.id.rating);
            RatingBar ratingBar = (RatingBar) tempView.findViewById(R.id.ratingBar);
            ImageView image = (ImageView) tempView.findViewById(R.id.bookImage);


            title.setText(Html.fromHtml(newArray.get(i).bookTitle));
            authour.setText(Html.fromHtml( "By : " + newArray.get(i).bookAuthor));
            rating.setText(Html.fromHtml("Rating :"));
            ratingBar.setRating((float) newArray.get(i).bookOverallRating);
            String uri = "@drawable/img" +  newArray.get(i).bookImageId;
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            image.setImageResource(imageResource);


            mainLayout.addView(tempView);
        }

    }

    public List<Book> findUsingLoop(String search, List<Book> list) {
        List<Book> matches = new ArrayList<Book>();
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).bookTitle.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))
                     ) {

                matches.add(new Book(
                        list.get(i).bookTitle,
                        list.get(i).bookId,
                        list.get(i).bookImageId,
                        list.get(i).bookCategory,
                        list.get(i).bookAuthor,
                        list.get(i).bookSubTitle,
                        list.get(i).bookCoverDetails,
                        list.get(i).bookOverallRating,
                        list.get(i).bookAddedBy,
                        list.get(i).bookPublishedBy,
                        list.get(i).bookRatingsAndReviews,
                        list.get(i).isBlocked
                ));
            }
        }
        return matches;
    }

    public void searchButton() {
        final Context context = this;
        searchButton = (Button) findViewById(R.id.searchButton);
        EditText view = (EditText) findViewById(R.id.filledTextField);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                LinearLayout mainLayout = (LinearLayout) findViewById(R.id.homeCardLayout);
                mainLayout.removeAllViews();

                if (!view.getText().toString().matches("")) {
                    ArrayList<Book> newArray = (ArrayList<Book>) findUsingLoop(String.valueOf(view.getText()), arrayBooks);

                    buildCard(newArray);
                }else{
                    buildCard(arrayBooks);
                }

            }
        });
    }

    private void getPostedBooks() {

        FirebaseFirestore.getInstance().collection("books-list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    QuerySnapshot jobs = task.getResult();

                    for(int i=0; i<jobs.getDocuments().size(); i++) {
                        DocumentSnapshot doc = jobs.getDocuments().get(i);
                        System.out.println(doc);
                        arrayBooks.add(new Book(
                                doc.getString("bookTitle"),
                                doc.getLong("bookId").intValue(),
                                doc.getLong("bookImageId").intValue(),
                                doc.getString("bookCategory"),
                                doc.getString("bookAuthor"),
                                doc.getString("bookSubTitle"),
                                doc.getString("bookCoverDetails"),
                                doc.getDouble("bookOverallRating"),
                                doc.getString("bookAddedBy"),
                                doc.getString("bookPublishedBy"),
                                (ArrayList<Rating>) doc.get("bookRatingsAndReviews"),
                                doc.getBoolean("isBlocked")
                        ));
                    }

                    buildCard(arrayBooks);

                } else {
                    Toast.makeText(home.this, "Error getting documents:", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}