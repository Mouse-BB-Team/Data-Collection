require('dotenv').config();
const request = require('request-promise');
const logger = require('../loggerModule.js');
const redis = require('redis');

const REDIS_PORT = 6379;
const redis_agent = redis.createClient(REDIS_PORT);

let SEC = 10;
const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

let collectedEventData = new Map();


const authenticateWithToken = async (req, res, next) => {
    const userAccessToken = req.cookies['mouse-bb-token'];
    if (userAccessToken == null) {
        logger.error("Error: token is null");
        res.redirect(301, '/user/login.html');
    } else {
        const redisCacheCheck = await checkCachedToken(userAccessToken);
        if (redisCacheCheck) {
            return next();
        } else {
            const options = {
                method: 'POST',
                uri: 'https://mouse-bb-api.herokuapp.com/oauth/check_token',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Authorization': 'Basic ' + Buffer.from(`${process.env.API_CLIENT_ID}:${process.env.API_CLIENT_SECRET}`).toString('base64')
                },
                form: {
                    token: userAccessToken
                },
                resolveWithFullResponse: true,
                json: true
            };

            try {
                const response = await request.post(options);
                if (response.statusCode === 200) {
                    logger.info("User authenticated with valid token");
                    logger.info(req.url);
                    next();
                }
            } catch (err) {
                logger.error("Error occur during token validation");
                return res.redirect(301, '/user/login.html')
            }
        }
    }
}


const checkCachedToken = async userToken => {
    return await redis_agent.get(userToken, (err, data) => {
        if (err) {
            logger.error("Redis " + err);
            return false;
        } else if (data !== null) {
            logger.info("User authenticated with cached token with redis");
            return true;
        }
    });
}


const sendDataToAPI = () => {
    if (collectedEventData.size !== 0) {
        logger.info("Sending POST to API");

        collectedEventData.forEach(async (data_list, token) => {
            const options = {
                method: 'POST',
                uri: 'https://mouse-bb-api.herokuapp.com/session/add',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: {
                    sessions: data_list
                },
                resolveWithFullResponse: true,
                json: true
            };

            try {
                const response = await request.post(options);
                logger.info(`API responded with status code ${response.statusCode}`);
            } catch (err) {
                logger.error(`An error has occurred – status code: ${err.statusCode}`);
                if (err.statusCode === undefined) {
                    logger.error(err);
                }
                if (err.statusCode === 401) {
                    logger.error(`User unauthorized`);
                }
            }
        });

        collectedEventData.clear();
    }

    wait(SEC * 1000).then(sendDataToAPI);
    logger.info(`waiting ${SEC} sec`);
}


module.exports =
    {
        sendDataToAPI: sendDataToAPI,
        authenticateWithToken: authenticateWithToken,
        get getRedisAgent() {
            return redis_agent;
        },
        set timeout(val) {
            SEC = val;
        },
        get getCollectedData() {
            return collectedEventData;
        }
    };
