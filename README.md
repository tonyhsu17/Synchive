# NuttySync
Used as a backup tool, NuttySync will sync one directory (source or master) and sub folders to
another directory (destination or backup). Any files not in destination but in source will be moved to a dump
folder. In order to achieve faster performance in subsequent runs, a file will be generated that lists all
items in the directory. This file will be used as an alternative to reading in each item in destination. 

## Features
TODO

## Additional Features To Implement
- Add checks to prevent crashes (no file found from long strings? 255, no write permission)
- Add Print List of sub directories
- Embed CRC to fileName
- Ignore List/extension exclusion
- Make a GUI

GUI TODOs
- Sync Dir: X to X
- Exclude Folders: X
- Exclude extensions
- Print sub directories
- Flags: Audit, CRC Tag, CRC Check, shutdown, standby, rediscover (generate src file)