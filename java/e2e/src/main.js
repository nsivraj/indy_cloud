import 'babel-polyfill'
import { CloudIndy } from './cloud-indy'

CloudIndy.createWalletKey(10).then(walletKey => {
  console.log('walletKey: ', walletKey)
})

CloudIndy.getColor('./test/navel-org.png').then(colorRGB => {
  console.log("colorRGB: ", colorRGB)
})
