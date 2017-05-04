package org.taitasciore.android.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by roberto on 30/04/17.
 */

public class FooterContent {

    @SerializedName("id_page")
    private int idPage;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;

    public int getIdPage() {
        return idPage;
    }

    public void setIdPage(int idPage) {
        this.idPage = idPage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
