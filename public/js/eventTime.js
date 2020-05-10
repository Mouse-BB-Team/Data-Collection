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

module.exports = getEventTime;