var urlData = location.href.split('?')[1].split("=");
var userId = urlData[0];
var gymList = [];//门店列表，用于消费记录列表中根据门店id获取门店名字
var userCourse = {};//用户预约课程列表数据
var orderChart;//预约情况图表
var classChart;//上课时间图表
$(function () {

    // 初始化健身房数据
    $.post(appPath + '/gym/getGyms.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var gym = '<option value="">门店筛选</option>';
            gymList = res.data;
            for (var i = 0; i < res.data.length; i++) {
                gym += '<option value="' + res.data[i].gymId + '">' + res.data[i].gymName + '</option>';
            }
            $(".gymId").html(gym);

        } else {
            layer.msg(res.msg);
        }
    })
    //会员标签切换
    $(".tab-button").on("click", function () {
        var $this = $(this);
        $(".tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        var divId = $this.attr("data-id");
        var init = $this.attr("data-init");//是否是初始化datatables
        $(".box-body").addClass("hidden");
        $("#" + divId).removeClass("hidden");
        $(window).resize();
        if (init == 1) {
            $this.attr("data-init", "2");
            if (divId == 'orderInfo') {
                orderTable();//初始化预约课程列表
            } else if (divId == 'expenseInfo') {
                expenseTable();//初始化消费记录列表
            }
            // 初始化教练数据
            $.post(appPath + '/user/getUsers.do', {"userType": 2, "status": 1}, function (res) {
                if (res.code == 0) {
                    var coach = '<option value="">教练</option>';
                    for (var i = 0; i < res.data.length; i++) {
                        coach += '<option value="' + res.data[i].userId + '">' + res.data[i].name + '</option>';
                    }
                    $("#coachId").html(coach);

                } else {
                    layer.msg(res.msg);
                }
            })
        }
    });
    //搜索条件变化
    $(".conditions").on("change", function () {
        var divId = $(".tab-button.btn-item-checked").attr("data-id");
        $('#' + divId + "_table").DataTable().ajax.reload();
    });

    // 初始化用户消费概览数据
    $.post(appPath + '/system/getUserView.do', {"userId": userId}, function (res) {
        if (res.code == 0) {
            if (res.data.totalAmount) {
                $("#totalAmount").html(res.data.totalAmount);
            } else {
                $("#totalAmount").html(0);
            }
            if (res.data.monthAmount) {
                $("#monthAmount").html(res.data.monthAmount);
            } else {
                $("#monthAmount").html(0);
            }
            if (res.data.monthNum) {
                $("#monthNum").html(res.data.monthNum);
            } else {
                $("#monthNum").html(0);
            }
        } else {
            layer.msg(res.msg);
        }
    })
    // 预约情况图表
    orderChart = echarts.init(document.getElementById('order'));
    queryOrder();
    //预约情况天数标签切换
    $("#order-div .btn-item").on("click", function () {
        var $this = $(this);
        $("#order-div .btn-item").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        $("#orderStart").val("");
        $("#orderEnd").val("");
        queryOrder();
        $(window).resize();
    });
    //预约情况自定义时间查询
    $("#order-div .chart-date").on("change", function () {
        $("#order-div .btn-item").removeClass("btn-item-checked");
        queryOrder();
        $(window).resize();
    })
    //周、月标签切换
    $(".tab-time").on("click", function () {
        var $this = $(this);
        $(".tab-time").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        queryOrder();
        $(window).resize();
    });

    //上课时间图表
    classChart = echarts.init(document.getElementById('classHour'));
    queryClass();
    //上课时间天数标签切换
    $("#class-div .btn-item").on("click", function () {
        var $this = $(this);
        $("#class-div .btn-item").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        $("#classStart").val("");
        $("#classEnd").val("");
        queryClass();
        $(window).resize();
    });
    //上课时间自定义时间查询
    $("#class-div .chart-date").on("change", function () {
        $("#class-div .btn-item").removeClass("btn-item-checked");
        queryClass();
        $(window).resize();
    })

    // 初始化数据
    $.post(appPath + '/user/getUserDetail.do', {'userId': userId}, function (res) {
        if (res.code == 0) {
            if (res.data.headimgurl) {
                if (res.data.headimgurl.indexOf("http") == -1) {
                    $("#headimgurl").prop("src", imgViewPath + res.data.headimgurl);
                } else {
                    $("#headimgurl").prop("src", res.data.headimgurl);
                }
            }
            $("#navName").html(res.data.name);
            $("#name").html(res.data.name);
            $("#phone").html(res.data.phone);
            if (res.data.status == 1) {
                $("#status").html("正常");
            } else if (res.data.status == 2) {
                $("#status").html("已封禁");
            }
            var cardList = res.data.cardList;
            var html = "";
            for (var i = 0; i < cardList.length; i++) {
                if (cardList[i].cardType == 1) {
                    html += '<sapn style="margin-right:10px; ">会员储值卡</sapn><a href="' + appPath + '/pages/cardDetail.html?' + cardList[i].cardId + '">' + cardList[i].cardNo + '</a><br>';
                } else if (cardList[i].cardType == 2) {
                    html += '<span style="margin-right:25px; ">会员次卡</span><a href="' + appPath + '/pages/cardDetail.html?' + cardList[i].cardId + '">' + cardList[i].cardNo + '</a><br>';
                }
            }
            $("#cardList").html(html);
            $("#intro").html(res.data.intro);
        } else {
            layer.msg(res.msg);
        }
    });


});

