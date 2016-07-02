package ProgrammableBot;

import javax.tools.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

;

/**
 * Created by HoseinGhahremanzadeh on 7/2/2016.
 */
public class JavaStringCompiler {
    public static boolean compileString (String code, String name) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();

        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.print(code);
        out.close();
        JavaFileObject file = new JavaSourceFromString(name, writer.toString());

        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

        boolean success = task.call();
        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
            System.out.println(diagnostic.getCode());
            System.out.println(diagnostic.getKind());
            System.out.println(diagnostic.getPosition());
            System.out.println(diagnostic.getStartPosition());
            System.out.println(diagnostic.getEndPosition());
            System.out.println(diagnostic.getSource());
            System.out.println(diagnostic.getMessage(null));

        }
        System.out.println("Success: " + success);


        return success;
    }
}
