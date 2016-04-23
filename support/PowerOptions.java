package support;

import java.io.IOException;

public class PowerOptions
{
    public static void shutdown() throws RuntimeException, IOException {
        String shutdownCommand;
        String operatingSystem = System.getProperty("os.name");

//        if (operatingSystem.startsWith("Linux") || operatingSystem.startsWith("Mac OS")) {
//            shutdownCommand = "shutdown -h now";
//        }
        if (operatingSystem.startsWith("Windows")) {
            shutdownCommand = "shutdown.exe -s -f -t 0";
        }
        else {
            throw new RuntimeException("Unsupported operating system.");
        }
        Runtime.getRuntime().exec(shutdownCommand);
    }
    
    public static void standby() throws RuntimeException, IOException {
        String standbyCommand;
        String operatingSystem = System.getProperty("os.name");
        System.out.println("operatingSystem:" + operatingSystem);
        if (operatingSystem.startsWith("Windows")) {
            standbyCommand = "rundll32.exe powrprof.dll,SetSuspendState Standby";
        }
        else {
            throw new RuntimeException("Unsupported operating system.");
        }
        Runtime.getRuntime().exec(standbyCommand);
    }
}
