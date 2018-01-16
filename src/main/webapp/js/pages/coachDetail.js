var urlData = location.href.split('?')[1].split("=");
var userId = urlData[0];
$(function () {
    /**
     * 编辑按钮
     */
    $("#editBtn").on("click", function () {
        window.location.href = appPath + '/pages/coachPage.html?edit=' + userId;
    });

    // 初始化数据
    $.post(appPath + '/user/getUserDetail.do', {'userId': userId}, function (res) {
        if (res.code == 0) {
            if (res.data.headimgurl) {
                if (res.data.headimgurl.indexOf("http") == -1) {
                    $("#headimgurl").prop("src", imgViewPath + res.data.headimgurl);
                } else {
                    $("#headimgurl").prop("src", res.data.headimgurl);
                }
            }
            $("#navName").html(res.data.name);
            $("#name").html(res.data.name);
            $("#phone").html(res.data.phone);
            var gymList = res.data.gymList;
            var str = "";
            for (var i = 0; i < gymList.length; i++) {
                str += gymList[i].gymName + "、";
            }
            if (str) {
                str = str.substring(0, str.length - 1);
            }
            $("#gymList").html(str);
            $("#weight").html(res.data.weight);
            if (res.data.coachType == 1) {
                $("#coachType").html("全职");
            } else {
                $("#coachType").html("兼职");
            }
            $("#intro").html(res.data.intro);
            var imgs = res.data.coachImages.split(",");
            var imgHtml = "";
            for (var i = 0; i < imgs.length; i++) {
                imgHtml += '<img src="' + imgViewPath + imgs[i] + '" class="img-normal" style="">&nbsp;';
            }
            $("#coachImages").html(imgHtml);

        } else {
            layer.msg(res.msg);
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
            if (btns.indexOf("edit") == -1) {
                $("#editBtn").prop("disabled", "disabled");
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
