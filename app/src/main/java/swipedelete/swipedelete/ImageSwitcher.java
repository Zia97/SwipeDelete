package swipedelete.swipedelete;

import android.os.Bundle;
import android.util.Log;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdRequest;

import java.util.Set;

public class ImageSwitcher extends AppCompatActivity {

    private String imagePath;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_switcher_xml);
        imageView = findViewById(R.id.imageViewXML);

        Set<String> imagePaths = getIntent().getCategories();
        Object[] imagePathArray = imagePaths.toArray();

        if (imagePathArray.length == 1)
        {
            imagePath = imagePathArray[0].toString();
        }
        else
        {
            Log.e("Path error","Incorrect number of paths sent on click");
        }

        Glide.with(this.getApplicationContext()).load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
