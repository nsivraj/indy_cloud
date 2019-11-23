#!/bin/sh
kill -9 `cat indy-cloud.pid`
> indy-cloud.pid
./gradlew clean build -x test

#RUST_LOG=trace RUST_BACKTRACE=1 java -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar > indy-cloud.server.output.log 2>&1 &
RUST_LOG=trace RUST_BACKTRACE=1 java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044 -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar > indy-cloud.server.output.log 2>&1 &

echo $! > indy-cloud.pid