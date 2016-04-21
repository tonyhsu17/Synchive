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
    private TabbedContainerPaneView tab;
    private int index;
    private Color defaultColor;
    private Color highlightedColor;
    private boolean highlighted;
    private int delay; // delay between switching color (in miliseconds)
    private Timer timer;
    
    public BlinkTab(TabbedContainerPaneView containerView, int index, Color highlightedColor)
    {
        this(containerView, index, highlightedColor, 500);
    }
    
    public BlinkTab(TabbedContainerPaneView containerView, int index, Color highlightedColor, int delayInMili)
    {
        tab = containerView;
        this.index = index;
        defaultColor = containerView.getBackgroundAt(index);
        this.highlightedColor = highlightedColor;
        highlighted = false;
        delay = delayInMili;
        
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                blink();
            }
        });
    }
    
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
    
    public void stopBlinking()
    {
        timer.stop();
        highlighted = false;
        tab.setBackgroundAt(index, defaultColor);
    }
    
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
