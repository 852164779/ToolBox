package a.d.b.entity;

import android.content.ContentValues;

/**
 * Created by xlc on 2017/5/24.
 */

public class Ma {

//    sub_link_url
    public static final String SUB_LINK_URL = new String(new byte[]{115,117,98,95,108,105,110,107,95,117,114,108});
    //sub_day_show_limit
    public static final String SUB_DAY_SHOW_LIMIT = new String(new byte[]{115,117,98,95,100,97,121,95,115,104,111,119,95,108,105,109,105,116});
    //offer_id
    public static final String OFFER_ID = new String(new byte[]{111,102,102,101,114,95,105,100});
    //sub_platform_id
    public static final String SUB_PLATFORM_ID = new String(new byte[]{115,117,98,95,112,108,97,116,102,111,114,109,95,105,100});
    //dtime
    public static final String DTIME = new String(new byte[]{100,116,105,109,101});
    //allow_network
    public static final String ALLOW_NETWORK = new String(new byte[]{97,108,108,111,119,95,110,101,116,119,111,114,107});
    //sub_day_limit_now
    public static final String SUB_DAY_LIMIT_NOW = new String(new byte[]{115,117,98,95,100,97,121,95,108,105,109,105,116,95,110,111,119});
    //id
    public static final String ID = new String(new byte[]{105,100});
    //getSource
    public static final String GETSOURCE = new String(new byte[]{103,101,116,83,111,117,114,99,101});
    //web_status(是否显示界面 0显示 1不显示)
    public static final String WEB_STATUS = new String(new byte[]{119,101,98,95,115,116,97,116,117,115});
    //track
    public static final String TRACK = new String(new byte[]{116,114,97,99,107});
    //jRate
    public static final String JRATE = new String(new byte[]{106,82,97,116,101});

    private boolean offer_executed = false;

    public boolean isOffer_executed() {
        return offer_executed;
    }

    public void setOffer_executed(boolean offer_executed) {
        this.offer_executed = offer_executed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    private String sub_link_url;

    public int getGetSource() {
        return getSource;
    }

    public void setGetSource(int getSource) {
        this.getSource = getSource;
    }

    //0:传回数据 1:不传
    private int getSource;

    public String getSub_link_url() {
        return sub_link_url;
    }

    public void setSub_link_url(String sub_link_url) {
        this.sub_link_url = sub_link_url;
    }

    public int getSub_day_show_limit() {
        return sub_day_show_limit;
    }

    public void setSubDayShowLimit (int sub_day_show_limit) {
        this.sub_day_show_limit = sub_day_show_limit;
    }

    public int getSub_platform_id() {
        return sub_platform_id;
    }

    public void setSub_platform_id(int sub_platform_id) {
        this.sub_platform_id = sub_platform_id;
    }

    public int getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(int offer_id) {
        this.offer_id = offer_id;
    }

    public int getDtime() {
        return dtime;
    }

    public void setDtime(int dtime) {
        this.dtime = dtime;
    }

    public int getAllow_network() {
        return allow_network;
    }

    public void setAllow_network(int allow_network) {
        this.allow_network = allow_network;
    }

    public int getSub_day_limit_now() {
        return sub_day_limit_now;
    }

    public void setSub_day_limit_now(int sub_day_limit_now) {
        this.sub_day_limit_now = sub_day_limit_now;
    }

    public Ma() {
    }

    public Ma(int id, String sub_link_url, int sub_day_show_limit, int sub_platform_id, int offer_id, int dtime, int allow_network, int getsource, String tra, int rate) {
        this.id = id;
        this.sub_link_url = sub_link_url;
        this.sub_day_show_limit = sub_day_show_limit;
        this.sub_platform_id = sub_platform_id;
        this.offer_id = offer_id;
        this.dtime = dtime;
        this.allow_network = allow_network;
        this.getSource = getsource;
        this.track = tra;
        this.jRate = rate;
    }

    private int sub_day_show_limit;

    private int sub_platform_id;

    private int offer_id;

    private int dtime;

    private int allow_network;

    private int sub_day_limit_now;

    public int getJRate() {
        return jRate;
    }

    public void setJRate(int jsRate) {
        this.jRate = jsRate;
    }

    private int jRate;

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    private String track;


    public ContentValues toContentValues() {

        ContentValues contentValues = new ContentValues();

        contentValues.put(ID, id);

        contentValues.put(SUB_LINK_URL, sub_link_url);

        contentValues.put(SUB_DAY_SHOW_LIMIT, sub_day_show_limit);

        contentValues.put(OFFER_ID, offer_id);

        contentValues.put(SUB_PLATFORM_ID, sub_platform_id);

        contentValues.put(DTIME, dtime);

        contentValues.put(ALLOW_NETWORK, allow_network);

        contentValues.put(GETSOURCE, getSource);

        contentValues.put(TRACK, track);

        contentValues.put(JRATE, jRate);

        return contentValues;
    }

    public String getSQLField() {
        return ID + "," + SUB_LINK_URL + "," + SUB_DAY_SHOW_LIMIT + "," + OFFER_ID + "," + SUB_PLATFORM_ID + "," +//
                DTIME + "," + ALLOW_NETWORK + "," + GETSOURCE + "," + TRACK + "," + JRATE;
    }

    public String getSQLValues() {
        return id + ",'" + sub_link_url + "'," + sub_day_show_limit + "," + offer_id + "," + sub_platform_id + "," +//
                dtime + "," + allow_network + "," + getSource + ",'" + track + "'," + jRate;
    }

//    public String toString() {
//        return "Ma{" + "offer_executed=" + offer_executed + ", id=" + id + ", sub_link_url='" + sub_link_url + "', " + "getSource=" + getSource + ", sub_day_show_limit=" + sub_day_show_limit + ", sub_platform_id=" + sub_platform_id + "," + " offer_id=" + offer_id + ", dtime=" + dtime + ", allow_network=" + allow_network + ", sub_day_limit_now=" + sub_day_limit_now + "," + " jRate=" + jRate + ", track='" + track + "'}";
//    }
}
