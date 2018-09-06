package swipedelete.swipedelete;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
        imageView.setBackgroundColor(Color.rgb(0, 0, 0));


        Set<String> imagePaths = getIntent().getCategories();
        folderPosition = getIntent().getIntExtra("Folder",0);

        Object[] imagePathArray = imagePaths.toArray();


        if (imagePathArray.length == 1)
        {
            imagePath = imagePathArray[0].toString();
            Log.e("test", imagePath);

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

        Collections.sort(Arrays.asList(files), new FileComparator());

        Collections.reverse(Arrays.asList(files));

        for (int i = 0; i <files.length; i++)
        {
            if(files[i].toString().endsWith("jpg") || files[i].toString().endsWith("png") || files[i].toString().endsWith("gif") || files[i].toString().endsWith("jpeg") || files[i].toString().endsWith("bmp")|| files[i].toString().endsWith("webp") || files[i].toString().endsWith("JPG"))
            {
                allImagesInFolder.add(files[i].toString());
//                Date dt1 = new Date(files[i].lastModified());
//                Log.e("Name",files[i].getName()+"   "+dt1);
            }
        }

        for(int i=0; i<allImagesInFolder.size(); i++)
        {
            if(allImagesInFolder.get(i).toString().equals(imagePath))
            {
                Log.e("path", imagePath);
                currentPositionInPhotoArray = i;
                Log.e("filelength", currentPositionInPhotoArray+" @@@");
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
        allImagesInFolder.clear();
        finish();
//        Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
//       // intent.putExtra("value", i);
//        startActivity(intent);
//        finish();
    }

    public void deleteButtonClicked(View view)
    {
        GoToNextPicture();
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

        Log.e("ret", String.valueOf(currentPositionInPhotoArray));

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
