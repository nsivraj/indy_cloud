import { CloudIndy } from './cloud-indy'
import { RestfulIndy } from './restful-indy'

export class Test1 {

  static async test() {
    console.log('starting Test1...')

    const serveronePool = '{"reqSignature":{},"txn":{"data":{"data":{"alias":"Node1","blskey":"4N8aUNHSgjQVgkpm8nhNEfDf6txHznoYREg9kirmJrkivgL4oSEimFF6nsQ6M41QvhM2Z33nves5vfSn9n1UwNFJBYtWVnHYMATn76vLuL3zU88KyeAYcHfsih3He6UHcXDxcaecHVz6jhCYz1P2UZn2bDVruL5wXpehgBfBaLKm3Ba","blskey_pop":"RahHYiCvoNCtPTrVtP7nMC5eTYrsUA8WjXbdhNc8debh1agE9bGiJxWBXYNFbnJXoXhWFMvyqhqhRoq737YQemH5ik9oL7R4NTTCz2LEZhkgLJzB3QRQqJyBNyv7acbdHrAT8nQ9UkLbaVL9NBpnWXBTw4LEMePaSHEw66RzPNdAX1","client_ip":"167.71.155.222","client_port":9702,"node_ip":"167.71.155.222","node_port":9701,"services":["VALIDATOR"]},"dest":"Gw6pDLhcBcoQesN72qfotTgFa7cbuqZpkX3Xo6pLhPhv"},"metadata":{"from":"Th7MpTaRZVRYnPiabds81Y"},"type":"0"},"txnMetadata":{"seqNo":1,"txnId":"fea82e10e894419fe2bea7d96296a6d46f50f93f9eeda954ec461b2ed2950b62"},"ver":"1"}\n'+
    '{"reqSignature":{},"txn":{"data":{"data":{"alias":"Node2","blskey":"37rAPpXVoxzKhz7d9gkUe52XuXryuLXoM6P6LbWDB7LSbG62Lsb33sfG7zqS8TK1MXwuCHj1FKNzVpsnafmqLG1vXN88rt38mNFs9TENzm4QHdBzsvCuoBnPH7rpYYDo9DZNJePaDvRvqJKByCabubJz3XXKbEeshzpz4Ma5QYpJqjk","blskey_pop":"Qr658mWZ2YC8JXGXwMDQTzuZCWF7NK9EwxphGmcBvCh6ybUuLxbG65nsX4JvD4SPNtkJ2w9ug1yLTj6fgmuDg41TgECXjLCij3RMsV8CwewBVgVN67wsA45DFWvqvLtu4rjNnE9JbdFTc1Z4WCPA3Xan44K1HoHAq9EVeaRYs8zoF5","client_ip":"167.71.155.222","client_port":9704,"node_ip":"167.71.155.222","node_port":9703,"services":["VALIDATOR"]},"dest":"8ECVSk179mjsjKRLWiQtssMLgp6EPhWXtaYyStWPSGAb"},"metadata":{"from":"EbP4aYNeTHL6q385GuVpRV"},"type":"0"},"txnMetadata":{"seqNo":2,"txnId":"1ac8aece2a18ced660fef8694b61aac3af08ba875ce3026a160acbc3a3af35fc"},"ver":"1"}\n'+
    '{"reqSignature":{},"txn":{"data":{"data":{"alias":"Node3","blskey":"3WFpdbg7C5cnLYZwFZevJqhubkFALBfCBBok15GdrKMUhUjGsk3jV6QKj6MZgEubF7oqCafxNdkm7eswgA4sdKTRc82tLGzZBd6vNqU8dupzup6uYUf32KTHTPQbuUM8Yk4QFXjEf2Usu2TJcNkdgpyeUSX42u5LqdDDpNSWUK5deC5","blskey_pop":"QwDeb2CkNSx6r8QC8vGQK3GRv7Yndn84TGNijX8YXHPiagXajyfTjoR87rXUu4G4QLk2cF8NNyqWiYMus1623dELWwx57rLCFqGh7N4ZRbGDRP4fnVcaKg1BcUxQ866Ven4gw8y4N56S5HzxXNBZtLYmhGHvDtk6PFkFwCvxYrNYjh","client_ip":"167.71.155.222","client_port":9706,"node_ip":"167.71.155.222","node_port":9705,"services":["VALIDATOR"]},"dest":"DKVxG2fXXTU8yT5N7hGEbXB3dfdAnYv1JczDUHpmDxya"},"metadata":{"from":"4cU41vWW82ArfxJxHkzXPG"},"type":"0"},"txnMetadata":{"seqNo":3,"txnId":"7e9f355dffa78ed24668f0e0e369fd8c224076571c51e2ea8be5f26479edebe4"},"ver":"1"}\n'+
    '{"reqSignature":{},"txn":{"data":{"data":{"alias":"Node4","blskey":"2zN3bHM1m4rLz54MJHYSwvqzPchYp8jkHswveCLAEJVcX6Mm1wHQD1SkPYMzUDTZvWvhuE6VNAkK3KxVeEmsanSmvjVkReDeBEMxeDaayjcZjFGPydyey1qxBHmTvAnBKoPydvuTAqx5f7YNNRAdeLmUi99gERUU7TD8KfAa6MpQ9bw","blskey_pop":"RPLagxaR5xdimFzwmzYnz4ZhWtYQEj8iR5ZU53T2gitPCyCHQneUn2Huc4oeLd2B2HzkGnjAff4hWTJT6C7qHYB1Mv2wU5iHHGFWkhnTX9WsEAbunJCV2qcaXScKj4tTfvdDKfLiVuU2av6hbsMztirRze7LvYBkRHV3tGwyCptsrP","client_ip":"167.71.155.222","client_port":9708,"node_ip":"167.71.155.222","node_port":9707,"services":["VALIDATOR"]},"dest":"4PS3EDQ3dW1tci1Bp6543CfuuebjFrg36kLAUcskGfaA"},"metadata":{"from":"TWwCRQRZ2ZHMJFn9TzLp7W"},"type":"0"},"txnMetadata":{"seqNo":4,"txnId":"aa5e817d7cc626170eca175822029339a444eb0ee8f0bd20d3b0b76e566fb008"},"ver":"1"}'

    const serveroneAgency = `${RestfulIndy.restProtocol}://${RestfulIndy.restHost}:${RestfulIndy.restPort}`

    // {"logLevel":"trace","uniqueId":"9971369c-c181-9b18-4d9a-6f5f01b40614","MAX_ALLOWED_FILE_BYTES":10000000}
    CloudIndy.setVcxLogger('trace', 'edb99697-0c3a-21cf-fe49-0e029181b1c7', 10000000).then(loggerPath => {
      console.log("loggerPath: ", loggerPath)

      // {"lengthOfKey":64}
      CloudIndy.createWalletKey(10).then(walletKey => {
        console.log('walletKey: ', walletKey)

        CloudIndy.getColor('./test/navel-org.png').then(colorRGB => {
          console.log("colorRGB: ", colorRGB)

          CloudIndy.createOneTimeInfo('{"agency_url":"' + serveroneAgency + '","agency_did":"L1gaixoxvbVg97HYnrr6rG","agency_verkey":"BMzy1cEuSFvnKYjjBxY4jC2gQbNmaVX3Kg5zJJiXAwq8","wallet_name":"9971369c-c181-9b18-4d9a-6f5f01b40614-cm-wallet","wallet_key":"nY89s/t3zuy7URDv11Y2g/AkHPUWmOXBo/WJgAYW/oo4MymFuuEtfH+thDoKDoGJxh+ExmOFMRgEckcZnzg0SA==","agent_seed":null,"enterprise_seed":null,"payment_method":"sov"}').then(oneTimeInfo => {
            console.log("oneTimeInfo: ", oneTimeInfo)

            CloudIndy.getGenesisPathWithConfig(serveronePool, 'serverone-pool_transactions_genesis.json').then(genesisPath => {
              console.log("genesisPath: ", genesisPath)

              vcxInitConfig = oneTimeInfo
              vcxInitConfig.institution_name = "some-random-name"
              vcxInitConfig.institution_logo_url = "https://robothash.com/logo.png"
              vcxInitConfig.pool_name = oneTimeInfo.wallet_name.replace('-','').replace('wallet','pool')
              vcxInitConfig.payment_method = "sov"
              vcxInitConfig.genesis_path = genesisPath
              vcxInitConfig.config = JSON.parse('['+serveronePool.replace(/(\r\n|\n|\r)/gm,",")+']')
              CloudIndy.init(vcxInitConfig).then(result => {
                console.log("result of init: ", result)
              })
            })
          })
        })
      })
    })

  }

}

