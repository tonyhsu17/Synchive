# Synchive
Used as a backup tool to keep your backup directory the same as your working directory.
Any files not in your working directory but the backup directory will be moved to another folder.
In order to achieve faster performance in subsequent runs, a file will be generated that lists all
items in the backup directory. This file will be used as an alternative to reading each item in backup. 

## Features
TODO

## Additional Features To Implement
- Add checks to prevent crashes (no file found from long strings? 255, no write permission)
- Embed CRC to fileName
- Ignore List/extension exclusion
- Make a GUI
- Persistent settings
