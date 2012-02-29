// 增加一层 ...
function nav_add(ele, ciName, objs) {
    var jNav = $("#nav");

    // 移除临时块
    $(".nav_transient", jNav).remove();

    var jCrumb = $("#nav_crumb");
    var jScroller = $("#nav_scroller");
    var jCi = $('<div class="nav_crumb_item"></div>').appendTo(jCrumb);
    var jBlock = $('<div class="nav_block"></div>').appendTo(jScroller);

    // 调整布局
    ajustLayout();

    // 记录
    jCi.text(ciName).data("jq-ul", ele);

    // 写入数据到块
    var pgan = z.pgan() || "";
    var pos = pgan ? pgan.lastIndexOf("!") : -1;
    if(pos > 0) {
        pgan = pgan.substring(0, pos);
    }
    for(var i = 0; i < objs.length; i++) {
        var obj = objs[i];
        // 加入块
        var html = "";
        // 目录
        if(obj.type == "folder") {
            html += '<a class="nav_folder">' + obj.text + '</a>';
        }
        // 文章
        else if(obj.type == "doc") {
            html += '<a class="nav_doc" href="#' + obj.href + '">' + obj.text + '</a>';
        }
        // 文章内索引
        else if(obj.type == "indoc") {
            html += '<a class="nav_indoc" href="#' + obj.href + '">' + obj.text + '</a>';
        }
        // 错误
        else {
            throw "Unknown obj :\n" + z.dump(obj);
        }
        $(html).appendTo(jBlock).data("dom-li", obj.dom);
    }
}

function div_text(div) {
    var str = $(".num", div).text();
    if(str)
        str += "." + $("b, a", div).text();
    else
        str = $("b, a", div).text();
    return str;
}

function nav_add_ul(ciName, ul) {
    // 总结数据
    var objs = [];
    ul.children("li").each(function() {
        var div = $(this).children("div");
        var str = div_text(div);
        // 加入块
        var html = "";
        // 目录
        if(div.hasClass("zdoc_folder")) {
            objs.push({
                type: "folder",
                text: str,
                dom: this
            });
        }
        // 内部
        else if(div.hasClass("zdoc_indoc")) {
            objs.push({
                type: "indoc",
                text: str,
                href: $("a",div).attr("href"),
                dom: this
            });
        }
        // 文章
        else {
            objs.push({
                type: "doc",
                text: str,
                href: $("a",div).attr("href"),
                dom: this
            });
        }
    });
    // 增加
    nav_add(ul, ciName, objs);
}

function nav_add_doc(li) {
    // 收集文档中的标题
    var ul = $(li).children("ul");
    // 没有缓存文档，进行记录
    if(ul.size() == 0) {
        ul = $('<ul></ul>').appendTo(li);
        // 收集标题
        var href = $("a", li).attr("href");
        $("#arena .zdoc_body").find("h1").each(function() {
            var text = $(this).text();
            var docAn = $.browser.mozilla ? escape(text) : text;
            var html = '<li>';
            html += '<div class="zdoc_indoc zdoc_indoc_' + this.tagName.toLowerCase() + '">';
            html += '<a href="' + href + '!' + docAn + '">' + text + '</a>';
            html += '</div>';
            html += '</li>';
            $(html).appendTo(ul);
        });
    }

    // 增加
    var str = $(li).children("div").text();
    nav_add_ul(str, ul);
    nav_active(-1);
}

function nav_active(index) {
    var jNav = $("#nav");
    var jCrumb = $("#nav_crumb");
    var jScroller = $("#nav_scroller");
    var jCi = z.get(jCrumb.children(".nav_crumb_item"), index);

    // 已经是高亮的
    if(jCi.hasClass("nav_crumb_item_hlt"))
        return;

    // 移除旧的高亮项目
    $(".nav_crumb_item_hlt", jCrumb).removeClass("nav_crumb_item_hlt");
    $(".nav_block_hlt", jScroller).removeClass("nav_block_hlt");

    // 设置高亮
    var jBlock = z.get(jScroller.children(".nav_block"), index);
    // 滚动
    var left = jNav.width() * jCi.prevAll().size() * -1;
    jScroller.animate({
        left: left
    }, 200, function() {
        jCi.addClass("nav_crumb_item_hlt");
        jBlock.addClass("nav_block_hlt");
        jCi.nextAll().addClass("nav_transient");
        jCi.prevAll().andSelf().removeClass("nav_transient");
        jBlock.nextAll().addClass("nav_transient");
        jBlock.prevAll().andSelf().removeClass("nav_transient");
    });
}

