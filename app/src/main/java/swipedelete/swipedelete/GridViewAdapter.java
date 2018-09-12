package swipedelete.swipedelete;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;


public class GridViewAdapter extends ArrayAdapter<FolderModel> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<FolderModel> al_menu = new ArrayList<>();
    int int_position;


    public GridViewAdapter(Context context, ArrayList<FolderModel> al_menu, int int_position) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
    }

    @Override
    public int getCount() {
        return al_menu.get(int_position).getImagePaths().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (al_menu.get(int_position).getImagePaths().size() > 0) {
            return al_menu.get(int_position).getImagePaths().size();
        } else {

            return 1;
        }
    }

    @Override
    public long getItemId(int position)
    {
        if (al_menu.get(int_position).getImagePaths().get(position).toString().endsWith("mp4"))
        {

            Intent intent = new Intent(context.getApplicationContext(), swipedelete.swipedelete.VideoSwitcher.class);
            intent.addCategory(al_menu.get(int_position).getImagePaths().get(position));
            intent.putExtra("Folder", int_position);
            intent.putExtra("files",al_menu.get(int_position).getImagePaths());
            context.startActivity(intent);
        } else
            {
            Intent intent = new Intent(context.getApplicationContext(), swipedelete.swipedelete.ImageSwitcher.class);
            intent.addCategory(al_menu.get(int_position).getImagePaths().get(position));
            intent.putExtra("Folder", int_position);
            intent.putExtra("files",al_menu.get(int_position).getImagePaths());
            context.startActivity(intent);
        }
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_foldern.setVisibility(View.GONE);
        viewHolder.tv_foldersize.setVisibility(View.GONE);

        String filePath = (al_menu.get(int_position).getImagePaths().get(position));

        File testFile = new File(filePath);
        if (testFile.exists()) {
            if (!isImageFile(filePath))
            {
                ContentResolver crThumb = getContext().getContentResolver();

                int id = getVideoIdFromFilePath(filePath, crThumb);

                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MICRO_KIND, options);

                if (curThumb != null)
                {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    curThumb.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Glide.with(context)
                            .load(stream.toByteArray())
                            .asBitmap()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(viewHolder.iv_image);
                    curThumb.recycle();
                }
            }
            else
                {
                Glide.with(context).load(filePath)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(viewHolder.iv_image);
            }
        }


        return convertView;

    }

    private int getVideoIdFromFilePath(String filePath, ContentResolver contentResolver) {

        int videoId;

        Uri videosUri = MediaStore.Video.Media.getContentUri("external");


        String[] projection = {MediaStore.Video.VideoColumns._ID};

        Cursor cursor = contentResolver.query(videosUri, projection, MediaStore.Video.VideoColumns.DATA + " LIKE ?", new String[]{filePath}, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(projection[0]);
        videoId = cursor.getInt(columnIndex);

        //Log.d(TAG,"Video ID is " + videoId);
        cursor.close();
        return videoId;
    }

    private static class ViewHolder
    {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
    }

    public static boolean isImageFile(String path)
    {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }


}