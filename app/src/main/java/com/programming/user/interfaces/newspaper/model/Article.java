package com.programming.user.interfaces.newspaper.model;

import com.programming.user.interfaces.newspaper.network.exceptions.ServerCommunicationError;
import com.programming.user.interfaces.newspaper.utils.Logger;
import com.programming.user.interfaces.newspaper.utils.SerializationUtils;

import org.json.simple.JSONObject;

import java.util.Date;
import java.util.Hashtable;
import java.util.Objects;

public class Article extends ModelEntity implements Comparable<Article> {

    private String title;
    private String subtitle;
    private String aAbstract;
    private String category;
    private String body;
    private String imageDescription;
    private String thumbnail;

    private int idUser;

    private Image image;

    private Date lastUpdate;

    private final String ERROR_WRONG_ID = "ERROR: Error setting a wrong id to an article:";
    private final String ERROR_EXISTING_ID = "ERROR: Error setting an id to an article with an already valid id:";

    public Article(JSONObject jsonArticle) {
        try {
            id = Integer.parseInt(Objects.requireNonNull(jsonArticle.get("id")).toString());
            idUser = Integer.parseInt(parseStringFromJSON(jsonArticle, "id_user", "0"));
            title = parseStringFromJSON(jsonArticle, "title", "").replaceAll("\\\\", "");
            category = parseStringFromJSON(jsonArticle, "category", "").replaceAll("\\\\", "");
            aAbstract = parseStringFromJSON(jsonArticle, "abstract", "").replaceAll("\\\\", "");
            body = parseStringFromJSON(jsonArticle, "body", "").replaceAll("\\\\", "");
            subtitle = parseStringFromJSON(jsonArticle, "subtitle", "").replaceAll("\\\\", "");

            imageDescription = parseStringFromJSON(jsonArticle, "image_description", "").replaceAll("\\\\", "");
            thumbnail = parseStringFromJSON(jsonArticle, "thumbnail_image", "").replaceAll("\\\\", "");

            lastUpdate = SerializationUtils.dateFromString(parseStringFromJSON(jsonArticle, "update_date", "").replaceAll("\\\\", ""));

            String imageData = parseStringFromJSON(jsonArticle, "image_data", "").replaceAll("\\\\", "");

            // Image data is never null
            if (!imageData.isEmpty()) {
                image = new Image(1, id, imageDescription, imageData);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "ERROR: Error parsing Article: from json" + jsonArticle + "\n" + e.getMessage());
            throw new IllegalArgumentException("ERROR: Error parsing Article: from json" + jsonArticle);
        }
    }

    public Article(String category, String title, String aAbstract,
                   String body, String subtitle, String idUser) {
        id = -1;
        this.category = category;
        this.title = title;
        this.aAbstract = aAbstract;
        this.body = body;
        this.subtitle = subtitle;
        this.idUser = Integer.parseInt(idUser);
    }

    @SuppressWarnings("unchecked")
    private String parseStringFromJSON(JSONObject jsonArticle, String key, String def) {
        Object in = jsonArticle.getOrDefault(key, def);
        return (in == null ? def : in).toString();
    }

    public void setID(int id) {
        if (id < 1) {
            throw new IllegalArgumentException(ERROR_WRONG_ID + id);
        }

        if (this.id > 0) {
            throw new IllegalArgumentException(ERROR_EXISTING_ID + this.id);
        }

        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getaAbstract() {
        return aAbstract;
    }

    public void setaAbstract(String aAbstract) {
        this.aAbstract = aAbstract;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public Image getImage() throws ServerCommunicationError {
        Image actualImage = image;

        if (image == null && thumbnail != null && !thumbnail.isEmpty()) {
            actualImage = new Image(1, getId(), "", thumbnail);
        }

        return actualImage;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Article [id=" + getId()
                +", titleText=" + title
                +", abstractText=" + aAbstract
                +  ", bodyText="	+ body + ", subtitleText=" + subtitle
                +", image_description=" + imageDescription
                +", image_data=" + image
                +", thumbnail=" + thumbnail
                + "]";
    }

    @Override
    protected Hashtable<String, String> getAttributes() {
        Hashtable<String, String> res = new Hashtable<>();

        res.put("category", category);
        res.put("abstract", aAbstract);
        res.put("title", title);
        res.put("body", body);
        res.put("subtitle", subtitle);

        if (image != null) {
            res.put("image_data", image.getImage());
            res.put("image_media_type", "image/png");
        }

        if (image != null && image.getDescription() != null && !image.getDescription().isEmpty()) {
            res.put("image_description", image.getDescription());
        } else if (imageDescription != null && !imageDescription.isEmpty()) {
            res.put("image_description", imageDescription);
        }

        return res;
    }


    @Override
    public int compareTo(Article article) {
        return this.getLastUpdate().compareTo(article.getLastUpdate());
    }
}
