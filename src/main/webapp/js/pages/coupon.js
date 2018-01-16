$(function () {
    //导航标签切换
    $(".header-tab .tab-button").on("click", function () {
        var $this = $(this);
        $(".header-tab .tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        var dataPage = $this.attr("data-page");
        window.location.href = appPath + '/pages/' + dataPage;
    });

    //列表标签切换
    $(".table-tab .tab-button").on("click", function () {
        //清空条件
        $("#couponId").val("");
        $("#startDate").val("");
        $("#endDate").val("");
        $("#conditions").val("");
        var $this = $(this);
        $(".table-tab .tab-button").removeClass("btn-item-checked");
        $this.addClass("btn-item-checked");
        var divId = $this.attr("data-id");
        var init = $this.attr("data-init");//是否是初始化datatables
        $(".box-body").addClass("hidden");
        $("#" + divId).removeClass("hidden");
        $(window).resize();
        if (init == 1) {
            $this.attr("data-init", "2");
            if (divId == 'use') {
                useTable();
            }
        } else {
            $('#' + divId + "_table").DataTable().ajax.reload();
        }
    });

    //搜索条件变化
    $(".conditions").on("change", function () {
        var divId = $(".table-tab .btn-item-checked").attr("data-id");
        $('#' + divId + "_table").DataTable().ajax.reload();
    });

    // 表格初始化
    $('#get_table').DataTable({
        ajax: {
            url: appPath + '/coupon/getUserCoupon.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var couponId = $("#couponId").val();
                var startDate = $.trim($("#startDate").val());
                var endDate = $.trim($("#endDate").val());
                d.conditions = conditions;
                d.couponId = couponId;
                d.startDate = startDate;
                d.endDate = endDate;
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
            {title: "优惠券类型", data: "couponName"},
            {
                title: "会员姓名", data: "name", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/userDetail.html?' + full.userId + '">' + data + '</a>';
            }
            },
            {
                title: "金额", data: "couponValue", render: function (data, type, full) {
                return "￥" + data;
            }
            },
            {
                title: "有效期", data: "indate", render: function (data, type, full) {
                return data + "天";
            }
            },
            {
                title: "渠道", data: "sendType", render: function (data, type, full) {
                if (data == 1) {
                    return "人工发放";
                } else {
                    return "系统自动";
                }
            }
            },
            {
                title: "获取时间", data: "createTime", render: function (data, type, full) {
                return formatDate(data);
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

    // 初始化优惠券下拉数据
    $.post(appPath + '/coupon/getCoupons.do', {"占位": 1}, function (res) {
        if (res.code == 0) {
            var couponId = '<option value="">优惠券类型</option>';
            for (var i = 0; i < res.data.length; i++) {
                couponId += '<option value="' + res.data[i].couponId + '">' + res.data[i].couponName + '</option>';
            }
            $("#couponId").html(couponId);

        } else {
            layer.msg(res.msg);
        }
    })
});


//优惠券使用
function useTable() {
    $('#use_table').DataTable({
        ajax: {
            url: appPath + '/order/getOrderList.do',
            data: function (d) {
                d.pageIndex = (d.start / d.length) + 1;
                d.pageSize = d.length;
                var conditions = $.trim($("#conditions").val());
                var couponId = $("#couponId").val();
                var startDate = $.trim($("#startDate").val());
                var endDate = $.trim($("#endDate").val());
                d.conditions = conditions;
                d.couponId = couponId;
                d.startDate = startDate;
                d.endDate = endDate;
                d.queryType = 2;//查询优惠券使用记录
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
                title: "会员姓名", data: "name", render: function (data, type, full) {
                return '<a href="' + appPath + '/pages/userDetail.html?' + full.userId + '">' + data + '</a>';
            }
            },
            {title: "优惠券类型", data: "couponName"},
            {
                title: "金额", data: "couponValue", render: function (data, type, full) {
                return "￥" + data;
            }
            },
            {
                title: "使用类型", data: "courseName", render: function (data, type, full) {
                if (full.courseId > 0) {
                    return "课程：" + data;
                }else{
                    return "套餐：" + full.packageName;
                }

            }
            },
            {
                title: "使用时间", data: "createTime", render: function (data, type, full) {
                return formatDate(data);
            }
            }
        ],
        sort: false,// 禁止排序
        pagingType: "full_numbers",// 设置分页控件的模式
        info: true,// 页脚信息
        processing: true, // 打开数据加载时的等待效果
        serverSide: true,// 打开后台分页
        searching: false,
        lengthChange: false,// 显示每页多少条数据
        lengthMenu: [10, 20, 50, 100],
        scrollCollapse: true,
        scrollX: "100%",
        scrollXInner: "100%",
        drawCallback: function (settings) {
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
}
