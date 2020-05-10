const express = require('express');
const path = require('path');
const logger = require('./loggerModule.js');
let router = require('./routes.js');
const utilities = require('./utils.js');

const app = express();
app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, 'public')));
app.use('', router.router);

const PORT = process.env.PORT || 9090;

server = app.listen(PORT, () => {
    logger.info(`Server running on port ${PORT}`);
});

utilities.sendDataToAPI();
