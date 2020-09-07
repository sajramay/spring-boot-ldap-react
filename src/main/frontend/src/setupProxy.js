const { createProxyMiddleware } = require("http-proxy-middleware")

module.exports = function(app) {
    app.use(createProxyMiddleware('/graphql', {target: 'http://localhost:9090/'}))
    app.use(createProxyMiddleware('/auth', {target: 'http://localhost:9090/'}))
    app.use(createProxyMiddleware('/logout', {target: 'http://localhost:9090/'}))
}