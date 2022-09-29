package visual.camp.sample.app.activity;

import static visual.camp.sample.app.utils.Config.GAZE_HISTORY_LENGTH;
import static visual.camp.sample.app.utils.Config.HIGHLIGHT_ELEVATION;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.ColorSpace;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.callback.UserStatusCallback;
import camp.visual.gazetracker.constant.StatusErrorType;
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


public class GazeControlledActivity extends AppCompatActivity {
    private static final String TAG = GazeControlledActivity.class.getSimpleName();
    private final ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();
    private GazePathView gazePathView;
    public GazeTrackerManager gazeTrackerManager;
    private final OneEuroFilterManager oneEuroFilterManager = new OneEuroFilterManager(
            2, 30, 0.5F, 0.001F, 1.0F);

    List<Button> targetButtons = new ArrayList<>();
    List<CardView> targetCardViews = new ArrayList<>();
    public ArrayDeque gazeHistory = new ArrayDeque<Integer>();
    public List<GazeButton> gazeButtons = new ArrayList<>();
    public List<GazeCardView> gazeCardViews = new ArrayList<>();


    Handler handler;


    public class GazeButton {
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private Button button;
        private float progress;

        public Button getButton() {
            return button;
        }


        public GazeButton(int x1, int x2, int y1, int y2, Button button) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.button = button;
        }

