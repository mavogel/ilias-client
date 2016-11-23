[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](http://opensource.org/licenses/MIT)
[![Travis Build Status](https://travis-ci.org/mavogel/ilias-client.svg?branch=master)](https://travis-ci.org/mavogel/ilias-client)
<!--[![GitHub version](https://badge.fury.io/gh/mavogel%2Filias-client.png)](https://github.com/mavogel/ilias-client/releases)-->
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mavogel/ilias-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mavogel/ilias-client)

# Ilias-client: A cmd client for the [ilias](http://ilias.de/) e-Learning platform

A command line interface client for accessing (a small part of at the moment of) the [SOAP](https://en.wikipedia.org/wiki/SOAP) interface of the Ilias e-Learning platform.

- Requires [jdk8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or higher and [maven](https://maven.apache.org/).
- Build by [travis](https://travis-ci-org)

## Purpose
The purpose was to automate recurring tasks, which can actually only be done by multiple clicks in the Ilias GUI. This takes a lot of time and lots of clicks.
   
These automated tasks are described in the use cases section.

## Implemented use cases
1. Remove all user from all or only some groups in one or more courses.
2. Remove all uploaded material from all or only some groups in one or more courses.
3. Sets or update a new registration period in all or only some groups in one or more courses.
4. File upload permission can be set for the group members role.
5. Groups with its members can be passed to Velocity template and printed. Two example templates are provided which are located in `templates`. 

This is achieved by the following steps:
- Creating the Webservice endpoint
- Login as the desired user
- Select one or more courses you're **admin** of
- Fetch all groups of all courses you selected
- Perform one or more actions, which have to be confirmed before execution, on some or all selected groups

## Important notes
- The WDSL of the SOAP interface, which is used to generate the Java classes, is part of the jar bundle. Hence even if you change the endpoint property on the provided file for execution, the WDSL behind this url will never be used in the tool. Hopefully all new versions of the Ilias are downgradable.
- The WDSL used in this client is of the **Ilias version 5.0.0**
- For setting a new registration date it is assumed that the Ilias server runs in the same timezone like the machine this client is running.

## Usage
### Build it on your own
```bash
# get latest tag or use the master branch
$ mvn clean package
$ cp config.properties.template config.properties
$ # adapt your settings 
$ vi config.properties
$ java -jar ilias-client-x.x.x.jar config.properties
```

### Download the build from OSS Sonatype
Signed and build by `travis`. Download the latest SNAPSHOT or release [here](https://oss.sonatype.org/content/groups/staging/com/github/mavogel/ilias-client/).