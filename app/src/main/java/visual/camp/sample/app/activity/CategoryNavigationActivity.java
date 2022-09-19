package visual.camp.sample.app.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import camp.visual.gazetracker.GazeTracker;
import visual.camp.sample.app.GazeTrackerManager;
import visual.camp.sample.app.databinding.ActivityCategoryNavigationBinding;


public class CategoryNavigationActivity extends GazeControlledActivity {

    ActivityCategoryNavigationBinding binding;

    CardView entertainmentCardView;
    CardView businessCardView;
    CardView technologyCardView;
    CardView sportsCardView;
    CardView scienceCardView;
    CardView audioSearchCardView;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    Map<String, CardView> cardViewMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // 1. Init View
        // 1.1 Bind Components
        entertainmentCardView = binding.entertainmentCardView;
        businessCardView = binding.businessCardView;
        technologyCardView = binding.technologyCardView;
        sportsCardView = binding.sportsCardView;
        scienceCardView = binding.scienceCardView;
        audioSearchCardView = binding.audioSearchCardView;


        cardViewMap.put("entertainment", entertainmentCardView);
        cardViewMap.put("business", businessCardView);
        cardViewMap.put("technology", technologyCardView);
        cardViewMap.put("sports", sportsCardView);
        cardViewMap.put("science", scienceCardView);
        cardViewMap.put("audio_search", audioSearchCardView);


        // 1.2 Init Components
        for (Map.Entry<String,CardView> entry : cardViewMap.entrySet()){
            String categoryName = entry.getKey();
            if(categoryName.equals("audio_search")){
                entry.getValue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("DEBUG", "Audio Search Activated");
                        Intent intent
                                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                                Locale.getDefault());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");


                        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//                        try {
//                            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//                        }
//                        catch (Exception e) {
//                            Toast.makeText(CategoryNavigationActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
            }
            else{
                entry.getValue().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("DEBUG", categoryName + " clicked!");
                        Intent intent = new Intent(getApplicationContext(), NewsCollectionByCategoryActivity.class);
                        intent.putExtra("categoryName", categoryName);
                        startActivity(intent);
                    }
                });
            }
        }


    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        // 2. Add Gaze Controlled Card / Buttons
        targetCardViews.add(entertainmentCardView);
        targetCardViews.add(businessCardView);
        targetCardViews.add(technologyCardView);
        targetCardViews.add(sportsCardView);
        targetCardViews.add(scienceCardView);

        // add buttons that require gaze-control function to this.gazeButtons
        for(int i=0; i<targetCardViews.size(); i++){
            int [] coordinates = new int[2];
            CardView targetCardView = targetCardViews.get(i);
            targetCardView.getLocationOnScreen(coordinates);
            int x1 = coordinates[0];
            int y1 = coordinates[1];
            int x2 = x1 + targetCardView.getWidth();
            int y2 = y1 + targetCardView.getHeight();

            Log.i("Target Card Bound",  String.format("%dth Card: ", i) + String.format("x1: %d, y1: %d, x2: %d, y2: %d",x1,y1,x2,y2));
            gazeCardViews.add(new GazeCardView(x1,x2,y1,y2,targetCardView));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);

                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("searchString", Objects.requireNonNull(result).get(0));
                startActivity(intent);
            }
        }
    }
}