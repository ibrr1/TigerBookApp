package com.tigerbook.ibrahim.tigerbook;
 
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

public class MainActivityListViewAdapter extends BaseAdapter {
 
    // Declare Variables
    Context context;
    LayoutInflater inflater;
	ImageLoader imageLoader;
	ProgressDialog mProgressDialog;

    private List<TigerBook> booklist = null;
    private ArrayList<TigerBook> arraylist;
 
    public MainActivityListViewAdapter(Context context, List<TigerBook> booklist) {
        this.context = context;
        this.booklist = booklist;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<TigerBook>();
        this.arraylist.addAll(booklist);
		imageLoader = new ImageLoader(context);

    }
 
    public class ViewHolder {
        TextView mTitle;
        TextView mPrice;
		ImageView mImage;
		//Button mDeleteBtn;

    }
 
    @Override
    public int getCount() {
        return booklist.size();
    }
 
    @Override
    public TigerBook getItem(int position) {
        return booklist.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_item, null);
            // Locate the TextViews in listview_item.xml
            holder.mTitle = (TextView) view.findViewById(R.id.titleTV);
            holder.mPrice = (TextView) view.findViewById(R.id.priceTV);

            // Locate the ImageView in listview_item.xml
         	holder.mImage = (ImageView) view.findViewById(R.id.image);
         	//holder.mDeleteBtn = (Button) view.findViewById(R.id.deleteBtn);
            view.setTag(holder);


        } else {
            holder = (ViewHolder) view.getTag();
        }

        String title = booklist.get(position).getTitle();
        String img = booklist.get(position).getImg();
        String userId = booklist.get(position).getUserId();

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



        if(title == null || title.isEmpty()){
            holder.mTitle.setText("No title");
            holder.mTitle.setText("No Price available");
        }

        else{
        // Set the results into TextViews
            holder.mTitle.setText(booklist.get(position).getTitle());
            holder.mPrice.setText("$"+ booklist.get(position).getPrice());

        }

        if(img == null){
        	holder.mImage.setBackgroundResource(R.drawable.temp_img);
        }
        else{
        	holder.mImage.setVisibility(View.VISIBLE);
            imageLoader.DisplayImage(booklist.get(position).getImg(),holder.mImage);

        }

        // Listen for ListView Item Click
        view.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
            	
            	Intent intent = new Intent(context, BookActivity.class);
				intent.putExtra("title", (booklist.get(position).getTitle()));
				intent.putExtra("img", (booklist.get(position).getImg()));
				intent.putExtra("category", (booklist.get(position).getCategory()));
				intent.putExtra("condition", (booklist.get(position).getCondition()));
				intent.putExtra("price", (booklist.get(position).getPrice()));
				intent.putExtra("userId", (booklist.get(position).getUserId()));
                intent.putExtra("bookObjectId", (booklist.get(position).getObjectId()));
				context.startActivity(intent);
            	
            	
            }
        });



        return view;
    }
}