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
- Disable "Run" until completion

## Additional Features To Implement
In no particular order:
- Include verbosity level for status reports
- What do to after completion (also add 4th option that closes program)
- Add folder selection for locations ("..." button)
- Add tooltips
- Add command line ability (-nogui ...)


Phase 2
- Add read from ID file for source (faster process aka diff changes)
- Make folder monitoring program to create ID File