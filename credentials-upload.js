let url = "localhost:8080/oauth/token"

let client_id = "client_id"
let secret = "password"

let username = "admin"
let password = "admin"

let authorizationHeader = btoa("Basic " + client_id + ":" + secret)

let details = {
    "grant_type": "password",
    "username": username,
    "password": password
};

let formBody = [];
for (let property in details) {
    let encodedKey = encodeURIComponent(property);
    let encodedValue = encodeURIComponent(details[property]);
    formBody.push(encodedKey + "=" + encodedValue);
}
formBody = formBody.join("&");


fetch(url, {
    method: "post",
    headers: {
        "Authorization": authorizationHeader,
        "Content-Type": "application/x-www-form-urlencoded",
    },
    body: formBody
}).then(response => console.log(response.status))

