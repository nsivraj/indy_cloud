import 'babel-polyfill'
import { Test1 } from './test1'
import { Test2 } from './test2'

export class MainTest {
  static async test() {
    await Test1.test()
    await Test2.test()
  }
}

MainTest.test()
