const dataSendTimeout = 5000;   //send data every dataSendTimeout[ms]
let mouseEvents = []


sendData = function () {
    const dataToSend = mouseEvents;
    mouseEvents = [];

    jQuery.ajax({
        url: '/api/store-data',
        type: 'post',
        data: {
            mouseEvents: JSON.stringify(dataToSend)
        },
        success: () => {
            setTimeout(sendData, dataSendTimeout);
        }
    })
}