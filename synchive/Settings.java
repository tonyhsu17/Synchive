package synchive;

import gui.tabbedPanels.FlagPanel;

public class Settings
{
    private static Settings self = new Settings();
    
    //flags panel
    private String sourcePath;
    private String destinationPath;
    private boolean auditTrailFlag;
    private boolean crcCheckFlag;
    private String skipFoldersName;
    private String skipExtensionTypesText;
    private FlagPanel.CompletionOptions completionFlag;
    
    //crc options panel
    private String crcDelimiterText;
    private boolean scanWithoutDelimFlag;
    private boolean crcInFilenameFlag;
    private String addCrcToExtensionTypeText;
    private String crcDelimLeadingText;
    private String crcDelimTrailingText;
    
    private Settings()
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
    
    public static Settings getInstance()
    {
        return self;
    }

    //flags panel
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

    public FlagPanel.CompletionOptions getCompletionFlag()
    {
        return completionFlag;
    }

    public void setCompletionFlag(FlagPanel.CompletionOptions completionFlag)
    {
        this.completionFlag = completionFlag;
    }

    //crc options panel
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
