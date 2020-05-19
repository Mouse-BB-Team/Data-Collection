const fetch = require("node-fetch");
const log4js = require('log4js');

log4js.configure({
    appenders: {
        everything: { type: 'file', filename: 'logs/performance-test-test.log'},
        console: { type: 'console' }
    },
    categories: {
        default: { appenders: [ 'everything' , 'console' ], level: 'debug'}
    }
});

const logger = log4js.getLogger('performance-test-test-logger')



function generateBasicAuthenticationHeader(client_id, secret) {
    return "Basic " + Buffer.from(client_id + ":" + secret).toString('base64')
}

async function getToken(login, password, auth_header, tokenUrl){

    let details = {
        'grant_type': 'password',
        'username': login,
        'password': password
    };

    let formBody = generate_X_WWW_FORM_URLENCODED(details)

    const tokenResponse = await fetch(tokenUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': auth_header
        },
        body: formBody
    });

    return tokenResponse.json()
}


function generate_X_WWW_FORM_URLENCODED(details){
    let formBody = [];
    for (let property in details) {
        let encodedKey = encodeURIComponent(property);
        let encodedValue = encodeURIComponent(details[property]);
        formBody.push(encodedKey + "=" + encodedValue);
    }
    return formBody.join("&");
}

async function refreshToken(refresh_token, auth_header, tokenUrl) {

    let details = {
        'grant_type': 'refresh_token',
        'refresh_token': refresh_token
    };

    let formBody = generate_X_WWW_FORM_URLENCODED(details)

    const tokenResponse = await fetch(tokenUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'Authorization': auth_header
        },
        body: formBody
    });

    return tokenResponse.json()
}

function sendRequests(interval_min, interval_max, count, access_token_val, refresh_token_val, login, tokenUrl, sessionUrl, authHeader) {

    let i = 0

    let current_token = access_token_val
    let current_refresh_token = refresh_token_val

    function pushData() {
        fetch(sessionUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + current_token
            },
            body: JSON.stringify(generateRandomBody())
        }).then(response => {
            if (response.status === 401) {
                logger.info("refreshing token...")
                refreshToken(current_refresh_token, authHeader, tokenUrl).then(result => {
                    current_token = result.access_token
                    current_refresh_token = result.refresh_token
                })
            } else {
                i++
                logger.info(login + " - no: " + i + " - status: " + response.status)
            }
        });
    }

    pushData()

    function recursiveTimeout(){

        let timeout = Math.floor(Math.random() * (interval_max - interval_min)) + interval_min;

        setTimeout(() => {
            pushData()
            count--
            if(count > 0){
                recursiveTimeout()
            }
        }, timeout)
    }

    recursiveTimeout()
}


function generateRandomBody() {

    let sessions = []

    for(let i = 0; i < Math.ceil(Math.random() * 100); i++){
        sessions.push({
            "x_cor": 10,
            "y_cor": 200,
            "event": "MOVE",
            "time": "2012-03-13 16:02:34.234",
            "x_res": 122,
            "y_res": 123
        })
    }

    return {
        sessions: sessions
    }
}


module.exports =
    {
        generateBasicAuthenticationHeader: generateBasicAuthenticationHeader,
        getToken: getToken,
        sendRequests: sendRequests
    };