var urlData = location.href.split('?')[1].split("=");
var pageType = urlData[0];
var page = urlData[1];
var templateId = urlData[2];
var courseType = 1;//课程类型 1：团课 2：私教
var functionId = "";
$(function () {
    $("#sideMenu").attr("data-pageid", page);
    if (page == "13_14") {
        functionId = 14;
        $(".header-one").html("团课");
        if (pageType == "add") {
            $(".header-three").html("添加团课模版");
        } else {
            $(".header-three").html("编辑团课模版");
        }
        courseType = 1;
    } else {
        functionId = 24;
        $(".header-one").html("私教");
        if (pageType == "add") {
            $(".header-three").html("添加私教模版");
        } else {
            $(".header-three").html("编辑私教模版");
        }
        courseType = 2;
        $("#groupMax").addClass("hidden");
        $("#privateMax").removeClass("hidden");
    }
    //如果是编辑，初始化模版数据
    if (pageType == "edit") {
        $.post(appPath + '/template/getTemplateDetail.do', {'templateId': templateId}, function (res) {
            if (res.code == 0) {
                $("#templateName").val(res.data.templateName);
                $("#minutes").val(res.data.minutes);
                $("#minNum").val(res.data.minNum);
                $("#maxNum").val(res.data.maxNum);
                $("#maxNumSelect").val(res.data.maxNum);
                $("#intro").val(res.data.intro);
                $("#notes").val(res.data.notes);
                //图片
                var images = res.data.images.split(",");
                var imgHtml = "";
                for (var i = 0; i < images.length; i++) {
                    $("#fileNumLimit").val(10 - images.length);
                    if (images[i]) {
                        imgHtml += '<span><img src="' + imgViewPath + images[i] + '" class="img-normal" data-value="' + images[i] + '" /><i class="fa fa-close del-img" onclick="delImg(this);"></i></span>';
                    }
                }
                $("#images").html(imgHtml);
            } else {
                layer.msg(res.msg);
            }
        });
    }

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var templateName = $.trim($("#templateName").val());
        var minutes = $.trim($("#minutes").val());
        var minNum = $.trim($("#minNum").val());
        var maxNum = 0;
        if (courseType == 1) {
            maxNum = $.trim($("#maxNum").val());
        } else {
            maxNum = $("#maxNumSelect").val();
        }
        var intro = $.trim($("#intro").val());
        var notes = $.trim($("#notes").val());
        //课程图片
        var oldImages = "";
        $("#images img").each(function () {
            oldImages += $(this).attr("data-value") + ",";
        })
        if (oldImages) {
            oldImages = oldImages.substring(0, oldImages.length - 1);
        }
        var images = $("#imgUrlValue").val();
        if (images) {
            images = images.substring(1);
        }
        if (images && oldImages) {
            images = oldImages + "," + images;
        } else if (!images) {
            images = oldImages;
        }
        if (images) {
            if (images.split(",").length > 10) {
                layer.msg('最多上传10张图片');
                return;
            }
        } else {
            layer.msg('请上传图片');
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
        if (!templateName) {
            layer.msg('请填写模版');
            $("#templateName").focus();
            return;
        }
        if (!minutes) {
            layer.msg('请填写时长');
            $("#minutes").focus();
            return;
        }
        if (!minNum) {
            layer.msg('请填写最少开课人数');
            $("#minNum").focus();
            return;
        }
        if (!maxNum) {
            layer.msg('请填写上课人数上限');
            $("#userId").focus();
            return;
        }
        if (!intro) {
            layer.msg('请填写文字介绍');
            $("#intro").focus();
            return;
        }
        if (!notes) {
            layer.msg('请填写注意事项');
            $("#notes").focus();
            return;
        }
        var url = "";
        var param = {};
        param.functionId = functionId;
        param.templateName = templateName;
        param.minutes = minutes;
        param.minNum = minNum;
        param.maxNum = maxNum;
        param.intro = intro;
        param.notes = notes;
        param.images = images;
        param.courseType = courseType;

        if (pageType == "add") {
            url = appPath + '/template/addTemplate.do';
        } else {
            url = appPath + '/template/updateTemplate.do';
            param.templateId = templateId;
        }
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/template.html?' + courseType;
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
            if (btns.indexOf("template-edit") == -1) {
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

