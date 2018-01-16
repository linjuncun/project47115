var urlData = location.href.split('?')[1].split("=");
var page = urlData[0];
var courseId = urlData[1];
var requestNum = 0;//页面请求数量，用于判断其他请求是否都完成
var templateList = {};// 模版json数据
var courseType = 1;//课程类型 1：团课 2：私教
var functionId = "";
$(function () {
    $("#sideMenu").attr("data-pageid", page);
    if (page == "13_14") {
        functionId = 14;
        $("title").html("编辑团课课程");
        $(".header-two").html("编辑课程");
        $(".header-three").html("编辑团课课程");
        $("#templateTitle").html("团课模板");
        courseType = 1;
    } else {
        functionId = 24;
        $("title").html("编辑私教课程");
        $(".header-two").html("编辑课程");
        $(".header-three").html("编辑私教课程");
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
            requestNum++;
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
            requestNum++;
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
            requestNum++;
        } else {
            layer.msg(res.msg);
        }
    })

    //初始化课程数据
    var timer = setInterval(function () {
        if (requestNum >= 3) {//当其他数据请求成功后再执行
            $.post(appPath + '/course/getCourseDetail.do', {'courseId': courseId}, function (res) {
                if (res.code == 0) {
                    $(".header-one").html(res.data.courseName);
                    $("#templateId").val(res.data.templateId);
                    $("#courseName").val(res.data.courseName);
                    $("#gymId").val(res.data.gymId);
                    $("#userId").val(res.data.userId);
                    $("#classHour").val(formatDate(res.data.classHour, 3));
                    $("#price").val(res.data.price);
                    $("#intro").html(res.data.intro);
                    $("#notes").html(res.data.notes);
                    var courseLabel = res.data.courseLabel;
                    var labels = courseLabel.split(",");
                    var html = "";
                    for (var i = 0; i < labels.length; i++) {
                        html += '<span class="check-item" data-value="' + labels[i] + '">' + labels[i] + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>';
                    }
                    $("#courseLabel").parent().append(html);
                } else {
                    layer.msg(res.msg);
                }
            });
            clearInterval(timer);//清除定时器
        }
    }, 10);

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var templateId = $("#templateId").val();
        var courseName = $.trim($("#courseName").val());
        var gymId = $("#gymId").val();
        var userId = $("#userId").val();
        var classHour = $.trim($("#classHour").val());
        var price = $.trim($("#price").val());
        var courseLabel = "";//标签
        $(".check-item").each(function () {
            courseLabel += $(this).attr("data-value") + ",";
        })
        if (courseLabel) {
            courseLabel = courseLabel.substring(0, courseLabel.length - 1);
        }else{
            courseLabel = $.trim($("#courseLabel").val());
        }
        var intro = $.trim($("#intro").val());
        var notes = $.trim($("#notes").val());
        var flag = false;
        $(".int-reg").each(function () {
            var temp = $.trim($(this).val());
            var id = $(this).attr("id");
            if (!intReg.test(temp)) {
                flag = true;
                return false;
            }
        })
        if(flag){
            layer.msg('请输入正确的数字');
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
        if (!classHour) {
            layer.msg('请选择时间');
            $("#classHour").focus();
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
        var url = appPath + '/course/updateCourse.do';
        var param = {};
        param.functionId = functionId;
        param.courseId = courseId;
        param.templateId = templateId;
        param.courseName = courseName;
        param.gymId = gymId;
        param.userId = userId;
        param.classHour = classHour;
        param.price = price;
        param.courseLabel = courseLabel;
        param.intro = intro;
        param.notes = notes;
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/course.html?'+courseType;
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
            if (btns.indexOf("edit") == -1) {
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
