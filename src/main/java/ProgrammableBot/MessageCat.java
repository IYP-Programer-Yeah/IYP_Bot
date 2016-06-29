package ProgrammableBot;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by HoseinGhahremanzadeh on 6/27/2016.
 */
public class MessageCat implements Serializable {
    public String messageTag;
    ArrayList<ArrayList<String>[]> messages = new ArrayList<ArrayList<String>[]>();
    ArrayList<ArrayList<String>[]> responds = new ArrayList<ArrayList<String>[]>();
}
