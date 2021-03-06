const request_promise = require('request-promise');
const request = require('supertest');
const utils = require('../src/server/server.utils.js');
const app = require('../src/server/server.config.js');
const MockDate = require('mockdate');
const redisClient = require('../src/redis.config.js');

describe("POST to /api/store-data", () => {
    let mouseEventList = [];
    let PROMISE_MOCK_PASS_FLAG;
    const mockedRequestPromisePostCall = jest.spyOn(request_promise, 'post');

    beforeAll(() => {
        utils.getCollectedData.clear();
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
        PROMISE_MOCK_PASS_FLAG = true;
        mockedRequestPromisePostCall.mockImplementation(async () => {
            return new Promise((resolve, reject) => {
                return PROMISE_MOCK_PASS_FLAG ? resolve({statusCode: 200}) : reject({statusCode: 400});
            });
        });

        request(app)
            .post('/api/store-data')
            .set('Cookie', ['mouse-bb-token=token'])
            .send({mouseEvents: JSON.stringify(mouseEventList)})
            .expect(201)
            .end((err, res) => {
                if (err) return done(err);
                console.log(res.statusCode)
                done();
            });
    });

    test('should return mouse data array size of 2', () => {
        expect(utils.getCollectedData.size).toBe(1);
    });


    afterAll(() => {
        utils.getCollectedData.clear();
        jest.resetAllMocks();
    });
});


describe('POST request to /api/login endpoint', () => {
    let PROMISE_MOCK_PASS_FLAG;
    const mockedRequestPromisePostCall = jest.spyOn(request_promise, 'post');
    const redisMock = jest.spyOn(redisClient, 'setex');


    beforeAll(() => {
        redisMock.mockImplementation(() => true);
        mockedRequestPromisePostCall.mockImplementation(async () => {
            return new Promise((resolve, reject) => {
                if (PROMISE_MOCK_PASS_FLAG) {
                    return resolve({
                        body: {
                            access_token: "token",
                            expires_in: 200,
                            refresh_token: "refresh_token"
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
        MockDate.set('2020-06-17'); // Mock data to be 17 May 2020 00:03:20 GMT-0200
    });

    afterEach(() => {
        MockDate.reset();
    });


    test('should receive valid username and password', done => {
        PROMISE_MOCK_PASS_FLAG = true;

        const username = 'windykator';
        const password = "windykator1";

        request.agent(app)
            .post('/api/login')
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
        const getFormattedTime = expTime => {
            const zero_str_padding = t => {
                return String("0" + t).slice(-2);
            }
            const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
            const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec'];
            const date = Date.now() + (expTime * 1000);
            const t = new Date(date);
            return `${days[t.getDay()]}, ${t.getDate()} ${months[t.getMonth()]} ${t.getUTCFullYear()} ${zero_str_padding(t.getUTCHours())}:${zero_str_padding(t.getMinutes())}:${zero_str_padding(t.getSeconds())} GMT`;
        }

        request.agent(app)
            .post('/api/login')
            .expect('set-cookie', `mouse-bb-token=token; Max-Age=${process.env.OAUTH2_TOKENEXPIREDTIME}; Path=/; Expires=${getFormattedTime(process.env.OAUTH2_TOKENEXPIREDTIME)}; HttpOnly,mouse-bb-refresh-token=refresh_token; Max-Age=${process.env.OAUTH2_REFRESHTOKENEXPIREDTIME}; Path=/; Expires=${getFormattedTime(process.env.OAUTH2_REFRESHTOKENEXPIREDTIME)}; HttpOnly`, done);
    });

    test('should verify that api has been called', () => {
        expect(mockedRequestPromisePostCall).toBeCalled();
    });

    test('should cause error with bad cred', done => {
        PROMISE_MOCK_PASS_FLAG = false;

        request.agent(app)
            .post('/api/login')
            .send({
                username: undefined,
                password: undefined
            })
            .expect(301, "Moved Permanently. Redirecting to /user/login_invalid.html")
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


describe('POST request to /api/signup', () => {
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
            .post('/api/signup')
            .send({
                username: username,
                password: password
            })
            .expect(301, "Moved Permanently. Redirecting to /user/login.html")
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
            .post('/api/signup')
            .send({
                username: undefined,
                password: undefined
            })
            .expect(301, "Moved Permanently. Redirecting to /user/signup_error.html")
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


describe('GET form health endpoint', () => {
    it('should respond with 200 OK', done => {
        request(app)
            .get('/api/health')
            .expect(200)
            .end((err, res) => {
                if (err) return done(err);
                done();
            });
    });
});
