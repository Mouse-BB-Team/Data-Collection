require('dotenv').config();
const express = require('express');
const logger = require('../loggerModule.js');
const request = require('request-promise');
const utils = require('./server.utils.js');

const router = express.Router();


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
        uri: 'https://mouse-bb-api.herokuapp.com/user/create',
        // uri: 'http://localhost:8080/user/create',
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
        uri: 'https://mouse-bb-api.herokuapp.com/oauth/token',
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
                maxAge: 1000 * response.body.expires_in,
                // sameSite: "strict",
                // // TODO
                // secure: true,
                httpOnly: true
            }

            utils.getRedisAgent.setex(jwtToken, response.body.expires_in, credentialLogin);

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
