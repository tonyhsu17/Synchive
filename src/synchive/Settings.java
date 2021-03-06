package synchive;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import gui.tabbedPanels.FlagPanel;
import gui.tabbedPanels.FlagPanel.CompletionOptions;
import synchive.EventCenter.Events;


/** 
 * Singleton class for a centralized location to store settings.
 * 
 * @author Tony Hsu
 */
public class Settings
{
    /**
     * Singleton Initialization
     */
    private static Settings self = new Settings();

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

    /** 
     * Private constructor to prevent instantiating multiple instances.
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

    /** 
     * @return Singleton of Settings
     */
    public static Settings getInstance()
    {
        return self;
    }

    /** 
     * Load settings
     */
    private void loadSettings()
    {
        Scanner sc;
        try
        {
            sc = new Scanner(settingsFile);
            while(sc.hasNextLine())
            {
                String line = sc.nextLine(); // Grab each setting line
                String[] splitLine = line.split("=", 2); // parse setting to [key, value]
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
                        skipFoldersName = normalizedSeperatorString(value, "", false);
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
                            case 1: // close program
                                completionFlag = CompletionOptions.close;
                                break;
                            case 2: // standby
                                completionFlag = CompletionOptions.standBy;
                                break;
                            case 3: // shutdown
                                completionFlag = CompletionOptions.shutdown;
                                break;
                        }
                        break;
                    case crcDelimiterTextKey:
                        crcDelimiterText = normalizedSeperatorString(value, "", false);
                        break;
                    case scanWithoutDelimFlagKey:
                        scanWithoutDelimFlag = Boolean.valueOf(value);
                        break;
                    case crcInFilenameFlagKey:
                        crcInFilenameFlag = Boolean.valueOf(value);
                        break;
                    case addCrcToExtensionTypeTextKey:
                        addCrcToExtensionTypeText = normalizedSeperatorString(value, ".", true);
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
//        postEvent(Events.Status, "Settings Loaded"); // call never posted to anything from timing
    }

    /** 
     * Save Settings
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
                case close:
                    output.write(completionFlagKey + "=" + 1);
                    break;
                case standBy:
                    output.write(completionFlagKey + "=" + 2);
                    break;
                case shutdown:
                    output.write(completionFlagKey + "=" + 3);
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

    /** 
     * Default Settings 
     */
    public void resetToDefaults()
    {
        sourcePath = "";
        destinationPath = "";
        auditTrailFlag = false;
        crcCheckFlag = false;
        skipFoldersName = "";
        skipExtensionTypesText = "";
        completionFlag = FlagPanel.CompletionOptions.doNothing;

        crcDelimiterText = "[], {}, (), __,";
        scanWithoutDelimFlag = false;
        crcInFilenameFlag = false;
        addCrcToExtensionTypeText = "";
        crcDelimLeadingText = "[";
        crcDelimTrailingText = "]";
    }
    
    /**
     * Add prefixes to a given list of words (comma separated).
     * 
     * @param str Words separated by comma
     * @param prefix Prefix to append to each word
     * @param toLowercase Should lowercase string?
     * @return String with each word including prefix and comma space separated
     */
    private String normalizedSeperatorString(String str, String prefix, boolean toLowercase)
    {
        String[] list = normalizedSeperatorList(str, prefix, toLowercase);
        String normalizedString = "";
        for(String s : list)
        {
            normalizedString += s + ", ";
        }
        return normalizedString;
    }
    
    /**
     * Add prefixes to a given list of words (comma separated).
     * 
     * @param str Words separated by comma
     * @param prefix Prefix to append to each word
     * @param toLowercase Should lowercase string?
     * @return List of each word including prefix
     */
    private String[] normalizedSeperatorList(String str, String prefix, boolean toLowercase)
    {
        String[] splitStr = str.split(",");
        Set<String> values = new HashSet<String>();
        for(String s : splitStr)
        {
            String trimmed = toLowercase ? s.trim().toLowerCase() : s.trim();
            if(trimmed.length() > 0)
            {
                if(!trimmed.startsWith(prefix))
                {
                    trimmed = prefix + trimmed; //normalize all extension types with prefix
                }
                values.add(trimmed);
            }
        }
        return (String[])values.toArray(new String[0]);
    }
    
    /**
     * Short handed method to post event
     * 
     * @param e Events
     * @param obj Any data
     */
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
        return normalizedSeperatorString(skipExtensionTypesText, "", false);
    }
    
    public String[] getSkipExtensionTypes()
    {
        return normalizedSeperatorList(skipExtensionTypesText, "", false);
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
    
    public String[] getAddCRCToExtensionTypes()
    {
        return normalizedSeperatorList(addCrcToExtensionTypeText, ".", true);
    }

    public void setAddCrcToExtensionTypeText(String addCrcToExtensionTypeText)
    {
        this.addCrcToExtensionTypeText = addCrcToExtensionTypeText;
    }
    
    /**
     * @param extension Include the '.'
     * @return
     */
    public boolean isExtensionInAddCrcToExtension(String extension)
    {
        for(String str : getAddCRCToExtensionTypes())
        {
            if(extension.toLowerCase().equals(str))
            {
                return true;
            }
        }
        return false;
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
