const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
let router = require('./server.routes.js');
const utils = require('./server.utils.js');


const app = express();

app.use(express.json());
app.use(cookieParser());
app.use(express.urlencoded({extended: true}));
app.use('/user', express.static(path.join(__dirname, "..", 'public', 'user')));
app.use('/api', router.router);
app.use(utils.authenticateWithToken, express.static(path.join(__dirname, "..", 'public')));

module.exports = app;
