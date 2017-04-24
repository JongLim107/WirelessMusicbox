package com.shenqu.wirelessmbox.bean;

import com.shenqu.wirelessmbox.tools.JLJSON;

import org.json.JSONObject;

/**
 * Created by JongLim on 2016/12/12.
 */

public class TrackMeta {
    private String artist = "";
    private String coverUrl = "";
    private String id = "";
    private String name = "";
    private int position = 0;
    private int source = 0;

    private String url = "";

    public String getArtist() {
        return artist;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPosition() {
        return position;
    }

    public int getSource() {
        return source;
    }

    public TrackMeta(String url, String artist, String albumUrl, String id, String name, int position, int source) {
        this.url = url;
        this.artist = artist;
        this.coverUrl = albumUrl;
        this.id = id;
        this.name = name;
        this.position = position;
        this.source = source;
    }

    public TrackMeta(JSONObject jobj){
        id = JLJSON.getString(jobj, "id");
        name = JLJSON.getString(jobj, "name");
        url = JLJSON.getString(jobj, "url");
        position = JLJSON.getInt(jobj, "position");
        artist = JLJSON.getString(jobj, "artist");
        source = JLJSON.getInt(jobj, "source");
        coverUrl = JLJSON.getString(jobj, "coverUrl");
    }

    public String toJsonString() {
        return "{\"id\":\"" + id + "\",\"name\":\"" + JLJSON.replace(name) + "\",\"url\":\"" + url + "\",\"position\":" + position + ",\"artist\":\"" + JLJSON.replace(artist)
                + "\",\"source\":" + source + ",\"coverUrl\":\"" + coverUrl + "\"}";
    }

    public String toString() {
        return "TrackMeta [id=" + id + ", name=" + name + ", source=" + source + ", url=" + url + ", position=" + position + ", coverUrl=" + coverUrl + ", artist=" + artist + "]";
    }
}

