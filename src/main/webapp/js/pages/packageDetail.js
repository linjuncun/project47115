var urlData = location.href.split('?')[1];
var packageId = urlData[0];
var cardType = 1;
var packageInfo = {};
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
            if (divId == 'cardTable') {
                cardTable();//初始化用户会员卡列表
            }
        }
    });

    /**
     * 编辑按钮
     */
    $("#editBtn").on("click", function () {
        window.location.href = appPath + '/pages/packagePage.html?edit=' + cardType + '=' + packageId;
    });

    // 初始化套餐数据
    $.post(appPath + '/package/getPackageDetail.do', {'packageId': packageId}, function (res) {
        if (res.code == 0) {
            packageInfo = res.data;
            cardType = res.data.cardType;
            $(".header-one").html(res.data.packageName);
            $("#packageName").html(res.data.packageName);
            if (res.data.cardType == 1) {
                $(".cardType").html("会员储值卡");
                $("#cardType").html("储值卡");
            } else if (res.data.cardType == 2) {
                $(".cardType").html("会员次卡");
                $("#cardType").html("次卡");
            }
            $("#cardNum").html(res.data.cardNum);

            if (res.data.isReturnCash == 1) {
                $("#isReturnCash").html("是");
                $("#condition-div").removeClass("hidden");
                var list = res.data.returnCashCondition;
                var html = '';
                for (var i = 0; i < list.length; i++) {
                    if (i > 0) {
                        html += "或&nbsp;";
                    }
                    html += '在' + list[i].days + '天内完成';
                    if (list[i].times > 0) {
                        html += list[i].times + '次课程预约，';
                    } else {
                        html += list[i].money + '元消费，';
                    }
                    html += '则返现' + list[i].returnCash + '元<br/>';
                }
                $("#returnCashCondition").html(html);

            } else {
                $("#isReturnCash").html("否");
            }
            $("#indate").html(res.data.indate + "天");
            $("#notes").html(res.data.notes);
            //适用门店
            var gyms = res.data.supportGym;
            var gymHtml = '';
            for (var i = 0; i < gyms.length; i++) {
                gymHtml += gyms[i].gymName + '<br>';
            }
            $("#supportGym").html(gymHtml);
            if (gyms.length == 0) {
                $("#supportGym").html("全部");
            }
            //适用课程
            var courses = res.data.supportCourse;
            var courseHtml = '';
            for (var i = 0; i < courses.length; i++) {
                courseHtml += courses[i].courseName + '、';
            }
            courseHtml = courseHtml.substring(0, courseHtml.length - 1);
            $("#supportCourse").html(courseHtml);
            if (courses.length == 0) {
                $("#supportCourse").html("全部");
            }
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
            if (btns.indexOf("edit") == -1) {
                $("#editBtn").prop("disabled", "disabled");
            }
            if (btns.indexOf("addCard") == -1) {
                $("#addBtn").prop("disabled", "disabled");
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

    /**
     * 新增用户会员卡
     */
    $("#addBtn").on("click", function () {
        if (packageInfo.status == 2) {
            layer.msg('套餐已下线，请先上线该套餐');
            return;
        }
        //清空数据
        var packageType = "";
        if (packageInfo.cardType == 1) {
            packageType += "会员储值卡";
        } else {
            packageType += "会员次卡";
        }
        packageType += "-" + packageInfo.packageName;
        $("#packageType").html(packageType);
        $("#addUser").val("");
        $("#addUserId").val("");
        $(".addUserName").html("");
        if (packageInfo.isReturnCash == 1) {
            $("#returnCash").removeClass("hidden");
        }
        var width = '33.33rem';
        var height = '18rem';
        if (pageWidth < 767) {
            width = '95%';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['新增用户会员卡', 'font-size:1.2rem;'],
            content: $("#alert-box"),// DOM或内容
            btn: ['确定', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var phone = $.trim($("#addUser").val());
                var addUserId = $("#addUserId").val();
                if (!phone) {
                    layer.msg('请填写手机号');
                    return;
                } else if (!addUserId) {
                    layer.msg('未找到用户，请先添加会员用户');
                    return;
                }
                var url = appPath + '/package/addClubCard.do';
                var param = {};
                param.packageId = packageId;
                param.userId = addUserId;
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        layer.msg("操作成功", {icon: 1});
                        // 刷新数据
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

function cardTable() {
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/package/getPackageCardList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var status = $("#status").val();
                var startDate = $.trim($("#startDate").val());
                var endDate = $.trim($("#endDate").val());
                var balance = $("#balance").val().split("-");
                d.packageId = packageId;
                d.status = status;
                d.startDate = startDate;
                d.endDate = endDate;
                d.balanceA = balance[0];
                d.balanceB = balance[1];
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
                title: "用户会员卡号", data: "cardNo", render: function (data, type, full) {
                var str = "";
                str += '<a href="' + appPath + '/pages/cardDetail.html?' + full.cardId + '">NO.' + data + '</a>';
                return str;
            }
            },
            {
                title: "姓名", data: "userName", render: function (data, type, full) {
                var str = "";
                str += '<a href="' + appPath + '/pages/userDetail.html?' + full.userId + '">' + data + '</a>';
                return str;
            }
            },
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
                title: "状态", data: "status", render: function (data, type, full) {
                var str = "";
                if (data == 1) {
                    str = "正常";
                } else {
                    str = "已封禁";
                }
                return str;
            }
            },
            {
                title: "余额", data: "balance", render: function (data, type, full) {
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
                title: "到期日期", data: "deadline", render: function (data, type, full) {
                return formatDate(data, 1);
            }
            },
            {
                title: "操作", data: "cardId",
                "mRender": function (data, type, full) {
                    var str = "";
                    str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "' >编辑</button>&nbsp;";
                    if (full.status == 1) {
                        str += "<button type='button' class='stopBtn btn btn-default' data-id='" + data + "'  data-status='2'>封禁</button>";
                    } else if (full.status == 2) {
                        str += "<button type='button' class='stopBtn btn btn-default' data-id='" + data + "'  data-status='1'>解封</button>";
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
     * 编辑
     */
    $(".editBtn").on("click", function () {
        var id = $(this).attr("data-id");
        window.location.href = appPath + '/pages/cardDetail.html?' + id;
    });

    /**
     * 封禁/解封
     */
    $(".stopBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var status = $(this).attr("data-status");
        var msg = "封禁后该卡将不能使用，确定封禁？";
        var url = appPath + '/package/updateClubCard.do';
        var param = {};
        param.cardId = id;
        param.status = status;//状态 1：正常 2：停用
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
}