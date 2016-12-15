package com.tigerbook.ibrahim.tigerbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Random;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

//-------------------------------------------------------------------
//
//-------------------------------------------------------------------
public class AddBookActivity extends AppCompatActivity {

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    // declare variables for the two buttons
    Button mAddBook;
    EditText mBookTitle, mPrice;
    Spinner mCategory, mCondition  ;
    //-------------------------------------------------------------------



    static boolean SD_AVAILABLE = false;
    static boolean SD_WRITABLE = false;

    static final int IMAGE_SIZE_IN_PIXELS = 600;
    static final int QUALITY_PERCENT = 70;

    static final int TAKE_A_PICTURE = 1;
    static final int CHOOSE_A_PICTURE = 2;
    Uri uPicture = null;

    static String SOURCE_TEMP_FOLDER = "";
    static final String TEMP_FOLDER = "PARSE_TEST_IMAGES";
    static final String TEMP_PICTURE_NAME = "TEMP.wayrasoft";

    String sSourcePictureTEMP = "";
    String sSourceResizedPictureTEMP = "";

    static final String PARSE_IMAGE_NAME = "image.jpg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_main);

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


        //Random object to Create Msg unique Id
        Random random = new Random();
        //create 5 random numbers
        //This gives a random integer between 10000 (inclusive) and 100000 (exclusive), one of 10000,10001,...,99998,99999.
        int rn = random.nextInt(100000 - 10000) + 10000;
        String TEMP_RESIZED_PICTURE_NAME = String.valueOf(rn);

        // Locate Button in activity_home.xml
        mAddBook = (Button) findViewById(R.id.addBookBtn);

        mBookTitle = (EditText) findViewById(R.id.titleET);
        mPrice = (EditText) findViewById(R.id.priceET);

        // Donation type spinner
        mCategory = ((Spinner) findViewById(R.id.categorySpr));
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter .createFromResource(this, R.array.category_list, R.layout.spinner_center_item);
        mCategory.setAdapter(spinnerAdapter);

        // Donation type spinner
        mCondition = ((Spinner) findViewById(R.id.conditionSpr));
        ArrayAdapter<CharSequence> spinnerAdapter2 = ArrayAdapter .createFromResource(this, R.array.condition_list, R.layout.spinner_center_item);
        mCondition.setAdapter(spinnerAdapter2);

        //-------------------------------------------------------------------
        int i = 0;
        int j = 10;

        while (mCreateAppFolders() == false)
        {
            i++;
            if (i >= j)
            {
                onBackPressed();
                return;
            }
        }

        //-------------------------------------------------------------------
        //
        //-------------------------------------------------------------------
        sSourcePictureTEMP = SOURCE_TEMP_FOLDER + "/" + TEMP_PICTURE_NAME;
        sSourceResizedPictureTEMP = SOURCE_TEMP_FOLDER + "/" + TEMP_RESIZED_PICTURE_NAME;


        //-------------------------------------------------------------------


        //-------------------------------------------------------------------
        mAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String bookTitle = mBookTitle.getText().toString();
                String category = mCategory.getSelectedItem().toString();
                String condition = mCondition.getSelectedItem().toString();
                String price = mPrice.getText().toString();
                ParseUser currentUser = ParseUser.getCurrentUser();
                String username = currentUser.getUsername().toString();
                String userId = currentUser.getObjectId();

                if(bookTitle.isEmpty() || price.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Book title and price can't be empty!", Toast.LENGTH_SHORT).show();
                }

                else if(category.equals("Select a category") || condition.equals("select a condition") ){
                    Toast.makeText(getApplicationContext(), "Please select a category and a confition", Toast.LENGTH_SHORT).show();
                }

