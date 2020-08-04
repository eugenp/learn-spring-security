const generateCodeVerifier = () => {
  let randomByteArray = new Uint8Array(32);
  window.crypto.getRandomValues(randomByteArray);
  return base64urlencode(randomByteArray);
};
