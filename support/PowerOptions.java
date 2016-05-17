package support;

import java.io.IOException;

/**
 * Power Options to handle computer shutdown or sleep. Only tested on Windows.
 * @author Tony Hsu
 */
public class PowerOptions
{
    /**
     * Shuts down the computer
     * @throws RuntimeException Throws Unsupported Operating System
     * @throws IOException Throws unable to execute command
     */
    public static void shutdown() throws RuntimeException, IOException {
        String shutdownCommand;
        String operatingSystem = System.getProperty("os.name");

        // untested code
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
    
    /**
     * Sleeps the computer
     * @throws RuntimeException Throws Unsupported Operating System
     * @throws IOException Throws unable to execute command
     */
    public static void sleep() throws RuntimeException, IOException {
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
