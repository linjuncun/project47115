var pageType = location.href.split('?')[1];
var functionId = "";
$(function () {
    if (pageType == 1) {
        $("#sideMenu").attr("data-pageid", "13_14");
        $("#addBtn").html("新增团课模版");
        functionId = 14;
    } else {
        $("#sideMenu").attr("data-pageid", "13_24");
        $("#addBtn").html("新增私教模版");
        functionId = 24;
    }

    //标签切换
    $(".tab-button").on("click", function () {
        var $this = $(this);
        $(".tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        var dataPage = $this.attr("data-page");
        window.location.href = appPath + '/pages/' + dataPage + '?' + pageType;
    });

    // 表格初始化
    $('#table_id').DataTable({
        ajax: {
            url: appPath + '/template/getTemplateList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                d.courseType = pageType;
                d.functionId = functionId;
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
                title: "模板名称", data: "templateName", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/templateDetail.html?' + full.templateId + '">' + data + '</a>';
            }
            },
            {
                title: "时长", data: "minutes", render: function (data, type, full) {
                return data + 'min';
            }
            },
            {
                title: "最少开课人数", data: "minNum", render: function (data, type, full) {
                return data + '人';
            }
            },
            {
                title: "上课人数上限", data: "maxNum", render: function (data, type, full) {
                return data + '人';
            }
            },
            {
                title: "操作", data: "templateId",
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

    /**
     * 新增
     */
    $("#addBtn").on("click", function () {
        var page = $("#sideMenu").attr("data-pageid");
        window.location.href = appPath + '/pages/templatePage.html?add=' + page;
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
        var page = $("#sideMenu").attr("data-pageid");
        var id = $(this).attr("data-id");
        window.location.href = appPath + '/pages/templatePage.html?edit=' + page + "=" + id;
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
            var url = appPath + '/template/updateTemplate.do';
            var param = {};
            param.functionId = functionId;
            param.templateId = id;
            param.status = 2;//状态 1：正常 2：删除
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
    var pageid = $("#sideMenu").attr("data-pageid").split("_")[1];// 页面ID
    $.post(appPath + '/user/getUserFunctions.do', {'functionId': pageid}, function (res) {
        if (res.code == 0) {
            for (var i = 0; i < res.data.length; i++) {
                btns += res.data[i].code + ",";
            }
            if (btns.indexOf("template-edit") == -1) {
                $("#addBtn").prop("disabled", "disabled");
                $(".editBtn").prop("disabled", "disabled");
                $(".delBtn").prop("disabled", "disabled");
            } else {
                $("#addBtn").removeAttr("disabled");
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
