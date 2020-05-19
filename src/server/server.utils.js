const request = require('request-promise');
const logger = require('../loggerModule.js');
let router = require('./server.routes.js');

let SEC = 5;
const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

const DOMAIN = 'mouse-bb-api'
const DATA_ENDPOINT = 'session/add'
const DOMAIN_LOCALHOST = 'localhost:9091';
const DATA_ENDPOINT_LOCALHOST = 'post-data'

const sendDataToAPI = () => {
    if (router.getCollectedData.length !== 0) {
        logger.info("Sending POST to API");

        const dataToSend = router.getCollectedData;
        router.setCollectedData = [];

        const options = {
            method: 'POST',
            // uri: 'http://' + DOMAIN + ".herokuapp.com/" + DATA_ENDPOINT,
            uri: 'http://' + DOMAIN_LOCALHOST + '/' + DATA_ENDPOINT_LOCALHOST,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + Buffer.from(`admin:admin`).toString('base64')
            },
            body: {
                sessions: dataToSend
            },
            resolveWithFullResponse: true,
            json: true
        };

        request.post(options)
            .then(response => {
                logger.info(`API Received data successfully with response status code: ${response.statusCode}`);
            })
            .catch(err => {
                logger.error(`An error has occurred – status code: ${err.statusCode}`);
                if (err.statusCode === undefined) {
                    logger.error(err);
                }
                if (err.statusCode === 401) {
                    logger.error(`User unauthorized`);
                }
            });
    }

    wait(SEC * 1000).then(sendDataToAPI);
    logger.info(`waiting ${SEC} sec`);
}

module.exports =
    {
        sendDataToAPI: sendDataToAPI,

        set timeout(val) {
            SEC = val;
        }
    };
