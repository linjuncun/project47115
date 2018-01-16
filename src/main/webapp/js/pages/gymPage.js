var urlData = location.href.split('?')[1].split("=");
var pageType = urlData[0];
var gymId = urlData[1];
var requestNum = 0;//页面请求数量，用于在省市区等数据初始化完成后再初始化健身房数据
$(function () {
    if (pageType == "edit") {
        $("title").html("编辑健身房");
        $(".pageType").html("编辑健身房");
    } else {
        $("title").html("添加健身房");
        $(".pageType").html("添加健身房");
    }
    // 初始化省份数据
    $.post(appPath + '/system/getLocation.do', {'queryType': 1}, function (res) {
        if (res.code == 0) {
            var str = '<option value="">选择省份</option>';
            for (var i = 0; i < res.data.length; i++) {
                str += '<option value="' + res.data[i].locationId + '">' + res.data[i].province + '</option>';
            }
            $("#province").html(str);
            requestNum++;
        } else {
            layer.msg(res.msg);
        }
    });
    //省、市数据联动
    $("#province").on("change", function () {
        var provinceId = $("#province").val();
        var provinceName = $('#province option:selected').text();
        //直辖市单独处理
        if (provinceId == 2 || provinceId == 19 || provinceId == 857 || provinceId == 2459) {
            $("#city").html('<option value="">选择城市</option><option value="' + provinceId + '">' + provinceName + '</option>');
        } else {
            $.post(appPath + '/system/getLocation.do', {'provinceId': provinceId}, function (res) {
                if (res.code == 0) {
                    var str = '<option value="">选择城市</option>';
                    for (var i = 0; i < res.data.length; i++) {
                        str += '<option value="' + res.data[i].locationId + '">' + res.data[i].city + '</option>';
                    }
                    $("#city").html(str);
                } else {
                    layer.msg(res.msg);
                }
            });
        }
        $("#district").html('<option value="">选择区域</option>');
    })
    //市、区数据联动
    $("#city").on("change", function () {
        var cityId = $("#city").val();
        $.post(appPath + '/system/getLocation.do', {'cityId': cityId}, function (res) {
            if (res.code == 0) {
                var str = '<option value="">选择区域</option>';
                for (var i = 0; i < res.data.length; i++) {
                    str += '<option value="' + res.data[i].locationId + '">' + res.data[i].district + '</option>';
                }
                $("#district").html(str);
            } else {
                layer.msg(res.msg);
            }
        });
    })

    // 初始化店长数据
    $.post(appPath + '/user/getSysUsers.do', {'post请求占位': 1}, function (res) {
        if (res.code == 0) {
            var str = '<option value="">选择员工</option>';
            for (var i = 0; i < res.data.length; i++) {
                str += '<option value="' + res.data[i].sysUserId + '">' + res.data[i].truename + '</option>';
            }
            $("#sysUserId").html(str);
            requestNum++;
        } else {
            layer.msg(res.msg);
        }
    });

    // 初始化教练数据
    $.post(appPath + '/user/getUsers.do', {"userType": 2, "status": 1}, function (res) {
        if (res.code == 0) {
            var fullTime = '<option value="">选择员工</option>';//全职教练
            var partTime = '<option value="">选择员工</option>';//兼职教练
            for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].coachType == 1) {
                    fullTime += '<option value="' + res.data[i].userId + '">' + res.data[i].name + '</option>';
                } else {
                    partTime += '<option value="' + res.data[i].userId + '">' + res.data[i].name + '</option>';
                }
            }
            $("#fullTime").html(fullTime);
            $("#partTime").html(partTime);
            requestNum++;
        } else {
            layer.msg(res.msg);
        }
    });

    //选择教练
    $(".coachList").on("change", function () {
        var $this = $(this);
        var userId = $this.val();
        var userName = $this.find("option:selected").text();
        if (userId && $this.parent().find(".check-item[data-id='" + userId + "']").length == 0) {
            $this.parent().append('<span class="check-item" data-id="' + userId + '">' + userName + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>');
        }
    })

    //如果是编辑，初始化健身房数据
    if (pageType == "edit") {
        var timer = setInterval(function () {
            if (requestNum >= 3) {//当其他数据请求成功后再执行
                $.post(appPath + '/gym/getGymDetail.do', {'gymId': gymId}, function (res) {
                    if (res.code == 0) {
                        $("#gymName").val(res.data.gymName);
                        if (res.data.provinceId == 1) {//如果是直辖市
                            $("#province").val(res.data.cityId);
                        } else {
                            $("#province").val(res.data.provinceId);
                        }
                        $("#province").change();
                        var cityTimer = setInterval(function () {
                            if ($("#city").children().length > 1) {
                                $("#city").val(res.data.cityId);
                                $("#city").change();
                                var districtTimer = setInterval(function () {
                                    if ($("#district").children().length > 1) {
                                        $("#district").val(res.data.districtId);
                                        clearInterval(districtTimer);//清除定时器
                                    }
                                }, 10);
                                clearInterval(cityTimer);//清除定时器
                            }
                        }, 10);
                        $("#address").val(res.data.address);
                        $("#weight").val(res.data.weight);
                        $("#sysUserId").val(res.data.sysUserId);
                        $("#gymType").val(res.data.gymType);
                        $("#intro").val(res.data.intro);
                        var coachList = res.data.coachList;
                        var fullTime = '';//全职教练
                        var partTime = '';//兼职教练
                        for (var i = 0; i < coachList.length; i++) {
                            if (coachList[i].coachType == 1) {
                                fullTime += '<span class="check-item" data-id="' + coachList[i].userId + '">' + coachList[i].name + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>&nbsp;';
                            } else {
                                partTime += '<span class="check-item" data-id="' + coachList[i].userId + '">' + coachList[i].name + ' <a href="javascript:void(0);" onclick="delSelf(this)">X</a></span>&nbsp;';
                            }
                        }
                        $("#fullTime").parent().append(fullTime);
                        $("#partTime").parent().append(partTime);
                        //健身房图片
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
                clearInterval(timer);//清除定时器
            }
        }, 10);
    }

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var gymName = $.trim($("#gymName").val());
        var locationId = $("#district").val();
        var address = $.trim($("#address").val());
        var weight = $.trim($("#weight").val());
        var sysUserId = $.trim($("#sysUserId").val());
        var gymType = $.trim($("#gymType").val());
        var intro = $.trim($("#intro").val());
        var coachIds = "";//教练id
        $(".check-item").each(function () {
            coachIds += $(this).attr("data-id") + ",";
        })
        if (coachIds) {
            coachIds = coachIds.substring(0, coachIds.length - 1);
        }
        if (!gymName) {
            layer.msg('请填写名称');
            $("#gymName").focus();
            return;
        }
        if (!locationId) {
            layer.msg('请选择区域');
            $("#district").focus();
            return;
        }
        if (!address) {
            layer.msg('请填写完整地址');
            $("#address").focus();
            return;
        }
        if (!weight) {
            layer.msg('请填写权重');
            $("#weight").focus();
            return;
        }
        if (!sysUserId) {
            layer.msg('请选择店长');
            $("#sysUserId").focus();
            return;
        }
        if (!gymType) {
            layer.msg('请选择类型');
            $("#gymType").focus();
            return;
        }
        if (!intro) {
            layer.msg('请填写文字描述');
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
        if (!intReg.test(weight)) {
            layer.msg('请输入正确的数字');
            $("#weight").focus();
            return;
        }
        var url = "";
        var param = {};
        param.gymName = gymName;
        param.locationId = locationId;
        param.address = address;
        param.weight = weight;
        param.sysUserId = sysUserId;
        param.gymType = gymType;
        param.intro = intro;
        param.coachIds = coachIds;
        param.images = images;
        if (pageType == "add") {
            url = appPath + '/gym/addGym.do';
        } else {
            url = appPath + '/gym/updateGym.do';
            param.gymId = gymId;
        }
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.closeAll();
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/gym.html';
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
    $(item).parent().parent().find(".coachList").val("");
    $(item).parent().remove();
}

