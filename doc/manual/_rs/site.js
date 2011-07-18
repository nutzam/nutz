var _INDEXES = {};

function doScrollToHead(txt) {
    // 在 #arena 中寻找所有的 h1,h2，找到第一个符合的
    var heads = $("#arena h1, #arena h2");
    var head = null;
    for (var i = 0; i < heads.size(); i++) {
        head = heads[i];
        if ($(head).text() == txt) 
            break;
    }
    if (head) {
        var off = $(head).offset();
        window.scrollBy(0, off.top - window.pageYOffset - 30);
    }
}

function doLoadPage(href) {
    var pos = href.indexOf("#");
    var an = pos > 0 ? href.substring(pos + 1) : null;
    href = pos > 0 ? href.substring(0, pos) : href;
    // 首先在 #nav 中寻找找个 a
    var ele = _INDEXES[href];
    if (ele) {
        // 如果找到了，则显示它的父亲
        var li = $(ele).parents("li.top");
        $("#nav li.current").removeClass("current");
        li.addClass("current");
        
        // 高亮 #sky 上对应的项目
        var chName = li.children(".zdoc_folder").find("b").text();
        $("#sky .hlt").removeClass("hlt");
        $("#sky a").each(function(index, ele) {
            if ($(ele).text() == chName) {
                $(ele).parent().addClass("hlt");
            }
        });
        
        // 接着尝试高亮这个 A 所在的 LI
        $("#nav .hlt").removeClass("hlt");
        var myli = $(ele).parent().parent().addClass("hlt");
        // 清除页内索引
        $("#nav .inner_anchors").remove();
        
        // 然后尝试加在相应网页
        $("#arena").load(href, function() {
            // 读取所有的 H1
            var headers = $("#arena h1, #arena h2");
            if (headers.size() > 0) {
                var div = $('<div class="inner_anchors"></div>').appendTo(myli);
                headers.each(function(index, ele) {
                    var nm = ele.tagName.toLowerCase();
                    $('<div class="anchor ' + nm + '"><b>' + $(ele).text() + "</b></div>").appendTo(div).data("ta", ele);
                });
                window.scrollBy(window.pageXOffset * -1, window.pageYOffset * -1);
                if (an) {
                    doScrollToHead(an);
                }
            }
        });
    }
}

function onClickInnerLink() {
    var txt = $(this).text();
    doScrollToHead(txt);
}

function onClickLink() {
    doLoadPage(pgan(this.href));
}

function main() {
    //........................................................................
    // 首先 copy 索引到 #nav
    $("#nav").html($("#indexes").html());
    // 修改 nav 所有的 href，并将其存入索引
    $("#nav a").each(function(index, ele) {
        var href = $(ele).attr("href");
        _INDEXES[href] = ele;
        $(ele).attr("href", "#" + href);
    });
    
    //........................................................................
    // 然后将所有的第一层，作为 #sky，链接为第一个子项目
    var html = "<ul>";
    $("#nav .zdoc_index_table").children("li").addClass("top").each(function(index, ele) {
        var txt = $(ele).children(".zdoc_folder").find("b").text();
        var an = $(ele).children("ul").find("a");
        html += '<li><a href="' + an.attr("href") + '">' + txt + '</a></li>';
    });
    html += "</ul>";
    $("#sky").html(html);
    
    //........................................................................
    // 捕获所有的 a 的点击事件
    $("a").live("click", onClickLink);
    $("#nav .inner_anchors b").live("click", onClickInnerLink);
    
    //........................................................................
    // 修改 #sky 大小
    adjustLayout();
    $(window).resize(function() {
        adjustLayout();
    });
    
    //........................................................................
    // 默认点击
    var href = pgan();
    if (href) 
        doLoadPage(href);
	else{
		$("#sky li:first-child a").click();
	}
}

(function($) {
    $(document.body).ready(main);
})(window.jQuery);

function pgan(lo) {
    var lo = lo ? lo : "" + unescape(window.location.href);
    var pos = lo.indexOf("#");
    return pos>0?lo.substring(pos + 1):"";
}

function adjustLayout() {
    var box = $("#arena").boxing();
    $("#sky").width(box.width);
}
