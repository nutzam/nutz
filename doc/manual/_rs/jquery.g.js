(function($) {

    var _G = {
        // ~~~~~~~~~~~~~~~~~~~~~~~~dump~~~~~~~~~~~~~~~~~~~~~~~~~~~ start
        /*
         * 打印对像或变量的详细信息 作用方法：$.dump(obj); obj:array object string Number
         */
        dump: function(obj, tab) {
            if (!tab) 
                tab = "";
            var re = "";
            if (null == obj) {
                return "null";
            } else if (typeof obj == 'function') {
            } else if ($.isArray(obj)) {
                re += "[";
                if (obj.length > 0) {
                    re += tab + $.dump(obj[0], tab + "   ");
                    for (var i = 1; i < obj.length; i++) {
                        re += tab + ", " + $.dump(obj[i], tab + " ");
                    }
                }
                re += "]";
                return re;
            } else if (typeof obj == 'string') {
                return '"' + obj.toString() + '"';
            } else if (typeof obj == 'object') {
                re += "{\n";
                for (var key in obj) {
                    var v = obj[key];
                    if (typeof v == 'function') {
                        continue;
                    }
                    re += tab + key + ":";
                    if ($.isArray(v)) 
                        re += $.dump(v, tab + " ");
                    else 
                        re += $.dump(v, tab + " ");
                    re += "\n";
                }
                return re + tab + "}";
            }
            return obj.toString();
        },
        sNull: function(str, def) {
            return str ? "" + str : (def ? "" + def : "");
        },
        // ~~~~~~~~~~~~~~~~~~~~~~~vieoport~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        // start ~
        /*
         * 获得当前窗口的大小 使用：$.winsz()
         */
        winsz: function() {
            if (window.innerWidth) {
                return {
                    width: window.innerWidth,
                    height: window.innerHeight
                };
            }
            if (document.documentElement) {
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
        // ~~~~~~~~~~~~~~~~msg~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        /**
         * 从 UL#__msg__ 获 LI.key 的文本内容
         *
         * @param key -
         *            里面的 '.' 将被替换成 '_'
         * @return 如果没找到 LI 对象，则返回 key，否则返回 LI 的文本内容
         */
        msg: function(key) {
            if (!key) 
                return "";
            var li = $("#__msg__ ." + key.replace(/[.]/g, "_"));
            return li.size() > 0 ? $(li[0]).text() : key;
        },
        // ~~~~~~~~~~~~~~~~pgan~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        /**
         *
         *
         * @return 返回当前URL中“#”后的内容。如果没有，则返回null
         */
        pgan: function() {
            var lo = "" + window.location.href;
            var pos = lo.indexOf("#");
            return pos < 0 ? null : lo.substring(pos + 1);
        },
        // ~~~~~~~~~~~~~~~~~~~~URL~~~~~~~~~~
        /**
         * 从 UL#__url__ 获 LI.key 的文本内容
         *
         * @param key -
         *            里面的 '.' 将被替换成 '_'
         * @return 没有找到 URL 的值，返回输入的 key
         */
        url: function(key) {
            var li = $("#__url__ ." + key.replace(/[.]/g, "_"));
            if (li.size() == 0) 
                return key;
            return $(li[0]).text();
        },
        /*
         * ~~~~~~~~~~~~~~~~~~~dup~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         * 复制num个字符或字符串成成一个新的字符串
         */
        dup: function(s, num) {
            var re = "";
            for (var i = 0; i < num; i++) 
                re = re + s;
            return re;
        },
        /**
         * ~~~~~~~~~~~~~~~~~~countDup~~~~~~~~~~~~~~~~~~~~~~~~~
         * 返回STR中ptn最后出现的位置
         */
        countDup: function(str, ptn) {
            var re = 0;
            var pos = 0;
            while ((pos = str.indexOf(ptn, re * ptn.length)) != -1) 
                re++;
            return re;
        },
        /*
         * ~~~~~~~~~~~~~~~~~~~~~parseData~~~~~~~~~~~~~~~~~~~~~~~~~~ 将符合
         * 列表数据源约定的参数变成JS对象数组
         * 如果data是String，则返回data；如果data是function,则执行this.data这个Function。
         * 并且把这个Function的返回值return给Data *这个Data Function必须是带有返回值的
         * 除此之外ParseData不接受任何其他类型的参数
         */
        parseData: function(data) {
            // alert(1212);
            if (!data) 
                return null;
            if ("string" == (typeof data)) {
                return data;
            }
            if ("function" == typeof data) {
                data = data.apply(window);
            }
            
            if ($.isArray(data)) {
                if (data.length == 0) 
                    return [];
                if ("object" != typeof data[0]) {
                    var newAry = [];
                    for (var i = 0; i < data.length; i++) {
                        newAry.push({
                            text: data[i],
                            value: data[i],
                            comment: data[i]
                        });
                    }
                    return newAry;
                }
                return data; // 如果以上都不符合，则返回Data本身。
            }
            throw "Unknowing data";
        },
        /*
         * ~~~~~~~~~~~$.parseName~~~~~~~~~~~~~~ str 传入的字符串 sep 用于分隔的标识标
         * 如：#、：等等分隔
         */
        parseName: function(str, sep) {
            if (!sep) 
                sep = ":";
            var newAry = str.split(sep);
            var newAryLen = newAry.length;
            if (3 == newAry.length) 
                return {
                    "className": newAry[0],
                    "text": $.msg(newAry[2])
                };
            else if (2 == newAry.length) {
                if ("" != newAry[0]) 
                    return {
                        "className": newAry[0],
                        "text": $.msg(newAry[1])
                    };
                
                return {
                    "text": $.msg(newAry[1])
                
                }
            } else if (1 == newAry.length) 
                return {
                    "text": $.msg(newAry[0])
                };
            
            throw "Unknowing  format of string";
        },
        /*
         * ~~~~~~~~~~~~~~~$.scrollBarWidth:获取当前窗口系统滚动条的宽度~~~~~~~~~~~~~~~~~~~
         * 新建两个DIV，
         */
        scrollBarWidth: function() {
            var newDivOut = "<div id='div_out' style='position:relative; overflow-y:scroll; overflow-x:scroll'></div>"
            var newDivIn = "<div id='div_in' style='position:absolute;width:100%;height:100%px;right:0px;'></div>";
            var scrollWidth = 0;
            $("body").append(newDivOut);
            $("#div_out").append(newDivIn);
            var divOutS = $("#div_out").boxing();
            var divInS = $("#div_in").boxing();
            scrollWidth = divOutS.width - divInS.width;
            $("#div_out").remove();
            $("#div_in").remove();
            return scrollWidth;
        },
        // ~~~~~~~~~~~~~toJson~~~~~~~~~~~~~~~~~~~
        toJson: function(obj) {
            var type = typeof obj;
            if (null == obj && "object" == type) 
                return 'null';
            
            if (null != obj && 'object' == type) {
                if (!obj.constructor) 
                    type = 'object';
                else {
                    if (Array == obj.constructor) 
                        type = 'array';
                    else if (RegExp == obj.constructor) 
                        type = 'regexp';
                    else 
                        type = 'object';
                }
            }
            switch (type) {
            case 'undefined':
            case 'unknown':
                return;case 'function':
            case 'boolean':
            case 'regexp':
                return obj.toString();
                break;
            case 'number':
                return isFinite(obj) ? obj.toString() : 'null';
                break;
            case 'string':
                return '"' +
                obj.replace(/(\\|\")/g, "\\$1").replace(/\n|\r|\t/g, function() {
                    var a = arguments[0];
                    return (a == '\n') ? '\\n' : (a == '\r') ? '\\r' : (a == '\t') ? '\\t' : "";
                }) +
                '"';
                break;
            case 'object':
                
                var results = [];
                for (var property in obj) {
                    var value = $.toJson(obj[property]);
                    if (value !== undefined) 
                        results.push($.toJson(property) + ':' + value);
                }
                return '{' + results.join(',') + '}';
                break;
            case 'array':
                var results = [];
                for (var i = 0; i < obj.length; i++) {
                    var value = $.toJson(obj[i]);
                    if (value !== undefined) 
                        results.push(value);
                }
                return '[' + results.join(',') + ']';
                break;
            }
        },
        /*
         * ~~~~~~~~~~求滚动条的宽度 scrollBarWidth~~~~~~~~~~~~
         *
         */
        scrollBarWidth: function() {
        
            var newDivOut = "<div id='div_out' style='position:relative;overflow-y:scroll'></div>"
            var newDivIn = "<div id='div_in'  style='position:absolute;width:100%;right:0px;'></div>";
            
            var divOut = $("body").append(newDivOut);
            var divIn = $("#div_out").append(newDivIn);
            
            var scrollBarW = $("#div_out").boxing().width -
            $("#div_in").boxing().width;
            $("#div_in").remove();
            $("#div_out").remove();
            return scrollBarW;
            
        },
        /*
         * ~~~~~~~~~~~格式化字符串strf~~~~~~~~~~~~~~~~ str = "%sabc%sd%se"
         * charAry = [1,2,3,4,5,6,7]; 数组长度可以<或>或=str中%s的个数
         */
        strf: function(str, charAry) {
        
            for (var i = 0; i < charAry.length; i++) {
                if (str.indexOf("%s") != -1) {
                    str = str.replace("%s", charAry[i]);
                }
            }
            /*
             * ~~以下这种方法感觉不好.如果数组长度<要替换的字符串个数,则没有数据可替换的%s将被替换成undefilend~~
             * while(str.indexOf("%s") != -1){
             * if("undefinde"!=charAry[i]){ str =
             * str.replace("%s",charAry[i]); } i++; }
             */
            return str;
            
        },
        /*
         * ~~~~~~~~~~~time:时间转换~~~~~~~~~~~~~~~~
         *
         *
         */
        time: function(time, timeA, timeB) {
            var timeSec = 0;
            if (!time) {
                var nowTime = new Date();
                return nowTime.getSeconds();
            }
            if ("number" == typeof(time) && time > 0) {
                // 如果是数字的time大于了全天24小时总秒数,则抛出异常.全天总秒数24*3600=86400-1
                if (time < 0 || time > 86399) 
                    throw "时间点的秒值不能小于0或大于全天最大时间点秒值";
                var timeMod = 0;
                var timeStr = "";
                
                timeStr += (Math.floor(time / 3600) > 10 ? Math.floor(time / 3600).toString() : "0" +
                Math.floor(time / 3600).toString()) +
                ":";
                time = time % 3600;
                timeStr += (Math.floor(time / 60) > 10 ? Math.floor(time / 60).toString() : "0" +
                Math.floor(time / 60).toString()) +
                ":";
                time = time % 60;
                timeStr += time >= 10 ? time.toString() : "0" +
                time.toString();
                
                return timeStr;
            } else if ("string" == typeof(time)) {
                if ("overlap" != time) {
                    var timeAry = time.split(":");
                    if (timeAry.length > 3) 
                        throw "时间字符串不符合要求:'hh:mm:ss'";
                    if (parseInt(timeAry[0]) > 23) 
                        throw "小时数不能大于23";
                    timeSec += parseInt(timeAry[0]) * 3600;
                    if (parseInt(timeAry[1]) > 59 ||
                    parseInt(timeAry[2]) > 59) 
                        throw "分钟值和秒值不能大于59";
                    timeSec += parseInt(timeAry[1]) * 60;
                    timeSec += parseInt(timeAry[2]);
                    return timeSec;
                } else {
                    // 判断两个时间差
                    if (!timeA || !timeB) 
                        return null;
                    
                    var timeAstart = ("string" == typeof(timeA.from)) ? $.time(timeA.from) : timeA.from;
                    var timeAend = ("string" == typeof(timeA.to)) ? $.time(timeA.to) : timeA.to;
                    var timeBstart = ("string" == typeof(timeB.from)) ? $.time(timeB.from) : timeB.from;
                    var timeBend = ("string" == typeof(timeB.to)) ? $.time(timeB.to) : timeB.to;
                    // 对时间格式进行判.对于两个时间区域,如果每个的开始时间小于0或开始时间大于了自身的结束时间,则抛出异常.
                    if ((timeAstart < 0 || timeAstart > timeAend) ||
                    (timeBstart < 0 || timeBstart > timeBend)) 
                        throw "开始时间不能小于0,且不能大于结束时间!";
                    
                    alert("Astr:" + timeAstart + ",Aend:" + timeAend +
                    "\nBstr:" +
                    timeBstart +
                    ",Bend:" +
                    timeBend);
                    // return
                    // parseInt(startTime.getTime()/1000/3600/24/360) ;
                    if (timeAend < timeBstart || timeBend < timeAstart) {
                        return true;
                    } else {
                        return false;
                    }
                    
                }
                
            }
            
        },
        /*
         * ~~~~~~~~~~~calendar:日历相关~~~~~~~~~~~~~~~~ time
         *
         *
         */
        calendar: function(year, month) {
        
            var MonHead = new Array(12); // 定义阳历中每个月的最大天数
            MonHead[0] = 31;
            MonHead[1] = 28;
            MonHead[2] = 31;
            MonHead[3] = 30;
            MonHead[4] = 31;
            MonHead[5] = 30;
            MonHead[6] = 31;
            MonHead[7] = 31;
            MonHead[8] = 30;
            MonHead[9] = 31;
            MonHead[10] = 30;
            MonHead[11] = 31;
            
            // 如果第一个参数为字符串,则
            if ("string" == typeof(year)) {
                var DateAry = year.split("-");
                if (3 < DateAry.length) 
                    throw "日期字符串数据格林不对.必须为:yyyy-mm-dd";
                
                var DateVal = new Date(DateAry[0], DateAry[1], DateAry[2]);
                // 计算从年初到这天的天数
                var DateNum = (Date.parse(DateAry[1] + " " + DateAry[2] + "," + DateAry[0]) - Date.parse("1 1," + DateAry[0])) / (1000 * 60 * 60 * 24);
                DateW = Math.floor(DateNum / 7) + 1;
                DateW = ((new Date(DateAry[0], 0, 1)).getDay() + Math.floor(DateNum % 7)) > 6 ? DateW + 1 : DateW;
                var DateObj = {
                    title: year,
                    year: DateVal.getFullYear(),
                    month: DateVal.getMonth(),
                    day: DateVal.getDate(),
                    week: DateW
                };
                
                return DateObj;
            }
            
            if (month > 12 || month < 1) 
                throw "月份为1-12之间";
            // 判断并计算本月的上一月及下一月
            var thisMonth = month - 1;
            var prevMonth = thisMonth == 0 ? 11 : thisMonth - 1;
            var nextMonth = thisMonth == 11 ? 1 : thisMonth + 1;
            // 定义calendar中每个月最大天数据 1-12月为从MonHead[0]-MonHead[11]
            var i, j;
            
            // 判断是否闰年
            var IsPinYear = function(year) {
                if (0 == year % 4 &&
                ((year % 100 != 0) || (year % 400 == 0))) 
                    return true;
                else 
                    return false;
            }
            if (IsPinYear(year)) 
                MonHead[1] = 29;
            
            
            var calendarDayAry = new Array(6); // 定义写日期的数组
            // 判断参数year给出的年号值是否润月,是的话,把year中第二个月的天数更新为29天
            var dayNumber = 1;
            var yearVal = year;
            var monthVal = thisMonth;
            // 判断this.month月的每一天是星期几
            var prevDayNum = (new Date(year, thisMonth, 1)).getDay() % 7;
            
            for (i = 0; i < calendarDayAry.length; i++) {
                calendarDayAry[i] = new Array(7);
                for (j = 0; j < calendarDayAry[i].length; j++) {
                    if (i == 0 && j < prevDayNum) {
                        yearVal = thisMonth == 0 ? year - 1 : year;
                        monthVal = prevMonth;
                        var thisDay = new Date(yearVal, monthVal, MonHead[monthVal] - (prevDayNum - j) + 1);
                        
                        calendarDayAry[i][j] = {
                            title: thisDay.getFullYear() + "-" +
                            (thisDay.getMonth() + 1) +
                            "-" +
                            thisDay.getDate(),
                            year: year,
                            month: (thisDay.getMonth() + 1),
                            day: thisDay.getDate(),
                            week: thisDay.getDay()
                        };
                        continue;
                    }
                    // 如果上一月换算完成.则需要把当前月份的信息再换算为当前月的年和月的正确值
                    yearVal = year;
                    monthVal = thisMonth;
                    
                    var thisDay = new Date(yearVal, monthVal, dayNumber);
                    // alert(thisDay);
                    calendarDayAry[i][j] = {
                        title: thisDay.getFullYear() + "-" +
                        (thisDay.getMonth() + 1) +
                        "-" +
                        thisDay.getDate(),
                        year: year,
                        month: (thisDay.getMonth() + 1),
                        day: thisDay.getDate(),
                        week: thisDay.getDay()
                    };
                    // alert(calendarDayAry[i][j].title+"::"+dayNumber);
                    dayNumber++; // 日期加一天
                }
            }
            
            return calendarDayAry;
        },
        /*
         * ~~~~~~~~~~~获取某月有多少天days~~~~~~~~~~~~~~~~
         *
         *
         *
         */
        days: function(year, month) {
            if (!year || !month) 
                throw "年值及月值不能为空!";
            var MonHead = new Array(12); // 定义阳历中每个月的最大天数
            MonHead[0] = 31;
            MonHead[1] = 28;
            MonHead[2] = 31;
            MonHead[3] = 30;
            MonHead[4] = 31;
            MonHead[5] = 30;
            MonHead[6] = 31;
            MonHead[7] = 31;
            MonHead[8] = 30;
            MonHead[9] = 31;
            MonHead[10] = 30;
            MonHead[11] = 31;
            if (month > 12 || month < 1) 
                throw "月份为1-12之间";
            // 判断是否闰年
            var IsPinYear = function(year) {
                if (0 == year % 4 &&
                ((year % 100 != 0) || (year % 400 == 0))) 
                    return true;
                else 
                    return false;
            }
            // 如果当年是润年则把当年的第二个月的天数更新为29天
            if (IsPinYear(year)) 
                MonHead[1] = 29;
            return MonHead[month - 1];
            
        },
        /*
         * ~~~~~~~~~~~quartz表达式~~~~~~~~~~~~~~~~ opt 操作对像 Stirng | object
         * pram 操作参数 String | object
         * 组合形式(String)|(object)|(object,object)|(object,String)|(String,object)|(String,String)
         */
        quartz: function(opt, pram) {
            // ~~~~~~~~~~~~~全局函数定义开始~~~~~~~~~~~~~~~
            // 定义全局月份和星期表进行格式化所用到的全局对像表
            function quaStr2NumForm(qstr) {// quz字符串中的所有英文代替的值格式化成数字代替的值
                if ("string" != typeof(qstr)) 
                    throw "not type of string!";
                var mObj = {
                    JAN: "1",
                    FEB: "2",
                    MAR: "3",
                    APR: "4",
                    MAY: "5",
                    JUN: "6",
                    JUL: "7",
                    AUG: "8",
                    SEP: "9",
                    OCT: "10",
                    NOV: "11",
                    DEC: "12",
                    SUN: "0",
                    MON: "1",
                    TUE: "2",
                    WED: "3",
                    THU: "4",
                    FRI: "5",
                    SAT: "6"
                };
                var mObjAry = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"];
                // 先把quaz字符串全部转换成大写
                qstr = qstr.toUpperCase();
                for (var i = 0; i < mObjAry.length; i++) {
                    if (qstr.indexOf(mObjAry[i]) != -1) {
                        qstr = qstr.replace(mObjAry[i], mObj[mObjAry[i]]);
                    }
                }
                return qstr;
            }
            
            // 定义单项拆分功能函数
            
            
            function string2quartz(obj) {
                if ("*" == obj || "" == obj) {
                    return {
                        type: "*"
                    };
                } else if ("?" == obj) {
                    return {
                        type: null
                    };
                } else if (obj.indexOf(',') != -1) {
                    var qary = obj.split(',');
                    return {
                        type: ",",
                        scope: qary
                    };
                } else if (obj.indexOf("-") != -1) {
                    var qary = obj.split("-");
                    if (qary.length != 2) 
                        throw "'-'分割失败,要分割的字符串应是'a-b或3-5'这种形式";
                    return {
                        type: "-",
                        from: qary[0],
                        to: qary[1]
                    };
                } else if (obj.indexOf('L') != -1) {
                    return {
                        type: 'L',
                        value: obj.replace('L', '')
                    };
                } else if (obj.indexOf('W') != -1) {
                    var qary = obj.split('W');
                    return {
                        type: 'W',
                        value: qary[0]
                    };
                } else if (obj.indexOf('/') != -1) {
                    var qary = obj.split('/');
                    return {
                        type: '/',
                        start: qary[0],
                        step: qary[1]
                    };
                    
                } else if (obj.indexOf('#') != -1) {
                    var qary = obj.split('#');
                    return {
                        type: '#',
                        value: qary[0],
                        num: qary[1]
                    };
                } else {
                    return {
                        type: '',
                        value: obj
                    };
                }
                
            }
            function quartz2string(obj) {
                if ("object" != typeof(obj)) 
                    throw "Not is a object! What do you want me to? ";
                if (obj.type == "*") {
                    return "*";
                } else if (obj.type == null) {
                    return "?";
                } else if (obj.type == '-') {
                    return obj.from + "-" + obj.to;
                } else if (obj.type == 'L') {
                    return obj.value + 'L';
                } else if (obj.type == ',') {
                    var thisStr = "";
                    for (var i = 0; i < obj.scope.length; i++) {
                        if (i != 0) {
                            thisStr += ",";
                        }
                        thisStr += obj.scope[i];
                    }
                    return thisStr;
                } else if (obj.type == '/') {
                    return obj.start + "/" + obj.step;
                } else if (obj.type == 'W') {
                    return obj.value + "W";
                    
                } else if (obj.type == '#') {
                    return obj.value + "#" + obj.num;
                } else if (obj.type == '') {
                    return obj.value;
                }
            }
            //
            function checkMap(obj, value) {// 检测匹配是否,返回值是一个bool值只检查通用的如:* -
                // , /四种符号
                
                if (obj.type == '*' || obj.type == '?') {
                    return true;
                } else if (obj.type == null) {
                    return true;
                } else if (obj.type == '-') {// 返回大于等于开始年份,或大于开始年份且小于等于结束年份
                    return parseInt(obj.from) < parseInt(value) && parseInt(value) <= parseInt(obj.to) || parseInt(obj.from) == parseInt(value);
                } else if (obj.type == ',') {
                
                    var flag = false;
                    value = parseInt(value);
                    // 拆分逗号里的带有'-'字符的数组,组成新的数组
                    for (var i = 0; i < obj.scope.length; i++) {
                    
                        if ((obj.scope[i]).indexOf('-') != -1) {// 如果数组中有某种连接符.则要做特别处理
                            var tempAry = obj.scope[i].split('-');
                            flag = (parseInt(tempAry[0]) < parseInt(value) && parseInt(value) <= parseInt(tempAry[1])) || (parseInt(tempAry[0]) == parseInt(value));
                            break;
                            
                        } else if (parseInt(value) == parseInt(obj.scope[i])) {
                        
                            flag = true;
                            break;
                        }
                    }
                    return flag;
                } else if (obj.type == '/') {
                
                    return 0 == (parseInt(value) - obj.start) % obj.step;
                } else if (obj.type == '') {
                    return parseInt(obj.value) == parseInt(value);
                } else {
                    throw "Invalid argument!";
                }
            }
            // 对当天的时间(小时,分钟,秒进行处理)
            function timeFormat2sec(tObj, tLoop, tWeigth) {// 时间格式化成秒:第几小时,第几分种,第几秒tObj时间对像,tLoop最大循环次数,tWeigth时间权重
                if ("object" != typeof(tObj)) {
                    throw "First must be a object of time";
                }
                var timeAry = new Array();
                
                if ('*' == tObj.type) {
                    for (var i = 0; i < tLoop; i++) {
                        timeAry.push(i * tWeigth);
                    }
                } else if (',' == tObj.type) {
                    for (var i = 0; i < tObj.scope.length; i++) {
                        if (tObj.scope[i].indexOf('-') != -1) {
                            var tempAry = tObj.scope[i].split('-');
                            for (var j = parseInt(tempAry[0]); j <= parseInt(tempAry[1]); j++) {
                                timeAry.push(j * tWeigth);
                            }
                        } else {
                            timeAry.push(parseInt(tObj.scope[i]) * tWeigth);
                        }
                        
                    }
                } else if ('-' == tObj.type) {
                    for (var i = parseInt(tObj.from); i <= parseInt(tObj.to); i++) {
                        timeAry.push(pasrseInt(i) * tWeigth);
                    }
                } else if ('/' == tObj.type) {
                    for (var i = parseInt(tObj.start); i < parseInt(tLoop); i = i + parseInt(tObj.step)) {
                        timeAry.push(parseInt(i * tWeigth));
                    }
                } else if ('' == tObj.type) {
                    timeAry.push(paseInt(tObj.value).tWeigth);
                }
                return timeAry;
                
            }
            // ~~~~~~~~~~~~~全局函数定义完毕~~~~~~~~~~~~~~~
            
            // 开始接收数据并处理
            if ("string" == typeof(opt) && !pram) {// 如果opt为String,且第二参数为空,则说明要做解析处理
                var qary = opt.split(" ");
                var newQary = new Array();
                for (var i = 0; i < qary.length; i++) {
                    if (qary[i] != "") {
                        newQary.push(string2quartz(qary[i]));// 调用函数对字符串进行对像格式化处理
                    }
                }
                
                return {
                    sec: newQary[0],
                    min: newQary[1],
                    hour: newQary[2],
                    day: newQary[3],
                    month: newQary[4],
                    year: newQary.length == 7 ? newQary[6] : {
                        type: '*'
                    }, // 如果为空.则不做处理
                    week: newQary[5],
                };
                
            } else if ("object" == typeof(opt) && !pram) {
            
                var quarzStr = "";
                quarzStr = " " + quartz2string(opt.sec);
                quarzStr += " " + quartz2string(opt.min);
                quarzStr += " " + quartz2string(opt.hour);
                quarzStr += " " + quartz2string(opt.day);
                quarzStr += " " + quartz2string(opt.month);
                quarzStr += " " + quartz2string(opt.week);
                quarzStr += " " + quartz2string(opt.year);
                return quarzStr;
            } else if (opt && pram) {// 执行操作
                if ("object" == typeof(opt) && "object" == typeof(pram)) {
                    // 这步很重要(首先对opt对像转化成quarz字符串,然后再对此字符串进行数字化格式化,最后再先成quarz对像)
                    opt = ($.quartz(quaStr2NumForm($.quartz(opt))));
                    // return($.dump(opt));
                } else if ("object" == typeof(opt) && "string" == typeof(pram)) {
                    // 这步很重要(首先对opt对像转化成quarz字符串,然后再对此字符串进行数字化格式化,最后再先成quarz对像)
                    opt = ($.quartz(quaStr2NumForm($.quartz(opt))));
                    pram = $.calendar(pram); // 把提供的日期值处理成date对像
                    // return($.dump(opt));
                } else if ("string" == typeof(opt) && "object" == typeof(pram)) {
                    opt = $.quartz(quaStr2NumForm(opt));// 把提供的quartz字符串处理成quartz对像
                    // return($.dump(opt));
                } else if ("string" == typeof(opt) && "string" == typeof(pram)) {
                
                    pram = $.calendar(pram); // 把提供的日期值处理成date对像
                    opt = $.quartz(quaStr2NumForm(opt));// 把提供的quartz字符串格式化后处理成quartz对像
                    // return($.dump(opt));
                } else {
                    throw "parameters illegal!";
                }
                // 对年进行处理 如果年份给出在1970-2099之间.则再进行下一步的判断.否则退出
                if (opt.year) {// 如果年为空,则不做处理如果为空,则不做处理
                    if (parseInt(pram.year) >= 1970 && parseInt(pram.year) < 2099 || parseInt(pram.year) == 2099) {
                        // 如果年份符合1970-2099后.但不符合特定表达式中所限定的约定则返回空数组.下边不再执行
                        if (!checkMap(opt.year, pram.year)) {
                            return "年份通不过";// return [];
                        }
                    } else {
                        return "年份不在1970-2099之间";// return [];
                    }
                } else {
                
                    return "年为空";
                }
                // (Month-1)对月进行处理
                // 首先检查月份是否符合1-12的要求(calendar控件已经对月进行了正确性检查).再进行下一步操作
                
                // (Month-2)检查月份是否能通过
                if (!checkMap(opt.month, pram.month)) {
                
                    return "月份通不过检查" + $.dump(pram.month);// return [];
                }
                // 对日进行处理 --选 对日进行一个通用处理(,?*-/)
                
                if (!checkMap(opt.day, pram.day)) {
                    return $.dump(pram.day) + "不在:" + $.dump(pram.day);
                }
                // ======接下来对当天进行时间详细处理=========
                var oHour = timeFormat2sec(opt.hour, 24, 3600); // 先处理小时,返回一个数组
                var oMin = timeFormat2sec(opt.min, 60, 60); // 再处理分钟
                var oSec = timeFormat2sec(opt.min, 60, 1); // 再处理秒
                var quarzAry = new Array();
                for (var i = 0; i < oHour.length; i++) {
                    for (var j = 0; j < oMin.length; j++) {
                        for (var l = 0; l < oSec.length; l++) {
                            quarzAry.push(oHour[i] + oMin[j] + oSec[l]);
                        }
                    }
                    
                }
                // return timeFormat2sec(opt.hour,24,3600);
                // return timeFormat2sec(opt.min,60,60);
                return "长度:" + quarzAry.length + "[" + quarzAry + "]"; // 最终返回的一个数组值
            }
            
        }
    };
    // 增加至 jQuery 全局插件 ...    
    $.extend(_G);
})(window.jQuery);
