package com.tigerbook.ibrahim.tigerbook;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends AppCompatActivity {

    // Declare Variables
    ListView listview;
    List<ParseObject> ob;
    ProgressBar mProgressBar;
    MainActivityListViewAdapter adapter;
    private List<TigerBook> booklist = null;

    TextView mbooklistEmptyTV;
    private static final int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Check if the user is logged in
        ParseAnalytics.trackAppOpened(getIntent());
        ParseUser currentUser = ParseUser.getCurrentUser();

        // if the the user logged out
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            //If set, this activity will become the start of a new task on this history stack.
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {

            //Add before making a phone call
            // whether I have a permission to make a phone call
            // if the result of the checkSelfPermission method granted contiune on and excute the makeCall method
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //If user don't have the permission u should ask for it.
                    /* the method shouldShowRequestPermissionRationale will return ture if the user already denied
                    the permission at some point */
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(MainActivity.this, "I know you said no, but I'm asking again", Toast.LENGTH_SHORT).show();
                }
                // Asking for the permission with requestPermissions method,
                // this will result opening dialog box
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);
                return;
            }
            Toast.makeText(getApplicationContext(), "Welcome " + currentUser.getUsername(), Toast.LENGTH_SHORT).show();
            mbooklistEmptyTV = (TextView) findViewById(R.id.tv1);

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

        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Direct the user to AddBookActivity.java
                Intent i = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(i);
            }
        });

        //---------------- To count the number of messages  ----------------------------//

        // Retrieve current user from Parse.com
        // Convert currentUser into String
        String userId = currentUser.getObjectId().toString();
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Messages");
        query2.whereEqualTo("recipientId", userId);
        query2.whereEqualTo("status", true);
        query2.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messagesList, ParseException e) {
                if (e == null) {

                    // Create Badger to show the number of messages in the app icon
                    int messagesCount = messagesList.size();
                    ShortcutBadger.applyCount(getApplicationContext(), messagesCount); //for 1.1.4+

                    // show Snakbar if there new messages
                    if(messagesCount > 0) {
                        View parentLayout = findViewById(R.id.fab);
                        Snackbar.make(parentLayout, "You have " + Integer.toString(messagesList.size()) + " new message(s)", Snackbar.LENGTH_INDEFINITE)
                                .setAction("CLOSE ", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                                .show();
                    }

                } else {
                    Log.d("Messages", "Error: " + e.getMessage());
                }
            }
        });
        //-------------------------------------------------------------------------------

    }

    // RemoteDataTask AsyncTask
    //Read more http://www.android2ut.com/2013/10/a-font-family-helvetica-helvetica-sans.html
    private class RemoteDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            booklist = new ArrayList<TigerBook>();
            try {
                // Locate the class table named "Country" in Parse.com
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Books");
                // by ascending
                query.orderByDescending("createdAt");
                ob = query.find();
                for (ParseObject po : ob) {
                    ParseFile image = null;
                    if (po.get("img") instanceof ParseFile)
                        image = (ParseFile) po.get("img");

                    TigerBook book = new TigerBook();

                    book.setTitle((String) po.get("bookTitle"));
                    book.setCategory((String) po.get("category"));
                    book.setCondition((String) po.get("condition"));
                    book.setPrice((String) po.get("price"));
                    book.setUserId((String) po.get("userId"));
                    book.setObjectId((String) po.getObjectId());

                    if (image != null && image.getUrl() != null) {
                        book.setImg(image.getUrl());

                        booklist.add(book);
                    } else {
                        booklist.add(book);
                    }
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
            adapter = new MainActivityListViewAdapter(MainActivity.this, booklist);
            // Binds the Adapter to the ListView
            listview.setAdapter(adapter);

            mbooklistEmptyTV = (TextView) findViewById(R.id.tv1);
            if (booklist.isEmpty()) {

                mbooklistEmptyTV.setText("No Books to show!");

            } else {
                mbooklistEmptyTV.setText("");
            }

            // Close the progressdialog
            mProgressBar.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.logout:

                Logout();
                break;

            case R.id.msg:

                Intent i3 = new Intent(getApplicationContext(), MessagesActivity.class);
                startActivity(i3);
                break;

//            case R.id.search:
//
//                Intent i4 = new Intent(getApplicationContext(), SearchActivity.class);
//                startActivity(i4);
//
//                break;

        }

        return true;

    }

    public void Logout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Message
        alertDialog.setMessage(Html.fromHtml("<font color='#f16e2f'> <strong> Are you sure you want to logout? </strong></font>"));

        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.ic_book);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Logout current user
                        ParseUser.logOut();
                        startActivity(new Intent(MainActivity.this,
                                LoginActivity.class));
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

    //Add this method to handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission was granted!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    //makeCall();
                } else {
                    Toast.makeText(MainActivity.this, "Permission was denied!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

}


