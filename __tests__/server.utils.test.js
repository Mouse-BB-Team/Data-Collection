const utils = require('../src/server/server.utils.js');
const request = require('request-promise');
const app = require('../src/server/server.config.js');
const supertest = require('supertest');


describe('Test External API call', () => {
    let PROMISE_MOCK_PASS_FLAG;
    const mockedRequestPromisePostCall = jest.spyOn(request, 'post');

    let mouseEventList = [];
    beforeAll(() => {
        mockedRequestPromisePostCall.mockImplementation(async () => {
            return new Promise((resolve, reject) => {
                return PROMISE_MOCK_PASS_FLAG ? resolve({statusCode: 201}) : reject({statusCode: 400});
            });
        });

        mouseEventList = [
            {
                x_cor: 100,
                y_cor: 200,
                event: "LEFT_DOWN",
                time: "2020-05-17 02:00:00.000",
                x_res: 1440,
                y_res: 900
            }];
        utils.getCollectedData.set("token", mouseEventList);
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    afterAll(() => {
        utils.getCollectedData.clear();
    });

    test('should make API call when array contains data', () => {
        PROMISE_MOCK_PASS_FLAG = true;
        utils.sendDataToAPI();
        expect(mockedRequestPromisePostCall).toBeCalled();
    });

    test('should not make API call with empty array', () => {
        utils.getCollectedData.clear();

        utils.sendDataToAPI();
        expect(mockedRequestPromisePostCall).not.toBeCalled();
    });
});


describe('Test for user authentication with JWT middleware', () => {
    let PROMISE_MOCK_PASS_FLAG;
    const mockedRequestPromisePostCall = jest.spyOn(request, 'post');

    beforeAll(() => {
        mockedRequestPromisePostCall.mockImplementation(async () => {
            return new Promise((resolve, reject) => {
                return PROMISE_MOCK_PASS_FLAG ? resolve({statusCode: 200}) : reject({statusCode: 400});
            });
        });
    });

    test('should return error during token validation', done => {
        PROMISE_MOCK_PASS_FLAG = false;

        supertest(app)
            .get('/')
            .set('Cookie', ['mouse-bb-token=token'])
            .expect(301, "Moved Permanently. Redirecting to /user/login.html")
            .end((err, res) => {
                if (err) return done(err);
                console.log(res.statusCode)
                done();
            });
    });

    test('should pass JWT validation', done => {
        PROMISE_MOCK_PASS_FLAG = true;

        supertest(app)
            .get('/')
            .set('Cookie', ['mouse-bb-token=token'])
            .expect(200)
            .end((err, res) => {
                if (err) return done(err);
                console.log(res.statusCode)
                done();
            });
    });

    test('should if token is not null', done => {
        supertest(app)
            .get('/')
            // .set('Cookie', ['mouse-bb-token=token']) // no cookie
            .expect(301, "Moved Permanently. Redirecting to /user/login.html")
            .end((err, res) => {
                if (err) return done(err);
                console.log(res.statusCode)
                done();
            });
    });

    afterAll(() => {
        jest.resetAllMocks();
    });
});
