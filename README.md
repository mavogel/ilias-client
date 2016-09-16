[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](http://opensource.org/licenses/MIT)
<!--[![Travis Build Status](https://travis-ci.org/javafx-maven-plugin/javafx-maven-plugin.svg?branch=master)](https://travis-ci.org/javafx-maven-plugin/javafx-maven-plugin)-->
<!--[![AppVeyor Build status](https://ci.appveyor.com/api/projects/status/64700ul3m9y88agi/branch/master?svg=true)](https://ci.appveyor.com/project/FibreFoX/javafx-maven-plugin/branch/master)-->

Ilias-client: A cmd client for the [ilias](http://ilias.de/) e-Learning platform
================================================================================

A command line interface client for accessing (a small part of at the moment of) the [SOAP](https://en.wikipedia.org/wiki/SOAP) interface of the Ilias e-Learning platform.

Requires JDK 1.8 or higher and Maven.

Purpose
=======
The purpose was to automate recurring tasks, which can actually only be done by multiple clicks in the Ilias GUI. This takes a lot of time and lots of clicks.
   
These automated task are described in the use cases section.

Implemented use cases
=====================
* Remove all user from all or only some groups in one or more courses.
* Remove all uploaded material from all or only some groups in one or more courses.
* Sets or update a new registration period in all or only some groups in one or more courses.

This is achieved by the following steps:
- Creating the Webservice endpoint
- Login as the desired user
- Select one or more courses you're **admin** of
- Fetch all groups of all courses you selected
- Perform one or more actions, which have to be confirmed before execution, on some or all selected groups

Usage
=====
```bash
$ git clone https://github.com/mavogel/ilias-client.git && cd ilias-client
$ mvn clean package
$ cp config.properties.template config.properties
$ # adapt your settings 
$ vi config.properties
$ java -jar ilias-client-1.0.0.jar config.properties
```

Important notes
===============
- The WDSL of the SOAP interface, which is used to generate the Java classes, is part of the jar bundle. Hence even if you change the endpoint property on the provided file for execution, the WDSL behind this url will never be used in the tool. Hopefully all new versions of the Ilias are downgradable.
- The WDSL used in this client is of the **Ilias version 5.0.0**
- For setting a new registration date it is assumed that the Ilias server runs in the same timezone like the machine this client is running.  


Latest Release Notes
====================

**Version 1.0.0 (15-Sept-2016)**

New:
* added feature to remove all user from all or only some groups in one or more courses
* added feature to remove all uploaded material from all or only some groups in one or more courses
* added feature to set a new registration period in all or only some groups in one or more courses

Bugfixes:

Improvements: