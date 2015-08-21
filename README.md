# clj-parse-server

Since Parse SDK is open source, this project implements a simple server, you can try to build your own parse server.

## Prerequisites

1. **Install Leiningen**

    You will need [Leiningen](https://github.com/technomancy/leiningen) 2.0 or above installed.

2. **Install MongoDB server**

    After MongoDB server installed, modify profile.clj, ensure :database-url can connect to your server.

## Running (server side)

    To start this server for the application, run:

        lein run

## Running client

1. **Clone official parse source code**

    git clone https://github.com/ParsePlatform/Parse-SDK-iOS-OSX

2.  **modify Parse/PFConstants.m**

    Set ```kPFParseServer``` to your server (for example, @"http://127.0.0.1:3000")

3. **rebuild**

        rake package:frameworks
    There are 2 archives at build/release directory, unzip one of them.

4. **Create a xcode project (iOS/OSX)**

    Folllow [Parse Quick Start][1], but replace Bolts.framework and Parse.framework for your own.

[1]: https://parse.com/apps/quickstart#parse_data/mobile/ios/native/new

5. **Add example code**

    You can add examples/UnitTest.m to your project, try and modify to see the server is OK or not.

## Support API

Currently just support simple PFObject, PFQuery, see examples/UnitTest.m for more details.

Warning: No authority for incoming HTTP connections, be aware of any malicious connections.


## How to trace this project

See ```src/clj_parse_server/routes/api.clj``` most of important code started from here.

## Contribute

1. Click 'Fork' button
2. git clone git@github.com:yourname/clj-parse-server.git
3. cd clj-parse-server
4. git remote add doremi git://github.com/doremi/clj-parse-server.git
5. git add & commit
6. git push
7. Go to your github page
8. Click 'Pull Request' button
9. Write some comment
10. Thanks for your contribute!

## License

Eclipse Public License, Version 1.0 (EPL-1.0)

[![Analytics](https://ga-beacon.appspot.com/UA-66606162-1/clj-parse-server/readme)](https://github.com/igrigorik/ga-beacon)

Copyright © 2015 doremi
