package swipedelete.swipedelete;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class VideoSwitcher extends AppCompatActivity implements BillingProcessor.IBillingHandler
{
    private VideoView videoView;
    private TextView textView;
    private String imagePath;
    private ImageView imageView;
    private Button deleteButton;
    private String imageFolder;
    private static ArrayList<String> allImagesInFolder = new ArrayList<>();
    private int currentPositionInPhotoArray;
    private int folderPosition;
    private AdView mAdView;
    private Menu mOptionsMenu;
    private GestureDetector gdt;
    private static final int MIN_SWIPPING_DISTANCE = 50;
    private static final int THRESHOLD_VELOCITY = 50;
    private boolean autoscrollEnabled = false;
    private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler = new Handler();
    private BillingProcessor bp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CheckAdds();
        if(bp.isPurchased("swipedelete.removeads"))
        {
            setContentView(R.layout.noads_video_switcher_xml);
        }
        else
        {
            setContentView(R.layout.video_switcher_xml);
        }

        videoView = findViewById(R.id.videoViewXML);
        textView = findViewById(R.id.vidSize);
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

    private void CheckAdds()
    {
        bp = new SubBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj/g4aFc1Snixe61A2v3EhLc3KURs84sLY1eKCqQAb4nJalYxGnDsO0KuiQF/IXv1ZHXew+z+n5A7+FcEbj4+0DJAi9QalMMhxR+g2j0XheN8qAzw45VMdslvvvNfhcKCQKVai9i6FFzlU7FnYl94K5xL9AZkfGuUc7fI54s6eaXnG4tkB0KUXPo1xR0ymq0t7ebWys/l7WDXCYvtFNTlzma05sbgWWaE7gnhtEuV75zo3NLD6fXohQjAWOlwy2OwUd9wlG8LWAKfFvszTu7HOdojuX8VCQhvy0GuJTMuPPWR0T+BKazvBSmbhcqS5VqYBAWd5R+KIncseDtM2g8E0QIDAQAB", this);
        bp.initialize();
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
        videoView.setVideoPath(imagePath);
        File myFile = new File(imagePath);
        String videosize = getFolderSizeLabel(myFile);
        textView.setText(videosize);
        videoView.start();
        textView.bringToFront();
    }

    @Override
    public void onBackPressed()
    {
        allImagesInFolder.clear();
        stopTimer();
        finish();
    }

    private void LoadAdds()
    {
        if(!bp.isPurchased("swipedelete.removeads"))
        {
            //Load adds
            mAdView = findViewById(R.id.adViewXML);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
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

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
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

    public static String getFolderSizeLabel(File file)
    {
        long size = getFolderSize(file) / 1024; // Get size and convert bytes into Kb.
        if (size >= 1024) {
            return (size / 1024) + " Mb";
        } else {
            return size + " Kb";
        }
    }

    public static long getFolderSize(File file) {
        long size = 0;
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                size += getFolderSize(child);
            }
        } else {
            size = file.length();
        }
        return size;
    }
}
