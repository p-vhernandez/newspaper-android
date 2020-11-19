package com.programming.user.interfaces.newspaper.model;

import com.programming.user.interfaces.newspaper.utils.Logger;
import com.programming.user.interfaces.newspaper.utils.SerializationUtils;

import org.json.simple.JSONObject;

import java.util.Hashtable;
import java.util.Objects;

public class Image extends ModelEntity {

    private int order;
    private int articleID;
    private String description;
    private String image;

    /**
     * Consructor of an Image, always through article, because an image shouldn't exist alone without one article
     *
     * @param order       of the image within the article
     * @param description of the image
     * @param articleID   - id of article of the image
     * @param b64Image    - data of the image
     */
    public Image(int order, int articleID, String description, String b64Image) {
        this.id = -1;
        this.order = order;
        this.articleID = articleID;
        this.description = description;
        this.image = SerializationUtils.createScaledStrImage(b64Image, 500, 500);
    }

    /**
     * @param jsonImage json object representing the image info
     */
    @SuppressWarnings("unchecked")
    protected Image(JSONObject jsonImage) {
        try {
            id = Integer.parseInt(Objects.requireNonNull(jsonImage.get("id")).toString());
            order = Integer.parseInt(Objects.requireNonNull(jsonImage.get("order")).toString());
            description = Objects.requireNonNull(jsonImage.getOrDefault("description", "")).toString().replaceAll("\\\\", "");
            articleID = Integer.parseInt(Objects.requireNonNull(jsonImage.get("id_article")).toString().replaceAll("\\\\", ""));
            image = (Objects.requireNonNull(jsonImage.get("data")).toString().replaceAll("\\\\", ""));
        } catch (Exception e) {
            Logger.log(Logger.ERROR, "ERROR: Error parsing Image: from json" + jsonImage + "\n" + e.getMessage());
            throw new IllegalArgumentException("ERROR: Error parsing Image: from json" + jsonImage);
        }
    }

    public int getOrder() {
        return order;
    }

    public int getArticleID() {
        return articleID;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setArticleID(int articleID) {
        this.articleID = articleID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Image [id=" + getId() + ", order=" + order +
                ", description=" + description +
                ", id_article=" + articleID +
                ", data=" + image + "]";
    }

    @Override
    protected Hashtable<String, String> getAttributes() {
        Hashtable<String, String> res = new Hashtable<>();

        res.put("id_article", "" + articleID);
        res.put("order", "" + order);
        res.put("description", description);
        res.put("data", image);
        res.put("media_type", "image/png");

        return res;
    }
}
