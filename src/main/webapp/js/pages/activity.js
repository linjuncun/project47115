$(function () {
    //标签切换
    $(".tab-button").on("click", function () {
        var $this = $(this);
        $(".tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        $('#table_id').DataTable().ajax.reload();
        $(window).resize();
    });
    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/course/getCourseList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var startDate = $.trim($("#startDate").val());
                var endDate = $.trim($("#endDate").val());
                var queryType = $(".tab-button.btn-item-checked").attr("data-value");
                d.conditions = conditions;
                d.startDate = startDate;
                d.endDate = endDate;
                d.queryType = queryType;
                d.type = 2;
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
                title: "活动名称", data: "courseName", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/activityDetail.html?' + full.courseId + '">' + data + '</a>';
            }
            },
            {title: "教练", data: "name"},
            {title: "参加会员人数", data: "orderNum"},
            {
                title: "活动时间", data: "classHour", render: function (data, type, full) {
                return formatDate(data);
            }
            },
            {
                title: "状态", data: "saleStatus", render: function (data, type, full) {
               if(data == 1){
                   return "在线";
               }else{
                   return "下线";
               }
            }
            },
            {
                title: "操作", data: "courseId",
                "mRender": function (data, type, full) {
                    var str = "";
                    //停止预约后，不让编辑
                    if (full.stopOrder == 0) {
                        str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "'>编辑</button>&nbsp;";
                        if (full.saleStatus == 1) {
                            str += "<button type='button' class='stopBtn btn btn-default' data-id='" + data + "' data-status='2'>下线</button>";
                        } else {
                            str += "<button type='button' class='stopBtn btn btn-default' data-id='" + data + "' data-status='1'>上线</button>";
                        }
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

    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        window.location.href = appPath + '/pages/activityPage.html?add';
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
        var id = $(this).attr("data-id");
        window.location.href = appPath + '/pages/activityPage.html?edit=' + id;
    });

    /**
     * 上/下线
     */
    $(".stopBtn").on("click", function () {
        var functionId = $("#sideMenu").attr("data-pageid");
        var id = $(this).attr("data-id");
        var saleStatus = $(this).attr("data-status");
        var url = appPath + '/course/updateCourse.do';
        var param = {};
        param.functionId = functionId;
        param.courseId = id;
        param.saleStatus = saleStatus;//活动销售状态 1：在售 2：下线
        if(saleStatus == 1){
            //上线活动前先检查是否有其他活动正在上线
            $.post(appPath + '/course/getActivityCourse.do', {"占位": 1}, function (res) {
                if (res.code == 0) {
                    if(res.data){
                        layer.msg('请先下线在线活动');
                    }else{
                        $.post(url, param, function (res) {
                            if (res.code == 0) {
                                layer.closeAll();
                                layer.msg("上线成功", {icon: 1});
                                // 刷新表格数据
                                $('#table_id').DataTable().draw( false);
                            } else {
                                layer.msg(res.msg);
                            }
                        });
                    }
                } else {
                    layer.msg(res.msg);
                }
            })
        }else{
            $.post(url, param, function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    var msg = "操作成功";
                    layer.msg(msg, {icon: 1});
                    // 刷新表格数据
                    $('#table_id').DataTable().draw( false);
                } else {
                    layer.msg(res.msg);
                }
            });
        }
    });
    // 按钮权限控制
    var btns = "";// 权限按钮字符串
    var pageid = $("#sideMenu").attr("data-pageid");// 页面ID
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
            if (btns.indexOf("edit") == -1) {
                $(".editBtn").prop("disabled", "disabled");
            }
            if (btns.indexOf("stop") == -1) {
                $(".stopBtn").prop("disabled", "disabled");
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
