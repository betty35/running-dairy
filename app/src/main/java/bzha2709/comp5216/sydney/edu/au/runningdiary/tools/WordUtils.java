package bzha2709.comp5216.sydney.edu.au.runningdiary.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/10/15.
 */

public class WordUtils
{
    public static boolean isChinese(String con) {

        for (int i = 0; i < con.length(); i = i + 1) {
            if (!Pattern.compile("[\u4e00-\u9fa5]").matcher(
                    String.valueOf(con.charAt(i))).find()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str)
    {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() )
        {
            return false;
        }
        return true;
    }

    public static boolean isWord(String con) {
        if (null != con && !"".equals(con)) {
            if ((isChinese(con) || con.matches("^[A-Za-z]+$"))
                    && con.length() <= 10) {
                return true;
            }
        }
        return false;
    }
}
