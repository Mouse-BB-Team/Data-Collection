const app = require('../server.config.js');
const request = require('supertest');
const acquiredData = require('../server.routes.js');


describe("POST to /api/store-data", () => {
    let mouseEventList = [];

    beforeAll(() => {
        acquiredData.setCollectedData = [];
        mouseEventList = [
            {
                x_cor: 100,
                y_cor: 200,
                event: "LEFT_DOWN",
                time: "2020-05-17 02:00:00.000",
                x_res: 1440,
                y_res: 900
            },
            {
                x_cor: 100,
                y_cor: 200,
                event: "LEFT_UP",
                time: "2020-05-17 02:00:00.020",
                x_res: 1440,
                y_res: 900
            }];
    });


    test('should receive valid data form client-side', done => {
        request(app)
            .post('/api/store-data')
            .send({mouseEvents: JSON.stringify(mouseEventList)})
            .expect(201)
            .end((err, res) => {
                        if (err) return done(err);
                        console.log(res.statusCode)
                        done();
                    });
    });

    test('should return mouse data array length of 2', () => {
        expect(acquiredData.getCollectedData.length).toBe(2);
    });


    afterAll(() => {
        acquiredData.setCollectedData = [];
    });
});