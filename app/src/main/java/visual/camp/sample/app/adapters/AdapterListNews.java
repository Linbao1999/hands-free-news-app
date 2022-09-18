package visual.camp.sample.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import visual.camp.sample.app.R;
import visual.camp.sample.app.clicklisteners.AdapterItemClickListener;
import visual.camp.sample.app.databinding.NewsCardBinding;
import visual.camp.sample.app.model.News;

// Data Binding
import visual.camp.sample.app.databinding.NewsCardBinding;


public class AdapterListNews extends RecyclerView.Adapter<AdapterListNews.NewsViewHolder> {

    private List<News> items;
    // AdapterItemClickListener adapterItemClickListener;

    public AdapterListNews(List<News> items, AdapterItemClickListener adapterItemClickListener) {
        this.items = items;
        //this.adapterItemClickListener = adapterItemClickListener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NewsCardBinding newsCardBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.news_card, parent, false);
        return new NewsViewHolder(newsCardBinding);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NewsViewHolder holder, final int position) {
        // holder.bind(getItem(position), adapterItemClickListener);
        holder.bind(getItem(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private News getItem(int position) {
        return items.get(position);
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder {
        private NewsCardBinding newsCardBinding;

        public NewsViewHolder(NewsCardBinding newsCardBinding) {
            super(newsCardBinding.getRoot());
            this.newsCardBinding = newsCardBinding;
        }

//        public void bind(News news, AdapterItemClickListener adapterItemClickListener) {
//            this.newsCardBinding.setNews(news);
//            this.newsCardBinding.setClickListener(adapterItemClickListener);
//        }

        public void bind(News news) {
            this.newsCardBinding.setNews(news);
        }
    }

}