var sysConfig = {};
$(function () {
    // 初始化数据
    $.post(appPath + '/system/getSysConfig.do', {'占位': 1}, function (res) {
        if (res.code == 0) {
            sysConfig = res.data;
            $("#brandName").html(res.data.brandName);
            $("#agreementName").html(res.data.agreementName);
            if (res.data.onOrderTime == 0) {
                $("#onOrderTime").html("立即");
            } else {
                $("#onOrderTime").html(res.data.onOrderTime + "小时");
            }
            $("#offOrderTime").html("开课前" + res.data.offOrderTime + "小时");
            $("#valueCardName").html(res.data.valueCardName);
            $("#valueCardIntro").html(res.data.valueCardIntro);
            $("#numberCardName").html(res.data.numberCardName);
            $("#numberCardIntro").html(res.data.numberCardIntro);
        } else {
            layer.msg(res.msg);
        }
    });

    /**
     * 编辑品牌
     */
    $("#editBrandName").on("click", function () {
        $("#brand").val(sysConfig.brandName);
        var width = '33.33rem';
        var height = '13.33rem';
        if (pageWidth < 767) {
            width = '95%';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['编辑品牌', 'font-size:1.2rem;'],
            content: $("#brandName-box"),// DOM或内容
            btn: ['保存', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var brand = $.trim($("#brand").val());
                if (!brand) {
                    layer.msg('请填写品牌名称');
                    return;
                }
                var url = appPath + '/system/updateSysConfig.do';
                var param = {};
                param.brandName = brand;
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        // layer.msg("操作成功", {icon: 1});
                        // window.setTimeout(function () {
                        window.location.reload();
                        // }, 1000);
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
     * 编辑会员协议
     */
    $("#editAgreement").on("click", function () {
        $("#agreement_name").val(sysConfig.agreementName);
        $("#agreement").val(sysConfig.agreement);
        var width = '43.33rem';
        var height = '40rem';
        if (pageWidth < 767) {
            width = '95%';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['编辑会员协议', 'font-size:1.2rem;'],
            content: $("#agreement-box"),// DOM或内容
            btn: ['保存', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var agreementName = $.trim($("#agreement_name").val());
                var agreement = $.trim($("#agreement").val());
                if (!agreementName) {
                    layer.msg('请填写协议名称');
                    return;
                }
                if (!agreement) {
                    layer.msg('请填写协议详情');
                    return;
                }
                var url = appPath + '/system/updateSysConfig.do';
                var param = {};
                param.agreementName = agreementName;
                param.agreement = agreement;
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        // layer.msg("操作成功", {icon: 1});
                        // window.setTimeout(function () {
                        window.location.reload();
                        // }, 1000);
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
     * 编辑预约课程设置
     */
    $("#editOrder").on("click", function () {
        $("#startHour").val(sysConfig.onOrderTime);
        $("#endHour").val(sysConfig.offOrderTime);
        var width = '26.66rem';
        var height = '17.33rem';
        if (pageWidth < 767) {
            width = '95%';
        }
        layer.open({
            type: 1,
            area: [width,height], // 宽高
            title: ['编辑预约课程设置', 'font-size:1.2rem;'],
            content: $("#order-box"),// DOM或内容
            btn: ['保存', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var onOrderTime = $("#startHour").val();
                var offOrderTime = $("#endHour").val();
                var url = appPath + '/system/updateSysConfig.do';
                var param = {};
                param.onOrderTime = onOrderTime;
                param.offOrderTime = offOrderTime;
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        // layer.msg("操作成功", {icon: 1});
                        // window.setTimeout(function () {
                        window.location.reload();
                        // }, 1000);
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
     * 编辑会员卡简介
     */
    $(".cardBtn").on("click", function () {
        var id = $(this).attr("data-id");
        if (id == "value") {
            $("#cardName").val(sysConfig.valueCardName);
            var intro = sysConfig.valueCardIntro.split("<br>");
            for (var i = 0; i < intro.length; i++) {
                if (i == 0) {
                    $("#introA").val(intro[i]);
                } else if (i == 1) {
                    $("#introB").val(intro[i]);
                } else {
                    $("#introC").val(intro[i]);
                }
            }
        } else {
            $("#cardName").val(sysConfig.numberCardName);
            var intro = sysConfig.numberCardIntro.split("<br>");
            for (var i = 0; i < intro.length; i++) {
                if (i == 0) {
                    $("#introA").val(intro[i]);
                } else if (i == 1) {
                    $("#introB").val(intro[i]);
                } else {
                    $("#introC").val(intro[i]);
                }
            }
        }
        var width = '28rem';
        var height = '21rem';
        if (pageWidth < 767) {
            width = '95%';
            height='24rem';
        }
        layer.open({
            type: 1,
            area: [width, height], // 宽高
            title: ['会员卡简介设置', 'font-size:1.2rem;'],
            content: $("#card-box"),// DOM或内容
            btn: ['保存', '取消']
            , yes: function (index, layero) { // 或者使用btn1
                var cardName = $.trim($("#cardName").val());
                var introA = $.trim($("#introA").val());
                var introB = $.trim($("#introB").val());
                var introC = $.trim($("#introC").val());
                var cardIntro = introA;
                if (introB) {
                    cardIntro += "<br>" + introB;
                }
                if (introC) {
                    cardIntro += "<br>" + introC;
                }
                if (!cardName) {
                    layer.msg('请填写会员卡名称');
                    return;
                }
                if (!cardIntro) {
                    layer.msg('请填写套餐简介');
                    return;
                }
                var url = appPath + '/system/updateSysConfig.do';
                var param = {};
                if (id == "value") {
                    param.valueCardName = cardName;
                    param.valueCardIntro = cardIntro;
                } else {
                    param.numberCardName = cardName;
                    param.numberCardIntro = cardIntro;
                }
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        // layer.msg("操作成功", {icon: 1});
                        // window.setTimeout(function () {
                        window.location.reload();
                        // }, 1000);
                    } else {
                        layer.msg(res.msg);
                    }
                });
            }, btn2: function (index) {// 或者使用btn2（concel）
            }, cancel: function (index) {// 或者使用btn2（concel）
            }
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
            if (btns.indexOf("edit") == -1) {
                $(".btn-default").prop("disabled", "disabled");
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

});
