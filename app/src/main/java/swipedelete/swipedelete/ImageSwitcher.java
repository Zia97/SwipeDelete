package swipedelete.swipedelete;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class ImageSwitcher extends AppCompatActivity implements Serializable {

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
    private Menu mOptionsMenu;
    private boolean autoscrollEnabled;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_switcher_xml);
        imageView = findViewById(R.id.imageViewXML);
        imageView.setBackgroundColor(Color.rgb(0, 0, 0));




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

        imageView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                gdt.onTouchEvent(event);
                return true;
            } });

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        mOptionsMenu = menu;
        updateOptionsMenu();
        return true;
    }

    private void updateOptionsMenu()
    {
        if (mOptionsMenu != null) {
            onPrepareOptionsMenu(mOptionsMenu);
            getMenuInflater().inflate(R.menu.autoscroll, mOptionsMenu);
        }
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

    public void AutoScrollButtonClicked(MenuItem item)
    {
        if(autoscrollEnabled)
        {
            autoscrollEnabled = false;
            stopTimer();
            Toast.makeText(this.getApplicationContext(),"Auto-scroll stopped", Toast.LENGTH_LONG).show();
        }
        else
        {
            autoscrollEnabled = true;
            startTimer();
            Toast.makeText(this.getApplicationContext(),"Auto-scroll enabled", Toast.LENGTH_LONG).show();
        }

    }

    private void LoadImageIntoImageView()
    {
        Glide.with(this.getApplicationContext()).load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

    @Override
    public void onBackPressed()
    {
        allImagesInFolder.clear();
        stopTimer();
        finish();
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

        Glide.with(this.getApplicationContext()).load(allImagesInFolder.get(currentPositionInPhotoArray))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
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
            Glide.with(this.getApplicationContext()).load(allImagesInFolder.get(currentPositionInPhotoArray))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(imageView);
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

        Glide.with(this.getApplicationContext()).load(allImagesInFolder.get(currentPositionInPhotoArray))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

    private void LoadAdds()
    {
        //Load adds
        mAdView = findViewById(R.id.adViewXML);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

    private void stopTimer()
    {
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }

    private void startTimer()
    {
        mTimer1 = new Timer();
        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable()
                {
                    public void run()
                    {
                        GoToNextPicture();
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 1, 2000);
    }
}
