var urlData = location.href.split('?')[1].split("=");
var sysUserId = urlData[0];
var name = decodeURI(urlData[1]);
$(function () {
    $("#name").val(name);
    // 初始化功能权限复选框列表
    $.post(appPath + '/system/getSysFunctions.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var htmls = "";
            for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].type == 1) {//一级菜单
                    htmls += '<div class="layui-div-item">'
                        + '<input class="checkbox-small first" type="checkbox" data-id="' + res.data[i].functionId + '" /><span class="item-span">' + res.data[i].name + '</span><div class="function-item">';
                    for (var j = 0; j < res.data.length; j++) {
                        if (res.data[j].parentId == res.data[i].functionId) {//二级菜单
                            htmls += '<input class="checkbox-small second" type="checkbox" data-id="' + res.data[j].functionId + '"/><span class="item-span">' + res.data[j].name + '</span>';
                            var hasSub = false;//二级菜单下是否还有子级菜单
                            for (var k = 0; k < res.data.length; k++) {
                                if (res.data[k].parentId == res.data[j].functionId) {//三级菜单
                                    if (!hasSub) {
                                        htmls += '<div class="function-item sub">';
                                        hasSub = true;
                                    }
                                    htmls += '<input class="checkbox-small third" type="checkbox" data-id="' + res.data[k].functionId + '" /><span class="item-span">' + res.data[k].name + '</span>';
                                }
                            }
                            if (hasSub) {
                                htmls += '</div>';
                            }
                        }
                    }
                    htmls += '</div></div>';
                }
            }
            $("#functionList").html(htmls);

            // 绑定勾选事件
            $(".checkbox-small").on("click", function () {
                var $this = $(this);
                var isChecked = !$this.is(':checked');// 因为在click事件里已经解决了一次click,所以checked属性的值已经和触发click之前相反了
                if ($this.hasClass("first")) {// 一级菜单
                    $this.parent().find(".second").each(function () {
                        var $second = $(this);
                        if (isChecked) {
                            $second.prop("checked", false);
                        } else {
                            $second.prop("checked", true);
                        }
                        $second.parent().find(".third").each(function () {
                            var $third = $(this);
                            if (isChecked) {
                                $third.prop("checked", false);
                            } else {
                                $third.prop("checked", true);
                            }
                        });
                    });
                }
                if ($this.hasClass("second")) {// 二级菜单
                    //关联操作三级菜单
                    $this.nextAll(".sub").eq(0).find(".third").each(function () {
                        var $third = $(this);
                        if (isChecked) {
                            $third.prop("checked", false);
                        } else {
                            $third.prop("checked", true);
                        }
                    });
                    // 关联操作第一级菜单
                    var flag = false;//是否有至少一个二级菜单选中
                    var father = $this.parent().parent().find(".first");
                    $this.parent().parent().find(".second").each(function () {
                        var $self = $(this);
                        if ($self.is(':checked')) {
                            flag = true;
                        }
                    });
                    if (flag) {
                        father.prop("checked", true);
                    } else {
                        father.prop("checked", false);
                    }
                }
                if ($this.hasClass("third")) {//三级菜单
                    // 关联操作第二级菜单
                    var flag = false;//是否有至少一个二级菜单选中
                    var second = $this.parent().prevAll(".second").eq(0);
                    $this.parent().find(".third").each(function () {
                        var $self = $(this);
                        if ($self.is(':checked')) {
                            flag = true;
                        }
                    });
                    if (flag) {
                        second.prop("checked", true);
                    } else {
                        second.prop("checked", false);
                    }
                    // 关联操作第一级菜单
                    flag = false;//是否有至少一个三级菜单选中
                    var first = $this.parent().parent().parent().find(".first");
                    $this.parent().parent().find(".second").each(function () {
                        var $self = $(this);
                        if ($self.is(':checked')) {
                            flag = true;
                        }
                    });
                    if (flag) {
                        first.prop("checked", true);
                    } else {
                        first.prop("checked", false);
                    }
                }
            });
            //初始化角色权限数据
            $.post(appPath + '/user/getUserFunctions.do', {'querySysUserId': sysUserId}, function (res) {
                if (res.code == 0) {
                    // 根据已选的id让其选中
                    for (var i = 0; i < res.data.length; i++) {
                        if (res.data[i].type == 3) {
                            $("#functionList").find("input[data-id='" + res.data[i].functionId + "']").click();
                        }
                    }
                } else {
                    layer.msg(res.msg);
                }
            });

        } else {
            layer.msg(res.msg);
        }
    });

    /**
     * 保存
     */
    $("#saveBtn").on("click", function () {
        var functionIds = "";
        $("#functionList").find(".checkbox-small:checked").each(function () {
            var $this = $(this);
            functionIds += $this.attr("data-id") + ",";
        });
        if (!functionIds) {
            layer.msg('请选择权限');
            return;
        }
        functionIds = functionIds.substring(0, functionIds.length - 1);
        var url = appPath + '/user/addSysUserFunctions.do';
        var param = {};
        param.handleSysUserId = sysUserId;
        param.functionIds = functionIds;
        $.post(url, param, function (res) {
            if (res.code == 0) {
                layer.msg("操作成功", {icon: 1});
                window.setTimeout(function () {
                    window.location.href = appPath + '/pages/sysUser.html';
                }, 1000);
            } else {
                layer.msg(res.msg);
            }
        });
    });

    // 按钮权限控制
    var btns = "";// 权限按钮字符串
    var pageid = $("#sideMenu").attr("data-pageid").split("_")[0];// 页面ID
    $.post(appPath + '/user/getUserFunctions.do', {'functionId': pageid}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                btns += res.data[i].code + ",";
            }
            if (btns.indexOf("auth") == -1) {
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

