## Build and Run

Get node-vcx-wrapper_0.3.49012884-be58f63_amd64.tgz from https://repo.corp.evernym.com/filely/npm/node-vcx-wrapper_0.3.49012884-be58f63_amd64.tgz

Using docker on a mac to install linux docker image and setup server
------------------------------------------------------------------------------------------------------
1) Install docker for mac -- https://docs.docker.com/v17.12/docker-for-mac/install/
2) cd vcx-cli/docker
3) docker build -f vcx-cli.dockerfile -t ${USER}/vcx_cli .
4) docker run -v /Users/norm/forge/work/code/evernym/vcx-cli:/root/vcx-cli -it ${USER}/vcx_cli:version2 /bin/bash
5) Once inside the container then do:
    a) cd /root/vcx-cli
    b) rm -rf node_modules
    c) Do this if needed: nvm install --lts=Carbon
    d) npm install
6) docker ps
7) docker commit f97c852a73f6 norm/vcx_cli:version2 -- where [CONTAINER ID] comes from 'docker ps' and [version_N] is version1, version2, ... etc.

docker rm $(docker ps -qa --no-trunc --filter "status=exited")
docker rmi $(docker images --filter "dangling=true" -q --no-trunc)
docker rmi $(docker images --format '{{.Repository}}:{{.Tag}}' |grep 'vcx_cli')


// "node-vcx-wrapper": "node-vcx-wrapper_0.3.49012884-be58f63_amd64.tgz"
// "node-vcx-wrapper": "https://repo.corp.evernym.com/filely/npm/node-vcx-wrapper_0.4.53979018-bd81815_amd64.tgz"

FOR DEBUGGING ISSUES WITH THE SCRIPTS DO...
------------------------------------------------------------------------------------------------------------------------
npm run maindebug [script_name] [script_parameters]
For example: npm run maindebug testVcx demo lab
In each of the steps below just replace "main" with "maindebug"


NOW DO THESE COMMANDS INSIDE THE CONTAINER
------------------------------------------------------------------------------------------------------------------------
mv data data-backup5
rm -rf ~/.indy_client/wallet;rm -rf ~/.indy_client/pool


npm run main testVcx demo lab
npm run main makeConnection demo lab QR norman 8016159697
npm run main sendMessage demo lab norman "hi from lab"
npm run main askProvableQuestion demo lab norman "are you walking?" yes no
NOTE: Before you can successfully write a schema to the ledger you must
POST to this URL -- https://selfserve.sovrin.org/nym the JSON body
{"network":"stagingnet","did":"[DID]","verkey":"[VERKEY]","paymentaddr":""}
curl -X POST -H "Content-Type: application/json" -d '{"network":"stagingnet","did":"PeMdTwgret7gyow2gAdqqT","verkey":"DLpmBbyfe1g8F6NFitQdiSDFUah24YA4PNYFg2rgPYvi","paymentaddr":""}' https://selfserve.sovrin.org/nym
--OR--
visit the website https://selfserve.sovrin.org/ in your browser and enter the DID and verkey of
the institution found in the [institution]-[agency]-eas-init-config.json
npm run main createSchema demo lab employee
npm run main createSchema demo lab secours
npm run main createCredentialDef demo lab employee
npm run main createCredentialDef demo lab secours
npm run main offerCredential demo lab employee norman
npm run main offerCredential demo lab secours norman
npm run main requestProof demo lab employee norman
npm run main requestProof demo lab secours norman


