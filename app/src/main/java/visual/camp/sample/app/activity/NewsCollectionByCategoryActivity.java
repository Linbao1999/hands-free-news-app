package visual.camp.sample.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import visual.camp.sample.app.R;
import visual.camp.sample.app.adapters.AdapterListNews;
import visual.camp.sample.app.clicklisteners.AdapterItemClickListener;
import visual.camp.sample.app.databinding.ActivityNewsCollecrtionByCategoryBinding;
import visual.camp.sample.app.model.News;
import visual.camp.sample.app.viewmodel.NewsByCategoryViewModel;

public class NewsCollectionByCategoryActivity extends GazeControlledActivity implements LifecycleOwner, AdapterItemClickListener {
    // View Binding
    RecyclerView recyclerView;

    // TODO: Put this in environment variable
    static final String NEWS_API_KEY = "2ada588a66e745cfbce485182fd34bf7";
    ActivityNewsCollecrtionByCategoryBinding binding;
    NewsByCategoryViewModel viewModel;
    NewsCollectionByCategoryActivity context;
    AdapterListNews adapterListNews;
    List<News> newsList;
    String categoryName;
    Button backButton;

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
        recyclerView = binding.recycleView;
        View view = binding.getRoot();
        setContentView(view);
        context = this;


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
        adapterListNews = new AdapterListNews(newsList, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapterListNews);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);


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

        Log.i("DEBUG","recyclerView.getChildCount(): " + String.valueOf(recyclerView.getChildCount()));
    }


    Observer<List<News>> newsListUpdateObserver = new Observer<List<News>>() {
        @Override
        public void onChanged(List<News> news) {
            newsList.clear();
            if (news != null) {
                newsList.addAll(news);
            }
            adapterListNews.notifyDataSetChanged();

            for(int i=0;i<recyclerView.getChildCount();i++){
                CardView cardView = (CardView)recyclerView.getChildAt(i);
                final int index = i;
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i("DEBUG","News: " + news.get(index).getTitle() + " clicked!");
                    }
                });
                targetCardViews.add(cardView);
            }
            Log.i("Debug","newsListUpdateObserver onChange triggered");
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


    @Override
    public void onNewsItemClick(News news) {
        Log.i("INFO", "Clicked " + news.getTitle());
    }

}