function orderTable() {
    // 表格初始化
    $('#orderInfo_table').DataTable({
        ajax: {
            url: appPath + '/course/getUserCourse.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var courseType = $("#courseType").val();
                var coachId = $("#coachId").val();
                var startDate = $.trim($("#startDate").val());
                var endDate = $.trim($("#endDate").val());
                var gymId = $("#gymId").val();
                var queryType = $("#orderStatus").val();
                d.courseType = courseType;
                d.queryUserId = userId;
                d.coachId = coachId;
                d.startDate = startDate;
                d.endDate = endDate;
                d.gymId = gymId;
                d.queryType = queryType;
                d.status = "3,5";
            },
            dataSrc: function (json) {
                if (json.code == 0) {
                    json.recordsTotal = json.data.recordsTotal;
                    json.recordsFiltered = json.data.recordsFiltered;
                    json.draw = json.data.draw;
                    if (json.data.results == null) {
                        return false;
                    } else {
                        // 维护返回的json
                        for (var i = 0; i < json.data.results.length; i++) {
                            userCourse['' + json.data.results[i].courseId] = json.data.results[i];
                        }
                        return json.data.results;
                    }
                } else {
                    layer.msg(json.msg);
                    json.recordsTotal = 0;
                    json.recordsFiltered = 0;
                    return [];
                }
            },
            error: function (data) {
                layer.msg("系统异常，请联系管理员");
            }
        },
        columns: [
            {
                title: "课程名称", data: "courseName", render: function (data, type, full) {
                return '<a onclick="toCourseDeatilPage(this)" data-id="' + full.courseId + '" data-href="' + appPath + '/pages/courseDetail.html?' + full.courseId + '" href="javascript:void(0)">' + data + '</a>';
            }
            },
            {
                title: "教练", data: "name", render: function (data, type, full) {
                return '<a onclick="toCoachDeatilPage(this)" data-id="' + full.courseId + '" data-href="' + appPath + '/pages/coachDetail.html?' + full.userId + '"  href="javascript:void(0)">' + data + '</a>';
            }
            },
            {title: "健身房", data: "gymName"},
            {
                title: "日期", data: "classHour", render: function (data, type, full) {
                return formatDate(data, 1);
            }
            },
            {
                title: "时间", data: "classHour", render: function (data, type, full) {
                var str = "";
                str += formatDate(data, 2);
                str += " - ";
                str += formatDate((data + 60 * 1000 * full.minutes), 2);
                return str;
            }
            },
            {
                title: "状态", data: "orderStatus", render: function (data, type, full) {
                var str = "";
                if (full.orderStatus == 3) {
                    str = "预约";
                } else if (full.orderStatus == 5) {
                    str = "退课";
                }
                return str;
            }
            }
        ],
        sort: false,// 禁止排序
        pagingType: "full_numbers",// 设置分页控件的模式
        info: true,// 页脚信息
        processing: true, // 打开数据加载时的等待效果
        serverSide: true,// 打开后台分页
        searching: false,
        lengthChange: false,// 显示每页多少条数据
        lengthMenu: [10, 20, 50, 100],
        scrollCollapse: true,
        scrollX: "100%",
        scrollXInner: "100%",
        drawCallback: function (settings) {
        },
        language: { //汉化
            "lengthMenu": "每页显示 _MENU_ 条数据",
            "emptyTable": "没有检索到数据",
            "zeroRecords": "没有检索到数据",
            "info": "共 _TOTAL_ 条数据",
            "sInfoEmpty": "无数据",
            "processing": "正在加载数据...",
            "paginate": {
                "first": "首页",
                "previous": "上一页",
                "next": "下一页",
                "last": "尾页"
            }
        }

    });// 表格初始化完毕
}

