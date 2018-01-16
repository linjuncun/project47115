var urlData = location.href.split('?')[1];
var cardType = 1;
if (urlData) {
    cardType = urlData[0];
}
$(function () {
    //总办卡数初始化
    $.post(appPath + '/package/getCardNumber.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].cardType == 1) {
                    if (res.data[i].cardNum) {
                        $("#cardNumA").html(res.data[i].cardNum);
                    } else {
                        $("#cardNumA").html(0);
                    }
                } else if (res.data[i].cardType == 2) {
                    if (res.data[i].cardNum) {
                        $("#cardNumB").html(res.data[i].cardNum);
                    } else {
                        $("#cardNumB").html(0);
                    }
                }
            }
        } else {
            layer.msg(res.msg);
        }
    })
    if (cardType == 2) {
        $("#cardNumB").parent().addClass("btn-item-checked");
        $("#cardNumA").parent().removeClass("btn-item-checked");
    } else {
        $("#cardNumA").parent().addClass("btn-item-checked");
        $("#cardNumB").parent().removeClass("btn-item-checked");
    }

    //标签切换
    $(".tab-button").on("click", function () {
        var $this = $(this);
        if (!$this.hasClass("btn-item-checked")) {
            $(".tab-button").removeClass("btn-item-checked");
            $this.addClass("btn-item-checked");
            // 刷新表格数据
            $('#table_id').DataTable().ajax.reload();
        }
    });

    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/package/getPackageList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var cardType = $(".btn-item-checked").attr("data-value");
                d.conditions = conditions;
                d.cardType = cardType;
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
                title: "套餐名称", data: "packageName", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/packageDetail.html?' + full.packageId + '">' + data + '</a>';
            }
            },
            {
                title: "充值金额", data: "recharge", render: function (data, type, full) {
                return "￥" + data;
            }
            },
            {
                title: "到账", data: "arrive", render: function (data, type, full) {
                var str = "";
                if (full.cardType == 1) {
                    str = "￥" + data;
                } else if (full.cardType == 2) {
                    str = data + "次";
                }
                return str;
            }
            },
            {
                title: "是否返现活动", data: "isReturnCash", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "是";
                } else if (data == 2) {
                    str = "否";
                }
                return str;
            }
            },
            {
                title: "返现金额", data: "returnCashCondition", render: function (data, type, full) {
                var str = "";
                for (var i = 0; i < data.length; i++) {
                    str += '￥' + data[i].returnCash + '<br>';
                }
                return str;
            }
            },
            {
                title: "办理卡数", data: "cardNum", render: function (data, type, full) {
                return data + "张";
            }
            },
            {
                title: "最后操作时间", data: "updateTime", render: function (data, type, full) {
                return formatDate(data);
            }
            },
            {
                title: "操作", data: "packageId",
                "mRender": function (data, type, full) {
                    var str = "";
                    str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "'>编辑</button>&nbsp;";
                    if (full.status == 1) {
                        str += "<button type='button' class='stopBtn btn btn-default' data-id='" + data + "' data-status='2'>下线</button>";
                    } else if (full.status == 2) {
                        str += "<button type='button' class='stopBtn btn btn-default' data-id='" + data + "' data-status='1'>上线</button>";
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
        var cardType = $(".btn-item-checked").attr("data-value");
        window.location.href = appPath + '/pages/packagePage.html?add=' + cardType;
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
        var cardType = $(".btn-item-checked").attr("data-value");
        var id = $(this).attr("data-id");
        window.location.href = appPath + '/pages/packagePage.html?edit=' + cardType + '=' + id;
    });

    /**
     * 上、下线
     */
    $(".stopBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var status = $(this).attr("data-status");
        var msg = "下线后本套餐则不可购买，但不影响已购用户,确定下线？";
        var url = appPath + '/package/updatePackage.do';
        var param = {};
        param.packageId = id;
        param.status = status;//状态 1:启用 2 :停用
        if (status == 1) {
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
        } else {
            layer.confirm(msg, {
                icon: 0,
                btn: ['确定', '取消'] // 按钮
            }, function () {
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
