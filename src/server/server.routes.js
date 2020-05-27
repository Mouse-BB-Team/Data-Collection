const express = require('express');
const request = require('request-promise');
const logger = require('../loggerModule.js');
const utils = require('./server.utils.js');
const redisClient = require('../redis.config.js');

const router = express.Router();

// NOTE: These routes are "api" routes,
// therefore express app is configured to use it as !!!"/api/:'endpoint'"!!!

router.get('/health', (req, res) => res.status(200).end());

router.post('/store-data', utils.authenticateWithToken, (req, res) => {
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

router.post('/signup', async (req, res) => {
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


router.post('/login', async (req, res) => {
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

            const cookieOptions = {
                maxAge: 1000 * (response.body.expires_in + 20),
                // sameSite: "strict",
                // // TODO
                // secure: true,
                httpOnly: true
            }

            redisClient.setex(jwtToken, response.body.expires_in, response.body.jti);
            redisClient.setex(`refresh:${jwtToken}`, (response.body.expires_in + 20), response.body.refresh_token);

            res.cookie('mouse-bb-token', jwtToken, cookieOptions).redirect(301, '/');
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
