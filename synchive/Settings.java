package synchive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import gui.tabbedPanels.FlagPanel;
import gui.tabbedPanels.FlagPanel.CompletionOptions;
import synchive.EventCenter.Events;


/** Centralized location for settings and allowing settings to be saved and loaded.
 * @author Tony Hsu
 * @component Model
 */
public class Settings
{
    private static Settings self = new Settings(); // Lazy init for singleton

    // flags panel
    private String sourcePath;
    private String destinationPath;
    private boolean auditTrailFlag;
    private boolean crcCheckFlag;
    private String skipFoldersName;
    private String skipExtensionTypesText;
    private CompletionOptions completionFlag;

    // crc options panel
    private String crcDelimiterText;
    private boolean scanWithoutDelimFlag;
    private boolean crcInFilenameFlag;
    private String addCrcToExtensionTypeText;
    private String crcDelimLeadingText;
    private String crcDelimTrailingText;

    private final String name = "~synchiveSettings.txt"; //TODO store in /Synchive/settings.ini
    private File settingsFile;
    
    // Key for writing
    private final String sourcePathKey = "sourcePath";
    private final String destinationPathKey = "destinationPath";
    private final String auditTrailFlagKey = "auditTrailFlag";
    private final String crcCheckFlagKey = "crcCheckFlag";
    private final String skipFoldersNameKey = "skipFoldersName";
    private final String skipExtensionTypesTextKey = "skipExtensionTypesText";
    private final String completionFlagKey = "completionFlag";
    private final String crcDelimiterTextKey = "crcDelimiterText";
    private final String scanWithoutDelimFlagKey = "scanWithoutDelimFlag";
    private final String crcInFilenameFlagKey = "crcInFilenameFlag";
    private final String addCrcToExtensionTypeTextKey = "addCrcToExtensionTypeText";
    private final String crcDelimLeadingTextKey = "crcDelimLeadingText";
    private final String crcDelimTrailingTextKey = "crcDelimTrailingText";

    /** Private constructor to prevent instantiating multiple instances.
     *  Use getInstance() to get singleton.
     */
    private Settings()
    {
        settingsFile = new File(name);
        if(settingsFile.exists())
        {
            resetToDefaults();
            loadSettings();
        }
        else
        {
            resetToDefaults();
        }
    }

    /** Get an instance of Settings.
     * @return Settings singleton
     */
    public static Settings getInstance()
    {
        return self;
    }

