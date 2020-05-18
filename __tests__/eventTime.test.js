const eventTime = require('../public/js/eventTime.js');
const MockDate = require('mockdate');

describe('Time format test setup', () => {
    beforeEach(() => {
       MockDate.set('2020-05-17'); // Mock data to be Sun May 2020 02:00:00 GMT-0200
    });
    afterEach(() => {
        MockDate.reset();
    });

    test('Should return event time with correct format', () => {
        expect(eventTime()).toBe("2020-05-17 02:00:00.000");
    });
});
