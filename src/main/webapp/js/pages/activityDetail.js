var courseId = location.href.split('?')[1];
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
        window.location.href = appPath + '/pages/activityPage.html?edit=' + courseId;
    });

    // 初始化课程数据
    $.post(appPath + '/course/getCourseDetail.do', {'courseId': courseId}, function (res) {
        if (res.code == 0) {
            $(".header-two").html(res.data.courseName);
            $("#courseName").html(res.data.courseName);
            $("#name").html(res.data.name);
            $("#classHour").html(formatDate(res.data.classHour, 3));
            $("#orderNum").html(res.data.orderNum);
            $("#maxPeople").html(res.data.maxPeople + "人");
            $("#intro").html(res.data.intro);
            var imgs = res.data.imgs.split(",");
            var imgHtml = "";
            for (var i = 0; i < imgs.length; i++) {
                imgHtml += '<img src="' + imgViewPath + imgs[i] + '" class="img-normal" style="">&nbsp;';
            }
            $("#imgs").html(imgHtml);
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
                title: "状态", data: "status", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "正常";
                } else if (data == 2) {
                    str = "已封禁";
                }
                return str;
            }
            },
            {
                title: "注册日期", data: "createTime", render: function (data, type, full) {
                return formatDate(data, 1);
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