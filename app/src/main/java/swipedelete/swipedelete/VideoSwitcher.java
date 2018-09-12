package swipedelete.swipedelete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class VideoSwitcher extends AppCompatActivity
{
    private VideoView videoView;
    private String imagePath;
    private ImageView imageView;
    private Button deleteButton;
    private String imageFolder;
    private static ArrayList<String> allImagesInFolder = new ArrayList<>();
    private int currentPositionInPhotoArray;
    private int folderPosition;
    private AdView mAdView;
    private GestureDetector gdt;
    private static final int MIN_SWIPPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_switcher_xml);
        videoView = findViewById(R.id.videoViewXML);
        videoView.setBackgroundColor(Color.rgb(0, 0, 0));
        videoView.setZOrderOnTop(true);


        Set<String> imagePaths = getIntent().getCategories();
        folderPosition = getIntent().getIntExtra("Folder",0);

        Object[] imagePathArray = imagePaths.toArray();


        if (imagePathArray.length == 1)
        {
            imagePath = imagePathArray[0].toString();

            File tempFile = new File(imagePath);
            imageFolder = tempFile.getParent();

        }
        else
        {
            Log.e("Path error", "Incorrect number of paths sent on click");
        }

        GetAllImagesInFolder();
        LoadImageIntoImageView();
        LoadAdds();

        gdt = new GestureDetector(new GestureListener());

        videoView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            } });

    }

    private void LoadImageIntoImageView()
    {
        videoView.setVideoPath(imagePath);
        videoView.start();
    }

    @Override
    public void onBackPressed()
    {
        allImagesInFolder.clear();
        finish();
    }

    private void LoadAdds()
    {
        //Load adds
        mAdView = findViewById(R.id.adViewXML);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void GetAllImagesInFolder()
    {
        allImagesInFolder = (ArrayList<String>) getIntent().getSerializableExtra("files");

        for(int i=0; i<allImagesInFolder.size(); i++)
        {
            if(allImagesInFolder.get(i).toString().equals(imagePath))
            {
                currentPositionInPhotoArray = i;
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            if (e1.getX() - e2.getX() > MIN_SWIPPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
            {
                GoToNextPicture();
                return false;
            }
            else if (e2.getX() - e1.getX() > MIN_SWIPPING_DISTANCE && Math.abs(velocityX) > THRESHOLD_VELOCITY)
            {
                GoToPreviousPicture();
                return false;
            }
            return false;
        }
    }

    public void deleteButtonClicked(View view)
    {
        File file = new File(imagePath);

        boolean deleted = file.delete();

        if(!deleted)
        {
            Log.e("Error","File was not deleted");
        }
        else
        {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            Toast.makeText(this.getApplicationContext(),"Deleted", Toast.LENGTH_SHORT).show();
        }

        allImagesInFolder.remove(currentPositionInPhotoArray);
        if(allImagesInFolder.isEmpty())
        {
            onBackPressed();
        }
        GoToNextPictureAfterDelete();
    }

    public void nextButtonClicked(View view)
    {
        GoToNextPicture();
    }

    public void previousButtonClicked(View view)
    {
        GoToPreviousPicture();
    }

    private void GoToNextPicture()
    {
        currentPositionInPhotoArray++;
        if(allImagesInFolder.size()==0)
        {
            onBackPressed();
        }

        if(currentPositionInPhotoArray> allImagesInFolder.size()-1)
        {
            currentPositionInPhotoArray = 0;
        }

        imagePath = allImagesInFolder.get(currentPositionInPhotoArray);

        LoadImageIntoImageView();
    }

    private void GoToNextPictureAfterDelete()
    {
        if(allImagesInFolder.size()==0)
        {
            onBackPressed();
        }

        if(currentPositionInPhotoArray> allImagesInFolder.size()-1)
        {
            currentPositionInPhotoArray = 0;
        }


        try {
            imagePath = allImagesInFolder.get(currentPositionInPhotoArray);
            LoadImageIntoImageView();
        }
        catch (Exception e)
        {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    private void GoToPreviousPicture()
    {
        currentPositionInPhotoArray--;
        if(allImagesInFolder.size()==0)
        {
            onBackPressed();
        }
        if(currentPositionInPhotoArray<0)
        {
            currentPositionInPhotoArray = allImagesInFolder.size()-1;
        }

        imagePath = allImagesInFolder.get(currentPositionInPhotoArray);

        LoadImageIntoImageView();
    }
}
