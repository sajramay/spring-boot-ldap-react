const proxy = require('http-proxy-middleware')

module.exports = function(app) {
    app.use(proxy('/graphql', {target: 'http://localhost:9090/'}))
    app.use(proxy('/auth', {target: 'http://localhost:9090/'}))
    app.use(proxy('/logout', {target: 'http://localhost:9090/'}))
}