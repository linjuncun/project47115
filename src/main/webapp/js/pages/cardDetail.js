var urlData = location.href.split('?')[1].split("=");
var cardId = urlData[0];
var cardInfo = {};//会员卡数据
var gymList = [];//门店列表，用于消费记录列表中根据门店id获取门店名字
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
            if (divId == 'expenseTable') {
                expenseTable();//初始化列表
            }
        }
    });

    /**
     * 管理会员
     */
    $("#bindBtn").on("click", function () {
        //清空数据
        $("#userId").html(cardInfo.userName + " " + cardInfo.userPhone);
        if (cardInfo.viceUserName) {
            $(".viceUserId").removeClass("hidden");
            $(".addUser").addClass("hidden");
            $("#viceUserId").html(cardInfo.viceUserName + ' ' + cardInfo.viceUserPhone + '<a class="margin-left-15" href="javascript:delBindUser();">删除</a>');
        } else {
            $(".viceUserId").addClass("hidden");
        }
        $("#addUser").val("");
        $("#addUserId").val("");
        $(".addUserName").html("");
        layer.open({
            type: 1,
            area: ['26.66rem', '16rem'], // 宽高
            title: ['管理会员卡绑定用户', 'font-size:1.2rem;'],
            content: $("#alert-box"),// DOM或内容
            btn: ['确定', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var param = {};
                param.cardId = cardId;
                if ($(".addUser").hasClass("hidden") && $(".viceUserId").hasClass("hidden")) {
                    //删除副卡用户
                    param.viceUserId = 0;
                } else if ($(".viceUserId").hasClass("hidden")) {
                    //绑定用户
                    var phone = $.trim($("#addUser").val());
                    var addUserId = $("#addUserId").val();
                    if (!phone) {
                        layer.msg('请填写手机号');
                        return;
                    } else if (!addUserId) {
                        layer.msg('未找到用户，请先添加会员用户');
                        return;
                    }
                    if (cardInfo.userId == addUserId) {
                        layer.msg('用户已绑定该卡，请绑定其他用户', {icon: 0});
                        return;
                    }
                    param.viceUserId = addUserId;
                }
                var url = appPath + '/user/updateClubCard.do';
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        layer.msg("操作成功", {icon: 1});
                        // 刷新页面
                        window.setTimeout(function () {
                            window.location.reload();
                        }, 1000);
                    } else {
                        layer.msg(res.msg);
                    }
                });
            }, btn2: function (index) {// 或者使用btn2（concel）
            }, cancel: function (index) {// 或者使用btn2（concel）
            }
        });

    });

    // 初始化会员卡数据
    $.post(appPath + '/user/getClubCardInfo.do', {'cardId': cardId}, function (res) {
        if (res.code == 0) {
            cardInfo = res.data;
            $(".header-one").html("NO." + res.data.cardNo);
            $("#cardNo").html("NO." + res.data.cardNo);
            if (res.data.cardType == 1) {
                $("#cardType").html("会员储值卡");
            } else if (res.data.cardType == 2) {
                $("#cardType").html("会员次卡");
            }
            $("#balance").html(res.data.balance);
            var userHtml = "";
            var userSelect = '<option value="">会员姓名</option>';
            userHtml += '<a href="' + appPath + '/pages/userDetail.html?' + res.data.userId + '">' + res.data.userName + '</a>';
            userSelect += '<option value="' + res.data.userId + '">' + res.data.userName + '</option>';
            if (res.data.viceUserName) {
                userHtml += '<br><a href="' + appPath + '/pages/userDetail.html?' + res.data.viceUserId + '">' + res.data.viceUserName + '</a>';
                userSelect += '<option value="' + res.data.viceUserId + '">' + res.data.viceUserName + '</option>';
            }
            $("#userName").html(userHtml);
            $("#userSelect").html(userSelect);
            $("#timesUsed").html(res.data.timesUsed);
            $("#createTime").html(formatDate(res.data.createTime, 1));
            if (res.data.isReturnCash == 1) {
                $("#isReturnCash").html("是");
            } else {
                $("#isReturnCash").html("否");
            }
            var list = res.data.returnCashCondition;
            var html = "";
            for (var i = 0; i < list.length; i++) {
                html += "在" + list[i].days + "天内";
                if (list[i].money > 0) {
                    html += "消费" + list[i].money + "元，";
                } else if (list[i].times > 0) {
                    html += "锻炼" + list[i].times + "次，";
                }
                html += "可获得返现" + list[i].returnCash + "元";
                html += "<br>";
            }
            $("#returnCashCondition").html(html);
            $("#sysUserName").html(res.data.sysUserName);
        } else {
            layer.msg(res.msg);
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
            if (btns.indexOf("bind") == -1) {
                $("#bindBtn").prop("disabled", "disabled");
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
    //动态查询用户
    $("#addUser").on("change", function () {
        $(".addUserName").html("");
        $("#addUserId").val("");
        var phone = $.trim($("#addUser").val());
        if (phone) {
            if (!phoneReg.test(phone)) {
                layer.msg("请输入正确的手机号");
                return;
            }
            // 初始化数据
            $.post(appPath + '/user/getUserDetail.do', {'phone': phone}, function (res) {
                if (res.code == 0) {
                    $(".addUserName").html(res.data.name);
                    $("#addUserId").val(res.data.userId);
                } else {
                    layer.msg("未找到用户，请先添加会员用户", {icon: 0});
                }
            });
        }
    })

});
/**
 * 消费记录列表初始化
 */
function expenseTable() {
    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/order/getOrderList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var orderType = $("#orderType").val();
                var gym = $("#gym").val();
                d.orderType = orderType;
                d.status = 3;//查询状态为已完成订单
                if (orderType == 4) {
                    d.orderType = 1;
                    d.status = 5;
                }
                d.gymId = gym;
                d.cardId = cardId;
                var queryUserId = $("#userSelect").val();
                d.queryUserId = queryUserId;
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
                title: "会员姓名", data: "userId", render: function (data, type, full) {
                var str = "";
                if (cardInfo.userId == data) {
                    str = cardInfo.userName;
                } else if (cardInfo.viceUserId == data) {
                    str = cardInfo.viceUserName;
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
/**
 * 删除绑定用户
 */
function delBindUser() {
    $(".viceUserId").addClass("hidden");
}