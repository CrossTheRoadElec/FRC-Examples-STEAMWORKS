These are projects to enable using the CTRE Pigeon IMU with the 2016 Control System.

To enable Web-Interface features for Pigeon, you must manually install the plug-in.
	1. Establish an FTP connection to the roboRIO.
		 - The roboRIO address is 172.22.11.2
		 - The username is admin
		 - There is no password (leave blank)
	2. The plug-in file is located in /usr/local/frc/lib.
		 - Backup the existing plug-in file (libfrccanfirmwareupdate.so) by renaming it.
			We recommend renaming the extension to .so.orig
		 - Copy the new file *.so file from this github repository into the ftp directory.