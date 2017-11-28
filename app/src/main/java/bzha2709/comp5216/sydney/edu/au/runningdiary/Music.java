package bzha2709.comp5216.sydney.edu.au.runningdiary;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.cleveroad.audiowidget.AudioWidget;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.Song;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.MusicUtils;
import bzha2709.comp5216.sydney.edu.au.runningdiary.tools.MyAdapter;
/**
 * A simple {@link Fragment} subclass.
 */
public class Music extends android.support.v4.app.Fragment {

    private static final String TAG = "MusicService";
    private static final long UPDATE_INTERVAL = 1000;
    Timer timer;
    AudioWidget audioWidget;
    ListView listView;
    EditText musicSearch;
    Button searchButton;
    private MediaPlayer mediaPlayer;

    private List<Song> list;
    private List<Song> musicList;
    private MyAdapter adapter;

    public static int currentListItem=0;



    public Music()
    {
        mediaPlayer=new MediaPlayer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_music, container, false);
        listView=view.findViewById(R.id.music_list);
        iniMusicList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentListItem=i;
                play(list.get(currentListItem));
            }
        });
        musicSearch=(EditText)view.findViewById(R.id.music_search);
        searchButton=(Button)view.findViewById(R.id.music_search_button);
        audioWidget = new AudioWidget.Builder(getActivity())
                .lightColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
        .darkColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
        .expandWidgetColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
        .progressColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                .shadowColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark))
        .build();
        audioWidget.controller().onControlsClickListener(new AudioWidget.OnControlsClickListener() {
            @Override
            public boolean onPlaylistClicked() {
                // playlist icon clicked
                // return false to collapse widget, true to stay in expanded state
                return false;
            }

            @Override
            public void onPreviousClicked() {
                currentListItem--;
                if(currentListItem==-1) currentListItem=list.size()-1;
                play(list.get(currentListItem));
                // previous track button clicked
            }

            @Override
            public boolean onPlayPauseClicked() {
                // return true to change playback state of widget and play button click animation (in collapsed state)
                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                }
                else
                {
                    if(mediaPlayer.getDuration()==0&&list.size()>0)
                    {play(list.get(0));}
                    else
                    mediaPlayer.start();
                }
                return false;
            }

            @Override
            public void onNextClicked() {
                // next track button clicked
                currentListItem++;
                if(currentListItem==list.size())currentListItem=0;
                play(list.get(currentListItem));
            }

            @Override
            public void onAlbumClicked() {
                // album cover clicked
            }

            @Override
            public void onPlaylistLongClicked() {
                // playlist button long clicked
            }

            @Override
            public void onPreviousLongClicked() {
                // previous track button long clicked
            }

            @Override
            public void onPlayPauseLongClicked() {
                // play/pause button long clicked
            }

            @Override
            public void onNextLongClicked() {
                // next track button long clicked
            }

            @Override
            public void onAlbumLongClicked() {
                // album cover long clicked
            }
        });
        audioWidget.controller().onWidgetStateChangedListener(new AudioWidget.OnWidgetStateChangedListener() {
            @Override
            public void onWidgetStateChanged(@NonNull AudioWidget.State state) {
                // widget state changed (COLLAPSED, EXPANDED, REMOVED)
            }
            @Override
            public void onWidgetPositionChanged(int cx, int cy) {
                // widget position change. Save coordinates here to reuse them next time AudioWidget.show(int, int) called.
            }
        });
        if(Settings.canDrawOverlays(getActivity())) audioWidget.show(200,300);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp=musicSearch.getText().toString();
                if(!temp.equals(""))
                {
                    list.clear();
                    for(int i=0;i<musicList.size();i++)
                    {
                        if(musicList.get(i).song.toLowerCase().contains(temp.toLowerCase()))
                        list.add(musicList.get(i));
                    }
                }
                else
                {
                    list.clear();
                    list.addAll(musicList);
                }
                adapter.notifyDataSetChanged();
            }
        });
        return view;
    }


    public void iniMusicList(){
        musicList=MusicUtils.getMusicData(getActivity());
        if(list!=null)list.clear();
        else list=new ArrayList<>();
        list.addAll(musicList);
        adapter = new MyAdapter(getActivity(),list);
        listView.setAdapter(adapter);
    }


    private void play(Song song) {
        String path=song.path;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            audioWidget.controller().stop();
            audioWidget.controller().duration(song.duration);
            audioWidget.controller().start();
            stopTrackingPosition();
            startTrackingPosition();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startTrackingPosition() {
        timer = new Timer(TAG);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                AudioWidget widget = audioWidget;
                MediaPlayer player = mediaPlayer;
                if (widget != null) {
                    widget.controller().position(player.getCurrentPosition());
                }
            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL);
    }

    private void stopTrackingPosition() {
        if (timer == null)
            return;
        timer.cancel();
        timer.purge();
        timer = null;
    }



    @Override
    public void onDestroy()
    {
        audioWidget.controller().onControlsClickListener(null);
        audioWidget.controller().onWidgetStateChangedListener(null);
        audioWidget.hide();
        audioWidget = null;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        stopTrackingPosition();
        super.onDestroy();

    }
}
