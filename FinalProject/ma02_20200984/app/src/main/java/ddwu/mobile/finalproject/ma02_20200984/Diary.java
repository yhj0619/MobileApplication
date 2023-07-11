package ddwu.mobile.finalproject.ma02_20200984;

import java.io.Serializable;

public class Diary implements Serializable {
    private long _id;
    private String title;
    private String contents;
    private String date;
    private String time;
    private String location;
    private String weather;

    public Diary(long _id, String title, String contents, String date, String time, String location, String weather) {
        this._id = _id;
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.time = time;
        this.location = location;
        this.weather = weather;
    }

    public Diary(String title, String contents, String date, String time, String location, String weather) {
        this.title = title;
        this.contents = contents;
        this.date = date;
        this.time = time;
        this.location = location;
        this.weather = weather;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    @Override
    public String toString() {
        return "Diary{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location='" + location + '\'' +
                ", weather='" + weather + '\'' +
                '}';
    }
}
