package ddwu.mobile.lecture.etc.myretrofittest.model.json;

public class Book {
    private String title;
    private String image;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    private String author;

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
