var urlData = location.href.split('?')[1].split("=");
var courseId = urlData[0];
var page = "";
var courseName = "";
$(function () {

    //标签切换
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
            if (divId == 'userTable') {
                userTable();//初始化预约会员列表
            }
        }
    });

    /**
     * 编辑按钮
     */
    $("#editBtn").on("click", function () {
        window.location.href = appPath + '/pages/courseEdit.html?' + page + "=" + courseId + "=" + courseName;
    });

    // 初始化课程数据
    $.post(appPath + '/course/getCourseDetail.do', {'courseId': courseId}, function (res) {
        if (res.code == 0) {
            if (res.data.courseType == 1) {
                $(".header-one").html("团课");
                page = "13_14";
            } else {
                $(".header-one").html("私教");
                page = "13_24";
            }
            courseName = res.data.courseName;
            $(".header-two").html(res.data.courseName);
            $("#courseName").html(res.data.courseName);
            $("#name").html(res.data.name);
            $("#orderNum").html(res.data.orderNum + "/" + res.data.maxNum);
            $("#templateName").html(res.data.templateName);
            $("#gymName").html(res.data.gymName);
            $("#price").html("￥" + res.data.price);
            $("#intro").html(res.data.intro);
            $("#notes").html(res.data.notes);
            var imgs = res.data.images.split(",");
            var imgHtml = "";
            for (var i = 0; i < imgs.length; i++) {
                imgHtml += '<img src="' + imgViewPath + imgs[i] + '" class="img-normal" style="">&nbsp;';
            }
            $("#images").html(imgHtml);
            $("#longGoal").html(res.data.longGoal);
            $("#shortGoal").html(res.data.shortGoal);
            $("#task").html(res.data.task);
            $("#summary").html(res.data.summary);
        } else {
            layer.msg(res.msg);
        }
    });
    // 初始化课程动作数据
    $.post(appPath + '/course/getCourseActions.do', {'courseId': courseId}, function (res) {
        if (res.code == 0) {
            var list = res.data;
            var html = "";
            for (var i = 0; i < list.length; i++) {
                html += '<div class="layui-form-item">';
                html += '<span class="span-title">动作</span>';
                html += '<span class="span-content">' + list[i].actions + '</span></div>';
                html += '<div class="layui-form-item">';
                html += '<span class="span-title">负荷次数</span>';
                html += '<span class="span-content">' + list[i].times + '</span></div>';
                html += '<div class="layui-form-item">';
                html += '<span class="span-title">间歇</span>';
                html += '<span class="span-content">' + list[i].intervals + '</span></div>';
                html += '<div class="layui-form-item">';
                html += '<span class="span-title">说明备注</span>';
                html += '<span class="span-content">' + list[i].remark + '</span></div>';
            }
            $("#actionList").html(html);
        } else {
            layer.msg(res.msg);
        }
    });

});

function userTable() {
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/course/getCourseUser.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                d.courseId = courseId;
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
                title: "会员姓名", data: "name", render: function (data, type, full) {
                var str = "";
                if (full.orderStatus == 5) {
                    str += '<span class="table-label">已退课</span>';
                }
                str += '<a href="' + appPath + '/pages/userDetail.html?' + full.userId + '">' + data + '</a>';
                return str;
            }
            },
            {title: "手机号", data: "phone"},
            {
                title: "性别", data: "sex", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "男";
                } else if (data == 2) {
                    str = "女";
                } else {
                    str = "未知";
                }
                return str;
            }
            },
            {
                title: "会员卡类型", data: "cardType", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "储值卡";
                } else {
                    str = "次卡";
                }
                return str;
            }
            },
            {
                title: "支付方式", data: "payType", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "微信支付";
                } else if (data == 2 && full.cardType == 1) {
                    str = "会员储值卡支付";
                } else if (data == 2 && full.cardType == 2) {
                    str = "会员次卡支付";
                }
                return str;
            }
            },
            {
                title: "预约时间", data: "orderTime", render: function (data, type, full) {
                return formatDate(data, 3);
            }
            },
            {
                title: "操作", data: "orderId",
                "mRender": function (data, type, full) {
                    var str = "";
                    if (full.orderStatus == 3 && full.isStart == 0) {
                        str += "<button type='button' class='rejectBtn btn btn-default' data-id='" + data + "' >退课</button>";
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
}


/**
 * 列表中按钮绑定事件
 */
function clickEvent() {

    /**
     * 退课
     */
    $(".rejectBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var msg = "退课后支付费用将按原路返回，确定继续？";
        layer.confirm(msg, {
            icon: 0,
            btn: ['确定', '取消'] // 按钮
        }, function () {
            var url = appPath + '/pay/dropCourse.do';
            var param = {};
            param.orderId = id;
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


    // 按钮权限控制
    var btns = "";// 权限按钮字符串
    var pageid = page.split("_")[1];// 页面ID
    $.post(appPath + '/user/getUserFunctions.do', {'functionId': pageid}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                btns += res.data[i].code + ",";
            }
            if (btns.indexOf("reject") == -1) {
                $(".rejectBtn").prop("disabled", "disabled");
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