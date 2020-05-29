const faqPanelSlideOut = $(".faq-panel-slide-out")
const dimmedPanelDimmedBanner = $(".dimmed-panel.dimmed-banner")
const rightPanel =  $(".right-panel")

function slideInRightFaqPanel() {
    faqPanelSlideOut.css({"pointer-events": "auto"})
    hide(0)
    let width = rightPanel[0].offsetWidth

    console.log(width)
    rightPanel.animate({"margin-right": '+=' + width + 'px'});
}


$(".faq-button button").click(function () {
    slideInRightFaqPanel()
    $(".dimmed-panel.faq-dimmed").fadeIn();
});

$(".faq-banner-button").click(function () {
    slideInRightFaqPanel()
    $(".dimmed-banner").fadeIn();
})

function slideOutRightFaqPanel () {
    faqPanelSlideOut.css({"pointer-events": "none"})

    let width = rightPanel[0].offsetWidth

    rightPanel.animate({"margin-right": '-=' + width + 'px'}, () => {
        hide(0)
        rotate(-90, 0, $(".indicator"), 0)
    });
    $(".dimmed-panel.faq-dimmed").fadeOut();

    if (dimmedPanelDimmedBanner.css("display") === "block")
        dimmedPanelDimmedBanner.fadeOut();
}

$(".right-panel .close").click(slideOutRightFaqPanel);

faqPanelSlideOut.click(slideOutRightFaqPanel)

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