function expenseTable() {
    // 表格初始化
    $('#expenseInfo_table').DataTable({
        ajax: {
            url: appPath + '/order/getOrderList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var orderType = $("#orderType").val();
                var gym = $("#gym").val();
                d.orderType = orderType;
                d.status = 3;//查询状态为已完成订单
                d.queryUserId = userId;
                d.gymId = gym;
                d.queryCardNo = 1;//是否查询会员卡编号 1：是
            },
            dataSrc: function (json) {
                if (json.code == 0) {
                    json.recordsTotal = json.data.recordsTotal;
                    json.recordsFiltered = json.data.recordsFiltered;
                    json.draw = json.data.draw;
                    if (json.data.results == null) {
                        return false;
                    } else {
                        return json.data.results;
                    }
                } else {
                    layer.msg(json.msg);
                    json.recordsTotal = 0;
                    json.recordsFiltered = 0;
                    return [];
                }
            },
            error: function (data) {
                layer.msg("系统异常，请联系管理员");
            }
        },
        columns: [
            {
                title: "会员卡", data: "queryCardNo", render: function (data, type, full) {
                var str = "";
                if (full.cardType == 1) {
                    str += '<sapn style="margin-right:10px; ">会员储值卡</sapn><a href="' + appPath + '/pages/cardDetail.html?' + full.cardId + '">' + full.cardNo + '</a>';
                } else if (full.cardType == 2) {
                    str += '<span style="margin-right:25px; ">会员次卡</span><a href="' + appPath + '/pages/cardDetail.html?' + full.cardId + '">' + full.cardNo + '</a>';
                }
                return str;
            }
            },
            {
                title: "金额", data: "amount", render: function (data, type, full) {
                var str = "";
                if (full.status == 3) {
                    str += "-￥";
                } else if (full.status == 5) {
                    str += "+￥";
                }
                str += full.amount;
                return str;
            }
            },
            {
                title: "说明", data: "orderId", render: function (data, type, full) {
                var str = "";
                if (full.orderType == 1 && full.status == 3) {
                    str = "购买课程<a href='" + appPath + "/pages/courseDetail.html?" + full.courseId + "'>" + full.courseName + "</a>";
                } else if (full.orderType == 1 && full.status == 5) {
                    str = "退课<a href='" + appPath + "/pages/courseDetail.html?" + full.courseId + "'>" + full.courseName + "</a>";
                } else if (full.orderType == 2) {
                    str = "购买会员卡套餐";
                } else if (full.orderType == 3) {
                    str = "会员卡余额过期";
                }
                return str;
            }
            },
            {
                title: "门店", data: "gymId", render: function (data, type, full) {
                var str = "";
                for (var i = 0; i < gymList.length; i++) {
                    if (gymList[i].gymId == data) {
                        str = gymList[i].gymName;
                        break;
                    }
                }
                return str;
            }
            },
            {
                title: "时间", data: "updateTime", render: function (data, type, full) {
                var str = "";
                str += formatDate(data, 3);
                return str;
            }
            }
        ],
        sort: false,// 禁止排序
        pagingType: "full_numbers",// 设置分页控件的模式
        info: true,// 页脚信息
        processing: true, // 打开数据加载时的等待效果
        serverSide: true,// 打开后台分页
        searching: false,
        lengthChange: false,// 显示每页多少条数据
        lengthMenu: [10, 20, 50, 100],
        scrollCollapse: true,
        scrollX: "100%",
        scrollXInner: "100%",
        drawCallback: function (settings) {
        },
        language: { //汉化
            "lengthMenu": "每页显示 _MENU_ 条数据",
            "emptyTable": "没有检索到数据",
            "zeroRecords": "没有检索到数据",
            "info": "共 _TOTAL_ 条数据",
            "sInfoEmpty": "无数据",
            "processing": "正在加载数据...",
            "paginate": {
                "first": "首页",
                "previous": "上一页",
                "next": "下一页",
                "last": "尾页"
            }
        }

    });// 表格初始化完毕
}

