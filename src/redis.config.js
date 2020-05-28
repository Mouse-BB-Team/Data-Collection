const redis = require('redis');
const logger = require('./logger.config.js');
const { promisify } = require("util");


let redisClient = redis.createClient({
    host: process.env.REDIS_IP,
    port: process.env.REDIS_PORT
});


redisClient.getAsync = promisify(redisClient.get).bind(redisClient);

redisClient.on("error", error => {
    logger.error(`Redis ${error}`);
});

redisClient.on("connect", () => {
    logger.info(`Redis connected`);
});

module.exports = redisClient;
