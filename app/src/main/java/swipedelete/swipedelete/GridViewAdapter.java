package swipedelete.swipedelete;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


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
    public int getCount() {

        Log.e("ADAPTER LIST SIZE", al_menu.get(int_position).getImagePaths().size() + "");
        return al_menu.get(int_position).getImagePaths().size();
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
            return al_menu.get(int_position).getImagePaths().size();
        } else
            {
            return 1;
        }
    }

    @Override
    public long getItemId(int position)
    {
//         Log.e("PATH@@@@@@@@", ""+al_menu.get(int_position).getImagePaths());
//         Log.e("selectedpic",""+al_menu.get(int_position).getImagePaths().get(position));
        Glide.with(context).load(al_menu.get(int_position).getImagePaths().get(position)).skipMemoryCache(false).into(viewHolder.iv_image);
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



        Glide.with(context).load( al_menu.get(int_position).getImagePaths().get(position))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(viewHolder.iv_image);

        return convertView;

    }

    private static class ViewHolder
    {
        TextView tv_foldern, tv_foldersize;
        ImageView iv_image;
    }


}