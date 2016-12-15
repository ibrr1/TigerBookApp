package com.tigerbook.ibrahim.tigerbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegistrationActivity extends AppCompatActivity {

    EditText mEmailET, mPasswordET, mPhoneNumberET;
    Button mRegisterBtn;
    ProgressBar mProgressBar;
    TextView mLoginTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        // to show the back btn in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // to set the actionbar title
        getSupportActionBar().setTitle("Log in");

        mEmailET = (EditText) findViewById(R.id.emailET);
        mPasswordET = (EditText) findViewById(R.id.passwordET);
        mPhoneNumberET = (EditText) findViewById(R.id.phoneNumberET);
        mRegisterBtn = (Button) findViewById(R.id.registerBtn);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mLoginTV = (TextView) findViewById(R.id.loginTV);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Declare variables	and get the text from the EditText
                String email = mEmailET.getText().toString();
                String password = mPasswordET.getText().toString();
                String phoneNumber = mPhoneNumberET.getText().toString();

                //email validation
                String emailPattern = "[a-zA-Z0-9._-]+@[grit]+\\.+edu+";


                if (email.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "email, password or phone number cant't be empty!", Toast.LENGTH_SHORT).show();
                }
                //if the email is not valid
                else if (!email.matches(emailPattern))
                {
                    Toast.makeText(getApplicationContext(),"Please enter a valid RIT email",Toast.LENGTH_SHORT).show();
                }

                //if the username or the password is lower than 4 characters show the following toast msg
                else if (phoneNumber.trim().length() < 10) {
                    Toast.makeText(getApplicationContext(), "Phone number can't be less than 10 digits", Toast.LENGTH_SHORT).show();
                }
                else {
                    ParseUser user = new ParseUser();
                    user.setUsername(email);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.put("phoneNumber", phoneNumber);

                    mProgressBar.setVisibility(View.VISIBLE);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Register Successful
                                Toast.makeText(getApplicationContext(), "You have successfully register!", Toast.LENGTH_SHORT).show();

                                // Direct the user to MainActivity.java
                                ParseApplication.updateParseInstallation(ParseUser.getCurrentUser());
                                Intent i = new Intent(RegistrationActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                //Register Fail
                                //get error by calling e.getMessage()
                                Toast.makeText(getApplicationContext(), "something went wrong!", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
            }
        });

        mLoginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    // to take the user to the previous activity when click on the arrow in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
