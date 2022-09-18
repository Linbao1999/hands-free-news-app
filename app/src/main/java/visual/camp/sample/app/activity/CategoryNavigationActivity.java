package visual.camp.sample.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import camp.visual.gazetracker.GazeTracker;
import visual.camp.sample.app.GazeTrackerManager;
import visual.camp.sample.app.databinding.ActivityCategoryNavigationBinding;


public class CategoryNavigationActivity extends AppCompatActivity {

    ActivityCategoryNavigationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}