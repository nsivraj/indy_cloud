package edu.self.indy.util;

public class Utils {
  public static final int PROTOCOL_VERSION = 2;
  public static final String STORAGE_TYPE = "default";
  public static final String STEWARD_SEED = "000000000000000000000000Steward1";
  public static final String SERVERONE_POOL_CONFIG = "{\"genesis_txn\": \"/root/indy_cloud/vcx-cli/config/serverone-pool_transactions_genesis.json\"}";
  public static final String DEFAULT_REVOCATION_CONFIG = "{\"support_revocation\":false}";


  public static final String TRUSTEE_WALLET_NAME = "TrusteeWallet";
  public static final String TRUSTEE_WALLET_CONFIG = "{ \"id\":\"" + TRUSTEE_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String TRUSTEE_WALLET_CREDENTIALS = "{\"key\":\"8dvfYSt5d1taSd5yJapjq4emkwsPDDLYxkNFysFD2cZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String TRUSTEE_POOL_NAME = "TrusteePool";


  public static final String ENDORSER_WALLET_NAME = "EndorserWallet";
  public static final String ENDORSER_WALLET_CONFIG = "{ \"id\":\"" + ENDORSER_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String ENDORSER_WALLET_CREDENTIALS = "{\"key\":\"8dvfYLt5d1taSd6yJdpjq4emkwsPDDLYxkNFysED2cZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String ENDORSER_POOL_NAME = "EndorserPool";


  public static final String AUTHOR_WALLET_NAME = "AuthorWallet";
  public static final String AUTHOR_WALLET_CONFIG = "{ \"id\":\"" + AUTHOR_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String AUTHOR_WALLET_CREDENTIALS = "{\"key\":\"8dvfYSt5d1taSd6yJdpjq4emkwsPDDLYxkNFysFD2cZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String AUTHOR_POOL_NAME = "AuthorPool";


  public static final String PROVER_WALLET_NAME = "ProverWallet";
  public static final String PROVER_WALLET_CONFIG = "{ \"id\":\"" + PROVER_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String PROVER_WALLET_CREDENTIALS = "{\"key\":\"9dvfYSt5d2ta4d6yJdpjq4emkwsPCDLYxkNFysFD2dZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String PROVER_POOL_NAME = "ProverPool";
  public static final String PROVER_MASTER_SECRET_ID = "6265b3f9-ca0d-454c-b03d-dbad0a314dc8";


  public static final String VERIFIER_WALLET_NAME = "VerifierWallet";
  public static final String VERIFIER_WALLET_CONFIG = "{ \"id\":\"" + VERIFIER_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String VERIFIER_WALLET_CREDENTIALS = "{\"key\":\"2dufYSt5d2tC4d6yJdpjq4emkwsPCALYxkNFysFD2dZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String VERIFIER_POOL_NAME = "VerifierPool";
}
