package com.tigerbook.ibrahim.tigerbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MessagesListViewAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    LayoutInflater inflater;
    ProgressDialog mProgressDialog;

    private List<TigerBook> messageslist = null;
    private ArrayList<TigerBook> arraylist;

    public MessagesListViewAdapter(Context context, List<TigerBook> messageslist) {
        this.context = context;
        this.messageslist = messageslist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<TigerBook>();
        this.arraylist.addAll(messageslist);

    }

    public class ViewHolder {
        TextView mTitle;
        TextView mPhoneNumber;
        //Button mDeleteBtn;

    }

    @Override
    public int getCount() {
        return messageslist.size();
    }

    @Override
    public TigerBook getItem(int position) {
        return messageslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item_msg, null);
            // Locate the TextViews in listview_item.xml
            holder.mTitle = (TextView) view.findViewById(R.id.titleTV);
            holder.mPhoneNumber = (TextView) view.findViewById(R.id.phoneNumberTV);
            //holder.mDeleteBtn = (Button) view.findViewById(R.id.deleteBtn);
            view.setTag(holder);


        } else {
            holder = (ViewHolder) view.getTag();
        }

        String title = messageslist.get(position).getTitle();
        String userId = messageslist.get(position).getUserId();

        ParseUser currentUser = ParseUser.getCurrentUser();
        String currentUserId = currentUser.getObjectId();

//		if(userId.equals(currentUserId)){
//			holder.mDeleteBtn.setVisibility(View.VISIBLE);
//		}

//		holder.mDeleteBtn.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
//
//				// Setting Dialog Message
//				alertDialog.setMessage("delete?");
//
//				// Setting Icon to Dialog
//				alertDialog.setIcon(R.drawable.icon_delete);
//
//				// Setting Positive "Yes" Button
//				alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								ParseObject.createWithoutData("Books",booklist.get(position).getObjectId()).deleteEventually();
//								Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
//				            	Intent intent = new Intent(context, MainActivity.class);
//								context.startActivity(intent);
//								((Activity) context).finish();
//							}
//						});
//				// Setting Negative "NO" Button
//				alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int which) {
//								// Write your code here to invoke NO event
//								dialog.cancel();
//							}
//						});
//				// Showing Alert Message
//				alertDialog.show();
//				}
//   });


        if (title == null || title.isEmpty()) {
            holder.mTitle.setText("No title");
        } else {
            // Set the results into TextViews
            holder.mTitle.setText("Title: " + messageslist.get(position).getTitle());
            holder.mPhoneNumber.setText("Phone: " + messageslist.get(position).getPhoneNumber());
        }


        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View arg0) {


                AlertDialog.Builder bDialogo = new AlertDialog.Builder(context);

                bDialogo.setCancelable(true);

                final String[] items = {"Call", "Delete"};

                bDialogo.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equalsIgnoreCase("Call")) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", messageslist.get(position).getPhoneNumber(), null));
                            context.startActivity(intent);

                        }

                        if (items[item].equalsIgnoreCase("Delete")) {
                            ParseObject.createWithoutData("Messages",messageslist.get(position).getObjectId()).deleteEventually();
                            Toast.makeText(context, "Message deleted", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(context, MessagesActivity.class);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }
                });

                bDialogo.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });


                bDialogo.show();

            }
        });


        return view;
    }
}