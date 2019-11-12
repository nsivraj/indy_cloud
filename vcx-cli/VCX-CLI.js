#!/usr/bin/env node

var vcx = require('node-vcx-wrapper');
var qr = require('qr-image');
var fs = require('fs-extra');
var ffi = require('ffi');
const base64url = require('base64url')
const crypto = require('crypto');
const logger = require('logger');


//vcx imports
const {
  Schema,
  CredentialDef,
  Connection,
  IssuerCredential,
  Proof,
  StateType,
  Error,
  rustAPI
} = vcx;


function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function run(){
    const myffi = ffi.Library('/usr/lib/libsovtoken.so', {sovtoken_init: ['void', []]});
    await myffi.sovtoken_init();
    fs.ensureDir('./data')
    fs.ensureDir('./config')
}

run();

//global vars
let initJSONFile = "eas-init-config.json";
let provisionJSONFile = "eas-provision-config.json";

function initFile(agencyName, institution) {
  return `./config/${agencyName}/${institution}/${agencyName}-${institution}-${initJSONFile}`;
}

function provisionFile(agencyName, institution) {
  return `./config/${agencyName}/${institution}/${agencyName}-${institution}-${provisionJSONFile}`;
}

function institutionFile(agencyName, institution) {
  return `./config/${agencyName}/${institution}/${institution}-config.json`;
}

async function testVcx(agencyName, institution) {
  await ensureWalletExists(agencyName, institution);
}

async function acceptTAA() {
  const dateSeconds = Math.floor(Date.now() / 1000)
  const nowDate = new Date(dateSeconds * 1000)
  const nonPreciseDate = new Date(nowDate.getFullYear(), nowDate.getMonth(), nowDate.getDate())
  let taa = await vcx.getLedgerAuthorAgreement()
  console.log('TAA: ', taa)
  taa = JSON.parse(taa)
  const acceptResult = await vcx.setActiveTxnAuthorAgreementMeta(taa.text, taa.version, undefined, "wallet_agreement", Math.floor(nonPreciseDate.getTime() / 1000))
  console.log('TAA accept result: ', acceptResult)
}


async function setupLogger() {
  if(process.env.VCXCLI_DEBUG == "true") {
    const logFn = (level, target, message, modulePath, file, line) => {
      console.log(`[${target}-${level} >> ${file}:${line}] ${message}`)
    }
    vcx.setLogger(logFn)
  }
}


async function ensureWalletExists(agencyName, institution) {
  fs.ensureDir(`./data/${agencyName}/${institution}`)
  fs.ensureDir(`./config/${agencyName}/${institution}`)

  console.log("agencyName: ", agencyName);
  console.log("institution: ", institution);
  try {
      console.log("Using init config in file: ", initFile(agencyName, institution));
      await vcx.initVcx(initFile(agencyName, institution));
      setupLogger();
      await acceptTAA();
      console.log("VCX has been successfully initiated");
  } catch(err){
      if(err.vcxCode === 1079 || err.vcxCode === 1004) {
        let institutionConfig = await fs.readJson(institutionFile(agencyName, institution));
        let provisionConfig = await fs.readJson(provisionFile(agencyName, institution));
        console.log("Creating the wallet with config: ", provisionConfig);
        //vcxConfig = await provisionAgent(JSON.stringify(enterprise_config))
        let vcxConfig = await vcx.provisionAgent(JSON.stringify(provisionConfig));
        setupLogger();
        vcxConfig = JSON.parse(vcxConfig);
        vcxConfig.genesis_path = provisionConfig.genesis_path;
        vcxConfig.institution_logo_url = institutionConfig.institution_logo_url;
        vcxConfig.institution_name = institutionConfig.institution_name;
        vcxConfig.payment_method = provisionConfig.payment_method;
        console.log("New wallet with vcxConfig: ", JSON.stringify(vcxConfig));
        await fs.writeJson(initFile(agencyName, institution), vcxConfig);
        console.log("Now that the wallet exists for institution " + institution + ", please re-run the script!!!!");
        process.exit();
      } else {
        console.log("VCX has not been successfully initiated, see error below...");
        console.log(err);
      }
  }
  return("success")
}

