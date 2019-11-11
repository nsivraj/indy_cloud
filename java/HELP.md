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


Using docker on a mac to install linux docker image and setup server
------------------------------------------------------------------------------------------------------
1) Install docker for mac -- https://docs.docker.com/v17.12/docker-for-mac/install/
2) cd java/docker
3) docker build -f indy-cloud.dockerfile -t ${USER}/indy_cloud .
4) docker run -v /Users/norm/forge/work/code/indy_cloud:/root/indy_cloud -p 9000:9000 -p 9001:9001 -it ${USER}/indy_cloud:version3 /bin/bash OR docker run -v /root/forge/personal/code/indy_cloud:/root/indy_cloud -p 80:80 -p 81:81 -it ${USER}/indy_cloud /bin/bash
5) Once inside the container then do:
    a) cd /root/indy_cloud/java
    b) ./run.sh
6) docker ps
7) docker commit [CONTAINER ID] norm/indy_cloud:[version_N] -- where [CONTAINER ID] comes from 'docker ps' and [version_N] is version1, version2, ... etc.


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

