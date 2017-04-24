package com.shenqu.wirelessmbox.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.shenqu.wirelessmbox.tools.JLJSON;

import org.json.JSONObject;

/**
 * Created by JongLim on 2016/11/29.
 */

public class MusicBoxState implements Parcelable {
    public static final String BOX_PLAYING = "PLAYING";

    public String AVTransportURI = "";  // /upnp/playlist.json;1480401666030
    public String CurrentTrackURI = ""; // http://192.168.0.80:12341/tracks/storage/emulated/0/Samsung/Music/Over%20the%20Horizon.mp3
    public String TransportState = "";  // PLAYING
    public String CurrentTrackDuration = "";    // 00:04:20
    public String RelativeTimePosition = "";    // 00:00:41
    public String AudioSource = ""; // WIFI
    public int NumberOfTracks = 0;
    public int CurrentTrack = 0;
    public int CurrentVolume = 60;

    public MusicBoxState(){}

    public void initFromJSON(JSONObject body) {
        AVTransportURI = JLJSON.getString(body, "AVTransportURI");
        CurrentTrackURI = JLJSON.getString(body, "CurrentTrackURI");
        TransportState = JLJSON.getString(body, "TransportState");
        CurrentTrackDuration = JLJSON.getString(body, "CurrentTrackDuration");
        NumberOfTracks = JLJSON.getInt(body, "NumberOfTracks");
        CurrentTrack = JLJSON.getInt(body, "CurrentTrack");
        RelativeTimePosition = JLJSON.getString(body, "RelativeTimePosition");
        CurrentVolume = JLJSON.getInt(body, "CurrentVolume");
        AudioSource = JLJSON.getString(body, "AudioSource");
    }

    private MusicBoxState(Parcel paramParcel) {
        this.AVTransportURI = paramParcel.readString();
        this.CurrentTrackURI = paramParcel.readString();
        this.TransportState = paramParcel.readString();
        this.CurrentTrackDuration = paramParcel.readString();
        this.NumberOfTracks = paramParcel.readInt();
        this.CurrentTrack = paramParcel.readInt();
        this.RelativeTimePosition = paramParcel.readString();
        this.CurrentVolume = paramParcel.readInt();
        this.AudioSource = paramParcel.readString();
    }

    public static final Creator CREATOR = new Creator() {
        public MusicBoxState createFromParcel(Parcel parcel) {
            return new MusicBoxState(parcel);
        }

        public MusicBoxState[] newArray(int paramAnonymousInt) {
            return new MusicBoxState[paramAnonymousInt];
        }
    };

    public void writeToParcel(Parcel paramParcel, int paramInt) {
        paramParcel.writeString(this.AVTransportURI);
        paramParcel.writeString(this.CurrentTrackURI);
        paramParcel.writeString(this.TransportState);
        paramParcel.writeString(this.CurrentTrackDuration);
        paramParcel.writeInt(this.NumberOfTracks);
        paramParcel.writeInt(this.CurrentTrack);
        paramParcel.writeString(this.RelativeTimePosition);
        paramParcel.writeInt(this.CurrentVolume);
        paramParcel.writeString(this.AudioSource);
    }

    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "BoxPlayerState [avUri=" + this.AVTransportURI + ", curUri=" + this.CurrentTrackURI + ", state=" + this.TransportState + ", " +
                "duration=" + this.CurrentTrackDuration + ", tracks=" + this.NumberOfTracks + ", curTrack=" + this.CurrentTrack + ", " +
                "curDura=" + this.RelativeTimePosition + ", curVolume=" + this.CurrentVolume + ", audioSource=" + this.AudioSource + "]";
    }
}