    /** Load settings
     */
    private void loadSettings()
    {
        Scanner sc;
        try
        {
            sc = new Scanner(settingsFile);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine();
                String[] splitLine = line.split("=", 2);
                String value = "";
                for(int i = 1; i < splitLine.length; i++)
                {
                    value += splitLine[i];
                }
                switch (splitLine[0])
                {
                    case sourcePathKey:
                        sourcePath = value;
                        break;
                    case destinationPathKey:
                        destinationPath = value;
                        break;
                    case auditTrailFlagKey:
                        auditTrailFlag = Boolean.valueOf(value);
                        break;
                    case crcCheckFlagKey:
                        crcCheckFlag = Boolean.valueOf(value);
                        break;
                    case skipFoldersNameKey:
                        skipFoldersName = value;
                        break;
                    case skipExtensionTypesTextKey:
                        skipExtensionTypesText = value;
                        break;
                    case completionFlagKey:
                        switch (Integer.valueOf(value))
                        {
                            case 0: // doNothing
                                completionFlag = CompletionOptions.doNothing;
                                break;
                            case 1: // standby
                                completionFlag = CompletionOptions.standBy;
                                break;
                            case 2: // shutdown
                                completionFlag = CompletionOptions.shutdown;
                                break;
                        }
                        break;
                    case crcDelimiterTextKey:
                        crcDelimiterText = value;
                        break;
                    case scanWithoutDelimFlagKey:
                        scanWithoutDelimFlag = Boolean.valueOf(value);
                        break;
                    case crcInFilenameFlagKey:
                        crcInFilenameFlag = Boolean.valueOf(value);
                        break;
                    case addCrcToExtensionTypeTextKey:
                        addCrcToExtensionTypeText = value;
                        break;
                    case crcDelimLeadingTextKey:
                        crcDelimLeadingText = value;
                        break;
                    case crcDelimTrailingTextKey:
                        crcDelimTrailingText = value;
                        break;
                }

            }
        }
        catch (FileNotFoundException e)
        {
            // Should not come here since file is confirmed first
        }
        postEvent(Events.Status, "Settings Loaded");
    }

    /** Save Settings
     */
    public void saveSettings()
    {
        try
        {
            BufferedWriter output = new BufferedWriter(new FileWriter(settingsFile));
            output.write(sourcePathKey + "=" + sourcePath);
            output.newLine();
            output.write(destinationPathKey + "=" + destinationPath);
            output.newLine();
            output.write(auditTrailFlagKey + "=" + auditTrailFlag);
            output.newLine();
            output.write(crcCheckFlagKey + "=" + crcCheckFlag);
            output.newLine();
            output.write(skipFoldersNameKey + "=" + skipFoldersName);
            output.newLine();
            output.write(skipExtensionTypesTextKey + "=" + skipExtensionTypesText);
            output.newLine();
            switch (completionFlag)
            {
                case doNothing:
                    output.write(completionFlagKey + "=" + 0);
                    break;
                case standBy:
                    output.write(completionFlagKey + "=" + 1);
                    break;
                case shutdown:
                    output.write(completionFlagKey + "=" + 2);
                    break;
            }
            output.newLine();

            output.write(crcDelimiterTextKey + "=" + crcDelimiterText);
            output.newLine();
            output.write(scanWithoutDelimFlagKey + "=" + scanWithoutDelimFlag);
            output.newLine();
            output.write(crcInFilenameFlagKey + "=" + crcInFilenameFlag);
            output.newLine();
            output.write(addCrcToExtensionTypeTextKey + "=" + addCrcToExtensionTypeText);
            output.newLine();
            output.write(crcDelimLeadingTextKey + "=" + crcDelimLeadingText);
            output.newLine();
            output.write(crcDelimTrailingTextKey + "=" + crcDelimTrailingText);
            output.newLine();

            output.close();
        }
        catch (IOException e)
        {
            postEvent(Events.ErrorOccurred, "Unable to save settings");
        }
        postEvent(Events.Status, "Saving Settings");
    }

    /** Default Settings 
     */
    private void resetToDefaults()
    {
        sourcePath = "";
        destinationPath = "";
        auditTrailFlag = false;
        crcCheckFlag = false;
        skipFoldersName = "";
        skipExtensionTypesText = "";
        completionFlag = FlagPanel.CompletionOptions.doNothing;

        crcDelimiterText = "[], {}, (), __";
        scanWithoutDelimFlag = false;
        crcInFilenameFlag = false;
        addCrcToExtensionTypeText = "";
        crcDelimLeadingText = "[";
        crcDelimTrailingText = "]";
    }
    
    private void postEvent(Events e, String str)
    {
        EventCenter.getInstance().postEvent(e, str);
    }

    // ~~~~~ Getters & Setters ~~~~~ //
    // flags panel //
    public String getSourcePath()
    {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath)
    {
        this.sourcePath = sourcePath;
    }

    public String getDestinationPath()
    {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath)
    {
        this.destinationPath = destinationPath;
    }

    public boolean getAuditTrailFlag()
    {
        return auditTrailFlag;
    }

    public void setAuditTrailFlag(boolean auditTrailFlag)
    {
        this.auditTrailFlag = auditTrailFlag;
    }

    public boolean getCrcCheckFlag()
    {
        return crcCheckFlag;
    }

    public void setCrcCheckFlag(boolean crcCheckFlag)
    {
        this.crcCheckFlag = crcCheckFlag;
    }

    public String getSkipFoldersName()
    {
        return skipFoldersName;
    }

    public void setSkipFoldersName(String skipFoldersName)
    {
        this.skipFoldersName = skipFoldersName;
    }

    public String getSkipExtensionTypesText()
    {
        return skipExtensionTypesText;
    }

    public void setSkipExtensionTypesText(String skipExtensionTypesText)
    {
        this.skipExtensionTypesText = skipExtensionTypesText;
    }

    public CompletionOptions getCompletionFlag()
    {
        return completionFlag;
    }

    public void setCompletionFlag(CompletionOptions completionFlag)
    {
        this.completionFlag = completionFlag;
    }

    // crc options panel //
    public String getCrcDelimiterText()
    {
        return crcDelimiterText;
    }

    public void setCrcDelimiterText(String crcDelimiterText)
    {
        this.crcDelimiterText = crcDelimiterText;
    }

    public boolean getScanWithoutDelimFlag()
    {
        return scanWithoutDelimFlag;
    }

    public void setScanWithoutDelimFlag(boolean scanWithoutDelimFlag)
    {
        this.scanWithoutDelimFlag = scanWithoutDelimFlag;
    }

    public boolean getCrcInFilenameFlag()
    {
        return crcInFilenameFlag;
    }

    public void setCrcInFilenameFlag(boolean crcInFilenameFlag)
    {
        this.crcInFilenameFlag = crcInFilenameFlag;
    }

    public String getAddCrcToExtensionTypeText()
    {
        return addCrcToExtensionTypeText;
    }

    public void setAddCrcToExtensionTypeText(String addCrcToExtensionTypeText)
    {
        this.addCrcToExtensionTypeText = addCrcToExtensionTypeText;
    }

    public String getCrcDelimLeadingText()
    {
        return crcDelimLeadingText;
    }

    public void setCrcDelimLeadingText(String crcDelimLeadingText)
    {
        this.crcDelimLeadingText = crcDelimLeadingText;
    }

    public String getCrcDelimTrailingText()
    {
        return crcDelimTrailingText;
    }

    public void setCrcDelimTrailingText(String crcDelimTrailingText)
    {
        this.crcDelimTrailingText = crcDelimTrailingText;
    }
}
