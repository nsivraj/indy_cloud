package edu.self.indy.agency;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import org.hyperledger.indy.sdk.anoncreds.AnoncredsResults;
import org.hyperledger.indy.sdk.anoncreds.CredentialsSearchForProofReq;
import org.hyperledger.indy.sdk.did.Did;
import org.hyperledger.indy.sdk.did.DidResults;
import org.hyperledger.indy.sdk.pool.Pool;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.hyperledger.indy.sdk.anoncreds.Anoncreds;
import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;
import static org.hyperledger.indy.sdk.ledger.Ledger.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import edu.self.indy.howto.Utils;
import edu.self.indy.indycloud.jpa.WalletRepository;
import edu.self.indy.util.Misc;

@Path("/trustee")
public class TrusteeResource {
  private static String trusteeSeed = "000000000000000000000000Trustee1";

  @GET
  @Path("createWallet/{id}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createTrusteeWallet(@PathParam("id") long id) throws Exception {

		// 1.
		System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		Pool.createPoolLedgerConfig(Utils.TRUSTEE_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 2
		System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		Pool pool = Pool.openPoolLedger(Utils.TRUSTEE_POOL_NAME, "{}").get();

    // 3. Create and Open Trustee Wallet
    String trusteeWalletConfig = new JSONObject().put("id", "trusteeWallet").toString();
    String trusteeWalletCredentials = new JSONObject().put("key", "trustee_wallet_key").toString();
    Wallet.createWallet(trusteeWalletConfig, trusteeWalletCredentials).exceptionally((t) -> {
      t.printStackTrace();
      return null;
    }).get();
    Wallet trusteeWallet = Wallet.openWallet(trusteeWalletConfig, trusteeWalletCredentials).get();

    // 4. Create Trustee DID
    DidJSONParameters.CreateAndStoreMyDidJSONParameter theirDidJson =
            new DidJSONParameters.CreateAndStoreMyDidJSONParameter(null, trusteeSeed, null, null);
    CreateAndStoreMyDidResult createTheirDidResult = Did.createAndStoreMyDid(trusteeWallet, theirDidJson.toJson()).get();
    String trusteeDid = createTheirDidResult.getDid();
    String trusteeVerkey = createTheirDidResult.getVerkey();
    System.out.println("Trustee did: " + trusteeDid);
    System.out.println("Trustee verkey: " + trusteeVerkey);

    return Response.ok( "{\"step\": \"trustee/createWallet\"}" ).build();
  }
}