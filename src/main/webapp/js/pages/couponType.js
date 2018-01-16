var oldData = {};// 列表json数据
$(function () {
    //导航标签切换
    $(".header-tab .tab-button").on("click", function () {
        var $this = $(this);
        $(".header-tab .tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        var dataPage = $this.attr("data-page");
        window.location.href = appPath + '/pages/' + dataPage;
    });
    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/coupon/getCouponList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
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
                            oldData['' + json.data.results[i].couponId] = json.data.results[i];
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
            {title: "优惠券类型", data: "couponName"},
            {
                title: "面值", data: "couponValue", render: function (data, type, full) {
                return "￥" + data;
            }
            },
            {
                title: "发放方式", data: "sendType", render: function (data, type, full) {
                if (data == 1) {
                    return "人工发放";
                } else {
                    return "系统自动";
                }
            }
            },
            {
                title: "有效期", data: "indate", render: function (data, type, full) {
                return data + "天";
            }
            },
            {
                title: "创建时间", data: "createTime", render: function (data, type, full) {
                return formatDate(data);
            }
            },
            {
                title: "操作", data: "couponId",
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

    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        //清空数据
        $("#couponName").val("");
        $("#couponValue").val("");
        $("#indate").val("");
        var width = '38rem';
        var height = '20rem';
        if (pageWidth < 767) {
            width = '95%';
            height='33rem';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['添加优惠券类型', 'font-size:1rem;'],
            content: $("#alert-box"),// DOM或内容
            btn: ['新建', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var couponName = $.trim($("#couponName").val());
                var couponValue = $.trim($("#couponValue").val());
                var indate = $.trim($("#indate").val());
                if (!couponName) {
                    layer.msg('请填写优惠券名称');
                    return;
                }
                if (!couponValue) {
                    layer.msg('请填写面值');
                    return;
                }
                if (!indate) {
                    layer.msg('请填写有效期');
                    return;
                }
                var flag = false;
                $(".int-reg").each(function () {
                    var temp = $.trim($(this).val());
                    if (!intReg.test(temp)) {
                        flag = true;
                        return false;
                    }
                })
                if (flag) {
                    layer.msg('请输入正确的数字');
                    return;
                }
                var url = appPath + '/coupon/addCoupon.do';
                var param = {};
                param.couponName = couponName;
                param.couponValue = couponValue;
                param.indate = indate;
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
     * 修改
     */
    $(".editBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var rowData = oldData['' + id];//原始行json数据
        //初始化数据
        $("#couponName").val(rowData.couponName);
        $("#couponValue").val(rowData.couponValue);
        $("#indate").val(rowData.indate);
        var width = '38rem';
        var height = '20rem';
        if (pageWidth < 767) {
            width = '95%';
            height='33rem';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['编辑优惠券类型', 'font-size:1rem;'],
            content: $("#alert-box"),// DOM或内容
            btn: ['修改', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var couponName = $.trim($("#couponName").val());
                var couponValue = $.trim($("#couponValue").val());
                var indate = $.trim($("#indate").val());
                if (!couponName) {
                    layer.msg('请填写优惠券名称');
                    return;
                }
                if (!couponValue) {
                    layer.msg('请填写面值');
                    return;
                }
                if (!indate) {
                    layer.msg('请填写有效期');
                    return;
                }
                var flag = false;
                $(".int-reg").each(function () {
                    var temp = $.trim($(this).val());
                    if (!intReg.test(temp)) {
                        flag = true;
                        return false;
                    }
                })
                if (flag) {
                    layer.msg('请输入正确的数字');
                    return;
                }
                var url = appPath + '/coupon/updateCoupon.do';
                var param = {};
                param.couponId = id;
                param.couponName = couponName;
                param.couponValue = couponValue;
                param.indate = indate;
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
        var msg = "是否确认删除本数据";
        layer.confirm(msg, {
            icon: 0,
            btn: ['确定', '取消'] // 按钮
        }, function () {
            var url = appPath + '/coupon/updateCoupon.do';
            var param = {};
            param.couponId = id;
            param.couponStatus = 2;//状态 1：正常 2：删除
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