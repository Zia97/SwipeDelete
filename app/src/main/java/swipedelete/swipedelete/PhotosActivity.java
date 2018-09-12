package swipedelete.swipedelete;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;
    public static ArrayList<FolderModel> allImageFolders = new ArrayList<>();
    public static ArrayList<FolderModel> allFolders = new ArrayList<>();
    public static ArrayList<FolderModel> allVideoFolders = new ArrayList<>();
    boolean boolean_folder;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SetGridView();
        LoadAdds();
    }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        RegenerateAllFolders();
        DrawRegeneratedGridView();
    }

    private void LoadAdds()
    {
        //Load adds
        mAdView = findViewById(R.id.adViewXML);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void SetGridView()
    {
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this, MainActivity.allFolders, int_position);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Deletion of photos may have occurred, reloading photos is required
    private void RegenerateAllFolders()
    {
        boolean_folder = false;
        allImageFolders.clear();
        allFolders.clear();
        allVideoFolders.clear();


        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_MODIFIED ;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        //While there is more data
        while (cursor.moveToNext())
        {
            //Get absolute path of image
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i <  allImageFolders.size(); i++)
            {
                //Check collection of all folders and see if the current image belongs to that folder
                if (allImageFolders.get(i).getFolderName().equals(cursor.getString(column_index_folder_name)))
                {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else
                {
                    boolean_folder = false;
                }
            }

            //If image belongs to that folder
            if (boolean_folder)
            {
                //Add the image to the folder
                if( allImageFolders.get(int_position).getImagePaths()!=null)
                {
                    allImageFolders.get(int_position).getImagePaths().add(absolutePathOfImage);
                }
            }
            else
            {
                //Create a new folder model and add the image to that folder
                ArrayList<String> imagePathsInFolder = new ArrayList<>();
                imagePathsInFolder.add(absolutePathOfImage);
                FolderModel newFolderModel = new FolderModel();
                newFolderModel.setFolderName(cursor.getString(column_index_folder_name));
                newFolderModel.setImagePaths(imagePathsInFolder);

                allImageFolders.add(newFolderModel);
            }

        }


        absolutePathOfImage = null;

        int_position = 0;
        boolean_folder = false;
        allVideoFolders.clear();

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection2 = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        final String orderBy2 = MediaStore.Images.Media.DATE_MODIFIED ;
        cursor = getApplicationContext().getContentResolver().query(uri, projection2, null, null, orderBy2 + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

        //While there is more data
        while (cursor.moveToNext())
        {
            //Get absolute path of image
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i <  allVideoFolders.size(); i++)
            {
                //Check collection of all folders and see if the current image belongs to that folder
                if (allVideoFolders.get(i).getFolderName().equals(cursor.getString(column_index_folder_name)+"_Videos"))
                {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else
                {
                    boolean_folder = false;
                }
            }

            //If image belongs to that folder
            if (boolean_folder)
            {
                //Add the image to the folder
                if( allVideoFolders.get(int_position).getImagePaths()!=null)
                {
                    allVideoFolders.get(int_position).getImagePaths().add(absolutePathOfImage);
                }
            }
            else
            {
                //Create a new folder model and add the image to that folder
                ArrayList<String> imagePathsInFolder = new ArrayList<>();
                imagePathsInFolder.add(absolutePathOfImage);
                FolderModel newFolderModel = new FolderModel();
                newFolderModel.setFolderName(cursor.getString(column_index_folder_name)+"_Videos");
                newFolderModel.setImagePaths(imagePathsInFolder);

                allVideoFolders.add(newFolderModel);
            }

        }
        allFolders.addAll(allImageFolders);
        allFolders.addAll(allVideoFolders);
    }

    //Redraw new grid view accounting for deleted photos
    private void DrawRegeneratedGridView()
    {
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this, allFolders, int_position);
        gridView.setAdapter(adapter);
    }

}