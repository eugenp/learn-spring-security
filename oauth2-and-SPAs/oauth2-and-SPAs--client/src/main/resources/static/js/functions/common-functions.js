const base64urlencode = (byteArray) => {
  const stringCode = String.fromCharCode.apply(null, byteArray);
  const base64Encoded = btoa(stringCode);
  const base64urlEncoded = base64Encoded.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
  return base64urlEncoded;
};

const generateState = () => {
  return randomString(48);
};

const randomString = (len) => {
  var arr = new Uint8Array(len);
  window.crypto.getRandomValues(arr);
  var str = base64urlencode(arr);
  return str.substring(0, len);
};
