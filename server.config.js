const express = require('express');
const path = require('path');
let router = require('./server.routes.js');

const app = express();

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(express.static(path.join(__dirname, 'public')));
app.use('', router.router);

module.exports = app;
