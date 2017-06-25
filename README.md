[![MIT license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](http://opensource.org/licenses/MIT)
[![Travis Build Status](https://travis-ci.org/mavogel/ilias-client.svg?branch=master)](https://travis-ci.org/mavogel/ilias-client)
[![Code Coverage](https://img.shields.io/codecov/c/github/mavogel/ilias-client/master.svg)](https://codecov.io/github/mavogel/ilias-client?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mavogel/ilias-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mavogel/ilias-client)
<!--[![GitHub version](https://badge.fury.io/gh/mavogel%2Filias-client.png)](https://github.com/mavogel/ilias-client/releases)-->

# Ilias-client: A cmd client for the [ilias](http://ilias.de/) e-Learning platform
A command line interface client for accessing (a small part of at the moment of) the [SOAP](https://en.wikipedia.org/wiki/SOAP) interface of the Ilias e-Learning platform.
The purpose was to automate recurring tasks, which can actually only be done by multiple clicks in the GUI. This takes a lot of time and lots of clicks.

## Table of Contents
- [Features](#features)
- [Usage](#usage)
    - [Quick Start](#quick-start)
    - [Built it on your own](#built-it-on-your-own)
- [Important Notes](#important-notes)
- [License](#license)

## <a name="features"></a>Features
1. **Remove all users** from all or only some groups in one or more courses.
2. **Remove all uploaded materials** from all or only some groups in one or more courses.
3. **Set or update a registration period** in all or only some groups in one or more courses.
3. **Set or update a maximum amount of members** in all or only some groups in one or more courses.
4. **File upload permission** can be set for the group members role.
5. **Groups with its members can be** passed to Velocity template and **printed**. Two example templates are provided which are located in `src/main/resources/templates`. 

## <a name="usage"></a>Usage
You can download the built artifact from Sonatype which is preferred or built it on your own. [jdk8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) is required in both cases.

### <a name="quick-start"></a>Quick start 

1. Download the latest [RELEASE](https://oss.sonatype.org/content/groups/staging/com/github/mavogel/ilias-client/)
2. Create a `config.properties` file with the following content and fill in your data:
```properties
# The endpoint of the Ilias Webservice Port
# Usually: https://ilias.mycompany.com/webservice/soap/server.php
endpoint=
#
# STD (for standard login) or LDAP
login.mode=LDAP
#
# Client id for login. Ask your administrator for the client_id
login.client=
#
# Name of the user you want to login
login.username=
#
# If not given or empty it will be prompted on cmd line
login.password=
#
# The maximum depth to search in the tree starting from the root node
# E.g. for listing all groups in courses. Groups can be in folders.
# If not given or empty, a default value of 5 is used
maxFolderDepth=5
#
# The logging level of the application. Default is INFO if not given or empty
# Choose between: OFF, FATAL, ERROR, WARN, INFO, DEBUG, ALL
log.level=INFO
```
3. Start it
```bash
$ java -jar ilias-client-x.x.x.jar config.properties
```

### <a name="built-it-on-your-own"></a>Built it on your own
Additionally requires [maven](https://maven.apache.org/).

```bash
$ git clone https://github.com/mavogel/ilias-client.git && cd ilias-client 
$ mvn clean package
$ cp config.properties.template config.properties
$ # adapt your settings 
$ vi config.properties
$ java -jar ilias-client-x.x.x.jar config.properties
```

## <a name="important-notes"></a>Important notes
- The WDSL of the SOAP interface, which is used to generate the Java classes, is part of the jar bundle. Hence even if you change the endpoint property on the provided file for execution, the WDSL behind this url will never be used in the tool. Hopefully all new versions of the Ilias are downgradable.
- The WDSL used in this client is of the **Ilias version 5.0.0**
- For setting a new registration date it is assumed that the Ilias server runs in the same timezone like the machine this client is running.

## <a name="license"></a>License
    Copyright (c) 2017 Manuel Vogel
    Source code is open source and released under the MIT license.