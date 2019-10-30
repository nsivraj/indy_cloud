import { RestfulIndy } from './restful-indy'

export class CloudIndy {
  static async vcxAcceptInvitation(
    connectionHandle,
    connectionOptions
  ) {
    const { cloudResponse } = await RestfulIndy.send('vcxAcceptInvitation', {
      connectionHandle,
      connectionOptions,
    })
    // returning a string
    return cloudResponse
  }

  static async vcxUpdatePushToken(pushTokenConfig) {
    RestfulIndy.send('vcxUpdatePushToken', { pushTokenConfig })
    // returning void
    return Promise.resolve()
  }

  static async deleteConnection(connectionHandle) {
    RestfulIndy.send('deleteConnection', { connectionHandle })
    // returning void
    return Promise.resolve()
  }

  static async reset(removeData) {
    const { cloudResponse } = await RestfulIndy.send('reset', {
      removeData,
    })
    // returning a boolean
    return cloudResponse
  }

  static async shutdownVcx(deletePool) {
    const { cloudResponse } = await RestfulIndy.send('shutdownVcx', {
      deletePool,
    })
    // return 0 for success
    return cloudResponse
  }

  static async getColor(imagePath) {
    const { cloudResponse } = await RestfulIndy.send('getColor', {
      imagePath,
    })
    return cloudResponse // ['', '']
  }

  static async sendTokens(
    paymentHandle,
    sovrinAtoms,
    recipientWalletAddress
  ) {
    const { cloudResponse } = await RestfulIndy.send('sendTokens', {
      paymentHandle,
      sovrinAtoms,
      recipientWalletAddress,
    })
    return cloudResponse // true
  }

  static async createOneTimeInfo(vcxProvisionConfig) {
    const { cloudResponse } = await RestfulIndy.send('createOneTimeInfo', {
      vcxProvisionConfig: JSON.parse(vcxProvisionConfig),
    })
    return JSON.stringify(cloudResponse) // ''
  }

  static async getGenesisPathWithConfig(poolConfig, fileName) {
    const poolConfigObj = JSON.parse('['+poolConfig.replace(/(\r\n|\n|\r)/gm,",")+']')
    //console.log("getGenesisPathWithConfig poolConfigObj: ", poolConfigObj)
    const { cloudResponse } = await RestfulIndy.send(
      'getGenesisPathWithConfig',
      {
        poolConfig: poolConfigObj,
        fileName,
      }
    )
    return cloudResponse // ''
  }

  static async init(vcxInitConfig) {
    var vcxConfigObj = JSON.parse(vcxInitConfig)
    vcxConfigObj.config = JSON.parse('['+vcxConfigObj.config.replace(/(\r\n|\n|\r)/gm,",")+']')
    const { cloudResponse } = await RestfulIndy.send('init', {
      vcxInitConfig: vcxConfigObj,
    })
    return cloudResponse // true
  }

  static async createConnectionWithInvite(
    invitationRequestId,
    inviteDetails
  ) {
    const { cloudResponse } = await RestfulIndy.send(
      'createConnectionWithInvite',
      {
        invitationRequestId,
        inviteDetails,
      }
    )
    // return the connectionHandle, i.e. 234
    return cloudResponse
  }

  static async getSerializedConnection(connectionHandle) {
    const { cloudResponse } = await RestfulIndy.send(
      'getSerializedConnection',
      {
        connectionHandle,
      }
    )
    return JSON.stringify(cloudResponse) // ''
  }

  static async deserializeConnection(serializedConnection) {
    const { cloudResponse } = await RestfulIndy.send('deserializeConnection', {
      serializedConnection,
    })
    // return the connectionHandle, i.e. 234
    return cloudResponse
  }

  static async credentialCreateWithMsgId(
    sourceId,
    connectionHandle,
    messageId
  ) {
    const { cloudResponse } = await RestfulIndy.send(
      'credentialCreateWithMsgId',
      {
        sourceId,
        connectionHandle,
        messageId,
      }
    )

    const offerResult = {
      credential_offer: JSON.stringify(cloudResponse.credential_offer),
      credential_handle: cloudResponse.credential_handle,
    }

    return offerResult
  }

  static async serializeClaimOffer(claimHandle) {
    const { cloudResponse } = await RestfulIndy.send('serializeClaimOffer', {
      claimHandle,
    })
    return cloudResponse // ''
  }

  static async deserializeClaimOffer(serializedClaimOffer) {
    const { cloudResponse } = await RestfulIndy.send('deserializeClaimOffer', {
      serializedClaimOffer,
    })
    // return claimHandle, i.e. 235
    return cloudResponse
  }

  static async sendClaimRequest(
    claimHandle,
    connectionHandle,
    paymentHandle
  ) {
    RestfulIndy.send('sendClaimRequest', {
      claimHandle,
      connectionHandle,
      paymentHandle,
    })
    // returning void
    return Promise.resolve()
  }

