var oldData = {};// 列表json数据
$(function () {
    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/user/getSysUserList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                d.conditions = conditions;
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
                            oldData['' + json.data.results[i].sysUserId] = json.data.results[i];
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
            {title: "帐号", data: "username"},
            {title: "姓名", data: "truename"},
            {
                title: "最后登录时间", data: "lastLoginTime", render: function (data, type, full) {
                return formatDate(data);
            }
            },
            {
                title: "操作", data: "sysUserId",
                "mRender": function (data, type, full) {
                    var str = "";
                    if (data == 1) {
                        str = "<button type='button' disabled='disabled' class='btn btn-default' >权限管理</button>&nbsp;";
                        str += "<button type='button' disabled='disabled' class='btn btn-default' >编辑</button>&nbsp;";
                        str += "<button type='button' disabled='disabled' class='btn btn-default'>删除</button>";
                    } else {
                        str = "<button type='button' class='authBtn btn btn-default' data-id='" + data + "' data-name='" + full.truename + "'>权限管理</button>&nbsp;";
                        str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "'>编辑</button>&nbsp;";
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

    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        //清空数据
        $("#truename").val("");
        $("#username").val("");
        $("#password").val("");
        $("#password").prop("placeholder", "请输入帐号初始密码");
        var width = '33.33rem';
        var height = '20rem';
        if (pageWidth < 767) {
            width = '95%';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['添加用户', 'font-size:1.2rem;'],
            content: $("#alert-box"),// DOM或内容
            btn: ['新增', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var truename = $.trim($("#truename").val());
                var username = $.trim($("#username").val());
                var password = $.trim($("#password").val());
                if (!truename) {
                    layer.msg('请填写姓名');
                    return;
                }
                if (!username) {
                    layer.msg('请填写帐号');
                    return;
                }
                if (!password) {
                    layer.msg('请填写密码');
                    return;
                }
                var url = appPath + '/user/addSysUser.do';
                var param = {};
                param.truename = truename;
                param.username = username;
                param.password = hex_md5(password);
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
            }, btn2: function (index) {// 或者使用btn2（concel）
            }, cancel: function (index) {// 或者使用btn2（concel）
            }
        });
    });

});

/**
 * 列表中按钮绑定事件
 */
function clickEvent() {
    /**
     * 权限管理
     */
    $(".authBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var name = $(this).attr("data-name");
        window.location.href = appPath + '/pages/function.html?' + id + '=' + name;
    });
    /**
     * 修改
     */
    $(".editBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var rowData = oldData['' + id];//原始行json数据
        //初始化数据
        $("#truename").val(rowData.truename);
        $("#username").val(rowData.username);
        $("#password").val("");
        $("#password").prop("placeholder", "********");
        var width = '33.33rem';
        var height = '20rem';
        if (pageWidth < 767) {
            width = '95%';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['编辑用户', 'font-size:1.2rem;'],
            content: $("#alert-box"),// DOM或内容
            btn: ['修改', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var truename = $.trim($("#truename").val());
                var username = $.trim($("#username").val());
                var password = $.trim($("#password").val());
                if (!truename) {
                    layer.msg('请填写姓名');
                    return;
                }
                if (!username) {
                    layer.msg('请填写帐号');
                    return;
                }
                var url = appPath + '/user/updateSysUser.do';
                var param = {};
                param.sysUserId = id;
                param.truename = truename;
                param.username = username;
                if (password) {
                    param.password = hex_md5(password);
                }
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
            }, btn2: function (index) {// 或者使用btn2（concel）
            }, cancel: function (index) {// 或者使用btn2（concel）
            }
        });
    });

    /**
     * 删除
     */
    $(".delBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var msg = "确认删除该账号？";
        layer.confirm(msg, {
            icon: 0,
            btn: ['删除', '取消'] // 按钮
        }, function () {
            var url = appPath + '/user/updateSysUser.do';
            var param = {};
            param.sysUserId = id;
            param.status = 3;//状态 1:启用 2 :停用 3:删除
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
            if (btns.indexOf("auth") == -1) {
                $(".authBtn").prop("disabled", "disabled");
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
