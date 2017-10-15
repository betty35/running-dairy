package bzha2709.comp5216.sydney.edu.au.runningdiary.tools;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import bzha2709.comp5216.sydney.edu.au.runningdiary.POJO.Song;

/**
 * Created by Administrator on 2017/10/13.
 */

public class MusicUtils
{

    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                if (song.size > 1000 * 800) {
                    if (song.song.contains("-")) {
                        String[] str = song.song.split("-");
                        song.singer = str[0];
                        song.song = str[1];
                    }
                    list.add(song);
                }
            }
            cursor.close();
        }
        return list;
    }

    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {return time / 1000 / 60 + ":0" + time / 1000 % 60;}
        else {return time / 1000 / 60 + ":" + time / 1000 % 60;}
    }
}
