package se.artcomputer.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class Logger {

    private String currentLog = "";
    private PrintWriter logStream;

    public Logger(File dir) {
        openLogStream(dir);
    }

    public void log(String message) {
        currentLog += message;
    }

    public void logNl(String message) {
        log(message);
        logStream.println(currentLog);
        currentLog = "";
    }

    public void flush() {
        logStream.flush();
    }

    public void close() {
        logStream.close();
    }

    private void openLogStream(File dir) {
        try {
            logStream = new PrintWriter(dir.getAbsolutePath() + File.separator + fileName() + ".log");
        } catch (FileNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }

    private String fileName() {
        return new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
    }
}
