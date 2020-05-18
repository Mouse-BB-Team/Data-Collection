const blur_max = 10

function blur(begin, end, element) {
    $({blurRadius: begin}).animate({blurRadius: end}, {
        duration: 500,
        easing: 'swing',

        step: function () {
            $(element).css({
                "-webkit-filter": "blur(" + this.blurRadius + "px)",
                "filter": "blur(" + this.blurRadius + "px)"
            });
        }
    });
}

$(".faq-button button").click(function () {
    hide(0)
    $(".right-panel").animate({"margin-right": '+=30%'});
    $(".main-content").css({"pointer-events": "none", "user-select": "none", "-webkit-user-select": "none"})
    blur(0, blur_max, '.main-content')
});

$(".right-panel .close").click(function () {

    $(".right-panel").animate({"margin-right": '-=30%'}, () => hide(0));
    $(".main-content").css({"pointer-events": "auto", "user-select": "auto", "-webkit-user-select": "auto"})
    blur(blur_max, 0, '.main-content')
});

function hide(time) {
    $(".right-panel .point .answer").slideUp(time);
}

$(".right-panel .question-list .point").each(function (index, element) {
        let question = $(this).find("div.question")
        let answer = $(this).find("div.answer")
        let indicator = $(this).find("div.indicator")
        $(question).click(function () {
            if($(answer).is(':visible')) {
                rotate(-90, 0, indicator, 300)
                $(answer).slideUp(300)
            }
            else {
                rotate(0, -90, indicator, 300)
                $(answer).slideDown(300)
            }
        })
})

function rotate(begin_angle, end_angle, element, duration) {

    $({deg: begin_angle}).animate({deg: end_angle}, {
        duration: duration,
        step: function(now) {
            element.css({
                transform: 'rotate(' + now + 'deg)'
            });
        }
    });
}