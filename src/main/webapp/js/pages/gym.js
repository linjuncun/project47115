$(function () {
    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/gym/getGymList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var gymType = $("#gymType").val();
                var provinceId = $("#province").val();
                var cityId = $("#city").val();
                var districtId = $("#district").val();
                d.conditions = conditions;
                d.gymType = gymType;
                d.provinceId = provinceId;
                d.cityId = cityId;
                d.districtId = districtId;
            },
            dataSrc: function (json) {
                if (json.code == 0) {
                    json.recordsTotal = json.data.recordsTotal;
                    json.recordsFiltered = json.data.recordsFiltered;
                    json.draw = json.data.draw;
                    if (json.data.results == null) {
                        return false;
                    } else {
                        return json.data.results;
                    }
                } else {
                    layer.msg(json.msg);
                    json.recordsTotal = 0;
                    json.recordsFiltered = 0;
                    return [];
                }
            },
            error: function (data) {
                layer.msg("系统异常，请联系管理员");
            }
        },
        columns: [
            {
                title: '<input type="checkbox" class="table-checkbox"  value="1" />',
                width: "2%",
                render: function (data, type, full) {
                    return '<input type="checkbox" class="tr-checkbox" value="' + full.gymId + '"  />';
                }
            },
            {
                title: "健身房名称", data: "gymName", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/gymDetail.html?' + full.gymId + '">' + data + '</a>';
            }
            },
            {title: "店长", data: "truename"},
            {
                title: "地址", data: "locationId", render: function (data, type, full) {
                return full.province + full.city + full.district + full.address;
            }
            },
            {title: "权重", data: "weight"},
            {
                title: "操作", data: "gymId",
                "mRender": function (data, type, full) {
                    var str = "";
                    str += "<button type='button' class='editBtn btn btn-default' data-id='" + data + "'>编辑</button>&nbsp;";
                    str += "<button type='button' class='delBtn btn btn-default' data-id='" + data + "'>删除</button>";
                    return str;
                }
            }
        ],
        sort: false,// 禁止排序
        pagingType: "full_numbers",// 设置分页控件的模式
        info: true,// 页脚信息
        processing: true, // 打开数据加载时的等待效果
        serverSide: true,// 打开后台分页
        searching: false,
        lengthChange: true,// 显示每页多少条数据
        lengthMenu: [10, 20, 50, 100],
        scrollCollapse: true,
        scrollX: "100%",
        scrollXInner: "100%",
        drawCallback: function (settings) {
            clickEvent();// 列表中按钮绑定事件
        },
        language: { //汉化
            "lengthMenu": "每页显示 _MENU_ 条数据",
            "emptyTable": "没有检索到数据",
            "zeroRecords": "没有检索到数据",
            "info": "共 _TOTAL_ 条数据",
            "sInfoEmpty": "无数据",
            "processing": "正在加载数据...",
            "paginate": {
                "first": "首页",
                "previous": "上一页",
                "next": "下一页",
                "last": "尾页"
            }
        }

    });// 表格初始化完毕

    //表格多选效果
    $('#table_id tbody').on('click', 'tr', function () {
        var $this = $(this);
        var $checkBox = $this.find("input:checkbox");
        if ($this.hasClass('selected')) {
            $checkBox.prop("checked", false);
            $(".table-checkbox").prop("checked", false);
        } else {
            $checkBox.prop("checked", true);
        }
        $this.toggleClass('selected');
    });

    //全选效果
    $(".table-checkbox").on('click', function () {
        var $this = $(".table-checkbox");
        if ($this.prop('checked')) {
            $(".tr-checkbox").prop("checked", true);
            $('#table_id tr').addClass("selected");

        } else {
            $(".tr-checkbox").prop("checked", false);
            $('#table_id tr').removeClass("selected");
        }

    });

    // 初始化省市区数据
    $.post(appPath + '/gym/getGymLocationList.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var provinceMap = new Map();
            var province = '<option value="">选择省</option>';
            for (var i = 0; i < res.data.length; i++) {
                provinceMap.set(res.data[i].provinceId + '', '<option value="' + res.data[i].provinceId + '">' + res.data[i].province + '</option>');
            }
            provinceMap.forEach(function (value, key, map) {
                province += value;
            });
            $("#province").html(province);
            //联动展示市
            $("#province").on("change", function () {
                var provinceId = $("#province").val();
                var city = '<option value="">选择市</option>';
                for (var i = 0; i < res.data.length; i++) {
                    if (res.data[i].provinceId == provinceId) {
                        city += '<option value="' + res.data[i].locationId + '">' + res.data[i].city + '</option>';
                    }
                }
                $("#city").html(city);
                $("#district").html('<option value="">选择区</option>');
            })
            //联动展示区
            $("#city").on("change", function () {
                var locationId = $("#city").val();
                var district = '<option value="">选择区</option>';
                for (var i = 0; i < res.data.length; i++) {
                    if (res.data[i].locationId == locationId) {
                        for (var j = 0; j < res.data[i].subLoction.length; j++) {
                            district += '<option value="' + res.data[i].subLoction[j].locationId + '">' + res.data[i].subLoction[j].district + '</option>';
                        }
                        break;
                    }
                }
                $("#district").html(district);
            })
        } else {
            layer.msg(res.msg);
        }
    })
    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        window.location.href = appPath + '/pages/gymPage.html?add';
    });
    /**
     * 导出
     */
    $("#exportBtn").on("click", function () {
        var gymIds = "";
        $(".tr-checkbox").each(function () {
            var $this = $(this);
            if ($this.prop('checked')) {
                gymIds += $this.val() + ",";
            }
        })
        gymIds = gymIds.substring(0, gymIds.length - 1);
        if (!gymIds) {
            layer.msg("请先选择需要导出的内容");
            return;
        }
        window.location.href = appPath + "/gym/exportGym.do?gymIds=" + gymIds;
    });

});

/**
 * 列表中按钮绑定事件
 */
function clickEvent() {
    /**
     * 修改
     */
    $(".editBtn").on("click", function () {
        var id = $(this).attr("data-id");
        window.location.href = appPath + '/pages/gymPage.html?edit=' + id;
    });

    /**
     * 删除
     */
    $(".delBtn").on("click", function () {
        var id = $(this).attr("data-id");
        var msg = "是否确认删除本数据";
        layer.confirm(msg, {
            icon: 0,
            btn: ['确定', '取消'] // 按钮
        }, function () {
            var url = appPath + '/gym/updateGym.do';
            var param = {};
            param.gymId = id;
            param.status = 2;//状态 1:启用 2 :停用
            $.post(url, param, function (res) {
                if (res.code == 0) {
                    layer.closeAll();
                    layer.msg("操作成功", {icon: 1});
                    // 刷新表格数据
                    $('#table_id').DataTable().ajax.reload();
                } else {
                    layer.msg(res.msg);
                }
            });
        }, function () {
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
            if (btns.indexOf("add") == -1) {
                $("#addBtn").prop("disabled", "disabled");
            } else {
                $("#addBtn").removeAttr("disabled");
            }
            if (btns.indexOf("export") == -1) {
                $("#exportBtn").prop("disabled", "disabled");
            } else {
                $("#exportBtn").removeAttr("disabled");
            }
            if (btns.indexOf("edit") == -1) {
                $(".editBtn").prop("disabled", "disabled");
            }
            if (btns.indexOf("del") == -1) {
                $(".delBtn").prop("disabled", "disabled");
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
}
