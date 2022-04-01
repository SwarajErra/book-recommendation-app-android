package com.example.book_recommendation_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class Rating {
    public float bookRating;
    public String bookReview;
    public String reviewerName;
    public String reviewerId;

    public Rating(
            float bookRating,
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
    public String bookTitle;
    public String bookId;
    public int bookImageId;
    public String bookCategory;
    public String bookAuthor;
    public String bookSubTitle;
    public String bookCoverDetails;
    public double bookOverallRating;
    public String bookAddedBy;
    public String bookPublishedBy;
    public ArrayList<Rating> bookRatingsAndReviews;
    public boolean isBlocked;

    public Book(
            String bookTitle,
            String bookId,
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
    Button Button,searchButton,apiBooksButton,sortButton,addBookButton,signOutButton;
    Boolean sortOrderAscending = true;
    public String currentUserEmail = "test";
    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user,mCurrentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_home);
        db = FirebaseFirestore.getInstance();
        mCurrentUser = mAuth.getInstance().getCurrentUser();
        if(mCurrentUser != null){
            currentUserEmail =  mCurrentUser.getEmail();
        }

        System.out.println(currentUserEmail);
        sortButton=findViewById(R.id.sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortList();
            }
        });
        addBookButton = findViewById(R.id.addBook);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(home.this,Add_Book.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        getPostedBooks();
        searchButton();
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
                Intent intentProfile= new Intent(home.this,profile.class);
                intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentProfile);
                return true;
            case R.id.home:
                Intent intentHome= new Intent(home.this,home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                return true;
            case R.id.aboutUs:
                Intent intentAboutUs= new Intent(home.this,AboutUsActivity.class);
                intentAboutUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAboutUs);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(home.this,LogoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(home.this,"Successfully signed out", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
         mCurrentUser = mAuth.getInstance().getCurrentUser();

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


    void buildCard(ArrayList<Book> newArray){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.homeCardLayout);
        mainLayout.removeAllViews();
        LayoutInflater li =  (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < newArray.size();  i++){
            ArrayList<Rating> ratings= new ArrayList<>();
            View tempView = li.inflate(R.layout.book_card, null);

            TextView title = (TextView) tempView.findViewById(R.id.title);
            TextView authour = (TextView) tempView.findViewById(R.id.authour);
            TextView rating = (TextView) tempView.findViewById(R.id.rating);
            RatingBar ratingBar = (RatingBar) tempView.findViewById(R.id.ratingBar);
            ImageView image = (ImageView) tempView.findViewById(R.id.bookImage);
            Button share = tempView.findViewById(R.id.share);
            Button edit = tempView.findViewById(R.id.edit);
            Button delete = tempView.findViewById(R.id.delete);
            Button block = tempView.findViewById(R.id.block);
            Button unblock = tempView.findViewById(R.id.unblock);

            title.setText(Html.fromHtml(newArray.get(i).bookTitle));
            authour.setText(Html.fromHtml( "By : " + newArray.get(i).bookAuthor));
            rating.setText(Html.fromHtml("Rating :"));
            ratings = (ArrayList<Rating>)  newArray.get(i).bookRatingsAndReviews;
            ratingBar.setRating(setOverallRating(ratings));
            String uri = "@drawable/img" +  newArray.get(i).bookImageId;
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            image.setImageResource(imageResource);

            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Check Out this Book : ");
                    intent.putExtra(Intent.EXTRA_TEXT,title.getText());
                    startActivity(Intent.createChooser(intent,"Share it through :  "));
                }
            });


            int finalI = i;
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(home.this, Update_Book.class);
                    intent.putExtra("docId",newArray.get(finalI).bookId );
                    startActivity(intent);
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("books-list").
                            document(newArray.get(finalI).bookId).
                            delete().
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(home.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(home.this, "Fail to delete the data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            });



            block.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("books-list").
                            document(newArray.get(finalI).bookId).
                            update("isBlocked",true).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(home.this, "Blocked Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(home.this, "Fail to Block the Book", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            unblock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("books-list").
                            document(newArray.get(finalI).bookId).
                            update("isBlocked",false).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    Toast.makeText(home.this, "Unblocked Successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(getIntent());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(home.this, "Fail to Unblock the Book", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            tempView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(home.this, bookDesc.class);
                    intent.putExtra("docId",newArray.get(finalI).bookId );
                    startActivity(intent);
                }
            });

            if(currentUserEmail.equalsIgnoreCase(newArray.get(finalI).bookAddedBy)) {
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
            }
            if(currentUserEmail.equalsIgnoreCase(getResources().getString(R.string.ADMIN))) {

                if(newArray.get(finalI).isBlocked){
                    unblock.setVisibility(View.VISIBLE);
                    block.setVisibility(View.GONE);
                }else{
                    block.setVisibility(View.VISIBLE);
                    unblock.setVisibility(View.GONE);
                }
                edit.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                mainLayout.addView(tempView);
            }else{
                if(!newArray.get(finalI).isBlocked){
                    mainLayout.addView(tempView);
                }

            }

        }

    }


    public void  sortList() {
        sortOrderAscending = !sortOrderAscending;
        List<Book> sortedList = new ArrayList<Book>();
        Collections.sort(arrayBooks, new Comparator<Book>(){
            public int compare(Book obj1, Book obj2) {
                if(sortOrderAscending) {
                    // Ascending order
                    return obj1.bookTitle.compareToIgnoreCase(obj2.bookTitle);
                    // return Integer.valueOf(obj1.bookId).compareTo(Integer.valueOf(obj2.bookId));
                }else {
                    // Descending order
                    return obj2.bookTitle.compareToIgnoreCase(obj1.bookTitle);
                    // return Integer.valueOf(obj2.bookId).compareTo(Integer.valueOf(obj1.bookId)); // To compare integer values
                }
            }
        });
        buildCard(arrayBooks);

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
        apiBooksButton = (Button) findViewById(R.id.apiBooks);
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
        apiBooksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent= new Intent(home.this,GoogleBooksAPI.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
                        arrayBooks.add(new Book(
                                doc.getString("bookTitle"),
                                doc.getId(),
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