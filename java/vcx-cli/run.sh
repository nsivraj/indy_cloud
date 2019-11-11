#!/bin/sh
#kill -9 `cat vcx-cli.pid`
#> vcx-cli.pid
./gradlew clean build -x test
RUST_LOG=trace RUST_BACKTRACE=1 java -jar build/libs/vcx-cli-0.0.1-SNAPSHOT.jar
#echo $! > vcx-cli.pid