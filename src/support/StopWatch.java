package support;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * Stop watch providing time seconds
 * @author Tony Hsu
 */
public class StopWatch
{
    /**
     * Delegate methods for StopWatchDelegate
     */
    public interface StopWatchDelegate {
        /**
         * Event notifier if seconds increased
         * @param watch StopWatch of seconds increased
         * @param string Converted to MM:SS or HH:MM:SS
         */
        public void timeChanged(StopWatch watch, String string); 
    }
    
    /**
     * Total seconds of running
     */
    private long totalInSeconds;
    /**
     * Timer to fire off event
     */
    private Timer timer;
    /**
     * Common methods for controller to implement to handle events
     */
    private StopWatchDelegate delegate;
    
    /**
     * Initializes stop watch with zero seconds
     * @param delegate Controller to handle events
     */
    public StopWatch(StopWatchDelegate delegate) {
        this(0, delegate);
    }
    
    /**
     * Initializes stop watch with a predetermined seconds
     * @param seconds Number of seconds to start from
     * @param delegate Controller to handle events
     */
    public StopWatch(long seconds, StopWatchDelegate delegate)
    {
        this.totalInSeconds = seconds;
        this.delegate = delegate;
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                incrementTime();
            }
        });
    }
    
    /**
     * Increments seconds and calls delegate
     */
    private void incrementTime()
    {
        totalInSeconds++;
        delegate.timeChanged(this, toString());
    }
    
    /**
     * Starts the stop watch
     */
    public void start()
    {
        timer.start();
    }
    
    /**
     * Stops the stop watch
     */
    public void stop()
    {
        timer.stop();
    }
    
    /**
     * Resets the stop watch to zero seconds
     */
    public void restart()
    {
        timer.restart();
    }
    
    // If under an hour MM:SS else HH:MM:SS
    public String toString()
    {
        long seconds = totalInSeconds % 60;
        long minutes = (totalInSeconds / 60) % 60;
        long hours = totalInSeconds / 60 / 60;
        if(hours > 0)
        {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        else
        {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