        public Boolean isButtonContains(int gazeX, int gazeY) {
            if (gazeX < x1 || gazeX > x2 || gazeY < y1 || gazeY > y2) {
                //Log.i(TAG, String.format("gazeInfo.x: %d, gazeInfo.y: %d", gazeX, gazeY));
                //Log.i(TAG, String.format("Button: x1: %d, y1: %d, x2: %d, y2: %d", x1, y1, x2, y2));
                return false;
            }
            return true;
        }
    }

    public class GazeCardView {
        private int x1;
        private int y1;
        private int x2;
        private int y2;
        private CardView cardView;
        private float progress;

        public CardView getCardView() {
            return cardView;
        }


        public GazeCardView(int x1, int x2, int y1, int y2, CardView cardView) {
            this.x1 = x1;
            this.x2 = x2;
            this.y1 = y1;
            this.y2 = y2;
            this.cardView = cardView;
        }

        public Boolean isCardViewsContains(int gazeX, int gazeY) {
            if (gazeX < x1 || gazeX > x2 || gazeY < y1 || gazeY > y2) {
                //Log.i(TAG, String.format("gazeInfo.x: %d, gazeInfo.y: %d", gazeX, gazeY));
                //Log.i(TAG, String.format("Button: x1: %d, y1: %d, x2: %d, y2: %d", x1, y1, x2, y2));
                return false;
            }
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gazeTrackerManager = GazeTrackerManager.getInstance();
        Log.i(TAG, "gazeTracker version: " + GazeTracker.getVersionName());
        handler = new Handler();
    }


    @Override
    protected void onStart() {
        super.onStart();
        gazeTrackerManager.setGazeTrackerCallbacks(gazeCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gazeHistory = new ArrayDeque<Integer>();
        gazeTrackerManager.startGazeTracking();
    }

    @Override
    protected void onPause() {
        gazeHistory = new ArrayDeque<Integer>();
        gazeTrackerManager.stopGazeTracking();
        super.onPause();

    }

    @Override
    protected void onStop() {
        gazeHistory = new ArrayDeque<Integer>();
        gazeTrackerManager.removeCallbacks(gazeCallback);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        gazeHistory = new ArrayDeque<Integer>();
        gazeTrackerManager.removeCallbacks(gazeCallback);
        super.onDestroy();
    }


    private final GazeCallback gazeCallback = new GazeCallback() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onGaze(GazeInfo gazeInfo) {
            //Log.i(TAG, String.format("x: %d, y: %d", Math.round(gazeInfo.x), Math.round(gazeInfo.y)));

            int triggeredButtonIndex = -1;
            // Check gaze buttons
            for (int i = 0; i < gazeButtons.size(); i++) {
                int x = (int) Math.round(gazeInfo.x);
                int y = (int) Math.round(gazeInfo.y);
                final GazeButton btn = gazeButtons.get(i);
                if (btn.isButtonContains(x, y)) {
                    triggeredButtonIndex = i;
                }
            }

            if (triggeredButtonIndex != -1) {
                final int index = triggeredButtonIndex;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < gazeButtons.size(); j++) {
                            // highlight selected button
                            if (j == index) {
                                gazeButtons.get(j).getButton().setElevation(HIGHLIGHT_ELEVATION);
                                gazeButtons.get(j).getButton().performClick();
                            }
                            // unhighlight unselected button
                            else {
                                gazeButtons.get(j).getButton().setElevation(0);
                            }
                        }

                    }
                });
                return;
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int j = 0; j < gazeButtons.size(); j++) {
                            // unhighlight unselected button
                            gazeButtons.get(j).getButton().setElevation(0);
                        }

                    }
                });
            }

            int newHistoryEntry = -1;
            // check gaze cards

            for (int i = 0; i < gazeCardViews.size(); i++) {
                int x = (int) Math.round(gazeInfo.x);
                int y = (int) Math.round(gazeInfo.y);
                final GazeCardView gazeCardView = gazeCardViews.get(i);
                if (gazeCardView.isCardViewsContains(x, y)) {
                    Log.i(TAG, i + "th card on gaze");
                    newHistoryEntry = i;
                    break;
                }
            }

            // Update gazeHistory
            if (gazeCardViews.size() > 0) {
                if (gazeHistory.size() >= GAZE_HISTORY_LENGTH) {
                    gazeHistory.pollLast();

                }
                gazeHistory.push(newHistoryEntry);

                if (newHistoryEntry != -1) {
                    for (int i = 0; i < gazeCardViews.size(); i++) {
                        final int index = i;

                        if (newHistoryEntry == i) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {

                                    // highlight the selected card
                                    //gazeCardViews.get(index).getCardView().setCardElevation(HIGHLIGHT_ELEVATION);
                                    ColorStateList color = gazeCardViews.get(index).getCardView().getCardBackgroundColor();
                                    gazeCardViews.get(index).getCardView().setCardBackgroundColor(color.withAlpha(200));


                                    Log.i("DEBUG",String.format("setCardElevation(HIGHLIGHT_ELEVATION) called for the %dth card",index));
                                    // unhighlight rest of the cards
                                    for (int j = 0; j < gazeCardViews.size(); j++) {
                                        if (j != index) {
                                            //gazeCardViews.get(j).getCardView().setCardElevation(0);
                                            ColorStateList crtColor = gazeCardViews.get(j).getCardView().getCardBackgroundColor();
                                            gazeCardViews.get(j).getCardView().setCardBackgroundColor(crtColor.withAlpha(255));
                                        }
                                    }
                                }
                            });

                            break;
                        }
                    }
                }
            } else {
                // if no card in the activity, skip the following checking
                return;
            }

            // check gazeHistory
            int[] temp = new int[gazeCardViews.size()];
            Object[] gazeHistoryArrayList = gazeHistory.toArray();

            // TODO: REMOVE THIS
            // TEST
            Object[] gazeHistoryTempArray = gazeHistory.toArray();
            String testString = "";
            for (int i = 0; i < gazeHistoryTempArray.length; i++) {
                testString += String.format("%d, ", (Integer) gazeHistoryTempArray[i]);
            }
            Log.i("DEBUG", "gaze history: " + testString);


            for (int i = 0; i < gazeHistoryArrayList.length; i++) {
                int currentHistoryVal = (Integer) gazeHistoryArrayList[i];
                if (currentHistoryVal != -1) {
                    temp[currentHistoryVal] += 1;
                }
            }

            for (int i = 0; i < temp.length; i++) {
                //Log.i("Gaze Count", String.format("%dth Card: %d",i,temp[i]));
                final int index = i;
                if (temp[i] > GAZE_HISTORY_LENGTH * 0.90) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            gazeCardViews.get(index).getCardView().performClick();
                        }
                    });
                    break;
                }
            }


        }
    };


}
