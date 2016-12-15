package com.tigerbook.ibrahim.tigerbook;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by ibrahim on 11/5/16.
 */

public class ParseApplication extends Application {
    private static ParseApplication instance = new ParseApplication();
    public static final String APPLICATION_ID = "Dxiw3kcYON7sFQBM6DrcUduqqhZ1Wab9OnxEAUWl";
    public static final String CLIENT_KEY = "JjpFJJS32a6kMQr2RtX2hzlaIT5giz6qeqoNa5VU";
    public static final String BACK4PAPP_API = "https://parseapi.back4app.com/";

    public ParseApplication(){
        instance = this;
    }

    public static Context getContext(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APPLICATION_ID)
                .clientKey(CLIENT_KEY)
                .server(BACK4PAPP_API).build());

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "819729137461");

        installation.saveInBackground();

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        //query to search in ParseInstallation class
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        // send push notification based on query2
        ParsePush push = new ParsePush();
        push.setQuery(query);
        //set message for the notification
        push.setMessage("kk");
        //send
        push.sendInBackground();
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("userId", user.getObjectId());
        installation.saveInBackground();
    }
}

class Receiver extends ParsePushBroadcastReceiver {
    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MessagesActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}