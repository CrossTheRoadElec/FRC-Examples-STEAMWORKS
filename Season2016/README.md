These are projects to enable using the CTRE Pigeon IMU with the 2016 Control System.

There are 2 ways to update the Web-Interface from 2016 to display Pigeon information.

Option A: Replace the File used in the Update Utility
	1. Install the CTRE Toolsuite. (Available here: http://www.ctr-electronics.com/control-system/hro.html#product_tabs_technical_resources)
	2. Copy libfrccanfirmwareupdate.so from this repository to C:\Users\Public\Documents\Cross The Road Electronics\LifeBoat\rio-files.
		NOTE: This will overwrite the plug-in that comes with the installer.  It is recommended to save a copy of the file first.
	3. Run the roboRIO upgrade utility from the CTRE Toolsuite LifeBoat Imager.

Option B: Manually Install the Plug-in
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