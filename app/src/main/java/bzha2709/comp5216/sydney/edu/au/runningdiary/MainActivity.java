package bzha2709.comp5216.sydney.edu.au.runningdiary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
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

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.TrackPoint;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.DaoMaster;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.DaoSession;
import bzha2709.comp5216.sydney.edu.au.runningdiary.db.TrackPointDao;
import bzha2709.comp5216.sydney.edu.au.runningdiary.listener.MyLocationListener;
import bzha2709.comp5216.sydney.edu.au.runningdiary.listener.MyStepListener;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.DateUtil;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.GeoUtils;
import bzha2709.comp5216.sydney.edu.au.runningdiary.simplepedometer.*;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener
{
    private static final int MAP_CAMERA_ENLARGE_LEVEL=15;

    private static final PatternItem DOT = new Dot();
    private static final PatternItem GAP = new Gap(3);
    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    SupportMapFragment mapFragment;
    public GoogleMap mMap;
    Stats stats;
    Music music;

    public MarshMallowPermission myPermission;
    LocationManager locationManager;
    List<String> providers;
    String locationProvider;
    Location location;
   // LatLng currentLoc;
    ArrayList<Polyline> lines;
    TrackPointDao tpDAO;
    FrameLayout content;
    /*TrackPoint initialPoint;
    TrackPoint lastPoint;
    TrackPoint currentPoint;*/

    Fragment currentFragment;

    private SimpleStepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;

    MyStepListener myStepListener;
    MyLocationListener locationListener;
           /* new LocationListener() {

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
                    else spd=GeoUtils.getSpeed(lastPoint,currentPoint);
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
                Toast.makeText(MainActivity.this,"lat:"+currentPoint.getLat()+",lng:"+currentPoint.getLng()+",alt:"+
                        currentPoint.getLat()+",spd:"+currentPoint.getSpeed()+",time"+currentPoint.getTime().toString(),Toast.LENGTH_LONG).show();
                //Toast.makeText(MainActivity.this,"lat:"+location.getLatitude()+",lng:"+location.getLongitude()+",alt:"+location.getAltitude()+",speed:"+location.hasSpeed(),Toast.LENGTH_LONG).show();
            }
        }
    };*/


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navi_map:
                {
                   changeFragment(R.id.navi_map);
                    //mapFragment.getMapAsync(MainActivity.this);
                    return true;
                }
                case R.id.navi_stats:
                {
                    changeFragment(R.id.navi_stats);
                    return true;
                }
                case R.id.navi_music:
                    changeFragment(R.id.navi_music);
                    return true;
            }
            return false;
        }

    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(locationManager!=null){
            //移除监听器
            locationManager.removeUpdates(locationListener);
        }
        sensorManager.unregisterListener(myStepListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGreenDao();
        Log.i("onCreate","here");

        lines=new ArrayList<Polyline>();
        content=(FrameLayout)findViewById(R.id.content);
        //readFromFile();

        myPermission = new MarshMallowPermission(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (!myPermission.checkPermissionForFineLocation()||!myPermission.checkPermissionForInternet())
        {//request for permission if location info is not permitted
            myPermission.requestPermissionForLocation();
        }
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

            // Get an instance of the SensorManager
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            simpleStepDetector = new SimpleStepDetector();
            myStepListener=new MyStepListener(simpleStepDetector);
            simpleStepDetector.registerListener(myStepListener);

            locationListener = new MyLocationListener(mMap,location,MAP_CAMERA_ENLARGE_LEVEL,MainActivity.this);
            locationManager.requestLocationUpdates(locationProvider, 5*1000, 10, locationListener);
        }

        initFragment();
        changeFragment(R.id.navi_map);
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
        polyline1.setTag("Today");
        List<LatLng> li=convertToLatLng(getPeriodsOfDataFromDB(DateUtil.getThisMorning(),DateUtil.getTodayEvening()));
        Toast.makeText(MainActivity.this,"list size:"+li.size(),Toast.LENGTH_LONG).show();
        if(li.size()>0)
        {
            Toast.makeText(MainActivity.this,"list",Toast.LENGTH_LONG).show();
            polyline1.setPoints(li);
        }
        if(!lines.contains(polyline1))
            lines.add(polyline1);
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

        Toast.makeText(this, "Route type " + polyline.getTag().toString(),
                Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
        if(tpList.size()>0)
        Toast.makeText(MainActivity.this,tpList.size()+" points, 4: lat:"+tpList.get(3).getLat()+",lng:"+tpList.get(3).getLng()+",spd:"+
        tpList.get(3).getSpeed(),Toast.LENGTH_LONG).show();
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
    }

    private void initFragment()
    {
        mapFragment=SupportMapFragment.newInstance();
        mapFragment.getMapAsync(MainActivity.this);
        stats=new Stats();//(Stats)getFragmentManager().findFragmentById(R.id.stats);
        music=new Music();//(Music)getFragmentManager().findFragmentById(R.id.music);
        currentFragment=mapFragment;
    }


    private void changeFragment(int id)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (null != currentFragment) {ft.hide(currentFragment);}
        int index=0;
        if(id==R.id.navi_stats)index=1;
        if(id==R.id.navi_music)index=2;
        else index=0;
        Fragment f=getSupportFragmentManager().findFragmentById(id);
        if (null == f)
        {
            if(id==R.id.navi_map) f=mapFragment;
            else if(id==R.id.navi_stats)f=stats;
            else f=music;
        }
        currentFragment=f;
        if(!f.isAdded()) { ft.add(R.id.content,f,f.getClass().getName());}
        else ft.show(f);
        ft.commit();
    }

    public void readFromFile()
    {
        String csv="1,1507005625361,-33.87939646,151.10104952,52.0,0.42034468054771423;2,1507005649151,-33.87948702,151.10104791,57.0,0.42034468054771423;3,1507005658150,-33.87957683,151.10090493,51.0,1.5399999618530273;4,1507005663148,-33.87969556,151.10097396,53.0,1.7100000381469727;5,1507005673144,-33.87976543,151.10110364,53.0,1.3200000524520874;6,1507005678154,-33.87979502,151.10121373,53.0,1.2599999904632568;7,1507005688147,-33.87981885,151.1013749,54.0,1.350000023841858;8,1507005698145,-33.8798801,151.10152407,58.0,0.9399999976158142;9,1507005703147,-33.87988528,151.10163205,58.0,2.990000009536743;10,1507005708158,-33.87989074,151.10176361,58.0,2.0799999237060547;11,1507005718150,-33.87989907,151.10196665,59.0,1.8200000524520874;12,1507005728159,-33.87993482,151.10215591,57.0,1.600000023841858;13,1507005738152,-33.87996439,151.10234799,56.0,2.2200000286102295;14,1507005748153,-33.87999079,151.10252041,57.0,1.5800000429153442;15,1507005758148,-33.88004861,151.10268977,62.0,1.9700000286102295;16,1507005768150,-33.88006956,151.10288286,61.0,1.9199999570846558;17,1507005774162,-33.88006,151.10300583,72.0,1.3300000429153442;18,1507005780173,-33.88000592,151.10310922,66.0,1.9800000190734863;19,1507005785168,-33.87991655,151.10319745,57.0,1.899999976158142;20,1507005794161,-33.87976702,151.10326058,66.0,0.800000011920929;21,1507005804165,-33.87964218,151.10332976,69.0,1.1200000047683716;22,1507005814080,-33.87950224,151.10337909,66.0,1.3700000047683716;23,1507005824082,-33.87935029,151.10342966,64.0,1.8799999952316284;24,1507005829072,-33.87924963,151.10348565,62.0,1.1200000047683716;25,1507005839082,-33.87909045,151.10350928,67.0,0.7099999785423279;26,1507005849080,-33.87897696,151.10350158,65.0,1.2002400159835815;27,1507005859075,-33.87882633,151.10345625,53.0,1.2200000286102295;28,1507005869094,-33.8786878,151.10347786,59.0,0.9700000286102295;29,1507005879083,-33.87854608,151.10353632,56.0,1.399999976158142;30,1507005884081,-33.87845525,151.10353669,61.0,1.5199999809265137;31,1507005889081,-33.87835804,151.10353381,56.0,1.3700000047683716;32,1507005899078,-33.87820718,151.10352371,54.0,0.699999988079071;33,1507005909080,-33.87809077,151.10364296,59.0,1.1200000047683716;34,1507005920089,-33.87788684,151.10373078,55.0,1.7300000190734863;35,1507005929071,-33.8777223,151.1037357,56.0,1.3899999856948853;36,1507005939082,-33.87760824,151.10374721,58.0,1.0499999523162842;37,1507005949087,-33.87750633,151.10363584,57.0,1.4992504119873047;38,1507005969079,-33.87738988,151.10356494,53.0,0.8199999928474426;39,1507005979092,-33.87728689,151.10361147,57.0,1.198441982269287;40,1507006151278,-33.877269,151.10505557,63.0,3.6721503734588623;41,1507006158086,-33.8774948,151.10505355,50.0,3.6721503734588623;42,1507006169095,-33.87739116,151.10522172,47.0,6.550000190734863;43,1507006178091,-33.87778717,151.10639063,57.0,14.180000305175781;44,1507006184085,-33.87814913,151.1073303,60.0,17.280000686645508;45,1507006189094,-33.87843095,151.10839456,57.0,19.1299991607666;46,1507006198081,-33.87917975,151.11014098,60.0,20.56999969482422;47,1507006203084,-33.87980646,151.11100566,63.0,20.450000762939453;48,1507006208085,-33.88049072,151.11172296,64.0,21.209999084472656;49,1507006213079,-33.88119877,151.11257214,67.0,21.299999237060547;50,1507006218091,-33.88183287,151.11343215,69.0,21.440000534057617;51,1507006223081,-33.88242822,151.11436167,67.0,20.5;52,1507006228086,-33.88295232,151.11520678,65.0,20.170000076293945;53,1507006236081,-33.88373559,151.11670605,58.0,20.889999389648438;54,1507006241084,-33.88439925,151.11764387,53.0,21.700000762939453;55,1507006246082,-33.88482977,151.11867898,49.0,20.799999237060547;56,1507006251077,-33.88528415,151.11962612,47.0,20.079999923706055;57,1507006256077,-33.88570425,151.12058294,50.0,19.719999313354492;58,1507006261076,-33.8861243,151.12157167,53.0,19.3799991607666;59,1507006266093,-33.88646529,151.12243278,50.0,18.690000534057617;60,1507006271076,-33.88671314,151.12340178,54.0,18.309999465942383;61,1507006277085,-33.88710042,151.12455115,50.0,17.600000381469727;62,1507006287084,-33.88768377,151.126233,53.0,16.450000762939453;63,1507006292086,-33.88798628,151.12680637,57.0,13.8100004196167;64,1507006302085,-33.8883145,151.1280886,59.0,12.4399995803833;65,1507006307092,-33.88850908,151.12872159,59.0,11.84000015258789;66,1507006317089,-33.88920822,151.13017779,61.0,14.09000015258789;67,1507006322083,-33.88942755,151.13094124,60.0,14.609999656677246;68,1507006332097,-33.88969561,151.13244793,57.0,13.989999771118164;69,1507006337079,-33.88963131,151.133264,56.0,15.989999771118164;70,1507006347077,-33.88974112,151.13523085,58.0,19.860000610351562;71,1507006352088,-33.88980346,151.136376,60.0,20.899999618530273;72,1507006362090,-33.89012835,151.13864829,61.0,21.260000228881836;73,1507006370078,-33.89061884,151.14036499,61.0,22.190000534057617;74,1507006375159,-33.89114479,151.14142697,61.0,22.260000228881836;75,1507006467080,-33.89461612,151.16150658,68.0,21.610000610351562;76,1507006475078,-33.8944709,151.16332426,61.0,21.610000610351562;77,1507006481106,-33.89423276,151.16478536,65.0,22.030000686645508;78,1507006486085,-33.89413915,151.16591776,62.0,22.0;79,1507006492098,-33.89406225,151.16723828,65.0,20.309999465942383;80,1507006497095,-33.89415075,151.1683929,59.0,21.209999084472656;81,1507006505079,-33.89478016,151.17023794,63.0,22.020000457763672;82,1507006510097,-33.89519698,151.17127373,60.0,21.739999771118164;83,1507006515079,-33.895565,151.17242271,49.0,22.110000610351562;84,1507006521105,-33.89627558,151.17360517,56.0,20.90999984741211;85,1507006527133,-33.89690997,151.17465995,57.0,19.8700008392334;86,1507006533120,-33.89737346,151.17576999,62.0,19.1200008392334;87,1507006538145,-33.89757252,151.17658652,61.0,16.260000228881836;88,1507006544146,-33.89763789,151.17754289,61.0,14.109999656677246;89,1507006549148,-33.89766678,151.17837955,60.0,14.59000015258789;90,1507006554121,-33.89766663,151.17916968,61.0,14.65999984741211;91,1507006559146,-33.89763484,151.17970053,59.0,13.420000076293945;92,1507006564151,-33.89766945,151.18053038,58.0,14.050000190734863;93,1507006569146,-33.89771637,151.18131557,56.0,14.199999809265137;94,1507006574144,-33.89768044,151.18217016,59.0,17.010000228881836;95,1507006584164,-33.89731215,151.18411861,54.0,20.1200008392334;96,1507006593138,-33.89667208,151.18600217,51.0,20.760000228881836;97,1507006598135,-33.89638118,151.18700343,49.0,19.709999084472656;98,1507006604151,-33.89592325,151.18824468,50.0,20.579999923706055;99,1507006614145,-33.89515143,151.19026174,48.0,20.260000228881836;100,1507006705331,-33.8920342,151.19827869,51.0,1.190000057220459;101,1507006713132,-33.89193639,151.19832276,48.0,1.190000057220459;102,1507006722119,-33.89191446,151.19846057,49.0,0.6800000071525574;103,1507006732111,-33.8917993,151.19849225,44.0,1.2200000286102295;104,1507006745187,-33.89174059,151.19862187,45.0,0.25999999046325684;105,1507006769146,-33.89169195,151.19872221,53.0,0.41999998688697815;106,1507006782126,-33.89166521,151.19882875,48.0,0.7900000214576721;107,1507006793158,-33.89139497,151.19889587,57.0,3.5999999046325684;108,1507006804146,-33.89134777,151.19873015,52.0,1.2300000190734863;109,1507006814149,-33.89134995,151.19852304,51.0,2.2699999809265137;110,1507006819139,-33.89134597,151.1983936,54.0,2.4000000953674316;111,1507006824141,-33.8913269,151.19827462,58.0,1.9700000286102295;112,1507006834140,-33.89134009,151.19812564,62.0,0.7200000286102295;113,1507006844142,-33.89138764,151.19794354,56.0,1.7000000476837158;114,1507006849134,-33.8913744,151.19781743,52.0,2.430000066757202;115,1507006854142,-33.89129757,151.19768301,55.0,2.0999999046325684;116,1507006864134,-33.89127879,151.19751041,55.0,1.7599999904632568;117,1507006874148,-33.89121047,151.19734353,49.0,1.25;118,1507006884148,-33.89115177,151.19719507,54.0,1.1399999856948853;119,1507006895150,-33.8911576,151.19700061,47.0,1.2999999523162842;120,1507006905135,-33.89114707,151.19681325,47.0,1.2599999904632568;121,1507006915138,-33.89116198,151.19659798,46.0,1.850000023841858;122,1507006920139,-33.89112344,151.19649279,46.0,0.949999988079071;123,1507006939119,-33.89104766,151.1964308,54.0,0.5268703699111938;124,1507006976139,-33.89096475,151.1963238,55.0,1.2200000286102295;125,1507006985157,-33.89100343,151.19620318,49.0,0.9599999785423279;126,1507006996130,-33.89097455,151.19604987,59.0,1.059999942779541;127,1507007005141,-33.89085372,151.19592288,54.0,0.7300000190734863;128,1507007010149,-33.89077765,151.19581239,46.0,4.110000133514404;129,1507007016161,-33.89068772,151.19565722,46.0,0.9599999785423279;130,1507007026139,-33.89074478,151.19551342,47.0,2.9000000953674316;131,1507007031137,-33.89075334,151.19534797,45.0,1.9800000190734863;132,1507007041146,-33.89083236,151.19522582,51.0,1.3600000143051147;133,1507007051165,-33.89091724,151.19509337,49.0,0.6800000071525574;134,1507007066170,-33.89100933,151.19490494,43.0,1.4299999475479126;135,1507007071150,-33.89103214,151.1947872,38.0,1.090000033378601;136,1507007080144,-33.89108611,151.19462701,44.0,2.200000047683716;137,1507007085256,-33.89113999,151.19452058,45.0,1.7799999713897705;138,1507007090129,-33.89120254,151.19437035,38.0,0.7400000095367432;139,1507007096149,-33.89120833,151.19425918,38.0,1.1699999570846558;140,1507007106139,-33.89128802,151.19407032,32.0,1.6799999475479126;141,1507007116143,-33.89131125,151.19382882,37.0,1.8799999952316284;142,1507007125139,-33.89126986,151.19369613,34.0,1.190000057220459;143,1507007136135,-33.89120194,151.19349931,40.0,1.6200000047683716;144,1507007141123,-33.891145,151.19336249,42.0,1.8899999856948853;145,1507007149147,-33.89109103,151.1932033,43.0,1.8799999952316284;146,1507007159153,-33.89100964,151.19303743,43.0,2.0;147,1507007169145,-33.89086857,151.19301401,49.0,2.9800000190734863;148,1507007179151,-33.8907566,151.19307383,59.0,1.100000023841858;149,1507007184152,-33.89067428,151.19312437,56.0,2.3499999046325684;150,1507007194158,-33.89051883,151.19324172,47.0,2.7699999809265137;151,1507007199147,-33.89042584,151.19329396,48.0,1.309999942779541;152,1507007210144,-33.89027273,151.19332053,46.0,1.7000000476837158;153,1507007221149,-33.89020235,151.19344023,44.0,1.5199999809265137;154,1507007231147,-33.89010004,151.19363406,46.0,0.8399999737739563;155,1507007241120,-33.88995007,151.19376378,53.0,1.0199999809265137;156,1507007256117,-33.88984907,151.19386529,51.0,1.5700000524520874;157,1507007266120,-33.88960248,151.19392998,45.0,2.7991602420806885;158,1507007274134,-33.88950592,151.19396337,55.0,1.4500000476837158;159,1507007279134,-33.88939663,151.19371606,48.0,1.1399999856948853;160,1507007289132,-33.88923627,151.19367838,40.0,1.440000057220459;161,1507007297143,-33.88910273,151.19369087,47.0,1.659999966621399;162,1507007302151,-33.88899836,151.19373531,43.0,2.4800000190734863;163,1507007307149,-33.88890152,151.19378843,46.0,1.6100000143051147;164,1507007317149,-33.88877065,151.19384889,45.0,2.0999999046325684;165,1507007327121,-33.88866934,151.1939255,45.0,1.2699999809265137;166,1507007338129,-33.88853099,151.19398512,45.0,0.7200000286102295";
        String[] ps=csv.split(";");
        for(int i=0;i<ps.length;i++)
        {
            String pt=ps[i];
            String[] vs=pt.split(",");
            long t11=Long.parseLong(vs[1]);
            Date d1=new Date();
            d1.setTime(t11);
            double lat1=Double.parseDouble(vs[2]);
            double lng1=Double.parseDouble(vs[3]);
            double alt1=Double.parseDouble(vs[4]);
            float spd1=Float.parseFloat(vs[5]);
            TrackPoint tp=new TrackPoint(lat1,lng1,alt1,spd1);
            tp.setTime(d1);
            savePoint(tp);
        }
    }
}