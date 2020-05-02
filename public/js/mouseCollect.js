const dataSendTimeout = 10000;   //send data every dataSendTimeout*1[ms]
let mouseEvents = [];

document.addEventListener('click', (e) => {
    let t = new Date();
    const clickEvent = {
        x_cor: e.clientX,
        y_cor: e.clientY,
        event: 'click',//e.button,
        time: `${t.getUTCFullYear()}-${zero_str_padding(t.getUTCMonth())}-${zero_str_padding(t.getUTCDay())} ${zero_str_padding(t.getUTCHours())}:${zero_str_padding(t.getUTCMinutes())}:${zero_str_padding(t.getUTCSeconds())}.${zero_str_padding_mls(t.getUTCMilliseconds())}`
    }
    mouseEvents.push(clickEvent);
    alert(e.clientX);
});

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

        });
}

sendData();

function zero_str_padding(t) {
    return String("0" + t).slice(-2);
}

function zero_str_padding_mls(t) {
    return String("00" + t).slice(-3);
}