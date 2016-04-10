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

## Additional Features To Implement
In no particular order:
- Rename file to include CRC (include drag n drop support for extension type)
- Include verbosity level for status reports
- Skip Folder Name (include drag n drop support)
- Skip Extension Type (include drag n drop support)
- What do to after completion (also add 4th option that closes program)
- Include progress stats (running time, etc)
- Add folder selection for locations
- Add UI indication if something goes wrong