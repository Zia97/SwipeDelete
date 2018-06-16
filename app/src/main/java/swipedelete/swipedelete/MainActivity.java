package swipedelete.swipedelete;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    public static ArrayList<FolderModel> allFolders = new ArrayList<>();
    boolean boolean_folder;
    boolean onCreateCalled = false;
    Adapter_PhotosFolder obj_adapter;
    GridView gridViewFolderHolder;
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onCreateCalled = true;
        setContentView(R.layout.activity_main);
        gridViewFolderHolder = findViewById(R.id.gv_folder);

        gridViewFolderHolder.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
                intent.putExtra("value", i);
                startActivity(intent);
            }
        });

        CheckPermissions();
    }

    private void CheckPermissions()
    {
        //If the app does not have permission to write and read external storage
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
        {
            if ((!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)))
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        }
        //If the app does have permission to write and read external storage
        else {
            FindImagePaths();
        }
    }


    public ArrayList<FolderModel> FindImagePaths()
    {
        allFolders.clear();

        int int_position = 0;
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;

        String absolutePathOfImage = null;
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        //While there is more data
        while (cursor.moveToNext())
        {
            //Get absolute path of image
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i <  allFolders.size(); i++)
            {
                //Check collection of all folders and see if the current image belongs to that folder
                if (allFolders.get(i).getFolderName().equals(cursor.getString(column_index_folder_name)))
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
                allFolders.get(int_position).getImagePaths().add(absolutePathOfImage);
            }
            else
            {
                //Create a new folder model and add the image to that folder
                ArrayList<String> imagePathsInFolder = new ArrayList<>();
                imagePathsInFolder.add(absolutePathOfImage);
                FolderModel newFolderModel = new FolderModel();
                newFolderModel.setFolderName(cursor.getString(column_index_folder_name));
                newFolderModel.setImagePaths(imagePathsInFolder);

                allFolders.add(newFolderModel);
            }

        }

        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(),  allFolders);
        gridViewFolderHolder.setAdapter(obj_adapter);
        return  allFolders;
    }

    @Override
    //The result when requesting permissions
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        boolean allResultsGranted = true;

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++)
                {
                    //If the permissions are granted upon request
                    if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    {
                        allResultsGranted = false;
                        Toast.makeText(MainActivity.this, "The app is unable to function correctly as you have denied storage access permissions", Toast.LENGTH_LONG).show();
                    }
                }

                if(allResultsGranted)
                {
                    FindImagePaths();
                }
            }
        }
    }

}
