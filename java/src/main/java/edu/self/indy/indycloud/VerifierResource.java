package edu.self.indy.indycloud;

import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;

import static org.hyperledger.indy.sdk.anoncreds.Anoncreds.*;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

import edu.self.indy.indycloud.jpa.JPAWalletRepository;
import edu.self.indy.util.Misc;

@Path("/verifier")
public class VerifierResource {

	@Autowired
  JPAWalletRepository walletRepository;

  @GET
  @Path("createProofRequest/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response createProofRequest(@PathParam("walletId") long walletId) throws Exception {

    //11. Verifier creates Proof Request
    String nonce = generateNonce().get();
    String proofRequestJson = new JSONObject()
        .put("nonce", nonce)
        .put("name", "proof_req_1")
        .put("version", "0.2")
        .put("requested_attributes", new JSONObject()
            .put("attr1_referent", new JSONObject().put("name", "name"))
            .put("attr2_referent", new JSONObject().put("name", "sex"))
            .put("attr3_referent", new JSONObject().put("name", "phone"))
        )
        .put("requested_predicates", new JSONObject()
            .put("predicate1_referent", new JSONObject()
                .put("name", "age")
                .put("p_type", ">=")
                .put("p_value", 18)
            )
        )
        .toString();

    return Response.ok( "{\"action\": \"verifier/createProofRequest\", \"proofRequestJson\": " + proofRequestJson + "}" ).build();

  }


  @POST
  @Path("verifyProof/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response verifyProof(
    @PathParam("walletId") long walletId,
    String proofPayload) throws Exception {

    JsonNode proofData = Misc.jsonMapper.readTree(proofPayload);
    String proofJson = proofData.get("proofJson").toString();
    String proofRequestJson = proofData.get("proofRequestJson").toString();
    String schemas = proofData.get("schemas").toString();
    String credentialDefs = proofData.get("credentialDefs").toString();

    System.out.println("proofJson: " + proofJson);
    JSONObject proof = new JSONObject(proofJson);

    //13. Verifier verify Proof
    JSONObject revealedAttr1 = proof.getJSONObject("requested_proof").getJSONObject("revealed_attrs").getJSONObject("attr1_referent");
    assertEquals("Alex", revealedAttr1.getString("raw"));

    assertNotNull(proof.getJSONObject("requested_proof").getJSONObject("unrevealed_attrs").getJSONObject("attr2_referent").getInt("sub_proof_index"));

    String selfAttestedValue = "8-800-300";
    assertEquals(selfAttestedValue, proof.getJSONObject("requested_proof").getJSONObject("self_attested_attrs").getString("attr3_referent"));

    String revocRegDefs = new JSONObject().toString();
    String revocRegs = new JSONObject().toString();

    Boolean valid = verifierVerifyProof(proofRequestJson, proofJson, schemas, credentialDefs, revocRegDefs, revocRegs).get();
    assertTrue(valid);

    return Response.ok( "{\"action\": \"verifier/createProofRequest\", \"proved\": " + valid + "}" ).build();
  }

  /*
  @GET
  @Path("step7/{walletId}")
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  public Response step7(@PathParam("walletId") long walletId) throws Exception {

		// 1.
		// System.out.println("\n1. Creating a new local pool ledger configuration that can be used later to connect pool nodes.\n");
		// Pool.setProtocolVersion(Utils.PROTOCOL_VERSION).get();
		// Pool.createPoolLedgerConfig(Utils.VERIFIER_POOL_NAME, Utils.SERVERONE_POOL_CONFIG).exceptionally((t) -> {
		// 		t.printStackTrace();
		// 		return null;
		// }).get();

		// // 2
		// System.out.println("\n2. Open pool ledger and get the pool handle from libindy.\n");
		// Pool pool = Pool.openPoolLedger(Utils.VERIFIER_POOL_NAME, "{}").get();

		// 3
		System.out.println("\n3. Creates a new secure wallet\n");
		Wallet.createWallet(Utils.VERIFIER_WALLET_CONFIG, Utils.VERIFIER_WALLET_CREDENTIALS).exceptionally((t) -> {
				t.printStackTrace();
				return null;
		}).get();

		// 4
		System.out.println("\n4. Open wallet and get the wallet handle from libindy\n");
		Wallet verifierWalletHandle = Wallet.openWallet(Utils.VERIFIER_WALLET_CONFIG, Utils.VERIFIER_WALLET_CREDENTIALS).get();

    // 17.5
    System.out.println("\n17.5. Verifier sends Proof Request to Prover\n");
    String nonce = generateNonce().get();
		String proofRequestJson = new JSONObject()
				.put("nonce", nonce)
				.put("name", "proof_req_1")
				.put("version", "0.1")
				.put("requested_attributes", new JSONObject()
						.put("attr1_referent", new JSONObject().put("name", "name"))
						.put("attr2_referent", new JSONObject().put("name", "sex"))
						.put("attr3_referent", new JSONObject().put("name", "phone"))
				)
				.put("requested_predicates", new JSONObject()
						.put("predicate1_referent", new JSONObject()
								.put("name", "age")
								.put("p_type", ">=")
								.put("p_value", 18)
						)
				)
        .toString();

    System.out.println("\n21. Close wallet\n");
    verifierWalletHandle.closeWallet().get();
    //Wallet.deleteWallet(walletName, null).get();
    //proverWalletHandle.closeWallet().get();

    // 22
    //System.out.println("\n22. Close pool\n");
    //pool.closePoolLedger().get();

    return Response.ok( "{\"msg\": \"verifier: step7 is done\", \"proofRequestJson\": " + proofRequestJson + "}" ).build();
  }
  */

}