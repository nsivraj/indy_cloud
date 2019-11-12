#!/bin/sh
#kill -9 `cat indy-cloud.pid`
#> indy-cloud.pid
./gradlew clean build -x test
# ./run-demo.sh WriteDIDAndQueryVerkey
RUST_LOG=trace RUST_BACKTRACE=1 java -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar
#echo $! > indy-cloud.pid