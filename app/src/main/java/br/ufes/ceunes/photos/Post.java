package br.ufes.ceunes.photos;

public class Post {
    private String id;
    private String image;
    private String height;
    private String width;
    private String description;
    private Long time;

    public Post() {

    }

    public Post(String image, String height, String width, String description, Long time) {
        this.image = image;
        this.height = height;
        this.width = width;
        this.description = description;
        this.time = time;
    }

    public Post(String id, String image, String height, String width, String description, Long time) {
        this.id = id;
        this.image = image;
        this.height = height;
        this.width = width;
        this.description = description;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
