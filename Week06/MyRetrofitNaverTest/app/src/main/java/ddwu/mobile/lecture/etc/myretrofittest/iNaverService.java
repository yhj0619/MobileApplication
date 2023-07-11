package ddwu.mobile.lecture.etc.myretrofittest;

import ddwu.mobile.lecture.etc.myretrofittest.model.json.BookRoot;
import retrofit2.Call;
public interface iNaverService {

    Call<BookRoot>
    Call<BookRoot> getBookList (@Header("X-Naver-Client-Id") String cliendId,
                                @Header)
Call
}
