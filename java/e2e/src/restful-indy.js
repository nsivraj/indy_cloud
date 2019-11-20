import { secureSet, secureGet, secureDelete } from './storage'
//import RNFetchBlob from 'rn-fetch-blob'
import fetch from 'node-fetch'
import FormData from 'form-data'
import fs from 'fs'

export class RestfulIndy {
  static restProtocol = 'http'
  static restHost = '192.168.0.3'
  //static restHost = '192.168.0.2'
  //static restHost = '10.4.1.73'
  //static restHost = '192.168.43.86'
  static restPort = '9000'

  static myWalletId = -1

  static async send(action, params) {
    if (this.myWalletId === -1) {
      const walletIdVal = await secureGet('myWalletId')
      this.myWalletId = walletIdVal ? parseInt(walletIdVal) : -1
      if(isNaN(this.myWalletId)) {
        this.myWalletId = -1
      }
    }

    if('writeToVcxLog' !== action) {
      console.log(
        'Trying to call: ',
        this.myWalletId,
        ' :: ',
        action,
        ' :: ',
        params
      )
    }

    if(this.myWalletId !== -1) {
      // Need to make sure that the walletId is still valid
      const response = await fetch(`${this.restProtocol}://${this.restHost}:${
          this.restPort
        }/api/v1/wallets/${this.myWalletId}`, {
          method: 'GET'
        }
      )

      if(response.status >= 500) {
        this.myWalletId = -1
        secureDelete('myWalletId')
      }
    }

    if (this.myWalletId === -1) {
      // get a brand new wallet id
      const response = await fetch(
        `${this.restProtocol}://${this.restHost}:${
          this.restPort
        }/api/v1/wallets/signupForWallet`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            // 'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: '{"secure": false}',
        }
      )

      const { cloudResponse } = await response.json()
      this.myWalletId = parseInt(cloudResponse)
      if(!isNaN(this.myWalletId)) {
        secureSet('myWalletId', this.myWalletId.toString())
      }
    }

    if ('getColor' === action) {

      const form = new FormData();

      const buffer = fs.readFileSync(params.imagePath);

      form.append('get-color-image', buffer, {
        contentType: 'image/png',
        name: 'get-color-image',
        filename: 'get-color-image.png',
      });

      return fetch(
          `${this.restProtocol}://${this.restHost}:${
          this.restPort
        }/api/v1/wallets/${this.myWalletId}/getColor`,
        {
          method: 'POST',
          body: form
        }).then(async resp => {
          const responseJSON = await resp.json()
          console.log('RestfulIndy Successfully received: ', responseJSON)
          return responseJSON
        }).catch(err => {
          console.log('RestfulIndy had error: ', err, " for action: " + action)
          return Promise.reject(err)
        })
    } else {
      return fetch(
        `${this.restProtocol}://${this.restHost}:${
          this.restPort
        }/api/v1/wallets/${this.myWalletId}/action/${action}`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            // 'Content-Type': 'application/x-www-form-urlencoded',
          },
          body: JSON.stringify(params),
        }
      )
        .then(response => Promise.all([response, response.json()]))
        .then(async ([response, responseObj]) => {
          if (response.status < 500) {
            if('writeToVcxLog' !== action) {
              console.log('RestfulIndy Successfully received: ', responseObj)
            }
            if (action === 'exportWallet') {
              const walletPath = params['walletPath']
              // await RNFetchBlob.config({
              //   // response data will be saved to this path if it has access rights
              //   path: walletPath,
              // })
              //   .fetch(
              fetch(
                'GET',
                `${this.restProtocol}://${this.restHost}:${
                  this.restPort
                }/api/v1/wallets/${this.myWalletId}/downloadExport`,
                {
                  //some headers
                }
              )
              .then(async res => {
                const filename = res.headers.get('content-disposition').split('filename=')[1]
                const blob = await res.blob()
                fs.writeFile(walletPath, blob, (err) => {
                  if (err) throw err;
                  console.log('The file ' + filename + ' saved to: ', walletPath)
                })
              })
            } else if (action === 'setVcxLogger') {
              // create the log file
              var vcxLogFilename = responseObj.cloudResponse
              vcxLogFilename = vcxLogFilename.substring(
                vcxLogFilename.lastIndexOf('/') + 1
              )
              //const documentDirectory = RNFetchBlob.fs.dirs.DocumentDir
              const documentDirectory = './vcxLogger'
              if(!fs.existsSync(documentDirectory)) {
                fs.mkdirSync(documentDirectory)
              }
              responseObj.cloudResponse = `${documentDirectory}/${vcxLogFilename}`
              //RNFetchBlob.fs.createFile(
              // fs.createFile(
              //   responseObj.cloudResponse,
              //   'starting',
              //   'utf8'
              // )
              var writeStream = fs.createWriteStream(responseObj.cloudResponse);
              writeStream.write('starting');
              writeStream.end();
            }

            return responseObj
          } else {
            if('writeToVcxLog' !== action) {
              console.log('RestfulIndy had error: ', response.status, " for action: ", action)
            }
            return Promise.reject(new Error(response.status))
          }
        })
        .catch(error => {
          if('writeToVcxLog' !== action) {
            console.log('RestfulIndy had error: ', error, " for action: ", action)
          }
          return Promise.reject(error)
        })
    }
  }
}
