# Getting Started

Need to keep these steps for Mac:
---------------------------------------------------------------------------------------------------
cd /Users/norm/forge/tools/evernym/lib/mac/darwin
clang -shared -L. -lvcx -lsovtoken -o /Users/norm/forge/tools/evernym/lib/mac/libvcx.dylib
cp ../libvcx.dylib /Users/norm/forge/work/code/indy_cloud/java/src/main/resources/darwin/
otool -L /Users/norm/forge/work/code/indy_cloud/java/src/main/resources/darwin/libvcx.dylib

./gradlew clean build
java -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar
OR
java -jar build/libs/indy-cloud-0.0.1-SNAPSHOT.jar > indy_cloud.server.output.log 2>&1 &


docker steps for running your own pool
==========================================================================================================
ssh root@167.71.155.222
cd /root/forge/personal/code/indy-sdk
docker build --build-arg pool_ip=167.71.155.222 -f ci/indy-pool.dockerfile -t indy_pool .
docker run -itd -p 167.71.155.222:9701-9708:9701-9708 indy_pool
docker ps -a;docker images -a
docker exec -it 4f79fbe2f45e /bin/bash
docker stop 4f79fbe2f45e
docker rm $(docker ps -qa --no-trunc --filter "status=exited")
docker rmi $(docker images --filter "dangling=true" -q --no-trunc)
docker rmi $(docker images --format '{{.Repository}}:{{.Tag}}' |grep 'indy_pool')


Using docker on a mac or linux to install linux docker image and setup server
------------------------------------------------------------------------------------------------------
1) Install docker for mac -- https://docs.docker.com/v17.12/docker-for-mac/install/
2) cd java/docker
3) docker build -f indy-cloud.dockerfile -t ${USER}/indy_cloud .
4) Use one of these docker run commands:
    a) docker run -v /Users/norm/forge/work/code/indy_cloud:/root/indy_cloud -p 1044:1044 -p 9000:9000 -p 9001:9001 -it ${USER}/indy_cloud:version2 /bin/bash
    b) docker run -v /root/forge/personal/code/indy_cloud:/root/indy_cloud -p 1044:1044 -p 80:80 -p 81:81 -it ${USER}/indy_cloud /bin/bash
    c) docker run -v /Users/norm/forge/work/code/indy_cloud:/root/indy_cloud -p 1044:1044 -p 9000:9000 -p 9001:9001 -t -d ${USER}/indy_cloud
    d) docker run -v /root/forge/personal/code/indy_cloud:/root/indy_cloud -p 1044:1044 -p 80:80 -p 81:81 -t -d ${USER}/indy_cloud
5) Once inside the container then do:
    a) cd /root/indy_cloud/java
    b) ./run.sh
6) docker ps -a;docker images -a
7) docker commit ad2d2b87dc69 norm/indy_cloud:version1 -- where [CONTAINER ID] comes from 'docker ps' and [version_N] is version1, version2, ... etc.

grep command to see where problems are ocurring
=========================================================================================================
This grep command searches for two strings and then only outputs the first 100 chars of the
log line that contains either string: grep 'PoolService::create\|WalletAction:' indy-cloud.server.output.log| cut -b 1-100


Next Ones to implement
------------------------------------------------------------------------------------------------------
indy-cloud.server.output.log.keepme.1:java.lang.ClassNotFoundException: edu.self.indy.indycloud.actionhandler.DeleteConnection
indy-cloud.server.output.log.keepme.3:java.lang.ClassNotFoundException: edu.self.indy.indycloud.actionhandler.GetTokenInfo
indy-cloud.server.output.log.keepme.3:java.lang.ClassNotFoundException: edu.self.indy.indycloud.actionhandler.GetTokenInfo
indy-cloud.server.output.log.keepme.3:java.lang.ClassNotFoundException: edu.self.indy.indycloud.actionhandler.DeserializeClaimOffer

apt-cache show libvcx
apt-cache show libsovtoken
apt-cache show libnullpay

gcc -v -shared -o libindycloud.so -fPIC indycloud.c -lvcx -L/usr/lib -lsovtoken -L/usr/lib

gcc -v -shared -o /usr/lib/libindycloud.so -Wl,--no-as-needed -lvcx -L/usr/lib -lsovtoken -L/usr/lib

do this https://stackoverflow.com/questions/5425034/java-load-shared-libraries-with-dependencies

gcc -shared -o libbar2.so -fPIC bar.c -lfoo -L$(pwd)
gcc -L/home/username/foo -Wl,-rpath=/home/username/foo -Wall -o test main.c -lfoo

readelf -d /usr/lib/libindycloud.so

docker stop indy_cloud

docker rm $(docker ps -qa --no-trunc --filter "status=exited")
docker rmi $(docker images --filter "dangling=true" -q --no-trunc)
docker rmi $(docker images --format '{{.Repository}}:{{.Tag}}' |grep 'indy_cloud')




### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/gradle-plugin/reference/html/)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#production-ready)
* [Jersey](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#boot-features-jersey)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

