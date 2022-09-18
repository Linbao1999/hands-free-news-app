package visual.camp.sample.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import visual.camp.sample.app.R;
import visual.camp.sample.app.adapters.AdapterListNews;
import visual.camp.sample.app.clicklisteners.AdapterItemClickListener;
import visual.camp.sample.app.databinding.ActivityNewsCollecrtionByCategoryBinding;
import visual.camp.sample.app.model.News;
import visual.camp.sample.app.viewmodel.NewsByCategoryViewModel;

public class NewsCollectionByCategoryActivity extends AppCompatActivity implements LifecycleOwner, AdapterItemClickListener {
    // View Binding
    RecyclerView recyclerView;

    // TODO: Put this in environment variable
    static final String NEWS_API_KEY = "2ada588a66e745cfbce485182fd34bf7";
    ActivityNewsCollecrtionByCategoryBinding binding;
    NewsByCategoryViewModel viewModel;
    NewsCollectionByCategoryActivity context;
    AdapterListNews adapterListNews;
    List<News> newsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // view binding
        binding = ActivityNewsCollecrtionByCategoryBinding.inflate(getLayoutInflater());
        recyclerView = binding.recycleView;
        View view = binding.getRoot();
        setContentView(view);
        context = this;


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
            viewModel.getNews("business");
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
            }
            adapterListNews.notifyDataSetChanged();
            Log.i("Debug","newsListUpdateObserver onChange triggered");
        }
    };

    @Override
    public void onNewsItemClick(News news) {
        Log.i("INFO", news.getTitle() + " clicked.");
    }

}