async function makeConnection(agencyName, institution,type,name,phonenumber){
    await ensureWalletExists(agencyName, institution);
    console.log("attempting connection creation");
    let connection = await Connection.create({id:'CONNECTIONID'});
    console.log('created Connection - attempting connect');
    if(type=="QR"){
        let connectionData = {data: '{"connection_type":"QR","phone":"5555555555"}',"use_public_did":true};
        await connection.connect(connectionData);
        console.log("connection.connect()");
        let details = await connection.inviteDetails(true);
        console.log("connection details: " + details);
        let qrcode = qr.image(details, { type: 'png' });
        qrcode.pipe(fs.createWriteStream(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${name}-connection.png`));
        let state = await connection.getState();
        while(state != StateType.Accepted) {
            console.log("The State of the Connection is "+ state);
            await sleep(2000);
            await connection.updateState();
            await connection.serialize();
            state = await connection.getState();
        }
        //function complete
        let serialized_connection = await connection.serialize();
        let connection_file_path = `./data/${agencyName}/${institution}/${agencyName}-${institution}-${name}-connection.json`;
        await fs.writeJson(connection_file_path,serialized_connection);
        console.log("Success!! Connection Complete. The State of the Connection is "+ state);
    }else if(type=="SMS"){
        let connection_info={"id":name,"connection_type":type,"phone":String(phonenumber)};
        let phonestring =String(phonenumber) ;
        let smsData = {data: '{"connection_type":"SMS","phone":"7073436737"}',"use_public_did":true};
        console.log('vcx will attempt Connection through SMS');
        await connection.connect(smsData);
        let details = await connection.inviteDetails(true);
        let state = await connection.getState();
        console.log(details);
        while(state != StateType.Accepted) {
            console.log("The State of the Connection is "+ state);
            await sleep(2000);
            await connection.updateState();
            state = await connection.getState();
        }
        let serialized_connection = await connection.serialize();
        let connection_file_path = `./data/${agencyName}/${institution}/${agencyName}-${institution}-${name}-connection.json`;
        await fs.writeJson(connection_file_path,serialized_connection);
        console.log("Success!! Connection Complete. The State of the Connection is "+ state);
    }else if(type=="URL"){
        let connectionData = {data: '{"connection_type":"QR","phone":"5555555555"}',"use_public_did":true};
        await connection.connect(connectionData);
        let details = await connection.inviteDetails(true);
        let serialized_connection = await connection.serialize();
        let url = serialized_connection['data']['invite_url'];
        url = encodeURIComponent(url);
        let link = "https://connectme.app.link?t=" + url;
        console.log(`Your invite link is : ${link} You must copy this into a browser on your mobile device with Connect.Me installed.`);
        let state = await connection.getState();
        while(state != StateType.Accepted) {
            console.log("The State of the Connection is "+ state);
            await sleep(2000);
            await connection.updateState();
            state = await connection.getState();
        }
        let connection_file_path = `./data/${agencyName}/${institution}/${agencyName}-${institution}-${name}-connection.json`;
        await fs.writeJson(connection_file_path,serialized_connection);
        console.log("Success!! Connection Complete. The State of the Connection is "+ state);
    }
}

async function sendMessage(agencyName, institution, connection_name,msgText){
    await ensureWalletExists(agencyName, institution);
    const expiration = getExpirationDate({ seconds: 60 });
    let msg_data =
    {
        '@type': 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/question',
        '@id': '518be002-de8e-456e-b3d5-8fe472477a86',
        'question_text': 'Announcement',
        'question_detail': msgText,
        'valid_responses': [
          { 'text': "OK", 'nonce': 'xyz' }
        ],
        '@timing': {
          'expires_time': expiration
        }
    }
    serialized_connection = await fs.readJson(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${connection_name}-connection.json`);
    deserialized_connection = await Connection.deserialize(serialized_connection);
    console.log(serialized_connection);
    let sendMsg = await deserialized_connection.sendMessage({
      'msg':JSON.stringify(msg_data),
      'type':'Question',
      'title':msgText
    });
}

async function askProvableQuestion (agencyName, institution, connection_name, question_prompt, button1, button2) {
    await ensureWalletExists(agencyName, institution);
    serialized_connection = await fs.readJson(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${connection_name}-connection.json`);
    deserialized_connection = await Connection.deserialize(serialized_connection);
    const pairwiseDid = serialized_connection.data.pw_did;
    const expiration = getExpirationDate({ seconds: 60 });
    const question = {
      '@type': 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/question',
      '@id': '518be002-de8e-456e-b3d5-8fe472477a86',
      'question_text': question_prompt,
      'valid_responses': [
        { 'text': button1, 'nonce': 'xyz1' },
        { 'text': button2, 'nonce': 'xyz2' }
      ],
      '@timing': {
        'expires_time': expiration
      }
    }
    await deserialized_connection.sendMessage({
      msg: JSON.stringify(question),
      type: 'Question',
      title: 'Asking login question'
    })
    let answer;
    while (!isExpired(expiration)) {
      let messages = await vcx.downloadMessages({ status: 'MS-103', pairwiseDids: pairwiseDid });
      messages = JSON.parse(messages);
      for (const message of messages[0]['msgs']) {
        if (message.type === 'Answer') {
          if (answer) {
            console.log('More then one "Answer" message')
          } else {
            answer = JSON.parse(JSON.parse(message['decryptedPayload'])['@msg'])
          }
          await vcx.updateMessages({ msgJson: JSON.stringify([{ 'pairwiseDID': pairwiseDid, 'uids': [message.uid] }]) });
        }
      }
      if (answer) {
        break
      }
    }
    if (isExpired(expiration)) {
      console.log("expired");
      throw Error('Timeout');
    } else {
      console.log(answer);
      const signature = Buffer.from(answer['response.@sig']['signature'], 'base64')
      const data = answer['response.@sig']['sig_data']
      console.log('validating signature');
      const valid = await deserialized_connection.verifySignature({ data: Buffer.from(data), signature });
      if (valid) {
        console.log('Signature is valid!')
        return base64decode(data)
      } else {
        console.log('Signature validation failed')
        return false
      }
    }
}


async function createSchema(agencyName, institution, schema_name){
    await ensureWalletExists(agencyName, institution);
    let schema_data = await fs.readJson(`./config/${agencyName}/${institution}/${agencyName}-${institution}-${schema_name}-schema.json`);
    //set up incremental version float in order to avoid schema version conflicts
    let currentVersion = parseFloat (schema_data.data.version);
    newVersion = currentVersion +.01;
    schema_data.data.version = String(newVersion.toFixed(2));
    console.log(schema_data);
    let schema = await Schema.create(schema_data);
    //retrieve schema ID on Ledger
    let schemaId = await schema.getSchemaId();
    //write the Ledger ID to the schema json file for future use
    schema_data['schemaId'] = schemaId;
    await fs.writeJson(`./config/${agencyName}/${institution}/${agencyName}-${institution}-${schema_name}-schema.json`, schema_data);
    console.log(`Congratulations! Your schema was written to the Ledger and the id is : ${schemaId}`);
}

async function createCredentialDef(agencyName, institution, schema_name){
    await ensureWalletExists(agencyName, institution);
    let schema_data = await fs.readJson(`./config/${agencyName}/${institution}/${agencyName}-${institution}-${schema_name}-schema.json`);
    console.log(schema_data.schemaId);
    console.log('creating credential definition');
    const data = {
        name: schema_name,
        paymentHandle: 0,
        revocation: false,
        revocationDetails: {
            tailsFile: 'tails.txt',
        },
        schemaId: schema_data.schemaId,
        sourceId: schema_data.sourceId
    };
    let credentialDef = await CredentialDef.create(data);
    // let credentialDef = await CredentialDef.create({"name":schema_data.name,"paymentHandle": 0,"revocation":false,"schemaId":schema_data.schemaId,"sourceId":"55555"});
    let ser_CredDef = await credentialDef.serialize();
    console.log(ser_CredDef);
    let credDefId = await credentialDef.getCredDefId();
    await fs.writeJson(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${schema_name}-credential-definition.json`,ser_CredDef);
    console.log(`Congratulations! Your Credential Definition was written to the Ledger and the id is : ${credDefId}`);

}

async function offerCredential(agencyName, institution, credential_name,connection_name){
    await ensureWalletExists(agencyName, institution);
    let credential_definition = await fs.readJson(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${credential_name}-credential-definition.json`);
    let credential_data = await fs.readJson(`./config/${agencyName}/${institution}/${agencyName}-${institution}-${connection_name}-${credential_name}-data.json`);
    let connection_data = await fs.readJson(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${connection_name}-connection.json`);
    let connection = await Connection.deserialize(connection_data);
    let serial_connection = await connection.serialize();
    var cred_def_deserialzed = await CredentialDef.deserialize(credential_definition);
    // get credential definition handle
    cred_def_handle = await cred_def_deserialzed.handle;
    console.log (`handle is _ ${cred_def_handle}`);
    console.log(credential_data.attrs);
    let credential = await IssuerCredential.create({
        "sourceId":"1",
        "credDefHandle": cred_def_handle,
          "attr": credential_data.attrs,
          "credentialName":"Cred Name",
          "price": credential_data.price
    });
    console.log(`Successfully created A Credential, now offering it to ${connection_name}...`);
    let ser_credential = await credential.serialize()
    console.log(ser_credential)
    await credential.sendOffer(connection);
    await credential.updateState();
    let state = await credential.getState();
    while(state != 3) {
        console.log("Offer Sent, The State of the Credential Offer is "+ state);
        await sleep(2000);
        await credential.updateState();
        state = await credential.getState();
    }
    await credential.sendCredential(connection);
    while(state != 4) {
      console.log("Credential Sent, The State of the Credential is "+ state);
      await sleep(2000);
      await credential.updateState();
      state = await credential.getState();
    }
    console.log(`Congratulations! Your Credential was offered and accepted by ${connection_name}`);
}

async function requestProof(agencyName, institution, proof_name,connection_name){
    await ensureWalletExists(agencyName, institution);
    let proof_data = await fs.readJson(`./config/${agencyName}/${institution}/${agencyName}-${institution}-${proof_name}-proof-definition.json`);
    let connection_data = await fs.readJson(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${connection_name}-connection.json`);
    let connection = await Connection.deserialize(connection_data);
    await connection.updateState();
    await connection.serialize();
    console.log(proof_data);
    let proof = await Proof.create(proof_data);
    await proof.requestProof(connection);
    await proof.updateState();
    state = await proof.getState();
    while(state != StateType.RequestReceived){
        console.log(`The state of the proof is ${state}`)
        await sleep(2000);
        await proof.updateState();
        state = await proof.getState();
        if(state == StateType.Accepted) {
            var proof_return = await proof.getProof(connection);
            console.log(`The get proof state is ${proof_return}`);
            break;
        }
    }
    await proof.updateState();
    state = await proof.getState();
    var proof_return = await proof.getProof(connection);
    if (verifyClaims(proof_return, proof_data.attrs) == true) {
        console.log("Claims in the proof satisfy all restrictions. The proof is verified!");
    }
    else {
        console.log("Claims DO NOT meet all restrictions");
    }
    await fs.writeFile(`./data/${agencyName}/${institution}/${agencyName}-${institution}-${connection_name}-proof.json`,proof_return);
}

function verifyClaims(the_proof, proof_template) {
    // determine which claims should have restrictions applied
    var restricted = [];
    var proof_obj = JSON.parse(the_proof['proof']);
    for (attribute in proof_template) {
        if ("restrictions" in proof_template[attribute]) {
            restricted.push(proof_template[attribute]["name"]);
        }
    }

    verified = true;
    for (claim in restricted) {
        if (!(restricted[claim] in proof_obj["requested_proof"]["revealed_attrs"])) {
            verified = false;
            console.log("Attribute " + restricted[claim] + " has unmet restrictions.");
        }
    }

    return verified;
}

module.exports={
  testVcx,
  makeConnection,
  sendMessage,
  askProvableQuestion,
  createSchema,
  createCredentialDef,
  offerCredential,
  requestProof
}
//make script runnable in CLI
require('make-runnable/custom')({
    printOutputFrame: false
})


function getToken (size) {
    return base64url(crypto.randomBytes(size))
  }
function getExpirationDate (timeConfig = {}) {
    let expiration = new Date()
    if (timeConfig.hours) {
        expiration = new Date(expiration.setHours(expiration.getHours() + timeConfig.hours))
    }
    if (timeConfig.minutes) {
        expiration = new Date(expiration.setMinutes(expiration.getMinutes() + timeConfig.minutes))
    }
    if (timeConfig.seconds) {
        expiration = new Date(expiration.setSeconds(expiration.getSeconds() + timeConfig.seconds))
    }

    return expiration.toISOString()
}
function isExpired (expirationDate) {
    // return (expirationDate < new Date().toISOString())
    return false;
}
function base64decode (data) {
    const buff = Buffer.from(data, 'base64')
    return buff.toString('ascii')
  }
function getConfig () {
  let yamlConfig = getYAML(CONFIG_PATH)

  if (yamlConfig.issueCredential) {
    assert(yamlConfig.credentialInfo)
    assert(yamlConfig.credentialInfo.credDefId)
    assert(yamlConfig.credentialInfo.credDef)
  }
  return yamlConfig
}
