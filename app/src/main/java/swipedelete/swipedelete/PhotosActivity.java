package swipedelete.swipedelete;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.*;


public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;
    public static ArrayList<FolderModel> allImageFolders = new ArrayList<>();
    public static ArrayList<FolderModel> allFolders = new ArrayList<>();
    public static ArrayList<FolderModel> allVideoFolders = new ArrayList<>();
    boolean boolean_folder;
    public static ArrayList<Integer> selectedPositions = new ArrayList<>();
    private MultiChoiceModeListener multiChoiceModeListener = new MultiChoiceModeListener();
    private ActionMode _actionMode;

    private boolean filesDeleted;


    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Removed regen all folders
        filesDeleted = false;
        allFolders = MainActivity.allFolders;
        SetGridView();
        LoadAdds();
        SetClickListeners();
    }


    private void SetClickListeners() {
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);

        gridView.setOnItemClickListener(new OnItemClickListener());

        gridView.setMultiChoiceModeListener(multiChoiceModeListener);
    }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        filesDeleted = false;
        allFolders.clear();
        selectedPositions.clear();
        try {
            String res = new LoadFilesTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        DrawRegeneratedGridView();
        SetClickListeners();
    }

    private void LoadAdds() {
        //Load adds
        mAdView = findViewById(R.id.adViewXML);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void SetGridView() {
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this, MainActivity.allFolders, int_position);
        gridView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        //unregisterReceiver();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Deletion of photos may have occurred, reloading photos is required
    private void RegenerateAllFolders() {
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
    }

    //Redraw new grid view accounting for deleted photos
    private void DrawRegeneratedGridView() {
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gv_folder);
        int_position = getIntent().getIntExtra("value", 0);
        adapter = new GridViewAdapter(this, allFolders, int_position);
        gridView.setAdapter(adapter);
    }

    public void PhotoActivityDeleteButtonClicked(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Confirm choice");
        builder.setMessage("Are you sure you want to delete the " + gridView.getCheckedItemCount() + " items?");
        builder.setPositiveButton("Confirm",
                (dialog, which) -> {
                    new DeletionTask(PhotosActivity.this).execute();
                    filesDeleted = true;
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });
        builder.show();
    }

    public class OnItemClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (isImageFile(allFolders.get(int_position).getImagePaths().get(position))) {
                Intent intent = new Intent(getApplicationContext(), swipedelete.swipedelete.ImageSwitcher.class);
                intent.addCategory(allFolders.get(int_position).getImagePaths().get(position));
                intent.putExtra("Folder", int_position);
                intent.putExtra("files", allFolders.get(int_position).getImagePaths());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), swipedelete.swipedelete.VideoSwitcher.class);
                intent.addCategory(allFolders.get(int_position).getImagePaths().get(position));
                intent.putExtra("Folder", int_position);
                intent.putExtra("files", allFolders.get(int_position).getImagePaths());
                startActivity(intent);
            }
        }
    }

    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


    public class MultiChoiceModeListener implements GridView.MultiChoiceModeListener {
        @SuppressLint("ResourceType")
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select items to delete");
            getMenuInflater().inflate(R.menu.action, menu);
            _actionMode = mode;
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            for (int pos : selectedPositions) {
                if (gridView.getChildAt(pos) != null) {
                    gridView.getChildAt(pos).setBackgroundColor(Color.WHITE);
                }
            }
            selectedPositions.clear();
            if (filesDeleted) {
                onRestart();
            }
        }

        // @RequiresApi(api = Build.VERSION_CODES.N)
        @TargetApi(Build.VERSION_CODES.N)
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                              boolean checked) {
            setCheckedItemCount(mode);

            int firstPosition = gridView.getFirstVisiblePosition();
            int childPosition = position - firstPosition;

            if (selectedPositions.contains(position)) {
                gridView.getChildAt(childPosition).setBackgroundColor(Color.WHITE);
                selectedPositions.removeIf(s -> s == position);
            } else {

                gridView.getChildAt(childPosition).setBackgroundColor(Color.CYAN);
                selectedPositions.add(position);
            }
            setCheckedItemCount(mode);
        }

        public void setCheckedItemCount(ActionMode mode) {
            int selectCount = gridView.getCheckedItemCount();

            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }

        }
    }

    private class DeletionTask extends AsyncTask<String, Integer, String> {

        private ProgressDialog dialog;

        public DeletionTask(PhotosActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        // Runs in UI before background thread is called
        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Deleting "+selectedPositions.size()+" files...");
            dialog.setCancelable(false);
            dialog.show();

            // Do something like display a progress bar
        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            for (int position : selectedPositions)
            {

                String filePath = allFolders.get(int_position).getImagePaths().get(position);

                File file = new File(filePath);

                boolean deleted = file.delete();

                if (!deleted)
                {
                    Log.e("Error", "File was not deleted");
                } else
                {
                    sendBroadcast(new Intent(ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                }

            }
                try {

                        Thread.sleep(selectedPositions.size()*150);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for (int each : selectedPositions)
                {
                    String fp = allFolders.get(int_position).getImagePaths().get(each);
                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{fp}, null, null);
                }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            multiChoiceModeListener.onDestroyActionMode(_actionMode);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            _actionMode.finish();
            onRestart();
        }
    }

    private class LoadFilesTask extends AsyncTask<String, Integer, String> {
        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {
            RegenerateAllFolders();
            return null;
        }
    }

}

