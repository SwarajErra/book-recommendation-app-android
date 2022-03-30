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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoogleBooksAPI extends AppCompatActivity {
    ArrayList<Book> arrayBooks = new ArrayList<>();
    ArrayList<Rating> arrayRating = new ArrayList<>();

    private RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_books_api);
        queue = Volley.newRequestQueue(this);
        getListFromAPI();

    }

    public void getListFromAPI() {
        String url  = "https://www.googleapis.com/books/v1/volumes?q=flowers+inauthor:keyes&key=AIzaSyDi5y2ij9RkWax8eGqfGfOEvrLvAXkLQkw";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray array = null;

                        try {
                            array = response.getJSONArray("items");
                            System.out.println(array.length());
                            for (int i = 0; i <array.length() ; i++) {
                                try {
                                    JSONObject obj = array.getJSONObject(i);
                                    JSONObject innerObj = obj.getJSONObject("volumeInfo");
                                    JSONArray authorArray  = innerObj.getJSONArray("authors");
                                    long rating;
                                    if (innerObj.has("averageRating")) {
                                        rating =  innerObj.getLong("averageRating");
                                    } else {
                                        rating = 0;
                                    }
                                    arrayBooks.add(new Book(
                                            innerObj.getString("title"),
                                            "",
                                            1,
                                            "apiCategory",
                                            authorArray.getString(0) ,
                                            "subtitle",
                                            "bookCoverDetails",
                                            rating,
                                            "bookAddedBy",
                                            "bookPublishedBy",
                                            null,
                                            false
                                    ));

                                    buildCard(arrayBooks);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(req);
    }

    void buildCard(ArrayList<Book> newArray){
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.homeCardAPILayout);

        LayoutInflater li =  (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < newArray.size();  i++){
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


            title.setText(Html.fromHtml(newArray.get(i).bookTitle));
            authour.setText(Html.fromHtml( "By : " + newArray.get(i).bookAuthor));
            rating.setText(Html.fromHtml("Rating :"));
            ratingBar.setRating((float) newArray.get(i).bookOverallRating);
            String uri = "@drawable/noimg" ;
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());
            image.setImageResource(imageResource);

//            if(isAdmin) {
//                edit.setVisibility(View.VISIBLE);
//                block.setVisibility(View.VISIBLE);
//                delete.setVisibility(View.VISIBLE);
//            }

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
            mainLayout.addView(tempView);
        }

    }
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent= new Intent(GoogleBooksAPI.this,home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                System.out.println(item.getItemId());
                return true;
            case R.id.home:
                Intent intentHome= new Intent(GoogleBooksAPI.this,home.class);
                intentHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentHome);
                return true;
            case R.id.aboutUs:
                Intent intentAboutUs= new Intent(GoogleBooksAPI.this,AboutUsActivity.class);
                intentAboutUs.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentAboutUs);
                return true;
            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(GoogleBooksAPI.this,LogoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}