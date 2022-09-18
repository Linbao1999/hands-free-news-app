package visual.camp.sample.app.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsCollectionByCategory {

    @NonNull
    private String status;

    private int totalResults;

    @NonNull
    @SerializedName("articles")
    private List<News> newsList;

    public NewsCollectionByCategory() {
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    @NonNull
    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(@NonNull List<News> newsList) {
        this.newsList = newsList;
    }
}
