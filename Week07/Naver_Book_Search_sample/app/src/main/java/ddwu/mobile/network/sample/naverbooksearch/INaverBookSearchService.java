package ddwu.mobile.network.sample.naverbooksearch;

import ddwu.mobile.network.sample.naverbooksearch.model.json.BookRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface INaverBookSearchService {

    // Service Interface 메소드 생성
    @GET("/v1/search.book.json")
    Call<BookRoot> getBooks(@Header("X-Naver-Client-Id") String id,
                            @Header("X-Naver-Client-Secret") String secret,
                            @Query()
    
}
