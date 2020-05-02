const express = require('express');
const path = require('path');

const app = express();

app.use(express.static(path.join(__dirname, 'public')));

const PORT = process.env.PORT || 8080;

server = app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
