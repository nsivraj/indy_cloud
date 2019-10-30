import { LocalStorage } from "node-localstorage";

global.localStorage = new LocalStorage('./temp-test-data');

// SECURE STORAGE
export const secureSet = (key, data) => {
  localStorage.setItem(key, data)
}

export const secureGet = async (key) => {
  return localStorage.getItem(key)
}

export const secureGetAll = () => {

}

export const secureDelete = (key) => {
  localStorage.removeItem(key)
}
