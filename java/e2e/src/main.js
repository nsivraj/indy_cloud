import 'babel-polyfill'
import { CloudIndy } from './cloud-indy'

CloudIndy.createWalletKey(10).then(walletKey => {
  console.log('walletKey: ', walletKey)
})

CloudIndy.getColor('./test/navel-org.png').then(colorRGB => {
  console.log("colorRGB: ", colorRGB)
})

CloudIndy.setVcxLogger('trace', 'edb99697-0c3a-21cf-fe49-0e029181b1c7', 10000000).then(loggerPath => {
  console.log("loggerPath: ", loggerPath)
});
