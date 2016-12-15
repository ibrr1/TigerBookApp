package com.tigerbook.ibrahim.tigerbook;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class BookActivity extends AppCompatActivity {

    //declare variables for the two buttons
    Button mRequesst;
    TextView mTitleTV, mCategoryTV, mConditionV, mPriceTV;
    ImageView mBookImg;
    String mTitle, mImg, mCategory, mCondition, mPrice, mUserId, mBookObjectIdId;
    ImageLoader imageLoader = new ImageLoader(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        ShortcutBadger.removeCount(getApplicationContext()); //for 1.1.4+


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct the user to MainActivity.java
                onBackPressed();
            }
        });

        // Get the results from the previous activity
        Intent i = getIntent();
        mTitle = i.getStringExtra("title");
        mImg = i.getStringExtra("img");
        mCategory = i.getStringExtra("category");
        mCondition = i.getStringExtra("condition");
        mPrice = i.getStringExtra("price");
        mUserId = i.getStringExtra("userId");
        mBookObjectIdId = i.getStringExtra("bookObjectId");



        mRequesst = (Button) findViewById(R.id.requestBtn);
        mTitleTV = (TextView) findViewById(R.id.bookTitleTV);
        mCategoryTV = (TextView) findViewById(R.id.categoryTV);
        mConditionV = (TextView) findViewById(R.id.conditionTV);
        mPriceTV = (TextView) findViewById(R.id.priceTV);
        mBookImg = (ImageView) findViewById(R.id.imageView);



        mTitleTV.setText("Title: "+ mTitle);
        mCategoryTV.setText("Category: "+mCategory);
        mConditionV.setText("Condition: "+mCondition);
        mPriceTV.setText("Price: "+mPrice);

        if(mImg!= null){
            // Locate the book Image
            mBookImg.setVisibility(View.VISIBLE);
            imageLoader.DisplayImage(mImg, mBookImg);}
        else{
            mBookImg.setVisibility(View.GONE);
            setMargins(mTitleTV, 0, 290, 0, 0);

        }

        // Button Click Listener for login button
        mBookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BookActivity.this, ImageDisplay.class);
                i.putExtra("img",mImg );
                startActivity(i);
            }

        });



        mRequesst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Retrieve current user from Parse.com
                ParseUser currentUser = ParseUser.getCurrentUser();
                // Convert currentUser into String
                String senderUsername = currentUser.getUsername().toString();
                String senderUserId = currentUser.getObjectId().toString();
                String phoneNumber = currentUser.get("phoneNumber").toString();
                String title = mTitle;
                //String reason = mReasonsET.getText().toString();

                if(mUserId.equals(senderUserId)){
                    Toast.makeText(getApplicationContext(), "You can't request a book from yourself!", Toast.LENGTH_SHORT).show();
                }

                else{
                    //Send data to Parse.com
                    ParseObject po = new ParseObject("Messages");
                    po.put("senderUsername", senderUsername);
                    po.put("senderUserId", senderUserId);
                    po.put("recipientId", mUserId);
                    po.put("title", title);
                    po.put("phoneNumber", phoneNumber);
                    po.put("status", true);
                   // po.put("Reason", reason);
                    po.saveInBackground();

                    //To Send Msg To Order Owner
                    //query to search in ParseInstallation class
                    ParseQuery<ParseInstallation> query2 = ParseInstallation.getQuery();
                    query2.whereEqualTo("userId", mUserId);

                    // send push notification based on query2
                    ParsePush push = new ParsePush();
                    push.setQuery(query2);

                    //set message for the notification
                    push.setMessage("You have new received new message!");
                    //send
                    push.sendInBackground();

                    Toast.makeText(getApplicationContext(), "Message has been sent", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        });

    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserId = currentUser.getObjectId();

        if(mUserId.equals(currentUserId)) {
//			holder.mDeleteBtn.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "You are the owner of this book", Toast.LENGTH_SHORT).show();


            inflater.inflate(R.menu.activity_book_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.delete_book:

                Delete_Book();

                break;
            }


        return true;

    }

    public void Delete_Book() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BookActivity.this);

        // Setting Dialog Message
        alertDialog.setMessage(Html.fromHtml("<font color='#f16e2f'> <strong> Are you sure you want to delete this post? </strong></font>"));

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ParseObject.createWithoutData("Books", mBookObjectIdId).deleteEventually();
                        Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();

    }

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}
