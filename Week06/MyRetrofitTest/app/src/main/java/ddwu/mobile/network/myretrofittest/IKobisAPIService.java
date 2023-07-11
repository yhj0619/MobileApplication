package ddwu.mobile.network.myretrofittest;

import ddwu.mobile.network.myretrofittest.model.json.BoxOfficeRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IKobisAPIService {

    @GET("/kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.{type}")
    Call<BoxOfficeRoot> getDailyBoxOfficeResult (@Path("type") String type, @Query("key") String key,
                                                @Query("targetDt") String targetDate);
}
