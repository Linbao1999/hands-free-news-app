package visual.camp.sample.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import visual.camp.sample.app.R;
import visual.camp.sample.app.databinding.ActivityNewsCollecrtionByCategoryBinding;
import visual.camp.sample.app.model.News;
import visual.camp.sample.app.viewmodel.NewsByCategoryViewModel;

public class NewsCollectionByCategoryActivity extends GazeControlledActivity implements LifecycleOwner {
    // View Binding

    // TODO: Put this in environment variable
    static final String NEWS_API_KEY = "2ada588a66e745cfbce485182fd34bf7";
    ActivityNewsCollecrtionByCategoryBinding binding;
    NewsByCategoryViewModel viewModel;
    NewsCollectionByCategoryActivity context;
    List<News> newsList;
    String categoryName;
    Button backButton;
    View view;

    List<CardView> newsCardViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get intent.extras
        Intent intent= getIntent();
        Bundle b = intent.getExtras();

        if(b!=null)
        {
            categoryName =(String) b.get("categoryName");
        }

        // view binding
        binding = ActivityNewsCollecrtionByCategoryBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        context = this;

        // Bind Card Views
        newsCardViewList=new ArrayList<>();
        newsCardViewList.add(binding.cardView1);
        newsCardViewList.add(binding.cardView2);
        newsCardViewList.add(binding.cardView3);


        // Set Category Name
        TextView categoryNameTextView = binding.categoryNameTextView;
        categoryNameTextView.setText(categoryName.toUpperCase(Locale.ROOT));

        // Set Up Back Button
        backButton = binding.gazeButtonBack;
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CategoryNavigationActivity.class);
                startActivity(intent);
            }
        });

        newsList = new ArrayList<>();


        viewModel = ViewModelProviders.of(context).get(NewsByCategoryViewModel.class);
        viewModel.getNewsLiveData().observe(context, newsListUpdateObserver);
        viewModel.setApiKey(NEWS_API_KEY);
        try {
            viewModel.getNews(categoryName);
            Log.i("Debug","viewModel.getNews() successes");
        } catch (Exception e) {
            Log.i("Debug","viewModel.getNews() failed");
        }
        //viewModel.setCountryCode(pref.getString(Util.COUNTRY_PREF, "tr"));

    }

    Observer<List<News>> newsListUpdateObserver = new Observer<List<News>>() {
        @Override
        public void onChanged(List<News> news) {
            newsList.clear();
            if (news != null) {
                newsList.addAll(news);
                Log.i("DEBUG",String.format("newsList.size(): %d",newsList.size()));

                // Clear monitored cardViews
                targetCardViews = new ArrayList<>();
                gazeHistory = new ArrayDeque<Integer>();
                gazeCardViews = new ArrayList<>();

                targetCardViews.add(binding.cardView1);
                targetCardViews.add(binding.cardView2);
                targetCardViews.add(binding.cardView3);

                // add buttons that require gaze-control function to gazeCardViews
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

                    final int index = i;
                    targetCardViews.get(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i("DEBUG", news.get(index).getTitle() + " clicked!");
                            Intent intent = new Intent(getApplicationContext(), NewsContentActivity.class);
                            intent.putExtra("newsUrl", news.get(index).getUrl());
                            startActivity(intent);
                        }
                    });
                }

                // THIS CAN"T BE DONE IN THE UI THREAD

                Picasso.get().load(newsList.get(0).getUrlToImage()).into(binding.newsImage1);
                Picasso.get().load(newsList.get(1).getUrlToImage()).into(binding.newsImage2);
                Picasso.get().load(newsList.get(2).getUrlToImage()).into(binding.newsImage3);


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.newsTitle1.setText(newsList.get(0).getTitle());
                        binding.newsTitle2.setText(newsList.get(1).getTitle());
                        binding.newsTitle3.setText(newsList.get(2).getTitle());
                        binding.newsPublishedAt1.setText(newsList.get(0).getPublishedAt());
                        binding.newsPublishedAt2.setText(newsList.get(1).getPublishedAt());
                        binding.newsPublishedAt3.setText(newsList.get(2).getPublishedAt());
                    }
                });
            }
        }
    };



    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);

        // add card views that require gaze-control function to this.gazeButtons
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

        super.onWindowFocusChanged(hasFocus);

        // 2. Add Gaze Controlled Card / Buttons
        targetButtons.add(binding.gazeButtonBack);
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
