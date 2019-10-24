package edu.self.indy.indycloud.actionhandler;

import com.evernym.sdk.vcx.connection.ConnectionApi;

import edu.self.indy.util.Misc;

public class CreateConnectionWithInvite extends AbstractActionHandler {
  @Override
  public String execute() throws Exception {
    String invitationRequestId = walletAction.getParameter("invitationRequestId").toString();
    invitationRequestId = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(invitationRequestId));
    String inviteDetails = walletAction.getParameter("inviteDetails").toString();
    inviteDetails = Misc.returnNullForEmptyOrNull(Misc.undoJSONStringify(inviteDetails));

    //Log.d(TAG, "createConnectionWithInvite() called with: invitationRequestId = [" + invitationRequestId + "], inviteDetails = ["
    //            + inviteDetails + "], promise = [" + promise + "]");
    //try {
        int connectionHandle = ConnectionApi.vcxCreateConnectionWithInvite(invitationRequestId, inviteDetails).get();

        // .exceptionally((t) -> {
        //     Log.e(TAG, "createConnectionWithInvite: ", t);
        //     promise.reject("FutureException", t.getMessage());
        //     return -1;
        // }).thenAccept(result -> {
        //     if (result != -1) {
        //         BridgeUtils.resolveIfValid(promise, result);
        //     }
        // });

    //} catch (Exception e) {
    //    promise.reject("VCXException", e.getMessage());
    //}

    return String.valueOf(connectionHandle);
  }
}

// WalletAction: id: 257 :: actionName: createConnectionWithInvite :: actionParams: {"invitationRequestId":"ZDIxNGE","inviteDetails":"{\"connReqId\":\"ZDIxNGE\",\"statusCode\":\"MS-102\",\"senderDetail\":{\"DID\":\"HHHDR9336DGyX5mKRPEypr\",\"agentKeyDlgProof\":{\"agentDID\":\"5iG3v1QaitLEqhBx39LSNZ\",\"agentDelegatedKey\":\"3ZwGiv88EQ5PMmHjdaQSSJrQujN98nWtidgPrGrqLPED\",\"signature\":\"s6/XJR/zS6f7Fl/HWTA4mZma59EjytpvEs/i/wuql7D7yzuh/M2vEJ9KOkTnDgPTnNteapbkCFje9Xe+5DPeAA==\"},\"logoUrl\":\"https://s3.us-east-2.amazonaws.com/public-demo-artifacts/demo-icons/cbFaber.png\",\"name\":\"DEMO-Faber College\",\"publicDID\":\"AVYNnUaVRb261zXkYCrxHz\",\"verKey\":\"9sfYDY6nrrHkp4PCLjTfPupxUqSjPs6BZSY3mQTVa8Vu\"},\"senderAgencyDetail\":{\"DID\":\"5YKgVzinHVv5XfudLv5F4k\",\"endpoint\":\"eas.evernym.com:80/agency/msg\",\"verKey\":\"3UX8ZEkpg6ZGPiqdTWdPm5c63z5XotrD7vSKp8DLE9iu\"},\"targetName\":\"DEMO-Faber College\",\"statusMsg\":\"message sent\",\"version\":\"1.0\"}"}
// cloudResponse: {"cloudResponse": -267896}
