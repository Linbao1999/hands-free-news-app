package visual.camp.sample.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.EyeMovementState;
import camp.visual.gazetracker.state.TrackingState;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import visual.camp.sample.app.GazeTrackerManager;
import visual.camp.sample.app.R;

import visual.camp.sample.view.GazePathView;
import visual.camp.sample.app.GazeTrackerManager;
import visual.camp.sample.app.databinding.NewsContentBinding;
import visual.camp.sample.view.GazePathView;

import visual.camp.sample.app.activity.GazeControlledActivity;

public class NewsContentActivity extends GazeControlledActivity {

    NewsContentBinding binding;
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewsContentBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        // 1. Init View
        // 1.1 Bind Components
        webView = binding.newsWebView;
        // 1.2 Init Components
        webView.loadUrl("https://www.abc.net.au/news/2022-09-01/house-price-plunge-continues-corelogic-proptrack/101392116");
        binding.gazeButtonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.scrollBy(0,100);
            }
        });

        binding.gazeButtonUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.scrollBy(0,-100);
            }
        });
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        // 2. Add Gaze Controlled Card / Buttons
        targetButtons.add(binding.gazeButtonDown);
        targetButtons.add(binding.gazeButtonUp);
        // add buttons that require gaze-control function to this.gazeButtons
        for(int i=0; i<targetButtons.size(); i++){
            int [] coordinates = new int[2];
            Button targetButton = targetButtons.get(i);
            targetButton.getLocationOnScreen(coordinates);
            int x1 = coordinates[0];
            int y1 = coordinates[1];
            int x2 = x1 + targetButton.getWidth();
            int y2 = y1 + targetButton.getHeight();

            Log.i("GAZE_DEBUG", String.format("x1: %d, y1: %d, x2: %d, y2: %d",x1,y1,x2,y2));
            gazeButtons.add(new GazeButton(x1,x2,y1,y2,targetButton));
        }
    }


}