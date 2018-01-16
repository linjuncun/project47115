var urlData = location.href.split('?')[1].split("=");
var gymId = urlData[0];
$(function () {
    /**
     * 编辑按钮
     */
    $("#editBtn").on("click", function () {
        window.location.href = appPath + '/pages/gymPage.html?edit=' + gymId;
    });

    // 初始化数据
    $.post(appPath + '/gym/getGymDetail.do', {'gymId': gymId}, function (res) {
        if (res.code == 0) {
            $("#navName").html(res.data.gymName);
            $("#gymName").html(res.data.gymName);
            $("#address").html(res.data.province + res.data.city + res.data.district + res.data.address);
            if (res.data.gymType == 2) {
                $("#gymType").html("共享店");
            } else {
                $("#gymType").html("常规店");
            }
            $("#truename").html(res.data.truename);
            $("#weight").html(res.data.weight);
            $("#intro").html(res.data.intro);
            var imgs = res.data.images.split(",");
            var imgHtml = "";
            for (var i = 0; i < imgs.length; i++) {
                imgHtml += '<img src="' + imgViewPath + imgs[i] + '" class="img-normal" style="">&nbsp;';
            }
            $("#images").html(imgHtml);

            var coachList = res.data.coachList;
            var fullTime = '';//全职教练
            var partTime = '';//兼职教练
            for (var i = 0; i < coachList.length; i++) {
                if (coachList[i].coachType == 1) {
                    fullTime += '<tr><td><img src="' + imgViewPath + coachList[i].headimgurl + '" class="table-img" style=""></td><td>' + coachList[i].name + '</td><td>' + coachList[i].phone + '</td></tr>';
                } else {
                    partTime += '<tr><td><img src="' + imgViewPath + coachList[i].headimgurl + '" class="table-img" style=""></td><td>' + coachList[i].name + '</td><td>' + coachList[i].phone + '</td></tr>';
                }
            }
            if (fullTime) {
                $("#fullTime").html(fullTime);
            } else {
                $(".fullTime").addClass("hidden");
            }
            if (partTime) {
                $("#partTime").html(partTime);
            } else {
                $(".partTime").addClass("hidden");
            }
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
