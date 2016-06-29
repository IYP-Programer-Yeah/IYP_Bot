package String;

import java.util.ArrayList;

/**
 * Created by HoseinGhahremanzadeh on 6/29/2016.
 */
public class StringManipulation {
    /***************************************************************************
     * split string at match
     ***************************************************************************/
    public static String[] split(String str, String match){
        if (str.length()==0) {//str length is zero return itself
            String[] ret=new String[1];
            ret[0]=str;
            return ret;
        }
        ArrayList<String> ret = new ArrayList<String>();
        int matchLength=match.length();
        int matchIndex;
        while ((matchIndex=str.indexOf(match))!=-1){//while contains
            ret.add(str.substring(0,matchIndex));
            str=str.substring(matchIndex+matchLength);
        }
        if (str.length()!=0)//prevent "" to get added to the end of the array
            ret.add(str);
        String[] retArray=new String[ret.size()];
        retArray=ret.toArray(retArray);//get array
        return retArray;
    }
    /***************************************************************************
     * replace the first match
     ***************************************************************************/
    public static String replaceFirst(String str, String find, String replace) {
        int index=str.indexOf(find);
        if (index==-1)
            return str;//no matches
        else {
            return str.substring(0,index)+replace+str.substring(index+find.length());
        }
    }
    /***************************************************************************
     * replace the last match
     ***************************************************************************/
    public static String replaceLast(String str, String find, String replace) {
        int index=str.lastIndexOf(find);
        if (index==-1)
            return str;//no matches
        else {
            return str.substring(0,index)+replace+str.substring(index+find.length());
        }
    }
}
