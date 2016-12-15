package com.tigerbook.ibrahim.tigerbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

public class ImageDisplay extends AppCompatActivity {

    //declare variables for the two buttons
    ImageView mBookImg;
    String mImg;
    ImageLoader imageLoader = new ImageLoader(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct the user to MainActivity.java
                onBackPressed();
            }
        });

        // Get the results from the previous activity
        Intent i = getIntent();
        mImg = i.getStringExtra("img");

        mBookImg = (ImageView) findViewById(R.id.iv);

        imageLoader.DisplayImage(mImg, mBookImg);

    }
}
