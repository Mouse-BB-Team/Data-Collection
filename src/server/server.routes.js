const express = require('express');
const logger = require('../loggerModule.js');
const request = require('request-promise');
const fs = require('fs');
const path = require('path');


const router = express.Router();

const HTTP_CREATED_201 = 201;

let collectedEventData = [];

router.post('/api/store-data', (req, res) => {
    const eventsList = JSON.parse(req.body.mouseEvents);
    for (let singleEvent of eventsList) {
        const userEvent = {
            x_cor: singleEvent.x_cor,
            y_cor: singleEvent.y_cor,
            event: singleEvent.event,
            time: singleEvent.event_time,
            x_res: singleEvent.x_res,
            y_res: singleEvent.y_res
        }
        collectedEventData.push(userEvent);
    }
    logger.info(`Got Post no ${collectedEventData.length}`);
    res.status(HTTP_CREATED_201).end();
});


router.post('/signup', (req, res) => {
    const login = req.body.login;
    const password = req.body.password;

    const options = {
        method: 'POST',
        uri: 'http://localhost:8080/user/create',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Basic ' + Buffer.from(`admin:admin`).toString('base64')
        },
        body: {
            "message": "singupMessage"
        },
        resolveWithFullResponse: true,
        json: true
    };
    res.setHeader('Content-Type', 'application/json');
    res.end();
});


router.post('/login', async (req, res) => {
    const credLogin = req.body.username;
    const credPassword = req.body.password;

    const appClientId = 'client_id';
    const appClientSecret = 'password';

    const options = {
        method: 'POST',
        uri: 'http://localhost:8080/oauth/token',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': 'Basic ' + Buffer.from(`${appClientId}:${appClientSecret}`).toString('base64')
        },
        form: {
            grant_type: "password",
            username: credLogin,
            password: credPassword
        },
        resolveWithFullResponse: true,
        json: true
    };

    try {
        const response = await request.post(options);
        const jwtToken = response.body.access_token;

        if (response.statusCode === 200) {
            const cookieOptions = {
                maxAge: 1000 * response.body.expires_in,
                // TODO: sameSite: "strict",
                // TODO: secure: true,
                httpOnly: true
            }
            res.cookie('mouse-bb-token', jwtToken, cookieOptions).redirect(301, '/');
        }
    } catch (err) {
        logger.error(`Login Error: ${err.message}`)
        res.redirect(301, '/login_invalid.html')
    }
});


module.exports =
    {
        router: router,
        get getCollectedData() {
            return collectedEventData
        },

        set setCollectedData(val) {
            collectedEventData = val;
        }
    };
