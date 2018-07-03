package ConflictDetector;

import ConflictDetector.Detector.Detector;
import ConflictDetector.Detector.Pair4J;

// Java Launcher
public class JavaMain {
    public static void main(String[] args){
        Pair4J result = Detector.processConflictDetect("originalPolicy", "modifiedPolicy");
        System.out.println(result);
    }
}