                else{

                    setProgressBarIndeterminateVisibility(true);
                    //First picture
                    File f = new File(sSourceResizedPictureTEMP);

                    byte[] b = null;
                    try
                    {
                        RandomAccessFile raf = new RandomAccessFile(f, "r");
                        b = new byte[(int) raf.length()];
                        raf.readFully(b);
                    }
                    catch (Exception ex)
                    {
                        //...
                    }

                    //Send data to Parse.com
                    ParseObject po = new ParseObject("Books");
                    po.put("bookTitle", bookTitle);
                    po.put("category", category);
                    po.put("condition", condition);
                    po.put("price", price);
                    po.put("username", username);
                    po.put("userId", userId);

                    //Send first picture
                    if (b != null)
                    {
                        //Toast.makeText(getApplicationContext(), "there is an image", Toast.LENGTH_SHORT).show();

                        ParseFile pf = new ParseFile(PARSE_IMAGE_NAME, b);
                        po.put("img", pf);
                    } else{
                       // Toast.makeText(getApplicationContext(), "No image found", Toast.LENGTH_SHORT).show();

                    }
                    po.saveInBackground(new SaveCallback() {
                        public void done(ParseException ex) {
                            if (ex == null)
                            {
                                Toast.makeText(getApplicationContext(), "You have successfully added the book", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "fail " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        };
                    });

                }
            }

        });
    }
    //-------------------------------------------------------------------
    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_new_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            // =================================================================================================
            case R.id.add_image:

                mAddAPicture();


                break;

        }

        return true;

    }
    //======end menu==========================================
    //-------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED)
        {
            String sSource = "";

            if (requestCode == TAKE_A_PICTURE)
            {
                if (uPicture != null)
                {
                    sSource = mGetSourceFromUri(getContentResolver(), uPicture);
                }
            }


            if (requestCode == CHOOSE_A_PICTURE)
            {
                if (data != null)
                {
                    Uri u = data.getData();
                    if (u != null)
                    {
                        sSource = mGetSourceFromUri(getContentResolver(), u);
                    }
                }
            }

            File fOrigen = new File(sSource);
            if (fOrigen.exists() == true)
            {
                try
                {
                    Bitmap b = mResizePicture(new File(sSource));

                    if (requestCode == TAKE_A_PICTURE || requestCode == CHOOSE_A_PICTURE)
                    {
                        mSaveBitmap(b, sSourceResizedPictureTEMP);
                        //  mShowPictureThumbnail(ivPicture, b);
                    }

                }
                catch (Exception ex)
                {
                    //...
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //-------------------------------------------------------------------
    //-------------------------------------------------------------------
    private void mTakeAPicture()
    {

        File f = new File(sSourcePictureTEMP);
        uPicture = Uri.fromFile(f);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uPicture);
        startActivityForResult(intent, TAKE_A_PICTURE);


    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    private void mChooseAPicture()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_an_app)), CHOOSE_A_PICTURE);

    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    private void mChekSdState()
    {
        String sState = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(sState))
        {
            SD_AVAILABLE = true;
            SD_WRITABLE = true;
        }
        else
        {
            if (Environment.MEDIA_MOUNTED_READ_ONLY.equalsIgnoreCase(sState))
            {
                SD_AVAILABLE = true;
                SD_WRITABLE = false;
            }
            else
            {
                SD_AVAILABLE = false;
                SD_WRITABLE = false;
            }
        }
    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    public boolean mCreateAppFolders()
    {
        mChekSdState();

        if (SD_AVAILABLE == false)
        {
            return false;
        }

        if (SD_WRITABLE == false)
        {
            return false;
        }

        final String ROOT = Environment.getExternalStorageDirectory().toString();

        if (ROOT.equalsIgnoreCase("") == true)
        {
            return false;
        }

        SOURCE_TEMP_FOLDER = ROOT + "/" + TEMP_FOLDER;
        File fCarpetaTemporal = new File(SOURCE_TEMP_FOLDER);
        fCarpetaTemporal.mkdirs();

        return true;
    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    public String mGetSourceFromUri(ContentResolver cr, Uri u)
    {
        Cursor c = cr.query(u, null, null, null, null);
        if (c == null)
        {
            return u.getPath();
        }
        else
        {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
        }
    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    public Bitmap mResizePicture (File f)
    {
        try
        {
            Bitmap b = null;

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int iScale = 1;
            if (o.outHeight > IMAGE_SIZE_IN_PIXELS || o.outWidth > IMAGE_SIZE_IN_PIXELS)
            {
                iScale = (int)Math.pow(2, (int) Math.round(Math.log(IMAGE_SIZE_IN_PIXELS / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = iScale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            ExifInterface eif = new ExifInterface(f.getAbsolutePath());
            int iOrientation = eif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int iRotate = 0;

            switch (iOrientation)
            {
                //----------
                case ExifInterface.ORIENTATION_ROTATE_270 :
                    iRotate = -90;
                    break;

                //----------
                case ExifInterface.ORIENTATION_ROTATE_180 :
                    iRotate = 180;
                    break;

                //----------
                case ExifInterface.ORIENTATION_ROTATE_90 :
                    iRotate = 90;
                    break;
            }

            if (iRotate != 0)
            {
                Matrix m = new Matrix();
                m.setRotate(iRotate);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, false);
            }

            return b;
        }
        catch (Exception ex)
        {
            //...
        }

        return null;
    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    public boolean mSaveBitmap(Bitmap b, String s)
    {
        File f = new File(s);
        if (f.exists() == true)
        {
            f.delete();
        }

        try
        {
            FileOutputStream fos = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, QUALITY_PERCENT, fos);
            fos.close();

            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    //-------------------------------------------------------------------
    //
    //-------------------------------------------------------------------
    private void mAddAPicture()
    {
        AlertDialog.Builder bDialogo = new AlertDialog.Builder(this);

        bDialogo.setCancelable(true);

        final String[] items = {getString(R.string.add_a_picture_option_1), getString(R.string.add_a_picture_option_2)};

        bDialogo.setItems(items, new  DialogInterface.OnClickListener() {
            public void  onClick(DialogInterface dialog, int item) {
                if (items[item].equalsIgnoreCase(getString(R.string.add_a_picture_option_1)))
                {
                    mTakeAPicture();
                }

                if (items[item].equalsIgnoreCase(getString(R.string.add_a_picture_option_2)))
                {
                    mChooseAPicture();
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


}