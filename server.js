const express = require('express');
const path = require('path');

const app = express();

app.use(express.json());
app.use(express.urlencoded({ extended: true}));
app.use(express.static(path.join(__dirname, 'public')));

const collectedEventData = [];

app.post('/api/store-data', (req, res) => {
    const userEvent = {
        x_cor: req.body.x_cor,
        y_cor: req.body.y_cor,
        event: req.body.event,
        time: req.body.time
    }

    collectedEventData.push(userEvent);
    console.log(userEvent);
    res.status(201).end();
});

const PORT = process.env.PORT || 8080;

server = app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
