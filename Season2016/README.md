These are projects to enable using the CTRE Pigeon IMU with the 2016 Control System.

To enable Web-Interface features for Pigeon, you must manually install the plug-in.
	1. Establish an FTP connection to the roboRIO.
		 - The roboRIO address is 172.22.11.2
		 - The username is admin
		 - There is no password (leave blank)
	2. Update the plug-in file.  It is located in /usr/local/frc/lib.
		 - Backup the existing plug-in file (libfrccanfirmwareupdate.so) by renaming it.
			We recommend renaming the extension to .so.orig
		 - Copy the new *.so file from this github repository into the ftp directory.
	3. Restart the webservice.  SSH into the roboRIO (same settings as FTP) and run:
		 - /etc/init.d/systemWebServer restart
		 
	The Webdash Interface should now display Pigeons that are connected to the roboRIO via CAN.