package net.hawkengine.core.utilities;

import java.io.File;
import java.net.URL;

public class EndpointFinder {
    private static final char PKG_SEPARATOR = '.';
    private static final char DIR_SEPARATOR = '/';
    private static final String CLASS_FILE_SUFFIX = ".class";
    private String endpoints ="";
    
    public String getClasses(String packageName){
        String scannedPath = packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        File scannedDir = new File(scannedUrl.getFile());
        for (File file : scannedDir.listFiles()){
            if(file.getAbsolutePath().endsWith(CLASS_FILE_SUFFIX)) {
                int endIndex = file.getName().length() - CLASS_FILE_SUFFIX.length();
                String className = file.getName().substring(0, endIndex);
                String finished = packageName+"."+className+", ";
                this.endpoints += finished;
            }
        }
        return endpoints;
    }
}
