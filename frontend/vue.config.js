module.exports = {
  baseUrl:
    process.env.NODE_ENV === 'production'
      ? '/tnapp/'
      : 'http:/127.0.0.1:8080/tnapp/'
}
