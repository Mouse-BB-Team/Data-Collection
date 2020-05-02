const express = require('express');
const path = require('path');

const app = express();

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, 'public')));

const collectedEventData = [];

app.post('/api/store-data', (req, res) => {
    const eventsList = JSON.parse(req.body.mouseEvents);
    for (let singleEvent of eventsList) {
        const userEvent = {
            x_cor: singleEvent.x_cor,
            y_cor: singleEvent.y_cor,
            event: singleEvent.event,
            time: singleEvent.time
        }
        collectedEventData.push(userEvent);
    }
    console.log(`Got Post no ${collectedEventData}`)
    res.status(201).end();
});

app.get('/api/get-data', (req, res) => {
    res.setHeader('Content-Type', 'application/json');
    res.end(JSON.stringify(collectedEventData));
});

const PORT = process.env.PORT || 8080;

server = app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
