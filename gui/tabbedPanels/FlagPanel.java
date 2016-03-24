package gui.tabbedPanels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class FlagPanel extends JPanel
{
    public enum CompletionOptions { doNothing, standBy, shutdown }
    
    public interface FlagPanelDelegate {
        public void auditTrailStateChange(JRadioButton button, int state);
        public void crcCheckStateChange(JRadioButton button, int state);
        public void afterCompletionOptionChange(JRadioButton button, CompletionOptions option);
        public void runNuttySync(JButton button);
    }
    
    JRadioButton previousOptionButton;
    FlagPanelDelegate delegate;

    public FlagPanel(FlagPanelDelegate del)
    {
        super();
        delegate = del;
        initialize();
    }
    
    private void initialize()
    {
        setLayout(null);
        
        JRadioButton auditTrailButton = new JRadioButton("Enable Audit Trail");
        auditTrailButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                delegate.auditTrailStateChange(auditTrailButton, arg0.getStateChange());
            }
        });
        
        auditTrailButton.setFocusPainted(false);
        auditTrailButton.setBounds(7, 7, 231, 23);
        add(auditTrailButton);
        
        JRadioButton crcCheckButton = new JRadioButton("Enable CRC Check");
        crcCheckButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                delegate.crcCheckStateChange(crcCheckButton, arg0.getStateChange());
            }
        });
        crcCheckButton.setFocusPainted(false);
        crcCheckButton.setBounds(7, 34, 231, 23);
        add(crcCheckButton);
        
        Box horizontalBox_1 = Box.createHorizontalBox();
        horizontalBox_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
        horizontalBox_1.setBounds(7, 64, 476, 2);
        add(horizontalBox_1);
        
        JLabel afterCompletionLabel = new JLabel("After Completion");
        afterCompletionLabel.setBounds(7, 77, 109, 14);
        add(afterCompletionLabel);
        
        // After Completion //
        JRadioButton doNothingButton = new JRadioButton("Do nothing", true);
        doNothingButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent arg0) {
                afterCompletionDidChange(arg0, CompletionOptions.doNothing);
            }
        });
        doNothingButton.setFocusPainted(false);
        doNothingButton.setBounds(7, 95, 109, 23);
        add(doNothingButton);
        previousOptionButton = doNothingButton;
        
        JRadioButton standbyButton = new JRadioButton("Standby");
        standbyButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent arg0) {
                afterCompletionDidChange(arg0, CompletionOptions.standBy);
            }
        });
        standbyButton.setFocusPainted(false);
        standbyButton.setBounds(191, 95, 109, 23);
        add(standbyButton);
        
        JRadioButton shutdownButton = new JRadioButton("Shutdown");
        shutdownButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent arg0) {
                afterCompletionDidChange(arg0, CompletionOptions.shutdown);
            }
        });
        shutdownButton.setFocusPainted(false);
        shutdownButton.setBounds(341, 95, 109, 23);
        add(shutdownButton);
        
        JButton runButton = new JButton("Run");
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delegate.runNuttySync(runButton);
            }
        });
        runButton.setFocusPainted(false);
        runButton.setBounds(244, 7, 240, 50);
        add(runButton);
    }
    
    private void afterCompletionDidChange(MouseEvent item, CompletionOptions option)
    {
        JRadioButton selectedButton = (JRadioButton)item.getSource();
        if(selectedButton == previousOptionButton)
        {
            previousOptionButton.setSelected(true);
        }
        else
        {
            previousOptionButton.setSelected(false);
            previousOptionButton = selectedButton;
            delegate.afterCompletionOptionChange(selectedButton, option);
        }
    }
}
