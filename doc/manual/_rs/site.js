var home = [];
var roots = [];

function doChangeChannel(num) {
    var li = $(this).parents("li");
    var ul = li.children("ul");
    // 将索引内容加入左侧导航条
    $("#nav").html("<ul>" + ul.html() + "</ul>");
    // 高亮当前项目
    $("#sky .hlt").removeClass("hlt");
    li.addClass("hlt");
    // 修改左侧所有链接
    var ch = $(this).attr("href");
    $("#nav a").each(function(index, ele) {
        var href = $(ele).attr("href");
        var num = $(ele).prev().text();
        $(ele).attr("href", ch + ":n" + num).attr("dest", href);
    });
    // 修改左侧所有 .num
    $("#nav .num").each(function(index, ele) {
        $(ele).addClass("n" + $(ele).text().replace(/[.]/g, "_"));
    });
    // 模拟点击
    if (num) 
        var jNum = $("#nav ." + num.replace(/[.]/g, "_"));
    if (jNum && jNum.size() > 0) {
        onClickNavItem.apply(jNum.next());
    } else {
        onClickNavItem.apply($("#nav a:first"));
    }
}

function onClickAnchor() {
    var ta = $(this).data("ta");
    ta.scrollIntoView();
}

function onClickNavItem() {
    $("#nav .inner_anchors").remove();
    $("#nav .hlt").removeClass("hlt");
    var li = $($(this).parents("li")[0]).addClass("hlt");
    var href = $(this).attr("dest");
    // 读取内容
    $("#arena").load(href, function() {
        // 读取所有的 H1
        var headers = $("#arena h1, #arena h2");
        if (headers.size() > 0) {
            var div = $('<div class="inner_anchors"></div>').appendTo(li);
            headers.each(function(index, ele) {
                var nm = ele.tagName.toLowerCase();
                $('<div class="anchor ' + nm + '"><b>' + $(ele).text() + "</b></div>").appendTo(div).data("ta", ele);
            });
        }
    });
}

function onClickSkyItem() {
    doChangeChannel.apply(this);
}

function main() {
    //........................................................................
    // 获取所有根节点
    $("#indexes .zdoc_index_table").children().each(function(index, ele) {
        if ($(ele).children(".zdoc_folder").size() > 0) 
            roots.push($(ele));
        else 
            home.push($(ele));
    });
    //........................................................................
    // 显示所有的根节点
    var sky = $("#sky")
    var skyUl = $("<ul></ul>").appendTo(sky);
    // 增加 HOME
    var homeLi = $('<li class="sky_menu_li"><div class="zdoc_folder sky_menu_div"><span class="num">0</span><b>Home</b></div></li>')
    var homeUl = $("<ul></ul>").appendTo(homeLi);
    for (var i = 0; i < home.length; i++) {
        home[i].appendTo(homeUl);
    }
    homeLi.appendTo(skyUl);
    for (var i = 0; i < roots.length; i++) {
        roots[i].addClass("sky_menu_li").appendTo(skyUl);
    }
    // 修改链接形式
    $("#sky .sky_menu_li").each(function(index, ele) {
        var li = $(ele);
        var div = li.addClass("sky_menu_li").children(".zdoc_folder").addClass("sky_menu_div");
        var num = $(".num", div).text();
        div.addClass("c" + num);
        div.html('<a href="#c' + num + '">' + $("b", div).text() + '</a>');
    });
    //........................................................................
    // 绑定点击事件
    $("#sky .sky_menu_div a").click(onClickSkyItem);
    $("#nav a").live("click", onClickNavItem);
    $("#nav .hlt .anchor").live("click", onClickAnchor);
    $(window).resize(function() {
        adjustLayout();
    });
    
    //........................................................................
    // 修改 #sky 大小
    adjustLayout();
    
    //........................................................................
    // 模拟点击
    var an = pgan();
    if (an.ch) {
        doChangeChannel.apply($("#sky ." + an.ch + " a"), [an.link]);
    } else {
        $("#sky .sky_menu_div:first a").click();
    }
    
}

(function($) {
    $(document.body).ready(main);
})(window.jQuery);

function pgan() {
    var lo = "" + window.location.href;
    var pos = lo.indexOf("#");
    if (pos < 0) 
        return {
            ch: null,
            link: null
        };
    var str = lo.substring(pos + 1);
    pos = str.indexOf(":");
    if (pos < 0) 
        return {
            ch: str,
            link: null
        }
    return {
        ch: str.substring(0, pos),
        link: str.substring(pos + 1)
    }
}

function adjustLayout() {
    var box = $("#arena").boxing();
    $("#sky").width(box.width);
}