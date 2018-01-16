var pageType = location.href.split('?')[1];
var functionId = "";
$(function () {
    if (pageType == 1) {
        $("#sideMenu").attr("data-pageid", "13_14");
        functionId = 14;
        $("#addBtn").html("新增课程");
    } else {
        $("#sideMenu").attr("data-pageid", "13_24");
        functionId = 24;
        $("#addBtn").html("新增私教");
    }

    //标签切换
    $(".tab-button").on("click", function () {
        var $this = $(this);
        $(".tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        var dataPage = $this.attr("data-page");
        window.location.href = appPath + '/pages/' + dataPage + '?' + pageType;
    });

    //列表、日历切换
    $(".list-type").on("click", function () {
        var $this = $(this);
        var id = $this.attr("data-id");
        $(".show-div").addClass("hidden");
        $("#" + id).removeClass("hidden");
        if (id == "calendar-div") {
            $("#exportBtn").addClass("hidden");
            $(".conditions").parent().addClass("hidden");
            $(".calendar-condition").parent().removeClass("hidden");
        } else {
            $(".conditions").parent().removeClass("hidden");
            $("#exportBtn").removeClass("hidden");
        }
        $(".conditions").val("");
        $(".conditions").change();
        $(window).resize();
    });

    // 日历初始化
    var calendarObj = $('#calendar').fullCalendar({
        header: {
            left: '',
            center: 'prev title next',
            right: 'month,basicWeek'
        },
        showNonCurrentDates: false,//只展示一个月的数据
        events: function (start, end, timezone, callback) {
            $.ajax({
                url: appPath + '/course/getCourseList.do',
                dataType: 'json',
                data: {
                    templateId: $("#templateId").val(),
                    userId: $("#userId").val(),
                    conditions: $.trim($("#conditions").val()),
                    startDate: start.format(),
                    courseType: pageType,
                    endDate: end.format()
                },
                success: function (doc) {
                    var events = [];
                    var results = doc.data.results;
                    for (var i = 0; i < results.length; i++) {
                        events.push({
                            title: results[i].name + ' - ' + results[i].courseName+'('+results[i].orderNum+'/'+results[i].maxNum+')',
                            start: results[i].classHour,
                            backgroundColor: '#5D9BDD',
                            url: appPath + '/pages/courseDetail.html?' + results[i].courseId
                        });
                    }
                    callback(events);
                }
            })
        }
    })

    /**
     * 条件搜索
     */
    $(".conditions").on("change", function () {
        calendarObj.fullCalendar('refetchEvents');
    });

    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/course/getCourseList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var templateId = $("#templateId").val();
                var userId = $("#userId").val();
                var startDate = $.trim($("#startDate").val());
                var endDate = $.trim($("#endDate").val());
                var gymId = $("#gymId").val();
                var status = $("#status").val();
                d.conditions = conditions;
                d.templateId = templateId;
                d.userId = userId;
                d.startDate = startDate;
                d.endDate = endDate;
                d.gymId = gymId;
                d.queryStatus = status;
                d.courseType = pageType;
                d.functionId = functionId;
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
                title: '<input type="checkbox" class="table-checkbox"  value="1" />',
                width: "2%",
                render: function (data, type, full) {
                    return '<input type="checkbox" class="tr-checkbox" value="' + full.courseId + '"  />';
                }
            },
            {
                title: "课程名称", data: "courseName", render: function (data, type, full) {
                var page = $("#sideMenu").attr("data-pageid");
                return '<a href="' + appPath + '/pages/courseDetail.html?' + full.courseId + '">' + data + '</a>';
            }
            },
            {
                title: "教练", data: "name", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/coachDetail.html?' + full.userId + '">' + data + '</a>';
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
                title: "课程状态", data: "status", render: function (data, type, full) {
                var now = new Date();
                var startTime = new Date(full.classHour);
                var endTime = new Date(full.classHour + 60 * 1000 * full.minutes);
                if (full.status == 2) {
                    return "已关闭";
                } else if (endTime < now) {
                    return "已完结";
                } else if (startTime < now && endTime > now) {
                    return "开课中";
                } else {
                    return full.orderNum + "/" + full.maxNum;
                }
            }
            },
            {
                title: "操作", data: "courseId",
                "mRender": function (data, type, full) {
                    var now = new Date();
                    var startTime = new Date(full.classHour);
                    var endTime = new Date(full.classHour + 60 * 1000 * full.minutes);
                    var str = "";
                    str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "'>编辑</button>&nbsp;";
                    if (full.status == 1 && full.stopOrder == 0) {
                        str += "<button type='button' class='offBtn btn btn-default' data-id='" + data + "' >关闭课程</button>&nbsp;";
                    }
                    //在课程已完结、已关闭或者未开课但预约数为0时显示删除按钮
                    if (full.status == 2 || endTime < now || full.orderNum == 0) {
                        str += "<button type='button' class='delBtn btn btn-default' data-id='" + data + "'>删除</button>";
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
        lengthChange: true,// 显示每页多少条数据
        lengthMenu: [10, 20, 50, 100],
        scrollCollapse: true,
        scrollX: "100%",
        scrollXInner: "100%",
        drawCallback: function (settings) {
            clickEvent();// 列表中按钮绑定事件
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

    //表格多选效果
    $('#table_id tbody').on('click', 'tr', function () {
        var $this = $(this);
        var $checkBox = $this.find("input:checkbox");
        if ($this.hasClass('selected')) {
            $checkBox.prop("checked", false);
            $(".table-checkbox").prop("checked", false);
        } else {
            $checkBox.prop("checked", true);
        }
        $this.toggleClass('selected');
    });

    //全选效果
    $(".table-checkbox").on('click', function () {
        var $this = $(".table-checkbox");
        if ($this.prop('checked')) {
            $(".tr-checkbox").prop("checked", true);
            $('#table_id tr').addClass("selected");

        } else {
            $(".tr-checkbox").prop("checked", false);
            $('#table_id tr').removeClass("selected");
        }
    });

    // 初始化模版类型数据
    $.post(appPath + '/template/getTemplates.do', {"courseType": pageType}, function (res) {
        if (res.code == 0) {
            var template = '<option value="">模板类型</option>';
            for (var i = 0; i < res.data.length; i++) {
                template += '<option value="' + res.data[i].templateId + '">' + res.data[i].templateName + '</option>';
            }
            $("#templateId").html(template);

        } else {
            layer.msg(res.msg);
        }
    })

    // 初始化教练数据
    $.post(appPath + '/user/getUsers.do', {"userType": 2, "status": 1}, function (res) {
        if (res.code == 0) {
            var coach = '<option value="">教练</option>';
            for (var i = 0; i < res.data.length; i++) {
                coach += '<option value="' + res.data[i].userId + '">' + res.data[i].name + '</option>';
            }
            $("#userId").html(coach);

        } else {
            layer.msg(res.msg);
        }
    })

    // 初始化健身房数据
    $.post(appPath + '/gym/getGyms.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var gym = '<option value="">门店筛选</option>';
            for (var i = 0; i < res.data.length; i++) {
                gym += '<option value="' + res.data[i].gymId + '">' + res.data[i].gymName + '</option>';
            }
            $("#gymId").html(gym);

        } else {
            layer.msg(res.msg);
        }
    })

    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        var page = $("#sideMenu").attr("data-pageid");
        window.location.href = appPath + '/pages/courseAdd.html?' + page;
    });
    /**
     * 导出
     */
    $("#exportBtn").on("click", function () {
        var ids = "";
        $(".tr-checkbox").each(function () {
            var $this = $(this);
            if ($this.prop('checked')) {
                ids += $this.val() + ",";
            }
        })
        ids = ids.substring(0, ids.length - 1);
        if (!ids) {
            layer.msg("请先选择需要导出的内容");
            return;
        }
        window.location.href = appPath + "/course/exportCourse.do?functionId=" + functionId + "&courseIds=" + ids;
    });

});

