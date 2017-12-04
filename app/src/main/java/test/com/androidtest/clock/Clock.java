package test.com.androidtest.clock;

public class Clock {

    private String times;
    private String jiange;

    public String getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(String current_time) {
        this.current_time = current_time;
    }

    public String getJiange() {
        return jiange;
    }

    public void setJiange(String jiange) {
        this.jiange = jiange;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    private String current_time;

    public Clock(String s,String j,String t)
    {
        this.times=s;
        this.jiange=j;
        this.current_time=t;
    }
}
