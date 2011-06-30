/**
 * 根据外尺寸设置（获取）对像外尺寸，或者取得对像外尺寸，不包括marging
 * 如果获取对像尺寸，并且如果对像为　：document时，将返回整个视口的宽高，并肯不支持设置
 */
(function($){
$.fn.extend({
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ start ~
    boxing: function(width, height){
        if (this.size() == 0) 
            return;
        if (this.size() > 0 && this[0] == document) {
            return $.viewport();
        }
        // 获取盒子内宽高
        if ("inner" == width) {
            return {
                width:  this[0].clientWidth,
                height: this[0].clientHeight
            };
        }
        // 获取盒子外宽高
        else 
            if (!width) {
                return {
                    width: this[0].offsetWidth,
                    height: this[0].offsetHeight
                };
            }
        if (this.size() == 0) 
            return;
        if (this[0].tagName == "TEXTAREA") {
            width -= 4;
            height -= 4;
        }
        return this.css({
            width: width,
            height: height
        });        
    }
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ end ~
});
})(window.jQuery)