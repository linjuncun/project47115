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
        $(".submitBtn").prop("disabled", true);//置灰
        login();
    });
});

// 回车键
document.onkeydown = function (event) {
    var e = event || window.event;
    if (e && e.keyCode == 13) {
        login();
    }
}

function login() {
    var username = $.trim($("#username").val());
    var pwd = $.trim($("#password").val());
    if (username && pwd) {
        $.post(appPath + '/sysLogin.do', {
            'username': username,
            'password': hex_md5(pwd)
        }, function (res) {
            if (res.code == 0) {
                window.location.href = appPath + '/pages/homePage.html';//登录成功跳转到后台首页
            } else {
                layer.msg(res.msg);
            }
            $(".submitBtn").removeAttr("disabled");
        });
    } else if (!username) {
        layer.msg('请输入帐号');
        $(".submitBtn").removeAttr("disabled");
    } else if (!pwd) {
        layer.msg('请输入密码');
        $(".submitBtn").removeAttr("disabled");
    }
}
