var tipsColor = "#78BA32";//小提示颜色
/**
 * 获取项目根目录全路径
 */
var appPath = getRootPath();
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0, pos);
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    return (localhostPath + projectName);
}

$(function () {
    //提交按钮
    $(".submitBtn").on('click', function () {
        resetPwd();
    })
    /**
     * 返回按钮
     */
    $("#backBtn").on("click", function () {
        window.location.href = document.referrer;//返回上一页并刷新
    })
})

// 回车键
document.onkeydown = function (event) {
    var e = event || window.event;
    if (e && e.keyCode == 13) {
        resetPwd();
    }
}

function resetPwd() {
    var oldPwd = $.trim($("#oldPwd").val());
    var password = $.trim($("#password").val());
    var passwordChk = $.trim($("#passwordChk").val());
    if (oldPwd && password && passwordChk) {
        if (password != passwordChk) {
            layer.msg("两次新密码输入不一致");
            return;
        }
        $.post(appPath + '/resetPwd.do', {
            'oldPwd': hex_md5(oldPwd),
            'password': hex_md5(password)
        }, function (res) {
            if (res.code == 0) {
                layer.msg("修改成功", {icon: 1});
                window.setTimeout(function () {
                    history.back();
                }, 1000);
            } else {
                layer.msg(res.msg);
            }
        });
    } else if (!oldPwd) {
        layer.tips('请输入原密码', '#oldPwd', {
            tips: [2, tipsColor]
        });
    } else if (!password) {
        layer.tips('请输入新密码', '#password', {
            tips: [2, tipsColor]
        });
    } else if (!passwordChk) {
        layer.tips('请再次输入新密码', '#passwordChk', {
            tips: [2, tipsColor]
        });
    }
}