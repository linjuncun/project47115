var urlData = location.href.split('?')[1].split("=");
var pageType = urlData[0];
var userId = urlData[1];
var requestNum = 0;//页面请求数量，用于在门店数据初始化完成后再初始化教练数据
var registered = false;//手机号是否注册为会员
var registeredUserId;//已注册会员userId
$(function () {
    if (pageType == "edit") {
        $("title").html("编辑教练");
        $(".pageType").html("编辑教练");
    } else {
        $("title").html("添加教练");
        $(".pageType").html("添加教练");
    }
    createUploader("#imgPicker", "#imgShow", "#imgShowDiv", "#headimgurl", 80, 80);//单张图片上传

    // 初始化门店数据
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

    //选择门店
    $("#gymId").on("change", function () {
        var $this = $(this);
        var gymId = $this.val();
        var gymName = $this.find("option:selected").text();
        if (gymId && $this.parent().find(".check-item[data-id='" + gymId + "']").length == 0) {
            $this.parent().append('<span class="check-item" data-id="' + gymId + '">' + gymName + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>');
        }
    })

    //检查手机号是否注册
    $("#phone").on("change", function () {
        var $this = $(this);
        var phone = $this.val();
        $.post(appPath + '/user/getUserDetail.do', {'phone': phone}, function (res) {
            if (res.data.userType == 1) {
                //已有会员帐号绑定此手机
                registered = true;
                registeredUserId = res.data.userId;
            } else {
                //已有教练帐号绑定此手机
                if ((pageType == "edit" && res.data.userId != userId) || pageType == 'add') {
                    layer.msg("该手机号码已注册");
                }
            }
        })

    })

    //如果是编辑，初始化教练数据
    if (pageType == "edit") {
        var timer = setInterval(function () {
            if (requestNum >= 1) {//当其他数据请求成功后再执行
                $.post(appPath + '/user/getUserDetail.do', {'userId': userId}, function (res) {
                    if (res.code == 0) {
                        if (res.data.headimgurl) {
                            if (res.data.headimgurl.indexOf("http") == -1) {
                                $("#imgShow").prop("src", imgViewPath + res.data.headimgurl);
                            } else {
                                $("#imgShow").prop("src", res.data.headimgurl);
                            }
                            $("#headimgurl").val(res.data.headimgurl);
                        }
                        $("#name").val(res.data.name);
                        $("#phone").val(res.data.phone);
                        $("#sex").val(res.data.sex);
                        $("#coachType").val(res.data.coachType);
                        $("#weight").val(res.data.weight);
                        var gymList = res.data.gymList;
                        var gymStr = '';
                        for (var i = 0; i < gymList.length; i++) {
                            gymStr += '<span class="check-item" data-id="' + gymList[i].gymId + '">' + gymList[i].gymName + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>&nbsp;';
                        }
                        $("#gymId").parent().append(gymStr);
                        var coachLabel = res.data.coachLabel;
                        var labels = coachLabel.split(",");
                        var html = "";
                        for (var i = 0; i < labels.length; i++) {
                            html += '<span class="check-item" data-value="' + labels[i] + '">' + labels[i] + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>';
                        }
                        $("#coachLabel").parent().append(html);

                        $("#intro").val(res.data.intro);

                        //教练图片
                        var images = res.data.coachImages.split(",");
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
        var headimgurl = $("#headimgurl").val();
        var name = $.trim($("#name").val());
        var phone = $.trim($("#phone").val());
        var sex = $("#sex").val();
        var coachType = $("#coachType").val();
        var weight = $.trim($("#weight").val());
        var intro = $.trim($("#intro").val());
        var gymIds = "";//门店id
        $("#gymId-div .check-item").each(function () {
            gymIds += $(this).attr("data-id") + ",";
        })
        if (gymIds) {
            gymIds = gymIds.substring(0, gymIds.length - 1);
        }
        var coachLabel = "";//标签
        $("#coachLabel-div .check-item").each(function () {
            coachLabel += $(this).attr("data-value") + ",";
        })
        if (coachLabel) {
            coachLabel = coachLabel.substring(0, coachLabel.length - 1);
        } else {
            coachLabel = $.trim($("#coachLabel").val());
        }

        if (!headimgurl) {
            layer.msg('请上传头像');
            $("#name").focus();
            return;
        }
        if (!name) {
            layer.msg('请填写姓名');
            $("#name").focus();
            return;
        }
        if (!phone) {
            layer.msg('请填写手机号码');
            $("#phone").focus();
            return;
        } else if (!phoneReg.test(phone)) {
            layer.msg("请输入正确的手机号");
        }
        if (!sex) {
            layer.msg('请选择性别');
            $("#sex").focus();
            return;
        }
        if (!coachType) {
            layer.msg('请选择类型');
            $("#coachType").focus();
            return;
        }
        if (!weight) {
            layer.msg('请填写权重');
            $("#weight").focus();
            return;
        }
        if (!intReg.test(weight)) {
            layer.msg('请输入正确的数字');
            $("#weight").focus();
            return;
        }
        if (!gymIds) {
            layer.msg('请选择门店');
            $("#gymId").focus();
            return;
        }
        if (!coachLabel) {
            layer.msg('请填写标签');
            $("#coachLabel").focus();
            return;
        }
        if (!intro) {
            layer.msg('请填写个人简介');
            $("#intro").focus();
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

        var url = "";
        var param = {};
        param.headimgurl = headimgurl;
        param.name = name;
        param.phone = phone;
        param.sex = sex;
        param.coachType = coachType;
        param.weight = weight;
        param.gymIds = gymIds;
        param.coachLabel = coachLabel;
        param.intro = intro;
        param.coachImages = images;
        param.userType = 2;//用户类型  1：普通用户  2：教练
        if (pageType == "add") {
            url = appPath + '/user/addUser.do';
        } else {
            url = appPath + '/user/updateUser.do';
            param.userId = userId;
        }
        //新增教练帐号时，如果已有会员帐号绑定此手机
        if (registered && pageType == 'add') {
            var msg = "该手机号已是您的用户，确认将其设置为教练？如该用户有充值记录，请在设置为教练之前先线下结算";
            layer.confirm(msg, {
                icon: 0,
                btn: ['确定', '取消'] // 按钮
            }, function () {
                url = appPath + '/user/updateUser.do';
                param.userId = registeredUserId;
                $.post(url, param, function (res) {
                    if (res.code == 0) {
                        layer.closeAll();
                        layer.msg("操作成功", {icon: 1});
                        window.setTimeout(function () {
                            window.location.href = appPath + '/pages/coach.html';
                        }, 1000);
                    } else {
                        layer.msg(res.msg);
                    }
                });
            }, function () {
            });
        } else {
            $.post(url, param, function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    layer.msg("操作成功", {icon: 1});
                    window.setTimeout(function () {
                        window.location.href = appPath + '/pages/coach.html';
                    }, 1000);
                } else {
                    layer.msg(res.msg);
                }
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
    $("#gymId").val("");
    $(item).parent().remove();
}


/**
 * 添加标签
 */
function addLabel() {
    var label = $.trim($("#coachLabel").val());
    if (label) {
        if ($("#coachLabel").parent().find(".check-item[data-value='" + label + "']").length == 0) {
            if ($("#coachLabel").parent().find(".check-item").length >= 8) {
                layer.msg("最多添加8个标签");
                return;
            }
            $("#coachLabel").parent().append('<span class="check-item" data-value="' + label + '">' + label + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>');
            $("#coachLabel").val("");
        } else {
            layer.msg("标签重复");
        }
    }
}
//移除标签
function delSelf(item) {
    $(item).parent().remove();
}
