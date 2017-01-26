#Running a LabVIEW Example on Your roboRIO
For any LabVIEW Example, the team number target settings and local build filepath must be manually adjusted to work on your local machine and roboRIO.


To change these settings, first begin by opening the project to the project tree.

![](https://github.com/CrossTheRoadElec/FRC-Examples/blob/master/README_Images/LV_Proj_Tree_Start.png)



Right-Click on the "Target" and click on "Properties".

![](https://github.com/CrossTheRoadElec/FRC-Examples/blob/master/README_Images/LV_Proj_Tree_Target_RClick.png)



In the General Options of the Target Properties, change the "IP Address/DNS Name" to reflect your team number.
If your team number is WXYZ, the address should be "roboRIO-WXYZ-frc.local".

Ignore leading zeros in your team number.  (Eg. if your team number is 0XYZ, the address would be "roboRIO-XYZ-frc.local")

Click "OK" to save changes.

![](https://github.com/CrossTheRoadElec/FRC-Examples/blob/master/README_Images/LV_Target_Properties.png)



Next, expand the "Build Specifications" node and right-click on "FRC Robot Boot-up Deployment".  Click on Properties.

![](https://github.com/CrossTheRoadElec/FRC-Examples/blob/master/README_Images/LV_Proj_Tree_Build_RClick.png)



In the Information Options, the "Local destination directory" must be changed to reflect the filepath on your local machine.
The portion of the filepath before "FRC-Examples" (here shown as "D:\CTR\FRC") must be changed to that of your local machine.

If you downloaded and unzipped the examples in your downloads folder, the entire path would look like:
"C:\Users\\[User]\Downloads\FRC-Examples\\[Example Name]\Builds", where [User] is your username and [Example Name] is the name of the example folder.

![](https://github.com/CrossTheRoadElec/FRC-Examples/blob/master/README_Images/LV_Build_Properties.png)


