FROM ubuntu:18.04

ENV NODE_ENV='development'
ENV RUST_BACKTRACE=1
ENV RUST_LOG=trace

ARG APT="/usr/bin/apt-get --no-install-recommends -o Dpkg::Options::=--force-confold -o Dpkg::Options::=--force-confdef"
ARG PACKAGES="software-properties-common build-essential sudo less screen tmux vim ca-certificates git curl iproute2 less gnupg2 openjdk-8-jdk python wget unzip"
#ENV DEBIAN_FRONTEND=noninteractive

RUN $APT update && \
    $APT upgrade -y && \
    $APT install -y $PACKAGES && \
    rm -rf /var/lib/apt/lists/*

#Add Sovrin SDK repo
RUN echo 'deb https://repo.sovrin.org/sdk/deb xenial stable' > /etc/apt/sources.list.d/sovrin.list && \
    cnt=0 ;\
    while ! apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 2>&1; do \
        echo "Waiting 1 second before retrying gpg key download.($cnt/10)" ;\
        sleep 1 ;\
        cnt=$((cnt + 1)); \
        if [ $cnt -ge 10 ] ; then \
            echo "Could not add gpg key. Aborting" ;\
            exit 1 ;\
        fi ;\
    done


RUN apt-get update && \
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.35.0/install.sh | bash && \
apt-get install --no-install-recommends -o Dpkg::Options::=--force-confold -o Dpkg::Options::=--force-confdef -y python3 python3-setuptools python3-pip python3-wheel && \
apt-get install -y libindy=1.10.1 libsovtoken=1.0.0 libnullpay=1.10.1 && \
dpkg -i libvcx_0.3.49011143-be58f63_amd64.deb && \
apt-get install -f && \
apt-get purge -y python3-pip && \
apt-get autoremove -y

# dpkg -i libvcx_0.2.41140129-e0d1c6e_amd64.deb
# "node-vcx-wrapper": "node-vcx-wrapper_0.2.41140129-e0d1c6e_amd64.tgz"


#apt-get install -y libindy=1.12.0 libsovtoken=1.0.3 libnullpay=1.12.0 libvcx=0.4.2 && \
#apt-get install -y libindy=1.11.1 libsovtoken=1.0.2 libnullpay=1.11.1 libvcx=0.4.1 && \

ENTRYPOINT /bin/bash
