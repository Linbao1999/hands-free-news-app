package visual.camp.sample.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import visual.camp.sample.app.databinding.ActivityNewsCollecrtionByCategoryBinding;
import visual.camp.sample.app.databinding.ActivitySearchResultBinding;
import visual.camp.sample.app.model.News;
import visual.camp.sample.app.viewmodel.NewsByCategoryViewModel;

public class SearchResultActivity extends GazeControlledActivity {

    static final String NEWS_API_KEY = "2ada588a66e745cfbce485182fd34bf7";
    ActivitySearchResultBinding binding;
    NewsByCategoryViewModel viewModel;
    SearchResultActivity context;
    List<News> newsList;
    String searchString;
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
            searchString =(String) b.get("searchString");
        }

        // view binding
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        setContentView(view);
        context = this;

        // Bind Card Views
        newsCardViewList=new ArrayList<>();
        newsCardViewList.add(binding.cardView1);
        newsCardViewList.add(binding.cardView2);
        newsCardViewList.add(binding.cardView3);

        // Set Search Text
        TextView searchStringTextView = binding.searchStringTextView;
        searchStringTextView.setText(searchString.toUpperCase(Locale.ROOT));

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
            viewModel.getSearchedNews(searchString);
            Log.i("Debug","viewModel.getSearchedNews() successes");
        } catch (Exception e) {
            Log.i("Debug", "viewModel.getSearchedNews() failed");
        }
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
                URL urlToImage1, urlToImage2, urlToImage3;
                try {
                    urlToImage1 = new URL(newsList.get(0).getUrlToImage());
                    urlToImage2 = new URL(newsList.get(1).getUrlToImage());
                    urlToImage3 = new URL(newsList.get(2).getUrlToImage());

                    binding.newsImage1.setImageBitmap(BitmapFactory.decodeStream(urlToImage1.openConnection().getInputStream()));
                    binding.newsImage2.setImageBitmap(BitmapFactory.decodeStream(urlToImage2.openConnection().getInputStream()));
                    binding.newsImage3.setImageBitmap(BitmapFactory.decodeStream(urlToImage3.openConnection().getInputStream()));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
}