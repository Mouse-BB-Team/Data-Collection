const dataSendTimeout = 1000 * 2;   //send data every dataSendTimeout*1[ms]
let mouseEvents = [];
let lastButtonState = 0;
let lastScrollState = document.scrollY;

const wait = ms => new Promise(resolve => setTimeout(resolve, ms));

document.addEventListener('mousedown', (e) => {
    const getMouseButtonEvent = (buttons) => {
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
        event_time: getEventTime(),
        x_res: window.screen.width,
        y_res: window.screen.height
    }
    mouseEvents.push(singleMouseEvent);
});

document.addEventListener('mouseup', (e) => {
    const getMouseButtonEvent = () => {
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
        event_time: getEventTime(),
        x_res: window.screen.width,
        y_res: window.screen.height
    }
    mouseEvents.push(clickEvent);
});

document.addEventListener('wheel', e => {
    const singleScrollEvent = {
        x_cor: e.clientX,
        y_cor: e.clientY,
        event: lastScrollState > this.scrollY ? "SCROLL_UP" : "SCROLL_DOWN",
        event_time: getEventTime(),
        x_res: window.screen.width,
        y_res: window.screen.height
    }
    mouseEvents.push(singleScrollEvent);
    lastScrollState = this.scrollY;
});

// document.addEventListener('mousemove', (e) => {
//     const mouseMoveEvent = {
//         x_cor: e.clientX,
//         y_cor: e.clientY,
//         event: 'MOVE',
//         event_time: getEventTime(),
//         x_res: window.screen.width,
//         y_res: window.screen.height
//     }
//     mouseEvents.push(mouseMoveEvent);
// });


const sendData = () => {
    if (mouseEvents.length !== 0) {
        const dataToSend = mouseEvents;
        mouseEvents = [];

        jQuery.ajax({
            url: '/api/store-data',
            type: 'post',
            data: {
                mouseEvents: JSON.stringify(dataToSend)
            }
        });
    }
    wait(dataSendTimeout).then(sendData);
}

sendData();
