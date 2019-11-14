package edu.self.indy.howto;

public class Utils {
  public static final int PROTOCOL_VERSION = 2;
  public static final String STORAGE_TYPE = "default";
  public static final String STEWARD_SEED = "000000000000000000000000Steward1";
  public static final String SERVERONE_POOL_CONFIG = "{\"genesis_txn\": \"/root/indy_cloud/vcx-cli/config/serverone-pool_transactions_genesis.json\"}";
  public static final String DEFAULT_REVOCATION_CONFIG = "{\"support_revocation\":false}";


  public static final String ISSUER_WALLET_NAME = "IssuerWallet";
  public static final String ISSUER_WALLET_CONFIG = "{ \"id\":\"" + ISSUER_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String ISSUER_WALLET_CREDENTIALS = "{\"key\":\"8dvfYSt5d1taSd6yJdpjq4emkwsPDDLYxkNFysFD2cZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String ISSUER_POOL_NAME = "IssuerPool";


  public static final String PROVER_WALLET_NAME = "ProverWallet";
  public static final String PROVER_WALLET_CONFIG = "{ \"id\":\"" + PROVER_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String PROVER_WALLET_CREDENTIALS = "{\"key\":\"9dvfYSt5d2ta4d6yJdpjq4emkwsPCDLYxkNFysFD2dZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String PROVER_POOL_NAME = "ProverPool";
  public static final String PROVER_MASTER_SECRET = "master_secret_tobekept";


  public static final String VERIFIER_WALLET_NAME = "VerifierWallet";
  public static final String VERIFIER_WALLET_CONFIG = "{ \"id\":\"" + VERIFIER_WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String VERIFIER_WALLET_CREDENTIALS = "{\"key\":\"2dufYSt5d2tC4d6yJdpjq4emkwsPCALYxkNFysFD2dZY\", \"key_derivation_method\":\"RAW\"}";
  public static final String VERIFIER_POOL_NAME = "VerifierPool";
}
