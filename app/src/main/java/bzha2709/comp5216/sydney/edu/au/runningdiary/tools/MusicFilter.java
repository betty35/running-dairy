package bzha2709.comp5216.sydney.edu.au.runningdiary.tools;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Bingqing ZHAO
 */

public class MusicFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String filename) {
        // TODO Auto-generated method stub
        return (filename.endsWith(".mp3"));
    }
}