function toCourseDeatilPage(obj) {
    var id = $(obj).attr("data-id");
    var href = $(obj).attr("data-href");
    var rowData = userCourse['' + id];//原始行json数据
    if (rowData.status == 3) {
        layer.msg("该课程已删除");
        return;
    }
    window.location.href = href;
}

function toCoachDeatilPage(obj) {
    var id = $(obj).attr("data-id");
    var href = $(obj).attr("data-href");
    var rowData = userCourse['' + id];//原始行json数据
    if (rowData.userStatus == 3) {
        layer.msg("该教练已删除");
        return;
    }
    window.location.href = href;
}
/**
 * 查询预约情况图表
 */
function queryOrder() {
    var groupType = $(".tab-time.btn-item-checked").attr("data-type");
    var startDate = "";
    var endDate = "";
    var days = $("#order-div .btn-item-checked").attr("data-days");
    if (days) {
        var tempDate = new Date();
        endDate = formatDate(tempDate.getTime(), 1);
        tempDate.setTime(tempDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(days));
        startDate = formatDate(tempDate.getTime(), 1);
    } else {
        startDate = $.trim($("#orderStart").val());
        endDate = $.trim($("#orderEnd").val());
    }
    $.post(appPath + '/system/getOrderView.do', {
        "userId": userId,
        "startDate": startDate,
        "endDate": endDate,
        "groupType": groupType
    }, function (res) {
        if (res.code == 0) {
            var xData = [];
            var yDataA = [];//团课
            var yDataB = [];//私教
            var yDataC = [];//活动
            var mapA = new Map();
            var mapB = new Map();
            var mapC = new Map();
            for (var i = 0; i < res.data.length; i++) {
                if (groupType == 1) {
                    //按周查询
                    xData.push(res.data[i].day);
                    if (res.data[i].type == 1 && res.data[i].courseType == 1) {
                        mapA.set(res.data[i].day, res.data[i].orderNum);
                    } else if (res.data[i].type == 1 && res.data[i].courseType == 2) {
                        mapB.set(res.data[i].day, res.data[i].orderNum);
                    } else if (res.data[i].type == 2) {
                        mapC.set(res.data[i].day, res.data[i].orderNum);
                    }
                } else {
                    //按月查询
                    xData.push(res.data[i].orderMonth);
                    if (res.data[i].type == 1 && res.data[i].courseType == 1) {
                        mapA.set(res.data[i].orderMonth, res.data[i].orderNum);
                    } else if (res.data[i].type == 1 && res.data[i].courseType == 2) {
                        mapB.set(res.data[i].orderMonth, res.data[i].orderNum);
                    } else if (res.data[i].type == 2) {
                        mapC.set(res.data[i].orderMonth, res.data[i].orderNum);
                    }
                }
            }
            xData = unique(xData);//过滤重复数据
            if (xData.length == 0) {
                xData[0] = "无数据";
            } else {
                for (var i = 0; i < xData.length; i++) {
                    if (mapA.has(xData[i])) {
                        yDataA.push(mapA.get(xData[i]));
                    } else {
                        yDataA.push(0);
                    }
                    if (mapB.has(xData[i])) {
                        yDataB.push(mapB.get(xData[i]));
                    } else {
                        yDataB.push(0);
                    }
                    if (mapC.has(xData[i])) {
                        yDataC.push(mapC.get(xData[i]));
                    } else {
                        yDataC.push(0);
                    }
                }
            }
            // 指定图表的配置项和数据
            var option = {
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    left:"right",
                    data: ['预约团课', "预约私教", "预约活动"]
                },
                xAxis: {
                    data: xData
                },
                yAxis: {},
                series: [{
                    name: '预约团课',
                    type: 'line',
                    itemStyle: {'normal': {'color': 'green'}},
                    lineStyle: {'normal': {'color': 'green'}},
                    data: yDataA
                }, {
                    name: '预约私教',
                    type: 'line',
                    itemStyle: {'normal': {'color': '#5D9BDD'}},
                    lineStyle: {'normal': {'color': '#5D9BDD'}},
                    data: yDataB
                }, {
                    name: '预约活动',
                    type: 'line',
                    itemStyle: {'normal': {'color': 'red'}},
                    lineStyle: {'normal': {'color': 'red'}},
                    data: yDataC
                }]
            };
            // 使用刚指定的配置项和数据显示图表。
            orderChart.setOption(option);
        } else {
            layer.msg(res.msg);
        }
    })
}