  static async proofCreateWithMsgId(
    sourceId,
    connectionHandle,
    messageId
  ) {
    const { cloudResponse } = await RestfulIndy.send('proofCreateWithMsgId', {
      sourceId,
      connectionHandle,
      messageId,
    })

    const proofResult = {
      proofHandle: cloudResponse.proofHandle,
      proofRequest: cloudResponse.proofRequest,
    }

    return proofResult
  }

  static async decryptWalletFile(walletConfig) {
    const { cloudResponse } = await RestfulIndy.send('decryptWalletFile', {
      walletConfig,
    })

    // return importHandle, i.e. 238
    return cloudResponse
  }

  static async restoreWallet(walletConfig) {
    const { cloudResponse } = await RestfulIndy.send('restoreWallet', {
      walletConfig,
    })

    // return importHandle, i.e. 238
    return cloudResponse
  }

  static async copyToPath(uri, destPath) {
    const { cloudResponse } = await RestfulIndy.send('copyToPath', {
      uri,
      destPath,
    })

    return cloudResponse // 0
  }

  static async updateClaimOfferState(claimHandle) {
    const { cloudResponse } = await RestfulIndy.send('updateClaimOfferState', {
      claimHandle,
    })

    // return updated claimOfferState, i.e. 239
    return cloudResponse
  }

  static async getClaimOfferState(claimHandle) {
    const { cloudResponse } = await RestfulIndy.send('getClaimOfferState', {
      claimHandle,
    })

    // return current state, i.e. 240
    return cloudResponse
  }

  static async getClaimVcx(claimHandle) {
    const { cloudResponse } = await RestfulIndy.send('getClaimVcx', {
      claimHandle,
    })

    // return VcxClaimInfo as string
    return cloudResponse // ''
  }

  static async exportWallet(walletPath, walletEncryptionKey) {
    const { cloudResponse } = await RestfulIndy.send('exportWallet', {
      walletPath,
      walletEncryptionKey,
    })

    // return exportHandle, i.e. 237
    return cloudResponse
  }

  static async createWalletBackup(sourceID, backupKey) {
    const { cloudResponse } = await RestfulIndy.send('createWalletBackup', {
      sourceID,
      backupKey,
    })

    return cloudResponse // 0
  }

  static async backupWalletBackup(walletBackupHandle, path) {
    const { cloudResponse } = await RestfulIndy.send('backupWalletBackup', {
      walletBackupHandle,
      path,
    })

    return cloudResponse // 0
  }

  static async updateWalletBackupState(walletBackupHandle) {
    const { cloudResponse } = await RestfulIndy.send(
      'updateWalletBackupState',
      {
        walletBackupHandle,
      }
    )

    return cloudResponse // 0
  }

  static async updateWalletBackupStateWithMessage(
    walletBackupHandle,
    message
  ) {
    const { cloudResponse } = await RestfulIndy.send(
      'updateWalletBackupStateWithMessage',
      {
        walletBackupHandle,
        message,
      }
    )

    return cloudResponse // 0
  }

  static async serializeBackupWallet(walletBackupHandle) {
    const { cloudResponse } = await RestfulIndy.send('serializeBackupWallet', {
      walletBackupHandle,
    })

    return cloudResponse // 0
  }

  static async deserializeBackupWallet(message) {
    const { cloudResponse } = await RestfulIndy.send(
      'deserializeBackupWallet',
      {
        message,
      }
    )

    return cloudResponse // 0
  }

  static async exitAppAndroid() {}

  static async proofRetrieveCredentials(proofHandle) {
    const { cloudResponse } = await RestfulIndy.send(
      'proofRetrieveCredentials',
      {
        proofHandle,
      }
    )

    return cloudResponse // ''
  }

  static async proofGenerate(
    proofHandle,
    selectedCredentials,
    selfAttestedAttributes
  ) {
    RestfulIndy.send('proofGenerate', {
      proofHandle,
      selectedCredentials,
      selfAttestedAttributes,
    })

    // returns void
    return Promise.resolve()
  }

  static async proofSend(proofHandle, connectionHandle) {
    RestfulIndy.send('proofSend', {
      proofHandle,
      connectionHandle,
    })

    // returns void
    return Promise.resolve()
  }

  static async proofCreateWithRequest(sourceId, proofRequest) {
    const { cloudResponse } = await RestfulIndy.send('proofCreateWithRequest', {
      sourceId,
      proofRequest,
    })

    return cloudResponse // 0
  }

  static async proofSerialize(proofHandle) {
    const { cloudResponse } = await RestfulIndy.send('proofSerialize', {
      proofHandle,
    })

    return cloudResponse // ''
  }

  static async proofDeserialize(
    serializedProofRequest
  ) {
    const { cloudResponse } = await RestfulIndy.send('proofDeserialize', {
      serializedProofRequest,
    })

    return cloudResponse // 0
  }

