const app = require('../src/server/server.config.js');
const request = require('supertest');
const acquiredData = require('../src/server/server.routes.js');
const request_promise = require('request-promise');
const MockDate = require('mockdate');

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


describe('POST request to /login endpoint', () => {
    let PROMISE_MOCK_PASS_FLAG;
    const mockedRequestPromisePostCall = jest.spyOn(request_promise, 'post');

    beforeAll(() => {
        mockedRequestPromisePostCall.mockImplementation(async () => {
            return new Promise((resolve, reject) => {
                if (PROMISE_MOCK_PASS_FLAG) {
                    return resolve({
                        body: {
                            access_token: "token",
                            expires_in: 200
                        },
                        statusCode: 200
                    })
                } else {
                    return reject({statusCode: 400});
                }
            });
        });
    });

    beforeEach(() => {
        MockDate.set('2020-05-17'); // Mock data to be Sun May 2020 02:00:00 GMT-0200
    });

    afterEach(() => {
        MockDate.reset();
    });


    test('should receive valid username and password', done => {
        PROMISE_MOCK_PASS_FLAG = true;

        const username = 'windykator';
        const password = "windykator1";

        request.agent(app)
            .post('/login')
            .send({
                username: username,
                password: password
            })
            .expect(301, "Moved Permanently. Redirecting to /")
            .end((err, res) => {
                if (err) return done(err);
                done();
            });
    });

    test('should set cookie', done => {
        request.agent(app)
            .post('/login')
            // .expect('set-cookie', 'mouse-bb-token=token; Max-Age=200; Path=/; Expires=Sun, 17 May 2020 00:03:20 GMT; HttpOnly', done);
            //TODO: release
            .expect('set-cookie', 'mouse-bb-token=token; Max-Age=200; Path=/; Expires=Sun, 17 May 2020 00:03:20 GMT; HttpOnly; Secure; SameSite=Strict', done);

    });

    test('should verify that api has been called', () => {
        expect(mockedRequestPromisePostCall).toBeCalled();
    });

    test('should cause error with bad cred', done => {
        PROMISE_MOCK_PASS_FLAG = false;

        request.agent(app)
            .post('/login')
            .send({
                username: undefined,
                password: undefined
            })
            .expect(301, "Moved Permanently. Redirecting to /login_invalid.html")
            .end((err, res) => {
                if (err) return done(err);
                done();
            });
    });

    test('should verify that api has been called with error', () => {
        expect(mockedRequestPromisePostCall).toBeCalledTimes(3);
    });

    afterAll(() => {
        jest.resetAllMocks();
    });
});


describe('POST request to /signup', () => {
    let PROMISE_MOCK_PASS_FLAG;
    const mockedRequestPromisePostCall = jest.spyOn(request_promise, 'post');

    beforeAll(() => {
        mockedRequestPromisePostCall.mockImplementation(async () => {
            return new Promise((resolve, reject) => {
                return PROMISE_MOCK_PASS_FLAG ? resolve({statusCode: 201}) : reject({statusCode: 400});
            });
        });
    });

    test('should successfully register user', done => {
        PROMISE_MOCK_PASS_FLAG = true;

        const username = 'windykator';
        const password = "windykator1";

        request(app)
            .post('/signup')
            .send({
                username: username,
                password: password
            })
            .expect(301, "Moved Permanently. Redirecting to /login.html")
            .end((err, res) => {
                if (err) return done(err);
                done();
            });
    });

    test('should verify that api has been called', () => {
        expect(mockedRequestPromisePostCall).toBeCalled();
    });

    test('should receive error and redirect to signup_error page', done => {
        PROMISE_MOCK_PASS_FLAG = false;

        request(app)
            .post('/signup')
            .send({
                username: undefined,
                password: undefined
            })
            .expect(301, "Moved Permanently. Redirecting to /signup_error.html")
            .end((err, res) => {
                if (err) return done(err);
                done();
            });
    });

    test('should verify that api has been called with error', () => {
        expect(mockedRequestPromisePostCall).toBeCalledTimes(2);
    });


    afterAll(() => {
        jest.resetAllMocks();
    });
});
