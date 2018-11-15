/**
 * 拖拽元素
 * @constructor
 */
function movePage(selector) {
    $(selector).mousedown(function (event) {
        event.stopPropagation();
        var offset = $(this).offset();
        var devX = event.pageX - offset.left;
        var devY = event.pageY - offset.top;
        //当鼠标移动时，div元素跟着鼠标移动
        $(this).bind("mousemove",function (event) {
            $(this).offset({
                top:event.pageY - devY,
                left:event.pageX - devX
            });
        });
        //当鼠标按钮被松开时，结束mousermove事件
        $(this).bind("mouseup",function () {
            $(this).unbind("mousemove");
        });
    });
    $(selector).children().bind("mousedown",function (event) {
       event.stopPropagation();
    });
}