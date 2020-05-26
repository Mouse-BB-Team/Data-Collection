const log4js = require('log4js');

log4js.configure({
    appenders: {
        everything: { type: 'file', filename: 'logs/node-server-logs.log'},
        console: { type: 'console' }
    },
    categories: {
        default: { appenders: [ 'everything' , 'console' ], level: 'debug'}
    }
});

const logger = log4js.getLogger('node-server-logger')

module.exports = logger;
