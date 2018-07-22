package swipedelete.swipedelete;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;

import java.io.File;
import java.util.List;
import java.util.Set;

import static android.provider.CalendarContract.CalendarCache.URI;

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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap mBitmapInsurance = BitmapFactory.decodeFile(imagePathArray[0].toString(), options);
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(mBitmapInsurance, mBitmapInsurance.getWidth(), mBitmapInsurance.getHeight(), true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            imageView.setImageBitmap(rotatedBitmap);
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();

    }


}
