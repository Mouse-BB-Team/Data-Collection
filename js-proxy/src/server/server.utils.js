const request = require('request-promise');
const logger = require('../logger.config.js');
const redisClient = require('../redis.config.js');


let SEC = 10;
const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

let collectedEventData = new Map();

const authenticateWithToken = async (req, res, next) => {
    const userAccessToken = req.cookies['mouse-bb-token'];
    const userRefreshToken = req.cookies['mouse-bb-refresh-token'];

    if (!userAccessToken && !userRefreshToken) {
        logger.error("Error: token is null");
        return res.redirect(301, '/user/login.html');
    }

    if (!userAccessToken && userRefreshToken) {
        logger.warn("Access token is null, attempting to refresh token");
        try {
            await checkRefresh(res, userRefreshToken);
            return next();
        } catch (err) {
            logger.error("Error when attempting to refresh token");
        }
    }

    if (userAccessToken) {
        const isCachedTokenValid = await checkCachedToken(userAccessToken);

        if (isCachedTokenValid) {
            return next();
        }

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
            if (err.statusCode === 400 && userRefreshToken) {
                logger.info("API validation failed, attempting to grant access with refresh token");
                try {
                    await checkRefresh(res, userRefreshToken, next);
                    return next();
                } catch (err) {
                    logger.error("Token refreshing failed after API failure");
                }
            }
        }
    } else {
        logger.error("Error occur during token validation");
        return res.redirect(301, '/user/login.html');
    }
}


const checkRefresh = async (res, refreshToken) => {
    const responseWithNewToken = await getRefreshedToken(refreshToken);
    if (responseWithNewToken) {
        const accessTokenExp = process.env.OAUTH2_TOKENEXPIREDTIME;
        const refreshTokenExp = process.env.OAUTH2_REFRESHTOKENEXPIREDTIME;

        const cookieOptions = {
            maxAge: 1000 * accessTokenExp,
            sameSite: "strict",
            secure: true,
            httpOnly: true
        }

        const refreshCookieOptions = {
            maxAge: 1000 * refreshTokenExp,
            sameSite: "strict",
            secure: true,
            httpOnly: true
        }
        res.cookie('mouse-bb-token', responseWithNewToken.access_token, cookieOptions);
        res.cookie('mouse-bb-refresh-token', responseWithNewToken.refresh_token, refreshCookieOptions);
        return;
    }
    return null;
}


const getRefreshedToken = async userRefreshToken => {
    const options = {
        method: 'POST',
        uri: process.env.API_ROUTE_TOKEN_GRANT,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Basic ' + Buffer.from(`${process.env.API_CLIENT_ID}:${process.env.API_CLIENT_SECRET}`).toString('base64')
        },
        form: {
            grant_type: "refresh_token",
            refresh_token: userRefreshToken
        },
        resolveWithFullResponse: true,
        json: true
    };

    try {
        const response = await request.post(options);

        if (response.statusCode === 200) {
            const newJwtToken = response.body.access_token;
            const newRefreshToken = response.body.refresh_token;

            const accessTokenExpire = process.env.OAUTH2_TOKENEXPIREDTIME;
            const refreshTokenExpire = process.env.OAUTH2_REFRESHTOKENEXPIREDTIME;

            if (redisClient.connected) {
                redisClient.setex(newJwtToken, accessTokenExpire, response.body.jti);
                redisClient.setex(newRefreshToken, refreshTokenExpire, response.body.jti);
            }
            logger.info("Refreshed token return");
            return response.body;
        }
    } catch (err) {
        logger.error("Error when refreshing");
        return null;
    }
    logger.error("Refresh token expired");
    return null;
}

const checkCachedToken = async userToken => {
    let success = false;
    if (userToken && redisClient.connected) {
        try {
            const reply = await redisClient.getAsync(userToken);
            if (reply !== null)
                success = true;
        } catch (err) {
            logger.error("Redis returned error when validating cached token: " + err);
            return false;
        }
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


module.exports =
    {
        sendDataToAPI: sendDataToAPI,
        authenticateWithToken: authenticateWithToken,
        checkCachedToken: checkCachedToken,
        set timeout(val) {
            SEC = val;
        },
        get getCollectedData() {
            return collectedEventData;
        }
    };