npm run main testVcx demo doctor
npm run main makeConnection demo doctor QR norman 8016159697
npm run main sendMessage demo doctor norman "hi from doctor"
npm run main askProvableQuestion demo doctor norman "are you driving?" yes no
NOTE: Before you can successfully write a schema to the ledger you must
POST to this URL -- https://selfserve.sovrin.org/nym the JSON body
{"network":"stagingnet","did":"[DID]","verkey":"[VERKEY]","paymentaddr":""}
curl -X POST -H "Content-Type: application/json" -d '{"network":"stagingnet","did":"J3DNGdWmn1W9U1NVa3ZRL8","verkey":"AHcNLzAizikyPDuK1nYpy68eAEy7P5Kpi5exiUa3DBtb","paymentaddr":""}' https://selfserve.sovrin.org/nym
--OR--
visit the website https://selfserve.sovrin.org/ in your browser and enter the DID and verkey of
the institution found in the [institution]-[agency]-eas-init-config.json
npm run main createSchema demo doctor employee
npm run main createSchema demo doctor secours
npm run main createCredentialDef demo doctor employee
npm run main createCredentialDef demo doctor secours
npm run main offerCredential demo doctor employee norman
npm run main offerCredential demo doctor secours norman
npm run main requestProof demo doctor secours norman
npm run main requestProof demo doctor employee norman


npm run main testVcx qatest1 pharmacy
npm run main makeConnection qatest1 pharmacy QR norman 8016159697
npm run main sendMessage qatest1 pharmacy norman "hi from pharmacy"
npm run main askProvableQuestion qatest1 pharmacy norman "are you walking?" yes no
NOTE: Before you can successfully write a schema to the ledger you must
POST to this URL -- https://selfserve.sovrin.org/nym the JSON body
{"network":"stagingnet","did":"[DID]","verkey":"[VERKEY]","paymentaddr":""}
curl -X POST -H "Content-Type: application/json" -d '{"network":"stagingnet","did":"4UmKoMmn8X3TQ5KUcbQaGB","verkey":"2tyAY48CHYfMzi57P5RKDN3c4MGnTZ9xqkmLmFMWHuTS","paymentaddr":""}' https://selfserve.sovrin.org/nym
--OR--
visit the website https://selfserve.sovrin.org/ in your browser and enter the DID and verkey of
the institution found in the [institution]-[agency]-eas-init-config.json
npm run main createSchema qatest1 pharmacy prescription
npm run main createSchema qatest1 pharmacy illness
npm run main createSchema qatest1 pharmacy healthcheck
npm run main createCredentialDef qatest1 pharmacy prescription
npm run main createCredentialDef qatest1 pharmacy illness
npm run main offerCredential qatest1 pharmacy prescription norman
npm run main offerCredential qatest1 pharmacy illness norman
npm run main requestProof qatest1 pharmacy prescription norman
npm run main requestProof qatest1 pharmacy illness norman



npm run main testVcx devrc UCCU
npm run main makeConnection devrc UCCU QR norman 8016159697
npm run main sendMessage devrc UCCU norman "hi from Universal Credential Credit Union"
npm run main askProvableQuestion devrc UCCU norman "are you walking?" yes no
NOTE: Before you can successfully write a schema to the ledger you must
POST to this URL -- https://selfserve.sovrin.org/nym the JSON body
{"network":"stagingnet","did":"[DID]","verkey":"[VERKEY]","paymentaddr":""}
curl -X POST -H "Content-Type: application/json" -d '{"network":"stagingnet","did":"762P99cbsRL4iotyUJnnq8","verkey":"4KQnBXSBD1edPP4LgvqkEPSxjz4Bh948JtGEAWeW1msu","paymentaddr":""}' https://selfserve.sovrin.org/nym
--OR--
visit the website https://selfserve.sovrin.org/ in your browser and enter the DID and verkey of
the institution found in the [institution]-[agency]-eas-init-config.json
npm run main createSchema devrc UCCU deposit
npm run main createSchema devrc UCCU withdrawal
npm run main createCredentialDef devrc UCCU deposit
npm run main createCredentialDef devrc UCCU withdrawal
npm run main offerCredential devrc UCCU deposit norman
npm run main offerCredential devrc UCCU withdrawal norman
npm run main requestProof devrc UCCU deposit norman
npm run main requestProof devrc UCCU withdrawal norman
