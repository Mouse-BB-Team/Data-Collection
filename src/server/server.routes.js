const express = require('express');
const request = require('request-promise');
const logger = require('../logger.config.js');
const utils = require('./server.utils.js');
const redisClient = require('../redis.config.js');

const router = express.Router();

// NOTE: These routes are "api" routes,
// therefore express app is configured to use it as !!!"/api/:'endpoint'"!!!

router.get('/api/health', (req, res) => res.status(200).end());


router.post('/api/store-data', utils.authenticateWithToken, (req, res) => {
    const userAccessToken = req.cookies['mouse-bb-token'];
    const eventsList = JSON.parse(req.body.mouseEvents);
    const list = [];
    for (let singleEvent of eventsList) {
        const userEvent = {
            x_cor: singleEvent.x_cor,
            y_cor: singleEvent.y_cor,
            event: singleEvent.event,
            time: singleEvent.event_time,
            x_res: singleEvent.x_res,
            y_res: singleEvent.y_res
        }
        list.push(userEvent);
    }

    utils.getCollectedData.has(userAccessToken) ? utils.getCollectedData.get(userAccessToken).push(...list) : utils.getCollectedData.set(userAccessToken, list);

    res.status(201).end();
});


router.post('/api/signup', async (req, res) => {
    const username = req.body.username;
    const password = req.body.password;

    const options = {
        method: 'POST',
        uri: process.env.API_ROUTE_USER_CREATE,
        headers: {
            'Content-Type': 'application/json'
        },
        body: {
            login: username,
            password: password
        },
        resolveWithFullResponse: true,
        json: true
    };

    try {
        const response = await request.post(options);
        if (response.statusCode === 201) {
            res.redirect(301, '/user/login.html');
        }
    } catch (err) {
        logger.error(`Signup error: ${err.message}`);
        if (err.statusCode === 400) {
            res.redirect(301, '/user/signup_error.html')
        }
    }
});


router.post('/api/login', async (req, res) => {
    const credentialLogin = req.body.username;
    const credentialPassword = req.body.password;

    const appClientId = process.env.API_CLIENT_ID;
    const appClientSecret = process.env.API_CLIENT_SECRET;

    const options = {
        method: 'POST',
        uri: process.env.API_ROUTE_TOKEN_GRANT,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Basic ' + Buffer.from(`${appClientId}:${appClientSecret}`).toString('base64')
        },
        form: {
            grant_type: "password",
            username: credentialLogin,
            password: credentialPassword
        },
        resolveWithFullResponse: true,
        json: true
    };

    try {
        const response = await request.post(options);

        if (response.statusCode === 200) {
            const jwtToken = response.body.access_token;
            const refreshToken = response.body.refresh_token;

            const accessTokenExpire = process.env.OAUTH2_TOKENEXPIREDTIME;
            const refreshTokenExpire = process.env.OAUTH2_REFRESHTOKENEXPIREDTIME;

            const accessCookieOptions = {
                maxAge: 1000 * accessTokenExpire,
                // sameSite: "strict",
                // // TODO
                // secure: true,
                httpOnly: true
            }

            const refreshCookieOptions = {
                maxAge: 1000 * refreshTokenExpire,
                // sameSite: "strict",
                // // TODO
                // secure: true,
                httpOnly: true
            }

            redisClient.setex(jwtToken, accessTokenExpire, response.body.jti);
            redisClient.setex(refreshToken, refreshTokenExpire, response.body.jti);

            res.cookie('mouse-bb-token', jwtToken, accessCookieOptions);
            res.cookie('mouse-bb-refresh-token', refreshToken, refreshCookieOptions);
            res.redirect(301, '/');
        }
    } catch (err) {
        logger.error(`Login Error: ${err.message}`)
        res.redirect(301, '/user/login_invalid.html')
    }
});


module.exports =
    {
        router: router
    };
