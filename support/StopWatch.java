package support;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class StopWatch
{
    public interface StopWatchDelegate {
        public void timeChanged(StopWatch watch, String string); 
    }
    
    private long totalInSeconds;
    private Timer timer;
    private StopWatchDelegate delegate;
    
    public StopWatch(StopWatchDelegate delegate) {
        this(0, delegate);
    }
    
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
    
    private void incrementTime()
    {
        totalInSeconds++;
        delegate.timeChanged(this, toString());
    }
    
    public void start()
    {
        timer.start();
    }
    
    public void stop()
    {
        timer.stop();
    }
    
    public void restart()
    {
        timer.restart();
    }
    
    public String toString()
    {
        long seconds = totalInSeconds % 60;
        long minutes = totalInSeconds / 60;
        long hours = totalInSeconds / 60 / 60;
        if(hours > 0)
        {
            return String.format("%02:%02d:%02d", hours, minutes, seconds);
        }
        else
        {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
}
