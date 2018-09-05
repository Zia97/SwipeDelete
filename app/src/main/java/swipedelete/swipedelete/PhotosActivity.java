package swipedelete.swipedelete;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class PhotosActivity extends AppCompatActivity {
    int int_position;
    private GridView gridView;
    GridViewAdapter adapter;

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
        SetGridView();

        Log.e("start","PHOTO ACTIVITY RESTARTED");
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

}