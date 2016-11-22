Release Notes
=============

**Version 1.1.3 (22-Nov-2016)**

Improvements:
* Added travis for automated testing and deploying to maven central.

**Version 1.1.2 (16-Oct-2016)**

Bugfixes:
* Replaced platform dependent file separator in output file name creation

**Version 1.1.1 (16-Oct-2016)**

Bugfixes:
* ByteStream errors on Windows
* Fixed ignore of property files.
* Template output filename is now correct for Windows
* Template output charset is now UTF-8
* Default value for property maxFolderDepth is used if given but empty
* Standard templates are now used if none is given

Improvements:
* HTML template enhanced with bootstrap
* Added title of course to output template file

**Version 1.1.0 (22-Sept-2016)**

Features:
* Added a feature to grant file upload permission to group members
* Added a feature to write the users of groups in a compileable latex and/or html file 

Bugfixes:
* Fixed confirm bug. Negative confirmation is now considered in choice. 

Improvements:
* Added a connector for testing purposes to print the returning XML strings from the ws endpoint


**Version 1.0.0 (15-Sept-2016)**

Features:
* Added feature to remove all user from all or only some groups in a course
* Added feature to remove all uploaded material from all or only some groups in a course
* Added feature to set a new registration period in all or only some groups in a course