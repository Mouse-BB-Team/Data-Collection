const dataSendTimeout = 1000;   //send data every dataSendTimeout*1[ms]
let mouseEvents = [];
let lastButtonState = 0;
let lastScrollState = document.scrollY;

document.addEventListener('mousedown', (e) => {
    function getMouseButtonEvent(buttons) {
        switch (buttons) {
            case 0:
                return "UNDEFINED";
            case 1:
                return "LEFT_DOWN";
            case 2:
                return "RIGHT_DOWN";
            case 3:
                return "LEFT_RIGHT_DOWN";
            case 4:
                return "SCROLL_PUSH";
        }
    }

    lastButtonState = e.buttons;
    const singleMouseEvent = {
        x_cor: e.clientX,
        y_cor: e.clientY,
        event: getMouseButtonEvent(lastButtonState),
        event_time: getEventTime()
    }
    mouseEvents.push(singleMouseEvent);
});

document.addEventListener('mouseup', (e) => {
    function getMouseButtonEvent() {
        switch (lastButtonState) {
            case 0:
                return "UNDEFINED";
            case 1:
                return "LEFT_UP";
            case 2:
                return "RIGHT_UP";  // TODO: problem detecting RIGHT_UP due to contextmenu pop-up
            case 3:
                return "LEFT_RIGHT_UP";
            case 4:
                return "SCROLL_PULL";
        }
        lastButtonState = 0;
    }

    const clickEvent = {
        x_cor: e.clientX,
        y_cor: e.clientY,
        event: getMouseButtonEvent(),
        event_time: getEventTime()
    }
    mouseEvents.push(clickEvent);
});

document.addEventListener('wheel', e => {
    const singleScrollEvent = {
        x_cor: e.clientX,
        y_cor: e.clientY,
        event: lastScrollState > this.scrollY ? "SCROLL_UP" : "SCROLL_DOWN",
        event_time: getEventTime()
    }
    mouseEvents.push(singleScrollEvent);
    lastScrollState = this.scrollY;
});

document.addEventListener('mousemove', (e) => {
    const clickEvent = {
        x_cor: e.clientX,
        y_cor: e.clientY,
        event: 'MOVE',
        event_time: getEventTime()
    }
    mouseEvents.push(clickEvent);
});


function sendData() {
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

function getEventTime() {
    const zero_str_padding = t => {
        return String("0" + t).slice(-2);
    }
    const zero_str_padding_mls = t => {
        return String("00" + t).slice(-3);
    }

    const t = new Date();
    return `${t.getFullYear()}-${zero_str_padding(t.getMonth() + 1)}-${zero_str_padding(t.getDate())} ${zero_str_padding(t.getHours())}:${zero_str_padding(t.getMinutes())}:${zero_str_padding(t.getSeconds())}.${zero_str_padding_mls(t.getMilliseconds())}`
}


// document.addEventListener('click', (e) => {
//     const clickEvent = {
//         x_cor: e.clientX,
//         y_cor: e.clientY,
//         event: 'click',//e.button,
//         event_time: getEventTime()
//     }
//     mouseEvents.push(clickEvent);
// });

// document.addEventListener('contextmenu', (e) => {
//     e.preventDefault();
// });


// document.addEventListener('click', (e) => {
//     alert(`Clicked! Context Menu opened: ${isContextMenuOpened}`)
//     if (isContextMenuOpened) {
//         const clickEvent = {
//             x_cor: e.clientX,
//             y_cor: e.clientY,
//             event: 'RIGHT_UP',//e.button,
//             event_time: getEventTime()
//         }
//         mouseEvents.push(clickEvent);
//         isContextMenuOpened = false;
//     }
// });



