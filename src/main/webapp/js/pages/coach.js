$(function () {
    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/user/getUserList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var coachType = $("#coachType").val();
                var sex = $("#sex").val();
                var gymId = $("#gymId").val();
                d.userType = 2;//用户类型 1：普通用户 2：教练
                d.conditions = conditions;
                d.coachType = coachType;
                d.sex = sex;
                d.gymId = gymId;
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
                    return '<input type="checkbox" class="tr-checkbox" value="' + full.userId + '"  />';
                }
            },
            {
                title: "教练姓名", data: "name", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/coachDetail.html?' + full.userId + '">' + data + '</a>';
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
                title: "所属门店", data: "gymList", render: function (data, type, full) {
                var gymList = full.gymList;
                var str = "";
                for (var i = 0; i < gymList.length; i++) {
                    str += gymList[i].gymName + "<br>";
                }
                return str;
            }
            },
            {
                title: "类型", data: "coachType", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "全职";
                } else {
                    str = "兼职";
                }
                return str;
            }
            },
            {title: "权重", data: "weight"},
            {
                title: "操作", data: "userId",
                "mRender": function (data, type, full) {
                    var str = "";
                    str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "'>编辑</button>&nbsp;";
                    str += "<button type='button' class='delBtn btn btn-default' data-id='" + data + "'>删除</button>";
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
     * 导出
     */
    $("#exportBtn").on("click", function () {
        var userIds = "";
        $(".tr-checkbox").each(function () {
            var $this = $(this);
            if ($this.prop('checked')) {
                userIds += $this.val() + ",";
            }
        })
        userIds = userIds.substring(0, userIds.length - 1);
        if (!userIds) {
            layer.msg("请先选择需要导出的内容");
            return;
        }
        window.location.href = appPath + "/user/exportUser.do?userIds=" + userIds+"&type=2";
    });

    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        window.location.href = appPath + '/pages/coachPage.html?add';
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
        window.location.href = appPath + '/pages/coachPage.html?edit=' + id;
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
            var url = appPath + '/user/updateUser.do';
            var param = {};
            param.userId = id;
            param.status = 3;//状态 1:启用 2 :停用 3:删除
            param.pageType = 2;//页面类型 1：会员管理  2：教练管理
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
