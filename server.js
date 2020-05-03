const express = require('express');
const path = require('path');
const request = require('request-promise');

const app = express();

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, 'public')));

let collectedEventData = [];

const DOMAIN = "mouse-bb-api";

sendDataToAPI = function() {
    const dataToSend = collectedEventData;
    collectedEventData = [];
    const options = {
        method: 'POST',
        uri: "localhost:8080/session/add",
        // uri: 'https://' + DOMAIN + ".herokuapp.com",
        body: {
            sessions: dataToSend
        },
        json: true
    };
    request(options)
        .then(response => {
            console.log(response.status);
            // if (response.status === 201) {
            //
            // }
        })
        .catch(err => {
            console.log(`An error has occured ${err}`);
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
    // setTimeout(sendDataToAPI, 10000);
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