function load_doc(jA, callback) {
    var href = jA.attr("href").substring(1);
    var li = jA.data("dom-li");
    var arena = $("#arena").empty().css("opacity", 0);
    arena.load(href, function() {
        arena.animate({
            "opacity": 1
        }, 200);
        nav_add_doc(li);
        if( typeof callback == "function") {
            callback(jA);
        }
    });
}

function main() {
    // 初始化 #nav 的 HTML
    var html = '<div id="nav_crumb"></div>';
    html += '<div id="nav_viewport"><div id="nav_scroller"></div></div>';
    var jNav = $("#nav").html(html);

    // 初始化数据
    nav_add_ul(document.title, $(".zdoc_index_table"));

    // 事件: 点击分类条目
    jNav.delegate(".nav_folder", "click", function() {
        var str = $(this).text();
        var li = $(this).data("dom-li");
        var ul = $(li).children("ul");
        if(ul.size() > 0) {
            nav_add_ul(str, ul);
            nav_active(-1);
        } else {
            alert("Nothing under this category!");
        }
    });
    // 事件: 点击文档条目
    jNav.delegate(".nav_doc", "click", function() {
        load_doc($(this));
    });
    // 事件: 点击文档内部标题
    jNav.delegate(".nav_indoc", "click", function() {
        var str = $(this).text();
        var h1 = $("#arena h1:contains('"+str+"')").first();
        if(h1.size() > 0) {
            window.scrollTo(0, h1.offset().top);
        }
    });
    // 事件: 点击面包屑
    jNav.delegate(".nav_crumb_item", "click", function() {
        if(!$(this).hasClass("nav_crumb_item_hlt")) {
            nav_active($(this).prevAll().size());
        }
    });
    // 事件: 点击文档内的链接
    $("#arena").delegate("a", "click", function() {
        var href = $(this).attr("href");
        if(href && href.match(/^#/)) {
            goto_page(href);
        }
    });
    // 根据 pgan 加载数据
    var pgan = z.pgan();
    if(pgan) {
        goto_page(pgan);
    }
    // 最后高亮最后一个块
    else {
        nav_active(-1);
    }
}

function goto_page(pgan) {
    if(!pgan)
        return;

    if(pgan.match(/^#/))
        pgan = pgan.substring(1);

    var ss = pgan.split("!");
    var href = ss[0];
    // 找到 li
    var li = $('.zdoc_index_table a[href="' + href + '"]');

    if(li.size() == 0)
        return;

    // 除了第一块，都清除
    $(".nav_block").first().nextAll().remove();
    $(".nav_crumb_item").first().nextAll().remove();

    // 增加父
    var uls = li.parents("ul");
    for(var i =                            uls.size() - 1; i >= 0; i--) {
        var ul = $(uls[i]);
        var str = div_text(ul.prev());
        nav_add_ul(str, ul);
    };
    // 在列表中找到自己
    var jA = $(".nav_block").last().find('a[href="#' + href + '"]');
    // 读取文档
    load_doc(jA, function() {
        if(ss.length > 1) {
            var an = ss[1];
            var jq = $('.nav_block').last().find('a:contains("' + an + '")');
            jq.click();
        }
        nav_active(-1);
    });
}

function ajustLayout() {
    var box = z.winsz();
    $("#nav").css("height", box.height);
    var jCrumb = $("#nav_crumb");
    if(jCrumb.size() > 0) {
        var h = jCrumb.outerHeight();
        var jViewport = $("#nav_viewport").css("height", box.height - h);
        var bW = jViewport.width();
        var bH = jViewport.height();
        $(".nav_block", jViewport).css({
            width: bW,
            height: bH
        });
    }
}

//
(function($) {
$(document.body).ready(main);
window.onresize = ajustLayout;
})(window.jQuery);
