const request = require('request-promise');
const logger = require('../loggerModule.js');
let router = require('./server.routes.js');

let SEC = 5;
const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

const sendDataToAPI = () => {
    if (router.getCollectedData.size !== 0) {
        logger.info("Sending POST to API");

        router.getCollectedData.forEach(async (data_list, token) => {
            logger.warn(token);
            logger.warn(data_list);
            const options = {
                method: 'POST',
                uri: 'https://mouse-bb-api.herokuapp.com/session/add',
                // uri: 'http://localhost:9091/post-data',
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
                await request.post(options);
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

        router.getCollectedData.clear();
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
