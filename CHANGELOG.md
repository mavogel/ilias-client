Release Notes
=============

**Version 1.2.0 (10-July-2017)**

Refactoring:
* Wrapper for the SOAP interface to make it more generic and the app won't break 
if there will be a REST interface in the future
* More detailed log message on logout.

Tests:
* Integration tests and add coverage report.

Bugfixes:
* Error log for wrong login.client clarified
* NullPointer fix when endpoint was not created

**Version 1.1.6 (21-June-2017)**

Bugfixes:
* Fixed default template usage from classpath.

Docs:
* Made README easier to understand.
* Update on ROADMAP.

**Version 1.1.5 (20-Mar-2017)**

Features: 
* Change max members allowed in a group.

Bugfixes:
* Fixed whitespace handling in user input.
 
**Version 1.1.4 (23-Nov-2016)**

Bugfixes:
* Fixed releasing to maven central due to missing project description. 

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
* Grant file upload permission to group members
* Write the users of groups in a compileable latex and/or html file 

Bugfixes:
* Fixed confirm bug. Negative confirmation is now considered in choice. 

Improvements:
* Added a connector for testing purposes to print the returning XML strings from the ws endpoint


**Version 1.0.0 (15-Sept-2016)**

Features:
* Remove all user from all or only some groups in a course
* Remove all uploaded material from all or only some groups in a course
* Set a new registration period in all or only some groups in a course