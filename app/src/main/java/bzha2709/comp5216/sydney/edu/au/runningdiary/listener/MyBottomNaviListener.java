package bzha2709.comp5216.sydney.edu.au.runningdiary.listener;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import bzha2709.comp5216.sydney.edu.au.runningdiary.MainActivity;
import bzha2709.comp5216.sydney.edu.au.runningdiary.Music;
import bzha2709.comp5216.sydney.edu.au.runningdiary.R;
import bzha2709.comp5216.sydney.edu.au.runningdiary.Stats;

/**
 * Created by Bingqing ZHAO on 2017/10/5.
 */

public class MyBottomNaviListener implements BottomNavigationView.OnNavigationItemSelectedListener
{
    MainActivity m;
    Fragment currentFragment;
    SupportMapFragment mapFragment;
    Stats stats;
    Music music;

    public MyBottomNaviListener(MainActivity c,SupportMapFragment map,Stats stats,Music music)
    {
        m=c;
        mapFragment=map;
        this.stats=stats;
        this.music=music;
        currentFragment=mapFragment;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Toast.makeText(m,item.getItemId(),Toast.LENGTH_LONG).show();
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

    private void changeFragment(int id)
    {
        FragmentTransaction ft = m.getSupportFragmentManager().beginTransaction();
        if (null != currentFragment) {ft.hide(currentFragment);}
        int index=0;
        if(id==R.id.navi_stats)index=1;
        else if(id==R.id.navi_music)index=2;
        else index=0;

        Button button=(Button)m.findViewById(R.id.main_start_button);

        if(index==0)
        {button.setVisibility(View.VISIBLE);}
        else
        {button.setVisibility(View.GONE);}


        Fragment f=m.getSupportFragmentManager().findFragmentById(id);
        if (null == f)
        {
            if(id==R.id.navi_map) f=mapFragment;
            else if(id==R.id.navi_stats)f=stats;
            else f=music;
        }
        currentFragment=f;
        if(!f.isAdded())
        {
            ft.add(R.id.content,f,f.getClass().getName());
        }
        else ft.show(f);
        ft.commit();
    }

}
