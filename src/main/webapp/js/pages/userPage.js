var urlData = location.href.split('?')[1].split("=");
var pageType = urlData[0];
var userId = urlData[1];
$(function () {
    if (pageType == "edit") {
        $("title").html("编辑会员");
        $(".pageType").html("编辑会员");
    } else {
        $("title").html("添加会员");
        $(".pageType").html("添加会员");
    }
    createUploader("#imgPicker", "#imgShow", "#imgShowDiv", "#headimgurl", 80, 80);//单张图片上传

    //如果是编辑，初始化会员数据
    if (pageType == "edit") {
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
                $("#intro").val(res.data.intro);
            } else {
                layer.msg(res.msg);
            }
        });
    }

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var headimgurl = $("#headimgurl").val();
        var name = $.trim($("#name").val());
        var phone = $.trim($("#phone").val());
        var sex = $("#sex").val();
        var intro = $.trim($("#intro").val());
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
            return;
        }
        if (!sex) {
            layer.msg('请选择性别');
            $("#sex").focus();
            return;
        }
        if (!intro) {
            layer.msg('请填写个人简介');
            $("#intro").focus();
            return;
        }
        if (!headimgurl) {
            layer.msg('请上传头像');
            $("#name").focus();
            return;
        }

        var url = "";
        var param = {};
        param.headimgurl = headimgurl;
        param.name = name;
        param.phone = phone;
        param.sex = sex;
        param.intro = intro;
        param.userType = 1;//用户类型  1：普通用户  2：会员
        param.pageType = 1;//页面类型 1：会员管理  2：教练管理
        if (pageType == "add") {
            url = appPath + '/user/addUser.do';
        } else {
            url = appPath + '/user/updateUser.do';
            param.userId = userId;
        }
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/user.html';
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