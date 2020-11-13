package com.programming.user.interfaces.newspaper.model;

import org.json.simple.JSONObject;

import java.util.Enumeration;
import java.util.Hashtable;

public abstract class ModelEntity {

    protected int id;

    private static String JSON_ID = "id";

    ModelEntity() { }

    /**
     * @return the object id (-1) if not saved
     */
    public int getId() {
        return id;
    }

    /**
     * @return hashtable of attributes of the entity (without id)
     */
    protected abstract Hashtable<String,String> getAttributes();

    /**
     * @return json object of the entity
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject jsonArticle = new JSONObject();

        try {
            if (getId() > 0) {
                jsonArticle.put(JSON_ID, getId());
            }

            Hashtable<String, String> result = getAttributes();
            Enumeration<String> keys = result.keys();

            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                jsonArticle.put(key, JSONObject.escape(result.get(key)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonArticle;
    }

}
