package it.broke31.filmm.support;

import android.widget.ImageView;

public class RowItem {
    private ImageView imageView;
    private String title;

    public RowItem(String title, ImageView imageId) {
        this.title = title;
        this.imageView = imageId;
    }

    ImageView getImageId() {
        return imageView;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}




