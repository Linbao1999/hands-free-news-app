package visual.camp.sample.app.restapi;

import visual.camp.sample.app.model.News;
import visual.camp.sample.app.model.NewsCollectionByCategory;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestInterface {

    @GET("v2/top-headlines")
    Call<NewsCollectionByCategory> getNewsByCategory(@Query("country") String country,
                                                     @Query("category") String category,
                                                     @Query("apiKey") String apiKey,
                                                     @Query("pageSize") int pageSize,
                                                     @Query("page") int page);

    @GET("v2/everything")
    Call<NewsCollectionByCategory> getSearchedTotalNews(@Query("q") String country,
                                                        @Query("apiKey") String apiKey,
                                                        @Query("pageSize") int pageSize,
                                                        @Query("page") int page);
}