/**
 * 列表中按钮绑定事件
 */
function clickEvent() {
    /**
     * 修改
     */
    $(".editBtn").on("click", function () {
        var page = $("#sideMenu").attr("data-pageid");
        var id = $(this).attr("data-id");
        window.location.href = appPath + '/pages/courseEdit.html?' + page + "=" + id;
    });

    /**
     * 关闭课程
     */
    $(".offBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var msg = "关闭课程将会把所有预约用户进行退课处理，确定继续？";
        layer.confirm(msg, {
            icon: 0,
            btn: ['确定', '取消'] // 按钮
        }, function () {
            var url = appPath + '/course/updateCourse.do';
            var param = {};
            param.courseId = id;
            param.status = 2;//状态 1：正常 2：关闭 3：删除
            param.functionId = functionId;
            $.post(url, param, function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    layer.msg("操作成功", {icon: 1});
                    // 刷新表格数据
                    $('#table_id').DataTable().draw( false);
                } else {
                    layer.msg(res.msg);
                }
            });
        }, function () {
        });
    });

    /**
     * 删除
     */
    $(".delBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var msg = "是否确认删除本数据";
        layer.confirm(msg, {
            icon: 0,
            btn: ['确定', '取消'] // 按钮
        }, function () {
            var url = appPath + '/course/updateCourse.do';
            var param = {};
            param.courseId = id;
            param.functionId = functionId;
            param.status = 3;//状态 1：正常 2：关闭 3：删除
            $.post(url, param, function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    layer.msg("操作成功", {icon: 1});
                    // 刷新表格数据
                    $('#table_id').DataTable().ajax.reload();
                } else {
                    layer.msg(res.msg);
                }
            });
        }, function () {
        });
    });
    // 按钮权限控制
    var btns = "";// 权限按钮字符串
    var pageid = $("#sideMenu").attr("data-pageid").split("_")[1];// 页面ID
    $.post(appPath + '/user/getUserFunctions.do', {'functionId': pageid}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                btns += res.data[i].code + ",";
            }
            if (btns.indexOf("add") == -1) {
                $("#addBtn").prop("disabled", "disabled");
            } else {
                $("#addBtn").removeAttr("disabled");
            }
            if (btns.indexOf("export") == -1) {
                $("#exportBtn").prop("disabled", "disabled");
            } else {
                $("#exportBtn").removeAttr("disabled");
            }
            if (btns.indexOf("edit") == -1) {
                $(".editBtn").prop("disabled", "disabled");
            }
            if (btns.indexOf("off") == -1) {
                $(".offBtn").prop("disabled", "disabled");
            }
            if (btns.indexOf("del") == -1) {
                $(".delBtn").prop("disabled", "disabled");
            }
        } else if (res.code == -1) {
            layer.confirm('登录已失效，请重新登录', {
                icon: 0,
                btn: ['确认']
            }, function () {
                window.location.href = appPath + '/login.html';
            });
        } else {
            layer.msg(res.msg);
        }
    });
}
