package com.example.ngokhai.photogalleryapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    File currentImage;
    File currentCaption;
    String[] imageList;
    String[] captionList;
    Date currentStamp;
    int imgPosition;
    int capPosition;

    static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try
        {
            currentImage = readImageFile();
            currentCaption = readCaptionFile();
            setImage();
        }
        catch(Exception e)
        {
            Toast.makeText(MainActivity.this, "No images!", Toast.LENGTH_SHORT).show();
        }

    }

    private void setImage() throws Exception
    {
        String[] time;
        ImageView capturedImage = findViewById(R.id.imageViewResult);
        EditText captionImage = findViewById(R.id.editTextCaption);
        TextView timeStamp = findViewById(R.id.textViewtime);
        StringBuilder text = new StringBuilder();
        try
        {
            time = currentImage.getName().toString().split("_");
            try
            {
                currentStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").parse(time[1]);
            }
            catch (java.text.ParseException e)
            {

            }
            BufferedReader reader = new BufferedReader(new FileReader(currentCaption));
            String line;
            while ((line = reader.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            reader.close();
        }
        catch(Exception e)
        {

        }
        finally
        {
            captionImage.setText(text,TextView.BufferType.EDITABLE);
            capturedImage.setImageBitmap(BitmapFactory.decodeFile(currentImage.getAbsolutePath()));
            timeStamp.setText(currentStamp.toString());
        }
    }

    private File readImageFile() throws Exception
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageList = dir.list(new FilenameFilter()
        {
            File image;
            @Override
            public boolean accept(File dir, String name)
            {
                if (name.endsWith(".jpg"))
                {
                    return true;
                }
                image = new File(dir.getAbsolutePath() + "/" + name);
                return image.isDirectory();
            }
        });
        return new File(dir, imageList[imgPosition]);
    }

    private File readCaptionFile() throws Exception
    {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        captionList = dir.list(new FilenameFilter()
        {
            File caption;
            @Override
            public boolean accept(File dir, String name)
            {
                if (name.endsWith(".txt"))
                {
                    return true;
                }
                caption = new File(dir.getAbsolutePath() + "/" + name);
                return caption.isDirectory();
            }
        });
        return new File(dir, captionList[capPosition]);
    }

    private File createImageFile() throws Exception
    {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy-HHmmss").format(new Date());
        String imageFileName = "Image_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        File caption = File.createTempFile(
                imageFileName,
                ".txt",
                storageDir
        );
        currentImage = image;
        currentCaption = caption;
        return image;
    }
    public void SnapPicture(View v)
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            }
            catch(Exception ex)
            {

            }
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.ngokhai.photogalleryapp",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                imgPosition = imageList.length ;
                capPosition = captionList.length;
            }
        }
    }

    public void SaveCaption(View v)
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        TextView captionImage = findViewById(R.id.editTextCaption);
        if (currentCaption != null)
        {
            try {
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(currentCaption));
                writer.write(captionImage.getText().toString());
                writer.close();
                Toast.makeText(MainActivity.this, "Caption Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

            }
        }
        else
        {
            Toast.makeText(MainActivity.this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveLeft(View v)
    {
        try
        {
            if (imgPosition > 0 && capPosition > 0)
            {
                imgPosition = imgPosition - 1;
                capPosition = capPosition - 1;
            }
            currentImage = readImageFile();
            currentCaption = readCaptionFile();
            setImage();
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "No Image Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void moveRight(View v)
    {
        try
        {
            if (imgPosition < imageList.length - 1 && capPosition < captionList.length - 1)
            {
                imgPosition = imgPosition + 1;
                capPosition = capPosition + 1;
            }
            currentImage = readImageFile();
            currentCaption = readCaptionFile();
            setImage();
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "No Image Found!", Toast.LENGTH_SHORT).show();
        }
    }

    public void SearchActivity(View v)
    {
        final Intent intent = new Intent(this,SearchActivity.class);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null)
        {
            imageList = data.getStringArrayExtra("01");
            captionList = data.getStringArrayExtra("02");
            for (int i = 0; i < imageList.length; i ++)
            {
                Toast.makeText(MainActivity.this, imageList[i], Toast.LENGTH_SHORT).show();
            }
        }
        try
        {
            //currentImage = readImageFile();
            //currentCaption = readCaptionFile();
            //imgPosition = 0;
            //capPosition = 0;
            setImage();
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, "Error OnActivityResult!", Toast.LENGTH_SHORT).show();
        }
    }
}
