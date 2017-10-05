package bzha2709.comp5216.sydney.edu.au.runningdiary.listener;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

import bzha2709.comp5216.sydney.edu.au.runningdiary.MainActivity;
import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.TrackPoint;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.TrackPointDao;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.GeoUtils;

/**
 * Created by Bingqing ZHAO on 2017/10/5.
 */

public class MyLocationListener implements LocationListener {
    GoogleMap mMap;
    LatLng currentLoc;
    Location location;
    ArrayList<Polyline> lines;
    TrackPointDao tpDAO;
    TrackPoint initialPoint;
    TrackPoint lastPoint;
    TrackPoint currentPoint;
    int MAP_CAMERA_ENLARGE_LEVEL;
    Context context;

    public void setMap(GoogleMap mMap)
    {
        this.mMap=mMap;
    }

    public MyLocationListener(GoogleMap mMap, Location location, int MAP_CAMERA_ENLARGE_LEVEL, Context c)
    {
        super();
        this.mMap=mMap;
        this.location=location;
        this.MAP_CAMERA_ENLARGE_LEVEL=MAP_CAMERA_ENLARGE_LEVEL;
        this.context=c;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle arg2){ }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(MainActivity.this,"location changed, lat:"+location.getLatitude()+",lng:"+location.getLongitude(),Toast.LENGTH_SHORT).show();
        if(mMap!=null)
        {
            if(null==initialPoint)
            {
                initialPoint=new TrackPoint(location);
                currentPoint=initialPoint;
            }
            else if(initialPoint!=null&&null==lastPoint)
            {
                lastPoint=initialPoint;
                currentPoint=new TrackPoint(location);

                float spd;
                if(location.hasSpeed()&&location.getSpeed()-0.0>0.00001)
                    spd=location.getSpeed();
                else spd= GeoUtils.getSpeed(lastPoint,currentPoint);
                lastPoint.setSpeed(spd);
                currentPoint.setSpeed(spd);
                savePoint(lastPoint);
                savePoint(currentPoint);
            }
            else
            {
                lastPoint=currentPoint;
                currentPoint=new TrackPoint(location);
                if(location.hasSpeed()&&location.getSpeed()-0.0>0.00001)
                    currentPoint.setSpeed(location.getSpeed());
                else
                    currentPoint.setSpeed(GeoUtils.getSpeed(lastPoint,currentPoint));
                savePoint(currentPoint);
            }

            currentLoc=new LatLng(location.getLatitude(),location.getLongitude());
            //savePoint(location);
            List<LatLng> points=lines.get(0).getPoints();
            points.add(currentLoc);
            lines.get(0).setPoints(points);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, MAP_CAMERA_ENLARGE_LEVEL));
            Toast.makeText(context,"lat:"+currentPoint.getLat()+",lng:"+currentPoint.getLng()+",alt:"+
                    currentPoint.getLat()+",spd:"+currentPoint.getSpeed()+",time"+currentPoint.getTime().toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void savePoint(TrackPoint p1)
    {
        tpDAO.insert(p1);
    }
}
