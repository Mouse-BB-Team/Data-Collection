const express = require('express');
const path = require('path');
const request = require('request-promise');

const app = express();

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, 'public')));

let collectedEventData = [];

// const DOMAIN = "mouse-bb-api";
const MOCK_API_DOMAIN = "mouse-bb-api-mock";
const DATA_ENDPOINT = "post-data"

sendDataToAPI = function() {
    console.log("Sending POST to API");
    const dataToSend = collectedEventData;
    collectedEventData = [];
    const options = {
        method: 'POST',
        // uri: "https://mouse-bb-api-mock.herokuapp.com/post-data",    //Działa
        uri: 'https://' + MOCK_API_DOMAIN + ".herokuapp.com/" + DATA_ENDPOINT,
        body: {
            sessions: dataToSend
        },
        json: true
    };
    request(options)
        .then(response => {
            console.log(`API Received data successfully with code: ${response.statusCode}`);
            // if (response.statusCode === 201) {
            //
            // }
        })
        .catch(err => {
            console.log(`An error has occurred with code: ${err.statusCode}`);
        });
}


app.post('/api/store-data', (req, res) => {
    const eventsList = JSON.parse(req.body.mouseEvents);
    for (let singleEvent of eventsList) {
        const userEvent = {
            x_cor: singleEvent.x_cor,
            y_cor: singleEvent.y_cor,
            event: singleEvent.event,
            event_time: singleEvent.event_time
        }
        collectedEventData.push(userEvent);
    }
    console.log(`Got Post no ${collectedEventData.length}`);
    setTimeout(sendDataToAPI, 10000);
    res.status(201).end();
});

app.get('/api/get-data', (req, res) => {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(collectedEventData));
});


app.post('/login', (req, res) => {
    res.end();
});

const PORT = process.env.PORT || 9090;

server = app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