/**
 * 查询上课时间图表
 */
function queryClass() {
    var startDate = "";
    var endDate = "";
    var days = $("#class-div .btn-item-checked").attr("data-days");
    if (days) {
        var tempDate = new Date();
        endDate = formatDate(tempDate.getTime(), 1);
        tempDate.setTime(tempDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(days));
        startDate = formatDate(tempDate.getTime(), 1);
    } else {
        startDate = $.trim($("#classStart").val());
        endDate = $.trim($("#classEnd").val());
    }
    $.post(appPath + '/system/getClassHourView.do', {
        "userId": userId,
        "startDate": startDate,
        "endDate": endDate
    }, function (res) {
        if (res.code == 0) {
            var xData = ["7:00以前", "7:00-10:00", "10:00-12:00", "12:00-15:00", "15:00-19:00", "19:00-21:00", "21:00以后"];
            var yData = [];
            var mapY = new Map();
            for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].classHour < 7) {
                    if (mapY.has("7:00以前")) {
                        mapY.set("7:00以前", parseInt(mapY.get("7:00以前")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("7:00以前", res.data[i].classNum);
                    }
                } else if (res.data[i].classHour >= 7 && res.data[i].classHour < 10) {
                    if (mapY.has("7:00-10:00")) {
                        mapY.set("7:00-10:00", parseInt(mapY.get("7:00-10:00")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("7:00-10:00", res.data[i].classNum);
                    }
                } else if (res.data[i].classHour >= 10 && res.data[i].classHour < 12) {
                    if (mapY.has("10:00-12:00")) {
                        mapY.set("10:00-12:00", parseInt(mapY.get("10:00-12:00")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("10:00-12:00", res.data[i].classNum);
                    }
                } else if (res.data[i].classHour >= 12 && res.data[i].classHour < 15) {
                    if (mapY.has("12:00-15:00")) {
                        mapY.set("12:00-15:00", parseInt(mapY.get("12:00-15:00")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("12:00-15:00", res.data[i].classNum);
                    }
                } else if (res.data[i].classHour >= 15 && res.data[i].classHour < 19) {
                    if (mapY.has("15:00-19:00")) {
                        mapY.set("15:00-19:00", parseInt(mapY.get("15:00-19:00")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("15:00-19:00", res.data[i].classNum);
                    }
                } else if (res.data[i].classHour >= 19 && res.data[i].classHour < 21) {
                    if (mapY.has("19:00-21:00")) {
                        mapY.set("19:00-21:00", parseInt(mapY.get("19:00-21:00")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("19:00-21:00", res.data[i].classNum);
                    }
                } else if (res.data[i].classHour >= 21) {
                    if (mapY.has("21:00以后")) {
                        mapY.set("21:00以后", parseInt(mapY.get("21:00以后")) + parseInt(res.data[i].classNum));
                    } else {
                        mapY.set("21:00以后", res.data[i].classNum);
                    }
                }
            }
            for (var i = 0; i < xData.length; i++) {
                var hasValue = false;
                mapY.forEach(function (value, key, map) {
                    if (xData[i] == key) {
                        yData[i] = value;
                        hasValue = true;
                    }
                });
                if (!hasValue) {
                    yData[i] = 0;
                }
            }
            // 指定图表的配置项和数据
            var option = {
                tooltip: {
                    trigger: 'axis'
                },
                legend: {},
                xAxis: {
                    data: xData
                },
                yAxis: {},
                series: [{
                    name: '上课次数',
                    type: 'line',
                    itemStyle: {'normal': {'color': '#5D9BDD'}},
                    lineStyle: {'normal': {'color': '#5D9BDD'}},
                    data: yData
                }]
            };
            // 使用刚指定的配置项和数据显示图表。
            classChart.setOption(option);
        } else {
            layer.msg(res.msg);
        }
    })
}

/**
 * 过滤数组中重复元素
 */
function unique(arr) {
    var result = [], hash = {};
    for (var i = 0, elem; (elem = arr[i]) != null; i++) {
        if (!hash[elem]) {
            result.push(elem);
            hash[elem] = true;
        }
    }
    return result;
}