const express = require('express')
const router = express.Router();
const logger = require('./loggerModule.js')

const HTTP_CREATED_201 = 201;
const HTTP_OK_200 = 200;

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

router.post('/register', (req, res) => {
    // TODO: user register
    console.log(req.body);
    res.setHeader('Content-Type', 'application/json');
    res.end();
});


router.post('/login', (req, res) => {
    //TODO: need endpoint for user verification
    const credentials = req.body.credentials;
    console.log(credentials.username);
    res.setHeader('Content-Type', 'application/json');
    res.status(HTTP_OK_200)
    res.end();
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
