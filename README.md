# Synchive
Used as a backup tool to keep your backup directory the same as your working directory.
Any files not in your working directory but the backup directory will be moved to another folder.
In order to achieve faster performance in subsequent runs, a file will be generated that lists all
items in the backup directory. This file will be used as an alternative to reading each item in backup. 

## Features
- Check CRC before copying
- Print Status Report
- Drag n Drop support to folder location
- Persistent Settings
- Rename file to include CRC (drag n drop supported for extension type)
- Skip specific folders (drag n drop supported)
- Skip extension type (drag n drop support)
- UI indication if something goes wrong
- Include current progress
- Disabled "Run" until completion
- Completion Options

## Additional Features To Implement
In no particular order:

- Add command line ability (-nogui ...)

"Release" Build
- Add read from ID file for source
- Add Changelog
- Add folder selection for locations ("..." button)
- Include verbosity level for status reports
- Add tooltips
- Add java docs and comments for everything
- Add test cases 




