package bzha2709.comp5216.sydney.edu.au.runningdiary.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.TrackPoint;

/**
 * Created by Administrator on 2017/10/2.
 */

public class GeoUtils {
    public static double getDistance(double lng1,double lat1,double lng2,double lat2){
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double a = radLat1 - radLat2;
        double b = Math.toRadians(lng1) - Math.toRadians(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;// radius of the earth
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static double getDistance(TrackPoint t1,TrackPoint t2)
    {
        double lng1=t1.getLng();
        double lng2=t2.getLng();
        double lat1=t1.getLat();
        double lat2=t2.getLat();
        return getDistance(lng1,lat1,lng2,lat2);
    }

    public static double[] getSpeed(List<TrackPoint> tpL,int interval_minites)
    {
        int interval=interval_minites;
        Long beginning=tpL.get(0).getTime().getTime();
        Long end=tpL.get(tpL.size()-1).getTime().getTime();
        long duration=end-beginning;
        double minutes=duration/(1000*60);
        int groups=(int)Math.ceil(minutes/interval);
        double[] re=new double[groups];//store distances
        int cursor=0;//for putting speeds in different time slots
        long sumOfTime=0;
        for(int i=0;i<tpL.size();i++)
        {
           if(i+1<tpL.size())
           {
               TrackPoint t1=tpL.get(i);
               TrackPoint t2=tpL.get(i+1);
               sumOfTime=sumOfTime+(t2.getTime().getTime()-t1.getTime().getTime());
               if(sumOfTime<interval*60*1000)
               {
                   re[cursor]+=getDistance(t1,t2);
               }
               else
               {
                   cursor++;
                   sumOfTime=0;
                   re[cursor]+=getDistance(t1,t2);
               }
           }
        }

        for(int i=0;i<re.length;i++)
        {
            re[i]=(re[i]/1000)/(interval/60);
        }
        return re;
    }


    public static float getSpeed(TrackPoint t1,TrackPoint t2)
    {
        double distance=getDistance(t1,t2);
        long time=getMiliSecondsInBetween(t1,t2);
        float spd=(float)(distance/(time/1000.0));
        return spd;
    }

    public static long getMiliSecondsInBetween(TrackPoint t1, TrackPoint t2)
    {
        long time1=t1.getTime().getTime();
        long time2=t2.getTime().getTime();
        long time3;
        if(time1>time2) time3=time1-time2;
        else time3=time2-time1;
        return time3;
    }

    public static ArrayList<ArrayList<TrackPoint>> split(List<TrackPoint> tpl)
    {
        long interval=5*60*1000;//5 minutes
        // if variance between two timestamps are larger than the interval, split the points
        ArrayList<ArrayList<TrackPoint>> l=new ArrayList<ArrayList<TrackPoint>>();
        ArrayList<TrackPoint> t=new ArrayList<TrackPoint>();
        for(int i=0;i<tpl.size();i++)
        {

        }
        return null;
    }
}
