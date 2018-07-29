package swipedelete.swipedelete;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ImageSwitcher extends AppCompatActivity {

    private String imagePath;
    private ImageView imageView;
    private Button deleteButton;
    private String imageFolder;
    private static ArrayList<String> allImagesInFolder = new ArrayList<>();
    private int currentPositionInPhotoArray;
    private int folderPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_switcher_xml);
        imageView = findViewById(R.id.imageViewXML);
        //imageView.setScaleType(ImageView.ScaleType.FIT_XY);


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

    }

    private void GetAllImagesInFolder()
    {
        File directory = new File(imageFolder);

        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++)
        {
            if(files[i].toString().endsWith("jpg") || files[i].toString().endsWith("png") || files[i].toString().endsWith("gif") || files[i].toString().endsWith("jpeg"))
            {
                if(files[i].equals(imagePath))
                {
                    currentPositionInPhotoArray = i;
                }

                Log.e("@@@@", files[i].toString());
                allImagesInFolder.add(files[i].toString());
            }
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
        super.onBackPressed();
        allImagesInFolder.clear();
        finish();
        Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
        intent.putExtra("value",folderPosition);
        startActivity(intent);
    }

    public void deleteButtonClicked(View view)
    {
        File file = new File(imagePath);

        boolean deleted = file.delete();

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

        if(!deleted)
        {
            Log.e("Error","File was not deleted");
        }
        else
        {
            Toast.makeText(this.getApplicationContext(),"Deleted", Toast.LENGTH_SHORT).show();
        }
        allImagesInFolder.remove(currentPositionInPhotoArray);
        GoToNextPicture();
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
        if(currentPositionInPhotoArray> allImagesInFolder.size()-1)
        {
            currentPositionInPhotoArray = 0;
        }

        Log.e("ret",allImagesInFolder.get(currentPositionInPhotoArray) );

        imagePath = allImagesInFolder.get(currentPositionInPhotoArray);

        Glide.with(this.getApplicationContext()).load(allImagesInFolder.get(currentPositionInPhotoArray))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }

    private void GoToPreviousPicture()
    {
        currentPositionInPhotoArray--;
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
}
