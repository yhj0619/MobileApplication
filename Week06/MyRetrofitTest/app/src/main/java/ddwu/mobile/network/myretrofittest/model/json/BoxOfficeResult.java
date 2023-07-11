package ddwu.mobile.network.myretrofittest.model.json;

import java.util.List;

public class BoxOfficeResult {
    private List<DailyBoxOffice> dailyBoxOfficelist;

    public List<DailyBoxOffice> getDailyBoxOfficelist() {
        return dailyBoxOfficelist;
    }

    public void setDailyBoxOfficelist(List<DailyBoxOffice> dailyBoxOfficelist) {
        this.dailyBoxOfficelist = dailyBoxOfficelist;
    }
}
