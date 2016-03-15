import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import support.FileDrop;
public class SummaryView
{
    public interface SummaryViewDelegate {
        public void setTest(int i);
        
//        public static void testMethod() {
//            System.out.println("");
//        }
    }
    
    private SummaryController vc;
    private SummaryViewDelegate delegate;
    private JFrame nuttySyncFrame;
    /**
     * Create the application.
     */
    public SummaryView(SummaryViewDelegate delegate)
    {
        this.delegate = delegate;
        initialize();
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        nuttySyncFrame = new JFrame();
        nuttySyncFrame.setTitle("NuttySync");
        nuttySyncFrame.setBounds(100, 100, 526, 320);
        nuttySyncFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel sourceLabel = new JLabel("Source:");
        sourceLabel.setPreferredSize(new Dimension(57, 14));
        sourceLabel.setMinimumSize(new Dimension(57, 14));
        sourceLabel.setMaximumSize(new Dimension(57, 14));
        sourceLabel.setBounds(7, 7, 67, 22);
        sourceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sourceLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        JTextField sourceTextField = new JTextField();
        sourceTextField.setBounds(93, 7, 352, 22);
        sourceTextField.setMinimumSize(new Dimension(50, 20));
        sourceTextField.setToolTipText("");
        sourceTextField.setColumns(10);
        
        JButton sourceMoreButton = new JButton("...");
        sourceMoreButton.setBounds(463, 7, 40, 22);
        sourceMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sourceMoreButton.setMinimumSize(new Dimension(23, 23));
        sourceMoreButton.setMaximumSize(new Dimension(45, 22));
        sourceMoreButton.setDefaultCapable(false);
        sourceMoreButton.setFocusPainted(false);
        
        new FileDrop(sourceTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                System.out.println(files[0].getPath());
                sourceTextField.setText(files[0].getPath());
                delegate.setTest(1);
                
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setBounds(7, 33, 67, 22);
        destinationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        destinationLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        JTextField destinationTextField = new JTextField();
        destinationTextField.setBounds(93, 33, 352, 22);
        destinationTextField.setMinimumSize(new Dimension(50, 20));
        destinationTextField.setColumns(10);
        
        new FileDrop(destinationTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                System.out.println(files[0].getPath());
                destinationTextField.setText(files[0].getPath());
                
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JButton destinationMoreButton = new JButton("...");
        destinationMoreButton.setBounds(463, 33, 40, 22);
        destinationMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        destinationMoreButton.setMinimumSize(new Dimension(23, 23));
        destinationMoreButton.setMaximumSize(new Dimension(45, 22));
        destinationMoreButton.setDefaultCapable(false);
        destinationMoreButton.setFocusTraversalKeysEnabled(false);
        destinationMoreButton.setFocusPainted(false);
        
        JLabel sizeReadLabel = new JLabel("Read: ");
        sizeReadLabel.setBounds(7, 238, 99, 14);
        nuttySyncFrame.getContentPane().add(sizeReadLabel);
        
        JLabel sizeReadSpeedLabel = new JLabel("Read Speed:");
        sizeReadSpeedLabel.setBounds(116, 238, 154, 14);
        nuttySyncFrame.getContentPane().add(sizeReadSpeedLabel);
        
        JLabel totalRunningTimeLabel = new JLabel("Running Time:");
        totalRunningTimeLabel.setBounds(280, 238, 223, 14);
        nuttySyncFrame.getContentPane().add(totalRunningTimeLabel);
        
        JProgressBar processingFileProgressBar = new JProgressBar();
        processingFileProgressBar.setBounds(7, 256, 496, 14);
        nuttySyncFrame.getContentPane().add(processingFileProgressBar);
        
        nuttySyncFrame.getContentPane().setLayout(null);
        nuttySyncFrame.getContentPane().add(sourceLabel);
        nuttySyncFrame.getContentPane().add(sourceTextField);
        nuttySyncFrame.getContentPane().add(sourceMoreButton);
        nuttySyncFrame.getContentPane().add(destinationLabel);
        nuttySyncFrame.getContentPane().add(destinationTextField);
        nuttySyncFrame.getContentPane().add(destinationMoreButton);
    }
    
    public JFrame getFrame()
    {
        return nuttySyncFrame;
    }
    
    public void setVisible(Boolean flag) {
        nuttySyncFrame.setVisible(flag);
    }
}