  static async downloadMessages(
    messageStatus,
    uid_s,
    pwdids
  ) {
    const { cloudResponse } = await RestfulIndy.send('downloadMessages', {
      messageStatus,
      uid_s,
      pwdids,
    })

    return cloudResponse
  }

  static async vcxGetAgentMessages(
    messageStatus,
    uid_s
  ) {
    const { cloudResponse } = await RestfulIndy.send('vcxGetAgentMessages', {
      messageStatus,
      uid_s,
    })

    return cloudResponse
  }

  static async updateMessages(
    messageStatus,
    pwdidsJson
  ) {
    const { cloudResponse } = await RestfulIndy.send('updateMessages', {
      messageStatus,
      pwdidsJson,
    })

    return cloudResponse // 0
  }

  static async getTokenInfo(paymentHandle) {
    const { cloudResponse } = await RestfulIndy.send('getTokenInfo', {
      paymentHandle,
    })

    return cloudResponse
  }

  static async createPaymentAddress(seed) {
    RestfulIndy.send('createPaymentAddress', {
      seed,
    })

    // returns void
    return Promise.resolve()
  }

  static async getLedgerFees() {
    const { cloudResponse } = await RestfulIndy.send('getLedgerFees', {})

    return cloudResponse
  }

  static totalMemory = 393939393

  static async getBiometricError() {
    const { cloudResponse } = await RestfulIndy.send('getBiometricError', {})

    return cloudResponse // ''
  }

  static async connectionSendMessage(
    connectionHandle,
    message,
    messageOptions
  ) {
    const { cloudResponse } = await RestfulIndy.send('connectionSendMessage', {
      connectionHandle,
      message,
      messageOptions,
    })

    return cloudResponse // ''
  }

  static async connectionSignData(
    connectionHandle,
    data,
    base64EncodingOption,
    encodeBeforeSigning
  ) {
    const { cloudResponse } = await RestfulIndy.send('connectionSignData', {
      connectionHandle,
      data,
      base64EncodingOption,
      encodeBeforeSigning,
    })

    const signDataResponse = {
      data: cloudResponse.data,
      signature: cloudResponse.signature,
    }

    return signDataResponse
  }

  static async connectionVerifySignature(
    connectionHandle,
    data,
    signature
  ) {
    const { cloudResponse } = await RestfulIndy.send(
      'connectionVerifySignature',
      {
        connectionHandle,
        data,
        signature,
      }
    )

    return cloudResponse
  }

  static async toBase64FromUtf8(
    data,
    base64EncodingOption
  ) {
    const { cloudResponse } = await RestfulIndy.send('toBase64FromUtf8', {
      data,
      base64EncodingOption,
    })

    return cloudResponse
  }

  static async toUtf8FromBase64(
    data,
    base64EncodingOption
  ) {
    const { cloudResponse } = await RestfulIndy.send('toUtf8FromBase64', {
      data,
      base64EncodingOption,
    })

    return cloudResponse
  }

  static async generateThumbprint(
    data,
    base64EncodingOption
  ) {
    const { cloudResponse } = await RestfulIndy.send('generateThumbprint', {
      data,
      base64EncodingOption,
    })

    return cloudResponse
  }

  static async setVcxLogger(
    logLevel,
    uniqueId,
    MAX_ALLOWED_FILE_BYTES
  ) {
    const { cloudResponse } = await RestfulIndy.send('setVcxLogger', {
      logLevel,
      uniqueId,
      MAX_ALLOWED_FILE_BYTES,
    })

    return cloudResponse
  }

  static async writeToVcxLog(
    loggerName,
    levelName,
    logMessage,
    logFilePath
  ) {
    const { cloudResponse } = await RestfulIndy.send('writeToVcxLog', {
      loggerName,
      levelName,
      logMessage,
      logFilePath,
    })

    return Promise.resolve()
  }

  static async encryptVcxLog(
    logFilePath,
    encryptionKey
  ) {
    const { cloudResponse } = await RestfulIndy.send('encryptVcxLog', {
      logFilePath,
      encryptionKey,
    })

    return cloudResponse
  }

  static async setWalletItem(key, value) {
    const { cloudResponse } = await RestfulIndy.send('setWalletItem', {
      key,
      value,
    })

    return cloudResponse // 0
  }

  static async getWalletItem(key) {
    const { cloudResponse } = await RestfulIndy.send('getWalletItem', {
      key,
    })

    return cloudResponse // ''
  }

  static async deleteWalletItem(key) {
    const { cloudResponse } = await RestfulIndy.send('deleteWalletItem', {
      key,
    })

    return cloudResponse // 0
  }

  static async updateWalletItem(key, data) {
    const { cloudResponse } = await RestfulIndy.send('updateWalletItem', {
      key,
      data,
    })

    return cloudResponse // 0
  }

  static async createWalletKey(lengthOfKey) {
    const { cloudResponse } = await RestfulIndy.send('createWalletKey', {
      lengthOfKey,
    })

    return cloudResponse // ''
  }
}
