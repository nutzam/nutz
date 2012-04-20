/**
 * 我的全局帮助函数
 */
//---------------------------------------------------------------------------------------
// 获得 os 信息
(function($) {
var str = (window.navigator.userAgent + "").toLowerCase();
window.os = {
    mac: str.match(/.*mac os.*/) ? true : false,
    pc: str.match(/.*mac os.*/) ? false : true,
};
//---------------------------------------------------------------------------------------
var INDENT_BY = "    ";
//---------------------------------------------------------------------------------------
var MONTH = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

var SCROLL_BAR_WIDTH = null;
//---------------------------------------------------------------------------------------
window.z = {
    REG_NOWORD: new RegExp("[ \t\r\b\n~!@#$%^&*()+=`:{}|\\[\\]\\\\:\"';<>?,.\/-]", "g"),
    //---------------------------------------------------------------------------------------
    local: {
        set: function(key, val) {
            if(localStorage) {
                localStorage.setItem(key, val);
            }
        },
        get: function(key) {
            if(localStorage) {
                return localStorage.getItem(key);
            }
        }
    },
    //---------------------------------------------------------------------------------------
    dump: function(obj, tab) {
        if(!tab)
            tab = "";
        var re = "";
        if(null == obj) {
            return "null";
        } else if( typeof obj == 'function') {
        } else if($.isArray(obj)) {
            re += "[";
            if(obj.length > 0) {
                re += tab + z.dump(obj[0], tab + "   ");
                for(var i = 1; i < obj.length; i++) {
                    re += tab + ", " + z.dump(obj[i], tab + " ");
                }
            }
            re += "]";
            return re;
        } else if( typeof obj == 'string') {
            return '"' + obj.toString() + '"';
        } else if( typeof obj == 'object') {
            re += "{\n";
            for(var key in obj) {
                var v = obj[key];
                if( typeof v == 'function') {
                    re += tab + '"' + key + '" : function(){...}';
                    continue;
                }
                re += tab + key + ":";
                if($.isArray(v))
                    re += this.dump(v, tab + " ");
                else
                    re += this.dump(v, tab + " ");
                re += "\n";
            }
            return re + tab + "}";
        }
        return obj.toString();
    },
    //---------------------------------------------------------------------------------------
    // 将一个 JS 对象或者数组进行浅层克隆
    clone: function(obj) {
        // 无
        if(!obj)
            return obj;
        // 数组
        if($.isArray(obj)) {
            var re = [];
            for(var i = 0; i < obj.length; i++) {
                re.push(obj[i]);
            }
            return re;
        }
        // 普通对象
        if($.isPlainObject(obj)) {
            var re = {};
            for(var key in obj) {
                re[key] = z.clone(obj[key]);
            }
            return re;
        }
        // 其它
        return obj;
    },
    //---------------------------------------------------------------------------------------
    remove: function(obj, keyWillBeRemoved) {
        if(!obj)
            return obj;
        delete
        obj[keyWillBeRemoved];
    },
    //---------------------------------------------------------------------------------------
    sNull: function(str, def) {
        var type = typeof str;
        if(type == "string" || type == "number" || type == "boolean")
            return str;
        return str ? "" + str : ( def ? def : "");
    },
    //---------------------------------------------------------------------------------------
    dup: function(s, num) {
        var re = "";
        for(var i = 0; i < num; i++)
        re = re + s;
        return re;
    },
    //---------------------------------------------------------------------------------------
    contains: function(array, o) {
        if(!$.isArray(array) || !o)
            return false;
        for(var i = 0; i < array.length; i++)
        if(array[i] == o)
            return true;
        return false;
    },
    //---------------------------------------------------------------------------------------
    // 让字符串居左对齐
    alignl: function(s, width, by) {
        if( typeof s != "string")
            s = "" + s;
        if(!by)
            by = " ";
        if(s.length < width)
            return s + z.dup(by, width - s.length);
        return s;
    },
    //---------------------------------------------------------------------------------------
    // 让字符串居右对齐
    alignr: function(s, width, by) {
        if( typeof s != "string")
            s = "" + s;
        if(!by)
            by = " ";
        if(s.length < width)
            return z.dup(by, width - s.length) + s;
        return s;
    },
    //---------------------------------------------------------------------------------------
    startsWith: function(str, sub) {
        if(!str || !sub)
            return false;
        if(str.length < sub.length)
            return false;
        return str.substring(0, sub.length) == sub
    },
    //---------------------------------------------------------------------------------------
    endsWith: function(str, sub) {
        if(!str || !sub)
            return false;
        if(str.length < sub.length)
            return false;
        return str.substring(str.length - sub.length, str.length) == sub
    },
    //---------------------------------------------------------------------------------------
    /**
     * 解析一个字符串为一个按钮控件的显示信息
     * @param str 格式 [#~:][类选择器:][显示文字] 比如 "#:abc" 或 "#~::ui.show"
     * @return 一个 js 对象
     */
    uname: function(str) {
        var ss = str.split(":")
        if(ss.length < 2)
            throw "Uknown uname '" + str + "'!!!";
        // 头部有标识
        if(ss.length > 2) {
            return {
                pin: ss[0].indexOf("#") >= 0,
                beginGroup: ss[0].indexOf("~") >= 0,
                className: ss[1],
                name: ss[2],
                text: this.msg(ss[2])
            }
        }
        // 头部无标识
        return {
            className: ss[0],
            name: ss[1],
            text: this.msg(ss[1])
        }
    },
    //---------------------------------------------------------------------------------------
    /** 将给定字符串，变成 "xxx...xxx" 形式的字符串
     * @param str 字符串
     * @param len 最大长度
     * @return 紧凑的字符串
     */
    strBrief: function(str, len) {
        if(!str || (str.length + 3) <= len)
            return str;
        var w = parseInt(len / 2);
        var l = str.length;
        return str.substring(0, len - w) + " ... " + str.substring( l - w);
    },
    //---------------------------------------------------------------------------------------
    // 监视键盘的各个状态，会不断更新 window.keyboard 这个对象
    watchKeyboard: function() {
        if(!window.keyboard) {
            window.keyboard = {};
            // 创建状态显示图标，需要 DOM 中存在 ".liveinfo .keyboard" 选择器
            var html = '';
            for(var keyCode in KEYS) {
                var key = KEYS[keyCode];
                html += '<i class="icon key_' + key + '" style="visibility:hidden;"></i>';
            }
            $(".liveinfo .keyboard").html(html);
            // 监视键盘事件
            $(window).keydown(function(e) {
            var key = KEYS["" + e.which];
            if(key) {
            window.keyboard[key] = true;
            $(".liveinfo .keyboard .key_" + key).css("visibility", "visible");
            }
            }).keyup(function(e) {
                var key = KEYS["" + e.which];
                if(key) {
                    window.keyboard[key] = false;
                    $(".liveinfo .keyboard .key_" + key).css("visibility", "hidden");
                }
            });
        }
    },
    //---------------------------------------------------------------------------------------
    // 将一个字符串变成 JS 对象，如果发生错误，返回空对象
    seval: function(str) {
        try {
            return $.trim(str) ? eval('(' + str + ')') : {};
        } catch (E) {
            return {};
        }
    },
    //---------------------------------------------------------------------------------------
    // 根据一个字节数，返回一个人类友好的显示，比如 xxxMB 等
    // unit 如果为 'M' 表示单位为M， 如果为 'K' 表示单位为 K
    sizeText: function(size, unit) {
        if( typeof size != "number")
            size = size * 1;
        if("M" == unit) {
            var g = size / 1000;
            if(g > 1)
                return Math.ceil(g * 10) / 10 + " GB";
            return size + "MB";
        }
        if("K" == unit) {
            var m = size / 1000;
            var g = m / 1000;
            if(g > 1)
                return Math.ceil(g * 10) / 10 + " GB";
            if(m > 1)
                return Math.ceil(m * 10) / 10 + " MB";
            return Math.ceil(k) + " KB";
        }
        var k = size / 1000;
        if(k > 1) {
            var m = k / 1000;
            var g = m / 1000;
            if(g > 1)
                return Math.ceil(g * 10) / 10 + " GB";
            if(m > 1)
                return Math.ceil(m * 10) / 10 + " MB";
            return Math.ceil(k) + " KB";
        }
        return size + " B";
    },
    //---------------------------------------------------------------------------------------
    /**
     * @return 返回当前URL中“#”后的内容。如果没有，则返回null
     */
    pgan: function() {
        var lo = z.urlDecode("" + window.location.href);
        var pos = lo.indexOf("#");
        return pos < 0 ? null : lo.substring(pos + 1);
    },
    //---------------------------------------------------------------------------------------
    /**
     * 内容应该为一个 json 字符串，并且，没有左右大括号
     * @return 返回当前URL中“#”后的内容。作为一个 js 对象返回
     */
    pgano: function() {
        var text = z.pgan();
        if(!text)
            return {};
        return eval('({' + text + '})');
    },
    //---------------------------------------------------------------------------------------
    // 将 ano 对象变成 ano 字符串
    pgans: function(ano) {
        var re = [];
        if(ano) {
            var toval = function(val) {
                if($.isArray(val)) {
                    var ss = [];
                    for(var i = 0; i < val.length; i++) {
                        ss.push(toval(val[i]));
                    }
                    return "[" + ss.join(",") + "]";
                }
                var tp = ( typeof val);
                if("string" == tp) {
                    return "'" + val + "'";
                }
                if("object" == tp) {
                    return "{" + z.pgans(val) + "}";
                }
                return val;
            }
            for(var key in ano) {
                re.push(key + ":" + toval(ano[key]));
            }
        }
        return re.join(",");
    },
    //---------------------------------------------------------------------------------------
    toRGBA: function(color) {
        if(!color || "none" == color)
            return {
                A: 0.0,
                R: 0,
                G: 0,
                B: 0
            };
        if( typeof color == "object")
            return color;
        // 格式为 #RRGGBB
        if(z.startsWith(color, "#")) {
            return {
                R: parseInt(color.substring(1, 3), 16),
                G: parseInt(color.substring(3, 5), 16),
                B: parseInt(color.substring(5, 7), 16),
                A: parseInt(color.substring(7), 16) / 255.0,
            }
        }
        // 格式为 0xAARRGGBB
        return {
            A: parseInt(color.substring(2, 4), 16) / 255.0,
            R: parseInt(color.substring(4, 6), 16),
            G: parseInt(color.substring(6, 8), 16),
            B: parseInt(color.substring(8), 16)
        }
    },
    //---------------------------------------------------------------------------------------
    toAHexString: function(color) {
        var rgba = z.toRGBA(color);
        var re = "0x";
        re += z.alignr(parseInt(rgba.A*255).toString(16).toUpperCase(), 2, "0");
        re += z.alignr(rgba.R.toString(16).toUpperCase(), 2, "0");
        re += z.alignr(rgba.G.toString(16).toUpperCase(), 2, "0");
        re += z.alignr(rgba.B.toString(16).toUpperCase(), 2, "0");
        return re;
    },
    //---------------------------------------------------------------------------------------
    toHexaString: function(color) {
        var rgba = z.toRGBA(color);
        var re = "#";
        re += z.alignr(rgba.R.toString(16), 2, "0");
        re += z.alignr(rgba.G.toString(16), 2, "0");
        re += z.alignr(rgba.B.toString(16), 2, "0");
        re += z.alignr(parseInt(rgba.A*255).toString(16), 2, "0");
        return re.toUpperCase();
    },
    //---------------------------------------------------------------------------------------
    toRGBAString: function(color) {
        var rgba = z.toRGBA(color);
        return "rgba(" + rgba.R + "," + rgba.G + "," + rgba.B + "," + rgba.A + ")";
    },
    //---------------------------------------------------------------------------------------
    // 从一个 jq 对象中选择一个 dom 元素，并用 jQuery 包裹返回
    // index == 0 表示第一个，
    // index == -1 表示最后一个
    // index == -2 表示倒数第二个
    get: function(jq, index) {
        if( typeof index != "number")
            return null;
        if(index < 0)
            index = jq.size() + index;
        if(index < 0 || (index + 1) > jq.size())
            return null;
        return $(jq[index]);
    },
    //---------------------------------------------------------------------------------------
    /**
     * 从 UL#__msg__ 获 LI.key 的文本内容
     *
     * @param key -
     *            里面的 '.' 将被替换成 '_'
     * @return 如果没找到 LI 对象，则返回 defval ? key，否则返回 LI 的文本内容
     */
    msg: function(key, defval) {
        if(!key)
            return "";
        var li = $("#__msg__ ." + key.replace(/[.]/g, "_"));
        return li.size() > 0 ? $(li[0]).html() : ( defval ? defval : key);
    },
    //---------------------------------------------------------------------------------------
    // 返回一个时间戳，其它应用可以用来阻止浏览器缓存
    timestamp: function() {
        return ((new Date()) + "").replace(/[ :\t*+()-]/g, "").toLowerCase();
    },
    //---------------------------------------------------------------------------------------
    winsz: function() {
        if(window.innerWidth) {
            return {
                width: window.innerWidth,
                height: window.innerHeight
            };
        }
        if(document.documentElement) {
            return {
                width: document.documentElement.clientWidth,
                height: document.documentElement.clientHeight
            };
        }
        return {
            width: document.body.clientWidth,
            height: document.body.clientHeight
        };
    },
    //---------------------------------------------------------------------------------------
    // jq - 要移除的对象
    // opt.after - 当移除完成后的操作, this 为 jq 对象
    // opt.holder - 占位符的 HTML，默认是 DIV.z_remove_holder
    // opt.speed - 移除的速度，默认为  300
    // opt.appendTo - (优先)一个目标，如果声明，则不会 remove jq，而是 append 到这个地方
    // opt.prependTo - 一个目标，如果声明，则不会 remove jq，而是 preppend 到这个地方
    removeIt: function(jq, opt) {
        // 格式化参数
        jq = $(jq);
        opt = opt || {};
        if( typeof opt == "function") {
            opt = {
                after: opt
            };
        } else if( typeof opt == "number") {
            opt = {
                speed: opt
            };
        }
        // 计算尺寸
        var w = jq.outerWidth();
        var h = jq.outerHeight();
        // 增加占位对象，以及移动 me
        var html = opt.holder || '<div class="z_remove_holder">&nbsp;</div>';
        var holder = $(html).css({
        "width" : w,
        "height":h
        }).insertAfter(jq);
        // 删除元素
        if(opt.appendTo)
            jq.appendTo(opt.appendTo);
        else if(opt.prependTo)
            jq.prependTo(opt.prependTo);
        else
            jq.remove();
        // 显示动画
        holder.animate({
            width: 0,
            height: 0
        }, opt.speed || 300, function() {
            $(this).remove();
            if( typeof opt.after == "function")
                opt.after.apply(jq);
        });
    },
    //---------------------------------------------------------------------------------------
    // jq - 要闪烁的对象
    // opt.after - 当移除完成后的操作
    // opt.html - 占位符的 HTML，默认是 DIV.z_blink_light
    // opt.speed - 闪烁的速度，默认为  500
    blinkIt: function(jq, opt) {
        // 格式化参数
        jq = $(jq);
        opt = opt || {};
        if( typeof opt == "function") {
            opt = {
                after: opt
            };
        } else if( typeof opt == "number") {
            opt = {
                speed: opt
            };
        }
        // 得到文档中的
        var off = jq.offset();
        // 样式
        var css = {
            "width": jq.outerWidth(),
            "height": jq.outerHeight(),
            "border-color": "#FF0",
            "background": "#FFA",
            "opacity": 0.8,
            "position": "fixed",
            "top": off.top,
            "left": off.left,
            "z-index": 9999999
        };
        // 建立闪烁层
        var lg = $(opt.html || '<div class="z_blink_light">&nbsp;</div>');
        lg.css(css).appendTo(document.body);
        lg.animate({
            opacity: 0.1
        }, opt.speed || 500, function() {
            $(this).remove();
            if( typeof opt.after == "function")
                opt.after.apply(jq);
        });
    },
    //---------------------------------------------------------------------------------------
    // ele - 为任何可以有子元素的 DOM 或者 jq，本函数在该元素的位置绘制一个 input 框，让用户输入新值
    // opt - object | function
    // opt.multi - 是否是多行文本
    // opt.text - 初始文字，如果没有给定，采用 ele 的文本
    // opt.width - 指定宽度
    // opt.height - 指定高度
    // opt.after - function(newval, oldval){...} 修改之后，
    //   - this 为被 edit 的 DOM 元素 (jq 包裹)
    //   - 传入 newval 和 oldval
    //   - 如果不给定这个参数，则本函数会给一个默认的实现
    editIt: function(ele, opt) {
        // 处理参数
        var me = $(ele);
        var opt = opt || {};
        if( typeof opt == "function") {
            opt = {
                after: opt
            };
        } else if( typeof opt == "boolean") {
            opt = {
                multi: true
            };
        }
        if( typeof opt.after != "function")
            opt.after = function(newval, oldval) {
                if(newval != oldval)
                    this.text(newval);
            };
        // 定义处理函数
        var onKeydown = function(e) {
            // Esc
            if(27 == e.which) {
                $(this).val($(this).attr("old-val")).blur();
            }
            // Ctrl + Enter
            else if(e.which == 13 && window.keyboard.ctrl) {
                $(this).blur();
            }
        };
        var func = function() {
            var me = $(this);
            var opt = me.data("z-editit-opt");
            opt.after.apply(me.parent(), [me.val(), me.attr("old-val")]);
            me.unbind("keydown",onKeydown).remove();
        };
        // 准备显示输入框
        var val = opt.text || me.text();
        var html = opt.multi ? '<textarea></textarea>' : '<input>';
        // 计算宽高
        var css = {
            "width": opt.width || me.outerWidth(),
            "height": opt.height || me.outerHeight(),
            "position": "absolute",
            "z-index": 999999
        };

        // 显示输入框
        var jq = $(html).prependTo(me).val(val).attr("old-val", val).addClass("z_editit").css(css);
        jq.data("z-editit-opt", opt);
        return jq.one("blur", func).one("change", func).keydown(onKeydown).select();
    },
    //---------------------------------------------------------------------------------------
    // 将任意 JS 对象转化成 JSON 串
    toJson: function(obj, depth) {
        var type = typeof obj;
        // 空对象
        if(null == obj && ("object" == type || 'undefined' == type || "unknown" == type))
            return 'null';
        // 字符串
        if("string" == type)
            return '"' + obj.replace(/(\\|\")/g, "\\$1").replace(/\n|\r|\t/g, function() {
                var a = arguments[0];
                return (a == '\n') ? '\\n' : (a == '\r') ? '\\r' : (a == '\t') ? '\\t' : "";
            }) + '"';
        // 布尔
        if("boolean" == type)
            return obj ? "true" : "false";
        // 数字
        if("number" == type)
            return obj;
        // 是否需要格式化
        var format = false;
        if( typeof depth == "number") {
            depth++;
            format = true;
        } else if(depth == true) {
            depth = 1;
            format = true;
        } else {
            depth = false;
        }
        // 数组
        if($.isArray(obj)) {
            var results = [];
            for(var i = 0; i < obj.length; i++) {
                var value = obj[i];
                results.push(this.toJson(obj[i], depth));
            }
            return '[' + results.join(', ') + ']';
        }
        // 函数
        if('function' == type)
            return '"function(){...}"';
        // 普通 JS 对象
        var results = [];
        // 需要格式化
        if(format) {
            // 判断一下，如果key少于3个，就不格式化了，并且，之内的所有元素都为 boolean, string,number
            var i = 0;
            for(var key in obj) {
                if(++i > 2) {
                    format = true;
                    break;
                }
                var type = typeof obj[key];
                if(type == "object") {
                    format = true;
                    break;
                }
            }
            // 确定要格式化
            if(format) {
                var prefix = "\n" + this.dup(INDENT_BY, depth);
                for(key in obj) {
                    var value = obj[key];
                    if(value !== undefined)
                        results.push(prefix + '"' + key + '" : ' + this.toJson(value, depth));
                }
                return '{' + results.join(',') + '\n' + this.dup(INDENT_BY, depth - 1) + '}';
            }
        }// 紧凑格式
        for(var key in obj) {
            var value = obj[key];
            if(value !== undefined)
                results.push('"' + key + '":' + this.toJson(value, depth));
        }
        return '{' + results.join(',') + '}';
    },
    //---------------------------------------------------------------------------------------
    // 解码 URL
    urlDecode: function(str) {
        return unescape(str.replace(/\+/g, " "));
    },
    //---------------------------------------------------------------------------------------
    // 编码 URL
    urlEncode: function(str) {
        return escape(result);
    },
    //---------------------------------------------------------------------------------------
    // 获取一个月的月历，它将返回一个数组数组的0元素必定为周一，因此如果本月一号如果不是周一，则用上月
    // 的日期来填充
    //   year  : 为四位
    //   month : 1-12 表示12个月
    monthDays: function(year, month) {
        // 支持直接给一个对象
        if( typeof year == "object") {
            month = year.month;
            year = year.year;
        }
        // 试图获取一个时间
        var d = new Date(year, month - 1, 1);
        var re = [];
        // 补齐一周的开始
        var wday = d.getDay();
        if(wday > 0) {
            // 获取上一个月的天数
            var prevDate = z.monthDate(year, month - 1);
            // 开始补齐
            for(var i = wday - 1; i >= 0; i--) {
                re[i] = prevDate--;
            }
        }
        // 添加本月
        var date = z.monthDate(year, month);
        for(var i = 0; i < date; i++) {
            re.push((i + 1));
        }
        return re;
    },
    //---------------------------------------------------------------------------------------
    // 获得某年某月，最大的天数
    //   year  : 可以为四位或两位，如果两位表示19xx
    //   month : 1-12 表示12个月
    monthDate: function(year, month) {
        if( typeof year == "object") {
            year = year.year;
            month = year.month;
        } else if(month <= 0) {
            year -= 1;
            month = 12;
        }
        var d = MONTH[ month - 1];
        return (d < 30 && z.leapYear(year)) ? d + 1 : d;
    },
    //---------------------------------------------------------------------------------------
    // @return 0 : 相等， -1 为 d1 小，1为 d2 小
    compareDate: function(d1, d2) {
        if(d1.year < d2.year)
            return -1;
        if(d1.year == d2.year) {
            if(d1.month < d2.month)
                return -1;
            if(d1.month == d2.month) {
                if(d1.date < d2.date)
                    return -1;
                else if(d1.date == d2.date)
                    return 0;
            }
        }
        return 1;
    },
    //---------------------------------------------------------------------------------------
    // 根据 offset 生成一个新日期, offset 是一个天数，可正可负
    offDate: function(d, offset) {
        if(offset == 0)
            return d;
        if(offset == -1) {
            var re = {
                year: d.year,
                month: d.month,
                date: d.date - 1
            };
            if(re.date == 0) {
                re.month -= 1;
                if(re.month == 0) {
                    re.year -= 1;
                    re.month = 12;
                }
                re.date = z.monthDate(re.year, re.month);
            }
            return re;
        } else if(offset == 1) {
            var re = {
                year: d.year,
                month: d.month,
                date: d.date + 1
            };
            if(re.date > z.monthDate(re.year, re.month)) {
                re.month += 1;
                if(re.month > 12) {
                    re.year += 1;
                    re.month = 1;
                }
                re.date = z.monthDate(re.year, re.month);
            }
            return re;
        } else if(offset > 1) {
            var re = d;
            for(var i = 0; i < offset; i++) {
                re = z.offDate(re, 1);
            }
            return re;
        } else {
            var re = d;
            for(var i = 0; i < Math.abs(offset); i++) {
                re = z.offDate(re, -1);
            }
            return re;
        }
    },
    //---------------------------------------------------------------------------------------
    // 判断一年是否为闰年
    leapYear: function(year) {
        return (year % 4 == 0) && (year % 400 == 0 || year % 100 == 0);
    },
    //---------------------------------------------------------------------------------------
    // 将一个 date 对象，输出成 yyyy-MM-dd 的格式
    dstr: function(d) {
        return z.alignr(d.year, 4, "19") + "-" + z.alignr(d.month, 2, "0") + "-" + z.alignr(d.date, 2, "0");
    },
    //---------------------------------------------------------------------------------------
    // 获得今天的日期对象
    today: function() {
        var now = new Date();
        return {
            year: now.getFullYear(),
            month: now.getMonth() + 1,
            date: now.getDate(),
            day: now.getDay()
        };
    },
    //---------------------------------------------------------------------------------------
    // 根据一个 yyyy-MM-dd 字符串，解析成一个 d 对象
    d: function(str) {
        var ss = str.split("-");
        if(ss.length < 3) {
            ss.push(1).push(1);
        }
        var now = new Date(ss[0] * 1, ss[1] * 1 - 1, ss[2] * 1);
        return {
            year: now.getFullYear(),
            month: now.getMonth() + 1,
            date: now.getDate(),
            day: now.getDay()
        };
    },
    //---------------------------------------------------------------------------------------
    // 根据 hh:mm:ss，或者一个秒数 生成一个时间对象
    tm: function(str) {
        var re;
        if( typeof str == "number") {
            re = {
                seconds: Math.min(str, 86400)
            };
            str = str % 3600;
            re.hh = (re.seconds - str) / 3600
            re.ss = str % 60;
            re.mm = ( str - re.ss) / 60;
            return re;
        } else {
            var ss = str.split(":");
            if(ss.length < 2) {
                ss.push("00");
                ss.push("00");
            }
            re = {
                hh: ss[0] * 1,
                mm: ss[1] * 1,
                ss: ss[2] * 1,
            };
            // 检查时间的合法性
            if(re.hh * 1 != re.hh || re.hh < 0 || re.hh > 23 || re.mm * 1 != re.mm || re.mm < 0 || re.mm > 59 || re.ss * 1 != re.ss || re.ss < 0 || re.ss > 59) {
                throw "Wrong time format [" + str + "], it should be 'hh:mm:sss'!";
            }
            re.seconds = re.hh * 3600 + re.mm * 60 + re.ss;
        }
        return re;
    },
    //---------------------------------------------------------------------------------------
    // 根据一个时间对象生成一个格式如 hh:mm:ss 的时间字符串
    tmstr: function(t) {
        return z.alignr(t.hh, 2, "0") + ":" + z.alignr(t.mm, 2, "0") + ":" + z.alignr(t.ss, 2, "0");
    },
    //---------------------------------------------------------------------------------------
    // 根据现在生成一个时间对象
    now: function() {
        var now = new Date();
        var re = {
            hh: now.getHours(),
            mm: now.getMinutes(),
            ss: now.getSeconds()
        };
        re.seconds = re.hh * 3600 + re.mm * 60 + re.ss;
        return re;
    },
    // 获得当前系统当前浏览器中滚动条的宽度
    scrollBarWidth: function() {
        if(SCROLL_BAR_WIDTH == null) {
            var newDivOut = "<div id='div_out' style='position:relative;width:100px;height:100px;overflow-y:scroll;overflow-x:scroll'></div>"
            var newDivIn = "<div id='div_in' style='position:absolute;width:100%;height:100%;'></div>";
            var scrollWidth = 0;
            $("body").append(newDivOut);
            $("#div_out").append(newDivIn);
            var divOutS = $("#div_out");
            var divInS = $("#div_in");
            scrollWidth =                                                                               divOutS.width() -                                                                               divInS.width();
            $("#div_out").remove();
            $("#div_in").remove();
            SCROLL_BAR_WIDTH = scrollWidth;
        }
        return SCROLL_BAR_WIDTH;
    }
    //---------------------------------------------------------------------------------------
};
// end of window.z
//---------------------------------------------------------------------------------------
// 键盘映射
var KEYS = {
    "16": "shift",
    "18": "alt"
};
if($.browser.webkit) {
    KEYS[os.mac ? "91" : "17"] = "ctrl";
} else {
    KEYS[os.mac && !$.browser.opera ? "224" : "17"] = "ctrl";
}
})(window.jQuery);
