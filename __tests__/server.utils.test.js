const utils = require('../src/server/server.utils.js');
const request = require('request-promise');
const router = require('../src/server/server.routes.js');

const mockedRequestPromisePostCall = jest.spyOn(request, 'post');

mockedRequestPromisePostCall.mockImplementation(async () => {
    return {
        fakedResponse: undefined
    }
});

describe('Test External API call', () => {
    let mouseEventList = [];
    beforeAll(() => {
        mouseEventList = [
            {
                x_cor: 100,
                y_cor: 200,
                event: "LEFT_DOWN",
                time: "2020-05-17 02:00:00.000",
                x_res: 1440,
                y_res: 900
            }];
        router.getCollectedData.set("token", mouseEventList);
    });

    afterEach(() => {
        jest.resetAllMocks();
    });

    afterAll(() => {
        router.getCollectedData.clear();
    });

    test('should make API call when array contains data', () => {
        utils.sendDataToAPI();
        expect(mockedRequestPromisePostCall).toBeCalled();
    });

    test('should not make API call with empty array', () => {
        router.getCollectedData.clear();

        utils.sendDataToAPI();
        expect(mockedRequestPromisePostCall).not.toBeCalled();
    });
});