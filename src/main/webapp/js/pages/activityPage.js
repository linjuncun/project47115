var urlData = location.href.split('?')[1].split("=");
var pageType = urlData[0];
var courseId = urlData[1];
var requestNum = 0;//页面请求数量，用于在其他请求完成后再初始化活动数据
$(function () {
    if (pageType == "edit") {
        $("title").html("编辑活动");
        $(".pageType").html("编辑活动");
    } else {
        $("title").html("添加活动");
        $(".pageType").html("添加活动");
    }

    // 初始化教练数据
    $.post(appPath + '/user/getUsers.do', {"userType": 2, "status": 1}, function (res) {
        if (res.code == 0) {
            var html = '<option value="">选择教练</option>';
            for (var i = 0; i < res.data.length; i++) {
                html += '<option value="' + res.data[i].userId + '">' + res.data[i].name + '</option>';
            }
            $("#userId").html(html);
            requestNum++;
        } else {
            layer.msg(res.msg);
        }
    });
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
    //选择框功能
    $(".selectList").on("change", function () {
        var $this = $(this);
        var id = $this.val();
        var text = $this.find("option:selected").text();
        if (id && $this.parent().find(".check-item[data-id='" + id + "']").length == 0) {
            $this.parent().append('<span class="check-item" data-id="' + id + '">' + text + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>');
        }
        if (id == "0") {
            $this.parent().find(".check-item").remove();
        }
    })

    if (pageType == "edit") {
        var timer = setInterval(function () {
            if (requestNum >= 2) {
                //当其他数据请求成功后再执行
                $.post(appPath + '/course/getCourseDetail.do', {'courseId': courseId}, function (res) {
                    if (res.code == 0) {
                        $("#courseName").val(res.data.courseName);
                        $("#maxPeople").val(res.data.maxPeople);
                        $("#solgan").val(res.data.solgan);
                        $("#userId").val(res.data.userId);
                        $("#classHour").val(formatDate(res.data.classHour, 3));
                        $("#price").val(res.data.price);
                        $("#intro").html(res.data.intro);
                        $("#notes").html(res.data.notes);
                        //门店
                        var gyms = res.data.supportGym;
                        var gymHtml = '';
                        for (var i = 0; i < gyms.length; i++) {
                            gymHtml += '<span class="check-item" data-id="' + gyms[i].gymId + '">' + gyms[i].gymName + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>&nbsp;';
                        }
                        $("#gymId").parent().append(gymHtml);
                        if (gyms.length == 0) {
                            $("#gymId").val("0");
                        }
                        //图片
                        var images = res.data.imgs.split(",");
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
                clearInterval(timer);//清除定时器
            }
        }, 10);
    }

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var courseName = $.trim($("#courseName").val());
        var maxPeople = $.trim($("#maxPeople").val());
        var solgan = $.trim($("#solgan").val());
        var userId = $("#userId").val();
        var classHour = $.trim($("#classHour").val());
        var price = $.trim($("#price").val());
        var intro = $.trim($("#intro").val());
        var notes = $.trim($("#notes").val());
        var flag = false;
        if (!courseName) {
            layer.msg('请填写活动名称');
            $("#courseName").focus();
            return;
        }
        if (!maxPeople) {
            layer.msg('请填写报名人数上限');
            $("#maxPeople").focus();
            return;
        }
        if (!solgan) {
            layer.msg('请填写广告语');
            $("#solgan").focus();
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
        var gymIds = "";//门店id
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
        if (!price) {
            layer.msg('请填写价格');
            $("#price").focus();
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
        $(".int-reg").each(function () {
            var temp = $.trim($(this).val());
            var id = $(this).attr("id");
            if (!intReg.test(temp)) {
                flag = true;
                return false;
            }
        })
        if (flag) {
            layer.msg('请输入正确的数字');
            return;
        }
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
        var functionId = $("#sideMenu").attr("data-pageid");
        var param = {};
        var url = url = appPath + '/course/addCourse.do';
        if (pageType == "edit") {
            url = appPath + '/course/updateCourse.do';
            param.courseId = courseId;
        }
        param.functionId = functionId;
        param.courseName = courseName;
        param.maxPeople = maxPeople;
        param.solgan = solgan;
        param.userId = userId;
        param.classHour = classHour;
        param.gymIds = gymIds;
        param.price = price;
        param.intro = intro;
        param.notes = notes;
        param.imgs = images;
        param.type = 2;
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/activity.html';
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
//移除自己
function delSelf(item) {
    $(item).parent().parent().find(".selectList").val("");
    $(item).parent().remove();
}

