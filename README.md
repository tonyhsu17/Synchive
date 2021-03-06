[![Build Status](https://travis-ci.org/tonyhsu17/Synchive.png?branch=master)](https://travis-ci.org/tonyhsu17/Synchive)

# Synchive
Used as a backup tool to keep your backup directory the same as your working directory.
Any files not in your working directory but the backup directory will be moved to another directory.
In order to achieve faster performance in subsequent runs, a file will be generated that lists all
items in the backup directory. This file will be used as an alternative to reading each item in backup. 
Use [SynchiveMonitor](https://github.com/tonyhsu17/SynchiveMonitor) to persistently keep source directory id file updated in order to skip reading each file in source when backing up.  

## Features
- Check CRC32 before copying
- Print Status Report
- Drag n Drop support to folder location
- Persistent Settings
- Rename file to include CRC (drag n drop supported for extension type)
- Skip specific folders (drag n drop supported)
- Skip extension type (drag n drop supported)
- UI indication if something goes wrong
- Include current progress
- Completion Options
- Read from ID file for source and destination


## Images
<a href="http://imgur.com/OvYHB7q"><img src="http://i.imgur.com/OvYHB7q.jpg" title="Flag Panel" /></a>
<a href="http://imgur.com/v1S17Hu"><img src="http://i.imgur.com/v1S17Hu.jpg" title="CRC Options" /></a>



