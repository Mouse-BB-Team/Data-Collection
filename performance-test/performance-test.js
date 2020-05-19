let sendData = require('./data-sender.js');

const tokenUrl = "https://mouse-bb-api.herokuapp.com/oauth/token"
const sessionUrl = "https://mouse-bb-api.herokuapp.com/session/add"

const client_id = "client_id"
const secret = "password"

let clients = [
    {
        login: "admin",
        password: "admin"
    }
    // },
    // {
    //     login: "windykator123",
    //     password: "windykator123"
    // }
]


function run(clients_list, interval_min, interval_max, count) {

    let authHeader = sendData.generateBasicAuthenticationHeader(client_id, secret)

    for (let i = 0; i < clients_list.length; i++) {
        new Promise((resolve, reject) => {
            sendData.getToken(clients_list[i].login, clients_list[i].password, authHeader, tokenUrl).then(response => {
                sendData.sendRequests(interval_min, interval_max, count, response.access_token, response.refresh_token, clients_list[i].login, tokenUrl, sessionUrl, authHeader)
            })
        }).then()
    }
}

run(clients, 20000, 20000, 12)