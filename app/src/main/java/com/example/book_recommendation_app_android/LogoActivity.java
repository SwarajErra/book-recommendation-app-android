package com.example.book_recommendation_app_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogoActivity extends AppCompatActivity {
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Context context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logos);

        button = (Button) findViewById(R.id.loginBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);

            }
        });


    }


    private void openLoginActivity() {
    }


}
