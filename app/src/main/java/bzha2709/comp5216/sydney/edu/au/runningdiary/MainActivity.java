package bzha2709.comp5216.sydney.edu.au.runningdiary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.StepRecord;
import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.TrackPoint;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.DaoMaster;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.DaoSession;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.StepRecordDao;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.TrackPointDao;
import bzha2709.comp5216.sydney.edu.au.runningdiary.listener.MyBottomNaviListener;
import bzha2709.comp5216.sydney.edu.au.runningdiary.listener.MyLocationListener;
import bzha2709.comp5216.sydney.edu.au.runningdiary.listener.MyStepListener;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.CounterThread;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.DateUtil;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.GeoUtils;
import bzha2709.comp5216.sydney.edu.au.runningdiary.simplepedometer.*;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener
{
    private static final int MAP_CAMERA_ENLARGE_LEVEL=15;
    private static final int OVERLAY_PERMISSION_REQ_CODE=1234;


    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(3);

    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    SupportMapFragment mapFragment;
    public GoogleMap mMap;
    Stats stats;
    Music music;
    int steps;
    Button startButton;
    public boolean started=false;

    public MarshMallowPermission myPermission;
    LocationManager locationManager;
    List<String> providers;
    String locationProvider;
    Location location;
   // LatLng currentLoc;
    ArrayList<Polyline> lines;
    TrackPointDao tpDAO;
    StepRecordDao srDAO;
    FrameLayout content;

    Fragment currentFragment;

    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    public int numSteps;

    public MyStepListener myStepListener;
    MyLocationListener locationListener;
    Handler handler;
    CounterThread ct;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            locationManager.removeUpdates(locationListener);
        }
        if(sensorManager!=null)
        {
            sensorManager.unregisterListener(myStepListener);
            finishRec(myStepListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGreenDao();
        steps=0;
        lines=new ArrayList<Polyline>();
        content=(FrameLayout)findViewById(R.id.content);
        startButton=(Button)findViewById(R.id.main_start_button);



        myPermission = new MarshMallowPermission(this);
        if (!myPermission.checkPermissionForFineLocation()||!myPermission.checkPermissionForInternet())
            myPermission.requestPermissionForLocation();
        else
        {//get location
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            providers = locationManager.getProviders(true);
            if (providers.contains(LocationManager.GPS_PROVIDER))
            {
                locationProvider = LocationManager.GPS_PROVIDER;
                Toast.makeText(this, "Using GPS", Toast.LENGTH_LONG).show();
            }
            else if (providers.contains(LocationManager.NETWORK_PROVIDER))
            {
                locationProvider = LocationManager.NETWORK_PROVIDER;
                Toast.makeText(this, "Using Network", Toast.LENGTH_LONG).show();
            }
            else
            {Toast.makeText(this, "No location provider available", Toast.LENGTH_LONG).show(); return;}

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            location = locationManager.getLastKnownLocation(locationProvider);

            requestDrawOverLays();
            myPermission.requestPermissionForWindow();

            if(!myPermission.checkPermissionForReadFiles())
            {
                myPermission.requestPermissionForReadfiles();
                myPermission.requestPermissionForExternalStorage();
            }
            locationListener = new MyLocationListener(mMap,location,MAP_CAMERA_ENLARGE_LEVEL,MainActivity.this,lines,tpDAO,this);
            locationManager.requestLocationUpdates(locationProvider, 5*1000, 10, locationListener);
        }

        initFragment();
        mOnNavigationItemSelectedListener= new MyBottomNaviListener(this,mapFragment,stats,music);
        ((BottomNavigationView) findViewById(R.id.navigation)).setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //initiate step count
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new SimpleStepDetector();
        myStepListener=new MyStepListener(simpleStepDetector,this,stats);
        initiateStepListener(myStepListener);
        simpleStepDetector.registerListener(myStepListener);
        sensorManager.registerListener(myStepListener,accel,SensorManager.SENSOR_DELAY_NORMAL);
        //readFromFile();

        handler=new Handler();
        ct=new CounterThread(handler,stats);
    }


    public void requestDrawOverLays() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
    }



    public void initiateStepListener(MyStepListener sl)
    {
      // Toast.makeText(this,"srDAO"+(null==srDAO),Toast.LENGTH_SHORT).show();
       List<StepRecord> sr=srDAO.queryBuilder().where(StepRecordDao.Properties.Date.eq(DateUtil.getThisMorning())).list();
        if(sr.size()==0)
        {
            sl.setNumSteps(0);
            Toast.makeText(this,"0 step today",Toast.LENGTH_SHORT).show();
        }
        else
        {
            int steps=sr.get(0).getSteps();
            sl.setNumSteps(steps);
            Toast.makeText(this,steps+" steps today",Toast.LENGTH_SHORT).show();
        }
    }

    public void finishRec(MyStepListener sl)
    {
        int steps=sl.getNumSteps();

        List<StepRecord> srl=srDAO.queryBuilder()
                .where(StepRecordDao.Properties.Date.eq(DateUtil.getThisMorning())).list();
        //Toast.makeText(this, "steps:"+steps, Toast.LENGTH_SHORT).show();
       if(srl.size()==0)
        {
            StepRecord sr=new StepRecord(DateUtil.getThisMorning(),steps);
            srDAO.save(sr);
        }
        else
        {
            StepRecord sr=srl.get(0);
            sr.setSteps(steps);
            srDAO.update(sr);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add polylines and polygons to the map. This section shows just
        // a single polyline. Read the rest of the tutorial to learn more.
        mMap = googleMap;
        locationListener.setMap(mMap);
        LatLng mypos;
        if(null==location)mypos=new LatLng(-33.88853099,151.19398512);
        else mypos=new LatLng(location.getLatitude(),location.getLongitude());
        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions().clickable(true).add(mypos));
        polyline1.setTag("Now");

        Polyline polyline2 = googleMap.addPolyline(new PolylineOptions().clickable(true).add(mypos));
        polyline2.setTag("Today");
        polyline2.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        List<LatLng> li=convertToLatLng(getPeriodsOfDataFromDB(DateUtil.getThisMorning(),DateUtil.getTodayEvening()));

        Polyline polyline3 = googleMap.addPolyline(new PolylineOptions().clickable(true).add(mypos));
        polyline3.setTag("Other");
        polyline3.setColor(ContextCompat.getColor(this, R.color.colorAccent));

        if(li.size()>0)
            polyline2.setPoints(li);
        if(!lines.contains(polyline1))
            lines.add(polyline1);
        if(!lines.contains(polyline2))
            lines.add(polyline2);
        if(!lines.contains(polyline3))
            lines.add(polyline3);

        locationListener.setLines(lines);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mypos, MAP_CAMERA_ENLARGE_LEVEL));
        //Set listeners for click events.
        googleMap.setOnPolylineClickListener(this);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // now you can show audio widget
                music.audioWidget.show(100,100);
            }
        }

        if (requestCode == MarshMallowPermission.MY_LOCATION_PERMISSION_REQUEST_CODE) {
            if (permissions.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(mMap!=null&&ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                mMap.setMyLocationEnabled(true);
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                providers = locationManager.getProviders(true);
                if(providers.contains(LocationManager.GPS_PROVIDER)){
                    locationProvider = LocationManager.GPS_PROVIDER;
                }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }else{
                    Toast.makeText(this, "No location provider available", Toast.LENGTH_LONG).show();
                }
                location = locationManager.getLastKnownLocation(locationProvider);
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this,"Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void savePoint(TrackPoint p1)
    {
        tpDAO.insert(p1);
    }


    public List<TrackPoint> getPeriodsOfDataFromDB(Date d1, Date d2)
    {
        List<TrackPoint> tpList=tpDAO.queryBuilder().where(TrackPointDao.Properties.Time.between(d1,d2)).orderAsc(TrackPointDao.Properties.Time).list();
        /*if(tpList.size()>0)
        Toast.makeText(MainActivity.this,tpList.size()+" points, 4: lat:"+tpList.get(3).getLat()+",lng:"+tpList.get(3).getLng()+",spd:"+
        tpList.get(3).getSpeed(),Toast.LENGTH_LONG).show();*/
        return tpList;
    }

    public ArrayList<LatLng> convertToLatLng(List<TrackPoint> tpList)
    {
        ArrayList<LatLng> line=new ArrayList<LatLng>();
        for(int i=0;i<tpList.size();i++)
        {
            TrackPoint tp=tpList.get(i);
            LatLng temp=new LatLng(tp.getLat(),tp.getLng());
            //Toast.makeText(this,temp.toString(),Toast.LENGTH_SHORT).show();
            line.add(temp);
        }
        return line;
    }

    private void initGreenDao()
    {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "running-diary-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        tpDAO=daoSession.getTrackPointDao();
        srDAO=daoSession.getStepRecordDao();
    }

    private void initFragment()
    {
        mapFragment=SupportMapFragment.newInstance();
        mapFragment.getMapAsync(MainActivity.this);
        stats=new Stats();//(Stats)getFragmentManager().findFragmentById(R.id.stats);
        stats.ini(this);
        music=new Music();//(Music)getFragmentManager().findFragmentById(R.id.music);
        currentFragment=mapFragment;
        if(!mapFragment.isAdded())
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.content,mapFragment,mapFragment.getClass().getName()).commit();
        }
    }



    public void counterChange(View view)
    {
        if(!started)
        {
            started=true;
            startButton.setText("Pause");
            handler.postDelayed(ct,1000);
        }
        else
        {
            started=false;
            startButton.setText("Start");
            handler.removeCallbacks(ct);
        }
    }

}