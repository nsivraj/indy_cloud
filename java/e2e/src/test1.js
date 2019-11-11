import { CloudIndy } from './cloud-indy'

export class Test1 {

  static async test() {
    console.log('starting Test1...')
    
    // {"lengthOfKey":64}
    CloudIndy.createWalletKey(10).then(walletKey => {
      console.log('walletKey: ', walletKey)
    })

    CloudIndy.getColor('./test/navel-org.png').then(colorRGB => {
      console.log("colorRGB: ", colorRGB)
    })

    // {"logLevel":"trace","uniqueId":"9971369c-c181-9b18-4d9a-6f5f01b40614","MAX_ALLOWED_FILE_BYTES":10000000}
    CloudIndy.setVcxLogger('trace', 'edb99697-0c3a-21cf-fe49-0e029181b1c7', 10000000).then(loggerPath => {
      console.log("loggerPath: ", loggerPath)
    })

    //{"agency_url":"http://casq002.pqa.evernym.com","agency_did":"L1gaixoxvbVg97HYnrr6rG","agency_verkey":"BMzy1cEuSFvnKYjjBxY4jC2gQbNmaVX3Kg5zJJiXAwq8","wallet_name":"9971369c-c181-9b18-4d9a-6f5f01b40614-cm-wallet","wallet_key":"nY89s/t3zuy7URDv11Y2g/AkHPUWmOXBo/WJgAYW/oo4MymFuuEtfH+thDoKDoGJxh+ExmOFMRgEckcZnzg0SA==","agent_seed":null,"enterprise_seed":null,"payment_method":"sov"}
    CloudIndy.createOneTimeInfo('{"agency_url":"http://casq002.pqa.evernym.com","agency_did":"L1gaixoxvbVg97HYnrr6rG","agency_verkey":"BMzy1cEuSFvnKYjjBxY4jC2gQbNmaVX3Kg5zJJiXAwq8","wallet_name":"9971369c-c181-9b18-4d9a-6f5f01b40614-cm-wallet","wallet_key":"nY89s/t3zuy7URDv11Y2g/AkHPUWmOXBo/WJgAYW/oo4MymFuuEtfH+thDoKDoGJxh+ExmOFMRgEckcZnzg0SA==","agent_seed":null,"enterprise_seed":null,"payment_method":"sov"}').then(oneTimeInfo => {
      console.log("oneTimeInfo: ", oneTimeInfo)
    })
  }

}