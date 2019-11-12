package edu.self.indy.howto;

public class Utils {
  public static final int PROTOCOL_VERSION = 2;
  /**
   * WALLET_NAME must be unique for each new wallet
   */
  public static final String WALLET_NAME = "Wallet1";
  public static final String STORAGE_TYPE = "default";
  public static final String WALLET_CONFIG = "{ \"id\":\"" + WALLET_NAME + "\", \"storage_type\":\"" + STORAGE_TYPE + "\"}";
  public static final String WALLET_CREDENTIALS = "{\"key\":\"8dvfYSt5d1taSd6yJdpjq4emkwsPDDLYxkNFysFD2cZY\", \"key_derivation_method\":\"RAW\"}";
  /**
   * poolName must be unique for each new wallet
   */
  public static final String POOL_NAME = "pool";
  public static final String STEWARD_SEED = "000000000000000000000000Steward1";
  public static final String POOL_CONFIG = "{\"genesis_txn\": \"/root/indy_cloud/vcx-cli/config/serverone-pool_transactions_genesis.json\"}";
  public static final String DEFAULT_REVOCATION_CONFIG = "{\"support_revocation\":false}";
}
