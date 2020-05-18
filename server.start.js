const app = require('./server.config.js');
const logger = require('./loggerModule.js');
const utilities = require('./server.utils.js');

const PORT = process.env.PORT || 9090;

server = app.listen(PORT, () => {
    logger.info(`Server running on port ${PORT}`);
});

utilities.sendDataToAPI();
