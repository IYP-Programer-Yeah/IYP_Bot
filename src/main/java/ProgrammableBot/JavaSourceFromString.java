package ProgrammableBot;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Created by HoseinGhahremanzadeh on 7/2/2016.
 */
public class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;

    public JavaSourceFromString(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
