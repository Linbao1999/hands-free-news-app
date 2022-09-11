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

public class NewsContentActivity extends AppCompatActivity {

    NewsContentBinding binding;
    WebView webView;

    private static final String TAG = DemoActivity.class.getSimpleName();
    private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private GazePathView gazePathView;
    private GazeTrackerManager gazeTrackerManager;
    private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(
            2, 30, 0.5F, 0.001F, 1.0F);



    List<Button> targetButtons = new ArrayList<>();
    private ArrayDeque gazeHistory= new ArrayDeque<GazeInfo>();
    private List<GazeButton> gazeButtons = new ArrayList<>();

    Handler handler;

    // TODO: Move this to helper class
    public class GazeButton {
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private Button button;
        private float progress;

        public Button getButton(){
            return button;
        }


        public GazeButton(int x1, int x2, int y1, int y2, Button button){
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.button = button;
        }

        public Boolean isButtonContains(int gazeX, int gazeY){
            if (gazeX < x1 || gazeX > x2 || gazeY < y1 || gazeY > y2) {
                Log.i(TAG, String.format("gazeInfo.x: %d, gazeInfo.y: %d",gazeX,gazeY));
                Log.i(TAG, String.format("Button: x1: %d, y1: %d, x2: %d, y2: %d",x1,y1,x2,y2));
                return false;
            }
            return true;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewsContentBinding.inflate(getLayoutInflater());

        gazeTrackerManager = GazeTrackerManager.getInstance();
        Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());

        View view = binding.getRoot();
        setContentView(view);

        // init view

        webView = binding.newsWebView;
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

        handler = new Handler();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gazeTrackerManager.startGazeTracking();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        gazeTrackerManager.stopGazeTracking();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        gazeTrackerManager.removeCallbacks(gazeCallback);
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        // Record Gaze Button Position
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

            Log.i(TAG, String.format("x1: %d, y1: %d, x2: %d, y2: %d",x1,y1,x2,y2));
            gazeButtons.add(new GazeButton(x1,x2,y1,y2,targetButton));
        }
    }

    private final GazeCallback gazeCallback = new GazeCallback() {
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            Log.i(TAG,String.format("x: %d, y: %d",Math.round(gazeInfo.x),Math.round(gazeInfo.y)));
//        if(gazeHistory.size()>gazeHistoryLength){
//            gazeHistory.pollFirst();
//        }
//        gazeHistory.push(gazeInfo);
            for(int i=0; i<gazeButtons.size();i++){
                int x = (int)Math.round(gazeInfo.x);
                int y = (int)Math.round(gazeInfo.y);
                final GazeButton btn = gazeButtons.get(i);
                if(btn.isButtonContains(x,y)){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            btn.getButton().performClick();
                        }
                    });
                    break;
                }
            }
        }
    };
}