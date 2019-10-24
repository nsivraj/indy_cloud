package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class DeserializeConnection extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String serializedConnection = walletAction.getParameter("serializedConnection").toString();
    serializedConnection = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(serializedConnection));

    if(serializedConnection == null) {
      return "-1";
    } else {
      int result = ConnectionApi.connectionDeserialize(serializedConnection).get();
      return String.valueOf(result);
    }
  }
}

// WalletAction: id: 289 :: actionName: deserializeConnection :: actionParams: {"serializedConnection":"{\"version\":\"1.0\",\"data\":{\"source_id\":\"ODFjZGR\",\"pw_did\":\"KmAYTCNpe8mqS8viJHAy2Z\",\"pw_verkey\":\"BE62VZFCj8abjrHRtfuHHExChcHDwax9VEZTKhrLwx7C\",\"state\":4,\"uuid\":\"\",\"endpoint\":\"\",\"invite_detail\":{\"statusCode\":\"MS-102\",\"connReqId\":\"ODFjZGR\",\"senderDetail\":{\"name\":\"DEMO-Faber College\",\"agentKeyDlgProof\":{\"agentDID\":\"Y5mwX2EKD871ubns2hkJw6\",\"agentDelegatedKey\":\"HwaGSeKsD3mMZC8jLfBQ8NQWz4t5WrNa5cTPN2Bm4MWx\",\"signature\":\"GEUvGh/VJ456+le8JxJlVRgFjeNR/APrNvFwJvvS6Ba01qLgiX3q+8vNd4G+ziD0N1oHjLLO5jlb9S5Ck3ETDw==\"},\"DID\":\"6sw6zzW7i3NCScYsVob5fb\",\"logoUrl\":\"https://s3.us-east-2.amazonaws.com/public-demo-artifacts/demo-icons/cbFaber.png\",\"verKey\":\"4CpZPkmeN17qzfPgbXRAz21yXYarkUfdZjCH5W8HZEfX\",\"publicDID\":\"AVYNnUaVRb261zXkYCrxHz\"},\"senderAgencyDetail\":{\"DID\":\"5YKgVzinHVv5XfudLv5F4k\",\"verKey\":\"3UX8ZEkpg6ZGPiqdTWdPm5c63z5XotrD7vSKp8DLE9iu\",\"endpoint\":\"eas.evernym.com:80/agency/msg\"},\"version\":\"1.0\",\"targetName\":\"DEMO-Faber College\",\"statusMsg\":\"message sent\",\"threadId\":null},\"invite_url\":null,\"agent_did\":\"JGfRJ4VaSRqwFQz9aceCTy\",\"agent_vk\":\"AQwXLBqbs39NX26Gb86sxjSMTmQHeG1CKYCc9UxjW9cQ\",\"their_pw_did\":\"6sw6zzW7i3NCScYsVob5fb\",\"their_pw_verkey\":\"4CpZPkmeN17qzfPgbXRAz21yXYarkUfdZjCH5W8HZEfX\",\"public_did\":null,\"their_public_did\":\"AVYNnUaVRb261zXkYCrxHz\",\"version\":\"1.0\"}}"}