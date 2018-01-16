var page = location.href.split('?')[1];
var templateList = {};// 模版json数据
var courseType = 1;//课程类型 1：团课 2：私教
var functionId = "";
$(function () {
    $("#sideMenu").attr("data-pageid", page);
    if (page == "13_14") {
        functionId = 14;
        $("title").html("添加团课课程");
        $(".header-one").html("团课");
        $(".header-two").html("添加课程");
        $(".header-three").html("添加团课课程");
        $("#templateTitle").html("团课模板");
        courseType = 1;
    } else {
        functionId = 24;
        $("title").html("添加私教课程");
        $(".header-one").html("私教");
        $(".header-two").html("添加课程");
        $(".header-three").html("添加私教课程");
        $("#templateTitle").html("私教模板");
        courseType = 2;
    }

    // 初始化模版类型数据
    $.post(appPath + '/template/getTemplates.do', {"courseType": courseType}, function (res) {
        if (res.code == 0) {
            var template = '<option value="">选择模板</option>';
            for (var i = 0; i < res.data.length; i++) {
                templateList['' + res.data[i].templateId] = res.data[i];// 维护返回的数据
                template += '<option value="' + res.data[i].templateId + '" data-minutes="' + res.data[i].minutes + '">' + res.data[i].templateName + '</option>';
            }
            $("#templateId").html(template);
        } else {
            layer.msg(res.msg);
        }
    })
    //模版变化事件
    $("#templateId").on("change", function () {
        var id = $("#templateId").val();
        if (id) {
            var template = templateList['' + id];
            $("#courseName").val(template.templateName);
            $("#intro").html(template.intro);
            $("#notes").html(template.notes);
        } else {
            $("#courseName").val("");
            $("#intro").html("");
            $("#notes").html("");
        }
    })

    // 初始化健身房数据
    $.post(appPath + '/gym/getGyms.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var gym = '<option value="">选择门店</option>';
            for (var i = 0; i < res.data.length; i++) {
                gym += '<option value="' + res.data[i].gymId + '">' + res.data[i].gymName + '</option>';
            }
            $("#gymId").html(gym);
        } else {
            layer.msg(res.msg);
        }
    })

    // 初始化教练数据
    $.post(appPath + '/user/getUsers.do', {"userType": 2, "status": 1}, function (res) {
        if (res.code == 0) {
            var coach = '<option value="">选择员工</option>';
            for (var i = 0; i < res.data.length; i++) {
                coach += '<option value="' + res.data[i].userId + '">' + res.data[i].name + '</option>';
            }
            $("#userId").html(coach);
        } else {
            layer.msg(res.msg);
        }
    })

    /**
     * 周期改变事件
     */
    $("#period-list").on("change", ".text-item", function () {
        var $div = $(this).parent().parent().parent();
        var periodText = "";
        $div.find(".period").each(function () {
            if ($(this).is(':checked')) {
                periodText += $(this).next().html() + "、";
            }
        })
        if (periodText) {
            periodText = periodText.substring(0, periodText.length - 1);
        }
        var hour = parseInt($.trim($($div).find(".hour").val()));
        var minute = parseInt($.trim($($div).find(".minute").val()));
        if (hour) {
            var time = hour;
            if (hour < 10) {
                time = "0" + hour;
            }
            if (minute) {
                if (minute < 10) {
                    time += ":0" + minute;
                } else {
                    time += ":" + minute;
                }
            } else {
                time += ":00";
            }
            periodText += "的 " + time;
            var minutes = $("#templateId").find("option:selected").attr("data-minutes");//模版中的课长（分钟）
            if (minutes) {
                if (!minute) {
                    minute = 0;//如果没填，则等于0
                }
                var courseHour = parseInt(parseInt(minutes) / 60);//课程时长的小时数
                hour += courseHour;
                minutes = parseInt(minutes) % 60;//课程时长的分钟数
                if (minute + parseInt(minutes) >= 60) {
                    hour = hour + 1;
                    minute = minute + parseInt(minutes) - 60;
                } else {
                    minute = minute + parseInt(minutes);
                }
                if (hour < 10) {
                    hour = "0" + hour;
                }
                if (minute < 10) {
                    minute = "0" + minute;
                }
                periodText += " - " + hour + ":" + minute;
            }
        }
        $div.find(".periodText").html(periodText);
    })

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        //获取周期数组
        var periods = [];
        var checkMap = new Map();//用于判断重复
        var isTimeOk = true;
        $(".period").each(function () {
            if ($(this).is(':checked')) {
                var $div = $(this).parent().parent().parent();
                var weekDay = $(this).attr("data-value");
                var hour = parseInt($.trim($($div).find(".hour").val()));
                var minuteValue = $.trim($($div).find(".minute").val());
                if (!minuteValue) {
                    minuteValue = 0;
                }
                var minute = parseInt(minuteValue);
                if (hour && minute >= 0) {
                    if (hour > 23) {
                        isTimeOk = false;
                        return false;
                    }
                    if (minute > 59) {
                        isTimeOk = false;
                        return false;
                    }
                    if (hour < 10) {
                        hour = "0" + hour;
                    }
                    if (minute < 10) {
                        minute = "0" + minute;
                    }
                    var time = hour + ":" + minute;
                    var timeList = checkMap.get(weekDay);
                    if (timeList && timeList.length > 0) {
                        var hasSame = false;//是否有相同时间的周期了
                        for (var i = 0; i < timeList.length; i++) {
                            if (timeList[i] == time) {
                                hasSame = true;
                            }
                        }
                        if (!hasSame) {
                            timeList.push(time);
                            checkMap.set(weekDay, timeList);
                            var obj = {};
                            obj.weekDay = weekDay;
                            obj.time = time;
                            periods.push(obj);
                        }
                    } else {
                        var temp = [];
                        temp.push(time);
                        checkMap.set(weekDay, temp);
                        var obj = {};
                        obj.weekDay = weekDay;
                        obj.time = time;
                        periods.push(obj);
                    }
                } else {
                    isTimeOk = false;
                    return false;
                }
            }
        })
        if (!isTimeOk) {
            layer.msg("请输入正确的时间");
            return;
        }

        var templateId = $("#templateId").val();
        var courseName = $.trim($("#courseName").val());
        var gymId = $("#gymId").val();
        var userId = $("#userId").val();
        var startDate = $.trim($("#startDate").val());
        var endDate = $.trim($("#endDate").val());
        var price = $.trim($("#price").val());
        var courseLabel = "";//标签
        $(".check-item").each(function () {
            courseLabel += $(this).attr("data-value") + ",";
        })
        if (courseLabel) {
            courseLabel = courseLabel.substring(0, courseLabel.length - 1);
        } else {
            courseLabel = $.trim($("#courseLabel").val());
        }
        var intro = $.trim($("#intro").val());
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

        //检查所选周期是否至少有一个可以和开始、截止日期匹配上
        var isBad = true;
        var beginTime = new Date(startDate).getTime();
        var endTime = new Date(endDate).getTime();
        if (( endTime - beginTime) / (1000 * 60 * 60 * 24) >= 7) {
            isBad = false;
        } else {
            var begin = new Date(startDate).getDay() + 1;
            var end = new Date(endDate).getDay() + 1;
            for (var i = 0; i < periods.length; i++) {
                if (periods[i].weekDay >= begin && periods[i].weekDay <= end) {
                    isBad = false;
                }
                if (periods[i].weekDay >= end && periods[i].weekDay <= begin) {
                    isBad = false;
                }
            }
        }
        if (isBad) {
            layer.msg('所选周期均在课程日期范围之外，请重新设置周期');
            return;
        }

        if (!templateId) {
            layer.msg('请选择模版');
            $("#templateId").focus();
            return;
        }
        if (!courseName) {
            layer.msg('请填写课程名称');
            $("#courseName").focus();
            return;
        }
        if (!gymId) {
            layer.msg('请选择门店');
            $("#gymId").focus();
            return;
        }
        if (!userId) {
            layer.msg('请选择教练');
            $("#userId").focus();
            return;
        }
        if (!startDate) {
            layer.msg('请选择时间');
            $("#startDate").focus();
            return;
        }
        if (!endDate) {
            layer.msg('请选择时间');
            $("#endDate").focus();
            return;
        }
        if (periods.length == 0) {
            layer.msg('请设置课程周期');
            return;
        }
        if (!price) {
            layer.msg('请填写价格');
            $("#price").focus();
            return;
        }
        if (!courseLabel) {
            layer.msg('请填写标签');
            $("#courseLabel").focus();
            return;
        }
        if (!intro) {
            layer.msg('请填写文字描述');
            $("#intro").focus();
            return;
        }
        if (!notes) {
            layer.msg('请填写注意事项');
            $("#notes").focus();
            return;
        }
        var url = appPath + '/course/addCourse.do';
        var param = {};
        param.functionId = functionId;
        param.templateId = templateId;
        param.courseName = courseName;
        param.gymId = gymId;
        param.userId = userId;
        param.startDate = startDate;
        param.endDate = endDate;
        param.price = price;
        param.courseLabel = courseLabel;
        param.intro = intro;
        param.notes = notes;
        param.periods = JSON.stringify(periods);
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/course.html?' + courseType;
                }, 1000);
            } else {
                layer.msg(res.msg);
            }
        });
    });

    // 按钮权限控制
    var btns = "";// 权限按钮字符串
    var pageid = $("#sideMenu").attr("data-pageid").split("_")[1];// 页面ID
    $.post(appPath + '/user/getUserFunctions.do', {'functionId': pageid}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                btns += res.data[i].code + ",";
            }
            if (btns.indexOf("add") == -1) {
                $("#saveBtn").prop("disabled", "disabled");
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
 * 新增周期
 */
function addPeriod() {
    var html = '';
    html += '<div class="period-div">';
    html += '<a href="javascript:void(0);" onclick="deletePeriod(this)"><i class="fa fa-close delete-i"></i></a>';
    html += '<div class="layui-form-item">';
    html += '<label class="layui-form-label">每</label>';
    html += '<div class="layui-input-block margin-left-80">';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="2" /><span class="item-span">星期一</span>';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="3" /><span class="item-span">星期二</span>';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="4" /><span class="item-span">星期三</span>';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="5" /><span class="item-span">星期四</span>';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="6" /><span class="item-span">星期五</span>';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="7" /><span class="item-span">星期六</span>';
    html += '<input class="checkbox-small period text-item" type="checkbox" data-value="1" /><span class="item-span">星期日</span>';
    html += '</div></div>';
    html += '<div class="layui-form-item">';
    html += '<label class="layui-form-label">上课时间</label>';
    html += '<div class="layui-input-block margin-left-80">';
    html += '<input class="layui-input-tiny hour-reg hour text-item" type="number"  maxlength="2" />&nbsp;时&nbsp;<input class="layui-input-tiny minute-reg minute text-item" type="number"  maxlength="2" />&nbsp;分';
    html += '</div></div>';
    html += '<div class="layui-form-item">';
    html += '<span style="padding-left: 0.26rem;">即<span style="margin-left: 4rem;">上课时间为每个&nbsp;<span class="periodText"></span></span></span>';
    html += '</div></div>';
    $("#period-list").append(html);
}

/**
 * 删除周期
 */
function deletePeriod(item) {
    $(item).parent().remove();
}

/**
 * 清除课程周期
 */
function clearPeriod() {
    $(".period").prop("checked", false);
    $(".hour").val("");
    $(".minute").val("");
    $(".periodText").html("");
}

/**
 * 添加标签
 */
function addLabel() {
    var label = $.trim($("#courseLabel").val());
    if (label) {
        if ($("#courseLabel").parent().find(".check-item[data-value='" + label + "']").length == 0) {
            if ($("#courseLabel").parent().find(".check-item").length >= 8) {
                layer.msg("最多添加8个标签");
                return;
            }
            $("#courseLabel").parent().append('<span class="check-item" data-value="' + label + '">' + label + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>');
            $("#courseLabel").val("");
        } else {
            layer.msg("标签重复");
        }
    }
}
//移除标签
function delSelf(item) {
    $(item).parent().remove();
}
