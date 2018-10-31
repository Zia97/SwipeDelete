package swipedelete.swipedelete;

import android.app.AlertDialog;
import android.graphics.Path;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler, Serializable {
    public static ArrayList<FolderModel> allImageFolders = new ArrayList<>();
    public static ArrayList<String> allImageFoldersAbsolutePaths = new ArrayList<>();
    public static ArrayList<FolderModel> allVideoFolders = new ArrayList<>();
    public static ArrayList<String> allVideoFoldersAbsolutePaths = new ArrayList<>();
    public static ArrayList<FolderModel> allFolders = new ArrayList<>();

    Menu mOptionsMenu;
    boolean boolean_folder;
    boolean onCreateCalled = false;
    Adapter_PhotosFolder obj_adapter;
    GridView gridViewFolderHolder;
    private static final int REQUEST_PERMISSIONS = 100;
    SubBillingProcessor bp;

    private String removeAds = "swipedelete.removeads";

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateCalled = true;
        setContentView(R.layout.activity_main);
        gridViewFolderHolder = findViewById(R.id.gv_folder);

        bp = new SubBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj/g4aFc1Snixe61A2v3EhLc3KURs84sLY1eKCqQAb4nJalYxGnDsO0KuiQF/IXv1ZHXew+z+n5A7+FcEbj4+0DJAi9QalMMhxR+g2j0XheN8qAzw45VMdslvvvNfhcKCQKVai9i6FFzlU7FnYl94K5xL9AZkfGuUc7fI54s6eaXnG4tkB0KUXPo1xR0ymq0t7ebWys/l7WDXCYvtFNTlzma05sbgWWaE7gnhtEuV75zo3NLD6fXohQjAWOlwy2OwUd9wlG8LWAKfFvszTu7HOdojuX8VCQhvy0GuJTMuPPWR0T+BKazvBSmbhcqS5VqYBAWd5R+KIncseDtM2g8E0QIDAQAB", this);
        bp.initialize();

        //Initialise adds
        MobileAds.initialize(this, "ca-app-pub-1994840857400080/7051857651");

        gridViewFolderHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
                intent.putExtra("value", i);
                startActivity(intent);
                finish();
            }
        });

        CheckPermissions();
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
            getMenuInflater().inflate(R.menu.removeads, mOptionsMenu);
        }
    }

    public void removeAdsButtonClicked(MenuItem item)
    {
        if(bp.isPurchased("swipedelete.removeads"))
        {
            Toast myToat = Toast.makeText(this,"Ads have already been removed!", Toast.LENGTH_LONG);
            myToat.show();
        }
        else {
                bp.purchase(this, "swipedelete.removeads");
                onRestart();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        setContentView(R.layout.activity_main);
        gridViewFolderHolder = findViewById(R.id.gv_folder);

        bp = new SubBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj/g4aFc1Snixe61A2v3EhLc3KURs84sLY1eKCqQAb4nJalYxGnDsO0KuiQF/IXv1ZHXew+z+n5A7+FcEbj4+0DJAi9QalMMhxR+g2j0XheN8qAzw45VMdslvvvNfhcKCQKVai9i6FFzlU7FnYl94K5xL9AZkfGuUc7fI54s6eaXnG4tkB0KUXPo1xR0ymq0t7ebWys/l7WDXCYvtFNTlzma05sbgWWaE7gnhtEuV75zo3NLD6fXohQjAWOlwy2OwUd9wlG8LWAKfFvszTu7HOdojuX8VCQhvy0GuJTMuPPWR0T+BKazvBSmbhcqS5VqYBAWd5R+KIncseDtM2g8E0QIDAQAB", this);
        bp.initialize();

        gridViewFolderHolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PhotosActivity.class);
                intent.putExtra("value", i);
                startActivity(intent);
            }
        });

        CheckPermissions();
    }

    private void CheckPermissions() {
        //If the app does not have permission to write and read external storage
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) || (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
            }
        }
        //If the app does have permission to write and read external storage
        else {
            FindImagePaths();
        }
    }

    private void LoadAdds() {
        //Load adds
        mAdView = findViewById(R.id.adViewXML);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }


    public ArrayList<FolderModel> FindImagePaths() {
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

        final String orderBy = MediaStore.Images.Media.DATE_MODIFIED;
        cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

        //While there is more data
        while (cursor.moveToNext()) {
            //Get absolute path of image
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i < allImageFolders.size(); i++) {
                //Check collection of all folders and see if the current image belongs to that folder
                if (allImageFolders.get(i).getFolderName().equals(cursor.getString(column_index_folder_name))) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            //If image belongs to that folder
            if (boolean_folder) {
                //Add the image to the folder
                if (allImageFolders.get(int_position).getImagePaths() != null) {
                    allImageFolders.get(int_position).getImagePaths().add(absolutePathOfImage);
                }
            } else {
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

        final String orderBy2 = MediaStore.Images.Media.DATE_MODIFIED;
        cursor = getApplicationContext().getContentResolver().query(uri, projection2, null, null, orderBy2 + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);

        //While there is more data
        while (cursor.moveToNext()) {
            //Get absolute path of image
            absolutePathOfImage = cursor.getString(column_index_data);

            for (int i = 0; i < allVideoFolders.size(); i++) {
                //Check collection of all folders and see if the current image belongs to that folder
                if (allVideoFolders.get(i).getFolderName().equals(cursor.getString(column_index_folder_name) + "_Videos")) {
                    boolean_folder = true;
                    int_position = i;
                    break;
                } else {
                    boolean_folder = false;
                }
            }

            //If image belongs to that folder
            if (boolean_folder) {
                //Add the image to the folder
                if (allVideoFolders.get(int_position).getImagePaths() != null) {
                    allVideoFolders.get(int_position).getImagePaths().add(absolutePathOfImage);
                }
            } else {
                //Create a new folder model and add the image to that folder
                ArrayList<String> imagePathsInFolder = new ArrayList<>();
                imagePathsInFolder.add(absolutePathOfImage);
                FolderModel newFolderModel = new FolderModel();
                newFolderModel.setFolderName(cursor.getString(column_index_folder_name) + "_Videos");
                newFolderModel.setImagePaths(imagePathsInFolder);

                allVideoFolders.add(newFolderModel);
            }

        }
        allFolders.addAll(allImageFolders);
        allFolders.addAll(allVideoFolders);

        obj_adapter = new Adapter_PhotosFolder(getApplicationContext(), allFolders);
        gridViewFolderHolder.setAdapter(obj_adapter);
        return allFolders;
    }


    @Override
//The result when requesting permissions
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean allResultsGranted = true;

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                for (int i = 0; i < grantResults.length; i++) {
                    //If the permissions are granted upon request
                    if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allResultsGranted = false;
                        Toast.makeText(MainActivity.this, "The app is unable to function correctly as you have denied storage access permissions", Toast.LENGTH_LONG).show();
                    }
                }

                if (allResultsGranted) {
                    FindImagePaths();
                }
            }
        }
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details)
    {
        onRestart();
    }

    @Override
    public void onPurchaseHistoryRestored()
    {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error)
    {

    }

    @Override
    public void onBillingInitialized()
    {
        if(!bp.isPurchased(removeAds))
        {
            LoadAdds();
        }
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
}
