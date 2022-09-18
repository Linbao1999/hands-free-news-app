package visual.camp.sample.app.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import visual.camp.sample.app.model.News;
import visual.camp.sample.app.model.NewsCollectionByCategory;
import visual.camp.sample.app.restapi.ApiClient;
import visual.camp.sample.app.restapi.RestInterface;
import visual.camp.sample.app.utils.Config;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class NewsByCategoryViewModel extends ViewModel{

    private MutableLiveData<List<News>> newsLiveData;
    private List<News> newsList;
    private String apiKey;

    public NewsByCategoryViewModel() {
        newsLiveData = new MutableLiveData<>();
        newsList = new ArrayList<>();
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }



    public MutableLiveData<List<News>> getNewsLiveData() {
        return newsLiveData;
    }

    private RestInterface getRestInterface() {
        RestInterface[] restInterface = new RestInterface[1];
        restInterface[0] = ApiClient.getClient(Config.API_BASE_URL).create(RestInterface.class);
        return restInterface[0];
    }

    public void getNews(String category) throws Exception {
        RestInterface restInterface = getRestInterface();
        Call<NewsCollectionByCategory> call;
        newsList.clear();
        newsLiveData.setValue(null);
        if (!category.equals("")) {
            call = restInterface.getNewsByCategory(Config.COUNTRY_CODE, category, apiKey);
        } else {
            throw new Exception("News category not specified.");
        }
        call.enqueue(new Callback<NewsCollectionByCategory>() {
            @Override
            public void onResponse(Call<NewsCollectionByCategory> call, Response<NewsCollectionByCategory> response) {
                Log.i("Debug", "Breakpoint - onResponse");
                if (response.body() != null) {
                    Log.i("Debug", "totalNews Response Body Size: " + response.body().getTotalResults());
                    NewsCollectionByCategory totalNews = response.body();
                    fillNewsList(totalNews);

                    Log.i("Debug", String.format("totalNews.getTotalResults: ",totalNews.getTotalResults()));
                }
                else{
                    Log.i("Debug", "getNews() failed because response body is null ");
                }
            }

            @Override
            public void onFailure(Call<NewsCollectionByCategory> call, Throwable t) {
                Log.i("Debug", "Breakpoint - onFailure" + " " + t.getMessage());
                newsLiveData.setValue(null);
                Log.i("Debug", "getNews() onFailure");

            }
        });
    }


    private void fillNewsList(NewsCollectionByCategory totalNews) {
        for(int i=0;i<totalNews.getNewsList().size();i++){
            Log.i("Debug", "News Title: " + totalNews.getNewsList().get(i).getTitle());
        }
        newsList.addAll(totalNews.getNewsList());
        newsLiveData.setValue(newsList);
    }

}
