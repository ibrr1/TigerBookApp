package com.tigerbook.ibrahim.tigerbook;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MessagesActivity extends AppCompatActivity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
   // ProgressBar mProgressBar;
    MessagesListViewAdapter adapter;
    private List<TigerBook> messageslist = null;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    TextView mMessagesListEmptyTV;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        TextView toolBarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolBarTitle.setText("Messages");

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Execute RemoteDataTask AsyncTask
                new RemoteDataTask().execute();
            }
        });

        // Execute RemoteDataTask AsyncTask
        new RemoteDataTask().execute();

        // Get the current user
        // Convert currentUser into String
        ParseUser currentUser = ParseUser.getCurrentUser();
        String userId = currentUser.getObjectId().toString();

        // ------- Change Messages Status and clean the badger -----------
        // change the status of the messages for new to read
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Messages");
        query2.whereEqualTo("recipientId", userId);
        query2.whereEqualTo("status", true);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messagesList, ParseException e) {
                if (e == null) {
                    for (ParseObject po: messagesList){
                        po.put("status", false);
                        po.saveInBackground();
                    }
                    // clear the number of new messages in the app icon
                    ShortcutBadger.removeCount(getApplicationContext()); //for 1.1.4+


                } else {
                    Log.d("Messages", "Error: " + e.getMessage());
                }
            }
        });
        // --------------------------------------------------------------------
    }

    // RemoteDataTask AsyncTask
    //Read more http://www.android2ut.com/2013/10/a-font-family-helvetica-helvetica-sans.html
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
//            mProgressBar
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            messageslist = new ArrayList<TigerBook>();
            try {
                // Locate the class table named "Country" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Messages");
                // by ascending
                query.orderByDescending("createdAt");
                // Retrieve current user from Parse.com
                ParseUser currentUser = ParseUser.getCurrentUser();
                // Convert currentUser into String
                String userId = currentUser.getObjectId().toString();
                query.whereEqualTo("recipientId", userId);
                ob = query.find();
                for (ParseObject po : ob) {

                    TigerBook messages = new TigerBook();

                    messages.setTitle((String) po.get("title"));
                    messages.setPhoneNumber((String) po.get("phoneNumber"));
                    messages.setObjectId((String) po.getObjectId());

                    messageslist.add(messages);
                    }

            } catch (ParseException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in MainActivity.xml
            listview = (ListView) findViewById(R.id.listview);
            // Pass the results into ListViewAdapter.java
            adapter = new MessagesListViewAdapter(MessagesActivity.this, messageslist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);

            mMessagesListEmptyTV = (TextView) findViewById(R.id.tv1);
            if(messageslist.isEmpty()){

                mMessagesListEmptyTV.setText("You don't have any messages!");

            }
            else{
                mMessagesListEmptyTV.setText("");
            }

            mSwipeRefreshLayout.setRefreshing(false);

        }
    }
}


