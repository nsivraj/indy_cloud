package edu.self.indy.cli;

import com.evernym.sdk.vcx.vcx.VcxApi;

public class MakeConnection {

  public void execute() throws Exception {

    try {
      int retCode = VcxApi.initSovToken();
      if(retCode != 0) {
        throw new RuntimeException("Could not init sovtoken: " + retCode);
      } else {
        int result = VcxApi.vcxInitWithConfig(vcxInitConfigNode.toString()).get();
        if (result != -1) {
          //return "true";
        } else {
          //return "false";
        }
      }
    } catch (AlreadyInitializedException e) {
      // even if we get already initialized exception
      // then also we will resolve promise, because we don't care if vcx is already
      // initialized
      //return "true";
    }

  }
  // async function makeConnection(type,name,phonenumber){
  //   await vcx.initVcx(config);
  //   console.log("attempting connection creation");
  //   let connection = await Connection.create({id:'CONNECTIONID'});
  //   console.log('created Connection - attempting connect');
  //   if(type=="QR"){
  //       let connectionData = {data: '{"connection_type":"QR","phone":"5555555555"}',"use_public_did":true};
  //       await connection.connect(connectionData);
  //       console.log("connection.connect()");
  //       let details = await connection.inviteDetails(true);
  //       console.log(details);
  //       let qrcode = qr.image(details, { type: 'png' });
  //       qrcode.pipe(fs.createWriteStream(`./data/${name}-connection.png`));
  //       let state = await connection.getState();
  //       while(state != StateType.Accepted) {
  //           console.log("The State of the Connection is "+ state);
  //           await sleep(2000);
  //           await connection.updateState();
  //           await connection.serialize();
  //           state = await connection.getState();
  //       }
  //       //function complete
  //       let serialized_connection = await connection.serialize();
  //       let connection_file_path = `./data/${name}-connection.json`;
  //       await fs.writeJson(connection_file_path,serialized_connection);
  //       console.log("Success!! Connection Complete. The State of the Connection is "+ state);
  //   }else if(type=="SMS"){
  //       let connection_info={"id":name,"connection_type":type,"phone":String(phonenumber)};
  //       let phonestring =String(phonenumber) ;
  //       let smsData = {data: '{"connection_type":"SMS","phone":"7073436737"}',"use_public_did":true};
  //       console.log('vcx will attempt Connection through SMS');
  //       await connection.connect(smsData);
  //       let details = await connection.inviteDetails(true);
  //       let state = await connection.getState();
  //       console.log(details);
  //       while(state != StateType.Accepted) {
  //           console.log("The State of the Connection is "+ state);
  //           await sleep(2000);
  //           await connection.updateState();
  //           state = await connection.getState();
  //       }
  //       let serialized_connection = await connection.serialize();
  //       let connection_file_path = `./data/${name}-connection.json`;
  //       await fs.writeJson(connection_file_path,serialized_connection);
  //       console.log("Success!! Connection Complete. The State of the Connection is "+ state);
  //   }else if(type=="URL"){
  //       let connectionData = {data: '{"connection_type":"QR","phone":"5555555555"}',"use_public_did":true};
  //       await connection.connect(connectionData);
  //       let details = await connection.inviteDetails(true);
  //       let serialized_connection = await connection.serialize();
  //       let url = serialized_connection['data']['invite_url'];
  //       url = encodeURIComponent(url);
  //       let link = "https://connectme.app.link?t=" + url;
  //       console.log(`Your invite link is : ${link} You must copy this into a browser on your mobile device with Connect.Me installed.`);
  //       let state = await connection.getState();
  //       while(state != StateType.Accepted) {
  //           console.log("The State of the Connection is "+ state);
  //           await sleep(2000);
  //           await connection.updateState();
  //           state = await connection.getState();
  //       }
  //       let connection_file_path = `./data/${name}-connection.json`;
  //       await fs.writeJson(connection_file_path,serialized_connection);
  //       console.log("Success!! Connection Complete. The State of the Connection is "+ state);
  //   }
  // }

}