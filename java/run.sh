#!/bin/sh
kill -9 `cat indy-cloud.pid`
> indy-cloud.pid
./gradlew clean build -x test

SERVERONE_GENESIS_PATH=/root/indy_cloud/vcx-cli/config/serverone-pool_transactions_genesis.json
if [ ! -z "$1" ]; then
    SERVERONE_GENESIS_PATH=$1
fi
echo "Using serverone genesis path: ${SERVERONE_GENESIS_PATH}"

RUST_LOG=trace RUST_BACKTRACE=1 java -Dserverone.genesis.path=${SERVERONE_GENESIS_PATH} -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar > indy-cloud.server.output.log 2>&1 &
#RUST_LOG=trace RUST_BACKTRACE=1 java -Dserverone.genesis.path=${SERVERONE_GENESIS_PATH} -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044 -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar > indy-cloud.server.output.log 2>&1 &
#RUST_LOG=trace RUST_BACKTRACE=1 java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044 -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar > indy-cloud.server.output.log 2>&1 &

echo $! > indy-cloud.pid
