const generateCodeChallenge = async (codeVerifier) => {
  const strBuffer = new TextEncoder('utf-8').encode(codeVerifier);
  const hashBuffer = await window.crypto.subtle.digest('SHA-256', strBuffer);
  const hashedByteArray = Array.from(new Uint8Array(hashBuffer));
  return base64urlencode(hashedByteArray);
};
