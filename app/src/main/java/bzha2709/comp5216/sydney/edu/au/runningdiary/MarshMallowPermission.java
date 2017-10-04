package bzha2709.comp5216.sydney.edu.au.runningdiary;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Edited by Bingqing Zhao on 2017/09/12
 */
public class MarshMallowPermission {

    public static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2;
    public static final int READ_FILES_PERMISSION_REQUEST_CODE = 3;
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }


    public boolean checkPermissionForExternalStorage(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }
    public boolean checkPermissionForInternet()
    {
        int result=ContextCompat.checkSelfPermission(activity,Manifest.permission.INTERNET);
        int result2=ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_NETWORK_STATE);
        if (result == PackageManager.PERMISSION_GRANTED && result==result2){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForCoarseLocation()
    {
        int result=ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForFineLocation()
    {
        int result=ContextCompat.checkSelfPermission(activity,Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForReadFiles(){
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForExternalStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "External Storage permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            System.out.print("get storage permission");
        }
    }


    public void requestPermissionForReadfiles(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "Read files permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},READ_FILES_PERMISSION_REQUEST_CODE);
        }
    }


    public void requestPermissionForLocation(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)){
            Toast.makeText(activity, "Location permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.INTERNET,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }



}
