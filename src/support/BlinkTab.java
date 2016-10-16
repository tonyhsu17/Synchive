package support;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import gui.tabbedPanels.TabbedContainerPaneView;

/**
 * Class encapsulation for blinking a tab.
 * @author Tony Hsu
 */
public class BlinkTab
{
    /**
     * Container of the tab
     */
    private TabbedContainerPaneView tab;
    /**
     * Index of the tab to blink 
     */
    private int index;
    /**
     * Default color of tab
     */
    private Color defaultColor;
    /**
     * Color to change to
     */
    private Color highlightedColor;
    /**
     * Is tab color changed. Less overhead than comparing tab's current color
     */
    private boolean highlighted;
    /**
     * Delay between switching color (in miliseconds)
     */
    private int delay; 
    /**
     * Timer to fire off changing color
     */
    private Timer timer;
    
    /**
     * Blinks a specific tab to provide a visual indicator. With default of 500ms.
     * @param containerView Container of the tab
     * @param index Index of the specific tab
     * @param highlightedColor Color to change to
     */
    public BlinkTab(TabbedContainerPaneView containerView, int index, Color highlightedColor)
    {
        this(containerView, index, highlightedColor, 500);
    }
    
    /**
     * Blinks a specific tab to provide a visual indicator.
     * @param containerView Container of the tab
     * @param index Index of the specific tab
     * @param highlightedColor Color to change to
     * @param delayInMili Delay between switching color
     */
    public BlinkTab(TabbedContainerPaneView containerView, int index, Color highlightedColor, int delayInMili)
    {
        tab = containerView;
        this.index = index;
        defaultColor = containerView.getBackgroundAt(index);
        this.highlightedColor = highlightedColor;
        highlighted = false;
        delay = delayInMili;
        
        //create a timer to fire off event
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                blink();
            }
        });
    }
    
    /**
     * Start blinking the tab. Recalling it will restart it.
     */
    public void startBlinking()
    {
        highlighted = false;
        if(timer.isRunning())
        {
            timer.restart();
        }
        else
        {
            timer.start();
        }
    }
    
    /**
     * Stop blinking the tab and reset to default color.
     */
    public void stopBlinking()
    {
        timer.stop();
        
        if(highlighted)
        {
            highlighted = false;
            tab.setBackgroundAt(index, defaultColor);
        }
    }
    
    /**
     * Triggers color change on the tab
     */
    private void blink()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                highlighted = !highlighted;
                tab.setBackgroundAt(index, highlighted ? highlightedColor : defaultColor);
            }
        });
    }
}
