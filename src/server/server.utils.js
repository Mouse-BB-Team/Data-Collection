const request = require('request-promise');
const logger = require('../loggerModule.js');
const redisClient = require('../redis.config.js');

const { promisify } = require("util");
let getAsync = promisify(redisClient.get).bind(redisClient);

let SEC = 10;
const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

let collectedEventData = new Map();


const getRefreshedToken = async userAccessToken => {
    const refreshToken = await getAsync(`refresh:${userAccessToken}`);

    if (refreshToken !== null) {
        const options = {
            method: 'POST',
            uri: process.env.API_ROUTE_TOKEN_GRANT,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': 'Basic ' + Buffer.from(`${process.env.API_CLIENT_ID}:${process.env.API_CLIENT_SECRET}`).toString('base64')
            },
            form: {
                grant_type: "refresh_token",
                refresh_token: refreshToken
            },
            resolveWithFullResponse: true,
            json: true
        };

        try {
            const response = await request.post(options);

            if (response.statusCode === 200) {
                const newJwtToken = response.body.access_token;
                redisClient.setex(newJwtToken, response.body.expires_in, response.body.jti);
                redisClient.setex(`refresh:${newJwtToken}`, (response.body.expires_in + 20), response.body.refresh_token);
                logger.info("Refreshed token return");
                return response.body;
            }
        } catch (err) {
            logger.error("Error when refreshing");
            return null;
        }
    }
    logger.error("Refresh token expired");
    return null;
}


const authenticateWithToken = async (req, res, next) => {
    const userAccessToken = req.cookies['mouse-bb-token'];

    if (userAccessToken == null) {
        logger.error("Error: token is null");
        res.redirect(301, '/user/login.html');
    } else {
        const isCachedTokenValid = await checkCachedToken(userAccessToken);
        if (isCachedTokenValid) {
            // logger.info("Redis token okay");
            return next();
        } else {
            logger.warn("Redis token not okay, checking with api");
            const options = {
                method: 'POST',
                uri: process.env.API_ROUTE_TOKEN_CHECK,
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
                    return next();
                }
            } catch (err) {
                if (err.statusCode === 400) {
                    const responseWithNewToken = await getRefreshedToken(userAccessToken);
                    if (responseWithNewToken !== null) {
                        const cookieOptions = {
                            maxAge: 1000 * (responseWithNewToken.expires_in + 20),
                            // sameSite: "strict",
                            // // TODO
                            // secure: true,
                            httpOnly: true
                        }
                        res.cookie('mouse-bb-token', responseWithNewToken.access_token, cookieOptions);
                        return next();
                    }
                }
                logger.error("Error occur during token validation");
                res.redirect(301, '/user/login.html')
            }
        }
    }
}

const checkCachedToken = async userToken => {
    let success = false;
    try {
        const reply = await getAsync(userToken);
        if (reply !== null)
            success = true;
    }
    catch (err) {
        logger.error("Redis returned error when validating cached token: " + err);
        return false;
    }
    return success;
}

const sendDataToAPI = () => {
    if (collectedEventData.size !== 0) {
        logger.info("Sending POST to API");

        collectedEventData.forEach(async (data_list, token) => {
            const options = {
                method: 'POST',
                uri: process.env.API_ROUTE_SESSION_ADD,
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

exports.getAsync = getAsync;
exports.checkCachedToken = checkCachedToken;

module.exports =
    {
        sendDataToAPI: sendDataToAPI,
        authenticateWithToken: authenticateWithToken,
        set timeout(val) {
            SEC = val;
        },
        get getCollectedData() {
            return collectedEventData;
        }
    };
