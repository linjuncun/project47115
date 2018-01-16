var urlData = location.href.split('?')[1].split("=");
var pageType = urlData[0];
var cardType = urlData[1];
var packageId = urlData[2];
var requestNum = 0;//页面请求数量，用于在省市区等数据初始化完成后再初始化健身房数据
$(function () {
    if (pageType == "edit") {
        $("title").html("编辑套餐");
        $(".header-one").html("编辑套餐");
        $(".header-three").html(" 编辑会员卡套餐");
    }
    if (cardType == 2) {
        $("#cardType").html("会员次卡");
        $(".arriveType").html("充值次数");
        $("#arrive").prop("placeholder", "输入充值次数");
        $("#isReturnCash-div").addClass("hidden");
    }
    //是否返现
    $("#isReturnCash").on("change", function () {
        if ($("#isReturnCash").is(':checked')) {
            $("#condition-div").removeClass("hidden");
        } else {
            $("#condition-div").addClass("hidden");
        }
    })
    //返回
    $("#returnBtn").on("click", function () {
        window.location.href = appPath + '/pages/package.html?' + cardType;
    })

    // 初始化健身房数据
    $.post(appPath + '/gym/getGyms.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var gym = '<option value="">选择门店</option><option value="0">全部</option>';
            for (var i = 0; i < res.data.length; i++) {
                gym += '<option value="' + res.data[i].gymId + '">' + res.data[i].gymName + '</option>';
            }
            $("#gymId").html(gym);
            requestNum++;
        } else {
            layer.msg(res.msg);
        }
    })
    if (pageType == "edit") {
        var timer = setInterval(function () {
            if (requestNum >= 1) {
                //当其他数据请求成功后再执行
                $.post(appPath + '/package/getPackageDetail.do', {'packageId': packageId}, function (res) {
                    if (res.code == 0) {
                        $("#packageName").val(res.data.packageName);
                        if (res.data.isReturnCash == 1) {
                            $("#isReturnCash").prop("checked", true);
                            $("#condition-div").removeClass("hidden");
                            var list = res.data.returnCashCondition;
                            $(".days").val(list[0].days);
                            if (list[0].money > 0) {
                                $(".conditionType").val(1);
                                $(".conditionValue").val(list[0].money);
                            } else {
                                $(".conditionType").val(2);
                                $(".conditionValue").val(list[0].times);
                            }
                            $(".returnCash").val(list[0].returnCash);
                            for (var i = 1; i < list.length; i++) {
                                var html = '';
                                html += '<div class="condition-item" style="margin-bottom: 10px;">在<input type="number" class="layui-input-tiny int-reg blank days" value="' + list[i].days + '"/>天内，完成&nbsp;';
                                html += '<select class="layui-select-tiny blank conditionType">';
                                html += '<option value="1">金额</option>';
                                var conditionValue = 0;
                                if (list[i].times > 0) {
                                    html += '<option value="2" selected="selected">次数</option>';
                                    conditionValue = list[i].times;
                                } else {
                                    html += '<option value="2">次数</option>';
                                    conditionValue = list[i].money;
                                }
                                html += '</select>&nbsp;';
                                html += '<input type="number" class="layui-input-small int-reg blank conditionValue" value="' + conditionValue + '"/>则返现<input type="number" class="layui-input-small int-reg blank returnCash" value="' + list[i].returnCash + '"/>&nbsp;';
                                html += '<button class="btn btn-default" type="button" onclick="javascript:delCondition(this);">-&nbsp;</button>&nbsp;';
                                html += '</div>';
                                $("#conditionList").append(html);
                            }

                        }
                        $("#recharge").val(res.data.recharge);
                        $("#arrive").val(res.data.arrive);
                        $("#indate").val(res.data.indate);
                        $("#notes").val(res.data.notes);
                        //适用门店
                        var gyms = res.data.supportGym;
                        var gymHtml = '';
                        for (var i = 0; i < gyms.length; i++) {
                            gymHtml += '<span class="check-item" data-id="' + gyms[i].gymId + '">' + gyms[i].gymName + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>&nbsp;';
                        }
                        $("#gymId").parent().append(gymHtml);
                        if (gyms.length == 0) {
                            $("#gymId").val("0");
                        }
                        //适用课程
                        var courses = res.data.supportCourse;
                        var courseHtml = '';
                        for (var i = 0; i < courses.length; i++) {
                            courseHtml += '<span class="check-item" data-id="' + courses[i].courseId + '">' + courses[i].courseName + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>&nbsp;';
                        }
                        $("#courseId").parent().append(courseHtml);
                        if (courses.length == 0) {
                            $("#courseId").val("0");
                        }
                    } else {
                        layer.msg(res.msg);
                    }
                });
                clearInterval(timer);//清除定时器
            }
        }, 10);
    }

    //选择框功能
    $(".selectList").on("change", function () {
        var $this = $(this);
        var id = $this.val();
        var text = $this.find("option:selected").text();
        if (id && $this.parent().find(".check-item[data-id='" + id + "']").length == 0) {
            $this.parent().append('<span class="check-item" data-id="' + id + '">' + text + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>');
        }
        if ($this.prop("id") == 'gymId') {
            var gymIds = "";
            $this.parent().find(".check-item").each(function () {
                gymIds += $(this).attr("data-id") + ",";
            })
            gymIds = gymIds.substring(0, gymIds.length - 1);
            if (id == "0") {
                gymIds = "0";
            }
            initCourseList(gymIds);
        }
        if (id == "0") {
            $this.parent().find(".check-item").remove();
        }
    })

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var packageName = $.trim($("#packageName").val());
        if (!packageName) {
            layer.msg('请填写套餐名称');
            $("#packageName").focus();
            return;
        }
        var isReturnCash;
        if ($("#isReturnCash").is(':checked')) {
            isReturnCash = 1;
        } else {
            isReturnCash = 2;
        }
        var conditions = [];//返现条件数组
        var isOk = true;//检查返现条件是否完善
        if (isReturnCash == 1) {
            $("#conditionList .condition-item").each(function () {
                var $this = $(this);
                var days = $.trim($this.find(".days").val());
                var conditionType = $this.find(".conditionType").val();
                var conditionValue = $.trim($this.find(".conditionValue").val());
                var returnCash = $.trim($this.find(".returnCash").val());
                if (!days || !conditionValue || !returnCash) {
                    isOk = false;
                    return false;
                }
                var obj = {};
                obj.days = days;
                if (conditionType == 1) {
                    obj.money = conditionValue;
                } else if (conditionType == 2) {
                    obj.times = conditionValue;
                }
                obj.returnCash = returnCash;
                conditions.push(obj);
            })
        }
        if (!isOk) {
            layer.msg("请填写正确的返现条件");
            return;
        }
        var recharge = $.trim($("#recharge").val());
        var arrive = $.trim($("#arrive").val());
        if (!recharge) {
            layer.msg('请填写充值金额');
            $("#recharge").focus();
            return;
        }
        if (!arrive) {
            var msg = "请填写实际套餐到帐金额";
            if (cardType == 2) {
                msg = "请填写充值次数";
            }
            layer.msg(msg);
            $("#arrive").focus();
            return;
        }
        //判断返现金额是否满足条件
        for (var i = 0; i < conditions.length; i++) {
            if (conditions[i].money > 0 && (arrive - conditions[i].money) <= 100) {
                layer.msg('请填写正确的返现条件');
                return;
            }
        }
        var gymIds = "";
        $("#gymId").parent().find(".check-item").each(function () {
            gymIds += $(this).attr("data-id") + ",";
        })
        gymIds = gymIds.substring(0, gymIds.length - 1);
        if (!gymIds) {
            if ($("#gymId").val() == "0") {
                gymIds = "0";
            } else {
                layer.msg('请选择适用门店');
                return;
            }
        }
        var courseIds = "";
        $("#courseId").parent().find(".check-item").each(function () {
            courseIds += $(this).attr("data-id") + ",";
        })
        courseIds = courseIds.substring(0, courseIds.length - 1);
        if (!courseIds) {
            if ($("#courseId").val() == "0") {
                courseIds = "0";
            } else {
                layer.msg('请选择适用课程');
                return;
            }
        }
        var indate = $.trim($("#indate").val());
        var notes = $.trim($("#notes").val());

        var flag = false;
        $(".int-reg").each(function () {
            var temp = $.trim($(this).val());
            var id = $(this).prop("id");
            if (!intReg.test(temp)) {
                flag = true;
                return false;
            }
        })
        if (flag) {
            layer.msg('请输入正确的数字');
            return;
        }
        if (!indate) {
            layer.msg('请填写有效天数');
            $("#indate").focus();
            return;
        }
        if (!notes) {
            layer.msg('请填写注意事项');
            $("#notes").focus();
            return;
        }
        var url = "";
        var param = {};
        if (pageType == "add") {
            url = appPath + '/package/addPackage.do';
        } else {
            url = appPath + '/package/updatePackage.do';
            param.packageId = packageId;
        }
        param.cardType = cardType;
        param.packageName = packageName;
        param.isReturnCash = isReturnCash;
        param.conditions = JSON.stringify(conditions);
        param.recharge = recharge;
        param.arrive = arrive;
        param.gymIds = gymIds;
        param.courseIds = courseIds;
        param.indate = indate;
        param.notes = notes;

        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/package.html?' + cardType;
                }, 1000);
            } else {
                layer.msg(res.msg);
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
            if (pageType == "add") {
                if (btns.indexOf("add") == -1) {
                    $("#saveBtn").prop("disabled", "disabled");
                }
            } else {
                if (btns.indexOf("edit") == -1) {
                    $("#saveBtn").prop("disabled", "disabled");
                }
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
/**
 * 新增返现条件
 */
function addCondition() {
    if ($("#conditionList .days").length >= 3) {
        layer.msg("最多添加3个返现条件", {icon: 0});
        return;
    }
    var html = '';
    html += '<div class="condition-item" style="margin-bottom: 10px;">在<input type="number" class="layui-input-tiny int-reg blank days" oninput="if(value.length>9)value=value.slice(0,9)"/>天内，完成&nbsp;';
    html += '<select class="layui-select-tiny blank conditionType"><option value="1">金额</option><option value="2">次数</option></select>&nbsp;';
    html += '<input type="number" class="layui-input-small int-reg blank conditionValue" oninput="if(value.length>9)value=value.slice(0,9)"/>则返现<input type="number" class="layui-input-small int-reg blank returnCash" oninput="if(value.length>9)value=value.slice(0,9)"/>&nbsp;';
    html += '<button class="btn btn-default" type="button" onclick="javascript:delCondition(this);">-&nbsp;</button>&nbsp;';
    html += '</div>';
    $("#conditionList").append(html);
}

/**
 * 删除返现条件
 */
function delCondition(item) {
    $(item).parent().remove();
}

//移除标签
function delSelf(item) {
    var $select = $(item).parent().parent().find(".selectList");
    $select.val("");
    $(item).parent().remove();
    if ($select.prop("id") == 'gymId') {
        var gymIds = "";
        $select.parent().find(".check-item").each(function () {
            gymIds += $(this).attr("data-id") + ",";
        })
        gymIds = gymIds.substring(0, gymIds.length - 1);
        initCourseList(gymIds);
    }
}

// 初始化课程数据
function initCourseList(gymIds) {
    var html = '<option value="">选择课程</option><option value="0">全部</option>';
    if (!gymIds) {
        $("#courseId").html(html);
        return;
    }
    $.post(appPath + '/course/getCourseNoRepeat.do', {"gymIds": gymIds, "status": 1}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                html += '<option value="' + res.data[i].courseId + '">' + res.data[i].courseName + '</option>';
            }
            $("#courseId").html(html);
        } else {
            layer.msg(res.msg);
        }
    })
}