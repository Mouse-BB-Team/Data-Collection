const termCookieName = "terms-accepted"
const termCookieValue = "true"
const termCookieParameters = "SameSite=Strict"


$(function () {
    let termsCookie = getCookie(termCookieName)

    if(termsCookie === null || termsCookie.value !== termCookieValue)
        $(".dimmed-panel.banner-panel").fadeIn();
})


$(".agree-banner-button").click(function () {
    $(".dimmed-panel.banner-panel").fadeOut();
    document.cookie = termCookieName + "=" + termCookieValue + ";" + termCookieParameters
})

function getCookie(name) {
    let cookies = document.cookie.split(';')

    let desiredCookie;

    for (let i = 0; i < cookies.length; i++){
        let cookieName = cookies[i].split('=')[0]
        if (cookieName === name) {
            desiredCookie = cookies[i]
            break
        }
    }

    if(desiredCookie === null || desiredCookie === undefined)
        return null

    let splitCookie = desiredCookie.split('=')

    return {name: splitCookie[0], value: splitCookie[1]}
}