package swipedelete.swipedelete;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class GridViewAdapter extends ArrayAdapter<FolderModel> {

    Context context;
    ViewHolder viewHolder;
    ArrayList<FolderModel> al_menu = new ArrayList<>();
    int int_position;


    public GridViewAdapter(Context context, ArrayList<FolderModel> al_menu,int int_position) {
        super(context, R.layout.adapter_photosfolder, al_menu);
        this.al_menu = al_menu;
        this.context = context;
        this.int_position = int_position;
    }

    @Override
    public int getCount()
    {
        int counter =0;

        Log.e("ADAPTER LIST SIZE", al_menu.get(int_position).getImagePaths().size() + "");
        Log.e("ADAPTER LIST NAME", al_menu.get(int_position).getFolderName() + "");
        return al_menu.get(int_position).getImagePaths().size();


//        for (int i = 0; i <al_menu.get(int_position).getImagePaths().size(); i++)
//        {
//            if(al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("jpg") || al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("png") || al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("gif") || al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("jpeg") || al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("bmp")|| al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("webp") || al_menu.get(int_position).getImagePaths().get(i).toString().endsWith("JPG"))
//            {
//                  File tempFile = new File(al_menu.get(int_position).getImagePaths().get(i));
//
//                  if(tempFile.exists())
//                  {
//                      counter++;
//                  }
////                Date dt1 = new Date(files[i].lastModified());
////                Log.e("Name",files[i].getName()+"   "+dt1);
//            }
//        }
//        return counter;
//        //return al_menu.get(int_position).getImagePaths().size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        if (al_menu.get(int_position).getImagePaths().size() > 0)
        {
            Log.e("viewtypecount", String.valueOf(al_menu.get(int_position).getImagePaths().size()));
            return al_menu.get(int_position).getImagePaths().size();
        } else
            {
            return 1;
        }
    }

    @Override
    public long getItemId(int position)
    {
        Intent intent = new Intent(context.getApplicationContext(), swipedelete.swipedelete.ImageSwitcher.class);
        intent.addCategory(al_menu.get(int_position).getImagePaths().get(position));
        intent.putExtra("Folder",int_position);
        context.startActivity(intent);

        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_photosfolder, parent, false);
            viewHolder.tv_foldern = (TextView) convertView.findViewById(R.id.tv_folder);
            viewHolder.tv_foldersize = (TextView) convertView.findViewById(R.id.tv_folder2);
            viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);

            convertView.setTag(viewHolder);
        }
        else
            {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_foldern.setVisibility(View.GONE);
        viewHolder.tv_foldersize.setVisibility(View.GONE);

        File tempFile = new File(al_menu.get(int_position).getImagePaths().get(position));

        if(tempFile.exists())
        {
            Glide.with(context).load(al_menu.get(int_position).getImagePaths().get(position))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(viewHolder.iv_image);
        }

        return convertView;

    }

    private static class ViewHolder
    {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
    }


}