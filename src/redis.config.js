const redis = require('redis');
const logger = require('./loggerModule.js');


const redisClient = redis.createClient({
    host: process.env.REDIS_IP,
    port: process.env.REDIS_PORT,
    // password  : process.env.REDIS_PASSWORD,
    /** if using SSL */
    // tls: {
    //     key : stringValueOfKeyFile,
    //     cert: stringValueOfCertFile,
    //     ca  : [ stringValueOfCaCertFile ]
    // }
});




redisClient.on("error", error => {
   logger.error(`Redis Error: ${error}`);
});

module.exports = redisClient;
