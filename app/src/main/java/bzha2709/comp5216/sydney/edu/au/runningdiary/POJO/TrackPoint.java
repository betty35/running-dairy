package bzha2709.comp5216.sydney.edu.au.runningdiary.POJO;

import android.location.Location;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;

import java.util.Date;

/**
 * Created by Bingqing ZHAO on 2017/10/2.
 */

@Entity
public class TrackPoint
{

    @Index(unique = true) private Date time;
    @Property private double lat;
    @Property private double lng;
    @Property private double alt;
    @Property private float speed;

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public  TrackPoint(){}

    public TrackPoint(double lat,double lng,double alt, float speed)
    {
        this.lat=lat;
        this.lng=lng;
        this.alt=alt;
        this.speed=speed;
        this.time=new Date();
    }

    public TrackPoint(Location l)
    {
        this.lat=l.getLatitude();
        this.lng=l.getLongitude();
        this.alt=l.getAltitude();
        this.speed=0;
        this.time=new Date();
    }

    @Generated(hash = 844170674)
    public TrackPoint(Date time, double lat, double lng, double alt, float speed) {
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.alt = alt;
        this.speed = speed;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
