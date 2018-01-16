var pageWidth = document.body.clientWidth;
var pageHeight = document.body.clientHeight;
var tipsColor = "#78BA32";//小提示颜色
var loginUserId = "";//当前登录用户id
var imgFlag = 0;//上传图片标识 0：未上传图片  1：上传中  2：上传成功
var intReg = new RegExp("^[0-9]*$");//验证整数
var phoneReg = new RegExp("^1(3|4|5|7|8)\\d{9}$");//验证手机号
/**
 * 获取项目根目录全路径
 */
var imgViewPath = "";//图片访问路径
var appPath = getRootPath();
function getRootPath() {
    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0, pos);
    imgViewPath = localhostPath;
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    if (/hyperfit/.test(projectName)) {
        return (localhostPath + projectName);
    } else {
        return (localhostPath);
    }
}

//动态生成权限菜单
$(function () {
    // 调整页面高度
    $("body").css("height", (pageHeight - 62) + "px");

    var degree = 0;
    $(".menu-header").on("click",function(){
        degree = (degree+90)%360;
        $(".menu-icon").css("transform","rotate("+degree+"deg)");
        if($("#sidebar").hasClass("mini-menu")){
            $("#sidebar").removeClass("mini-menu");
            $("#main-content").removeClass("margin-left-30");
        }else{
            $("#sidebar").addClass("mini-menu");
            $("#main-content").addClass("margin-left-30");
        }
        $(window).resize();
    });
    $.post(appPath + '/user/getUserFunctions.do', {"占位": 1}, function (res) {
        var html = "菜单读取失败或没有权限";
        if (res.code == 0) {
            html = "";
            var pageid = $("#sideMenu").attr("data-pageid").split("_");
            var firstMenu = pageid[0];//一级菜单的functionId
            var secondMenu = pageid[1];//二级菜单的functionId
            var firsts = [];//一级菜单数组
            for (var i = 0; i < res.data.length; i++) {
                var str = "";
                if (res.data[i].type == 1) {//一级菜单
                    if (res.data[i].functionId == firstMenu) {
                        str += '<li class="has-sub active open">';
                    } else {
                        str += '<li class="has-sub">';
                    }
                    if (res.data[i].url) {//如果没有子级菜单
                        str += '<a href="' + appPath + res.data[i].url + '" class=""><span class="menu-text">' + res.data[i].name + '</span></a><ul class="sub">';
                    } else {
                        str += '<a href="javascript:;" class=""><span class="menu-text">' + res.data[i].name + '</span><span class="arrow"></span></a><ul class="sub">';
                        var seconds = [];//二级菜单数组
                        for (var j = 0; j < res.data.length; j++) {
                            var strs = "";
                            if (res.data[j].parentId == res.data[i].functionId) {//相应的二级菜单
                                if (res.data[j].functionId == secondMenu) {
                                    strs += '<li class="current">';
                                } else {
                                    strs += '<li>';
                                }
                                strs += '<a href="' + appPath + res.data[j].url + '" class=""><span class="sub-menu-text">' + res.data[j].name + '</span></a></li>';
                                seconds[res.data[j].orderIndex - 1] = strs;
                            }
                        }
                        for (var k = 0; k < seconds.length; k++) {
                            if (typeof(seconds[k]) != "undefined") {
                                str += seconds[k];
                            }
                        }
                    }
                    str += '</ul></li>';
                    firsts[res.data[i].orderIndex - 1] = str;
                }
            }
            for (var i = 0; i < firsts.length; i++) {
                if (typeof(firsts[i]) != "undefined") {
                    html += firsts[i];
                }
            }
        } else if (res.code == -1) {
            layer.confirm('登录已失效，请重新登录', {
                    icon: 0,
                    btn: ['确认'] // 按钮
                }, function () {
                    window.location.href = appPath + '/login.html';
                }
            );
        } else {
            layer.msg(res.msg);
        }
        $("#sideMenu").html(html);

        //菜单效果
        jQuery('.sidebar-menu .has-sub > a').click(function () {
            var $this = $(this);
            var isOpen = $this.parent().hasClass("open");
            if (isOpen) {
                $this.parent().removeClass("open")
                $this.parent().removeClass("active")
                $('.arrow', $this).removeClass("open");
            } else {
                var allOpen = $('.has-sub.open', $('.sidebar-menu'));
                allOpen.removeClass("open");
                allOpen.removeClass("active");
                $('.arrow', allOpen).removeClass("open");
                $('.sub', allOpen).slideUp(200);

                $this.parent().addClass("open")
                $this.parent().addClass("active")
                $('.arrow', $this).addClass("open");
            }
            handleSidebarAndContentHeight();
        });

        var handleSidebarAndContentHeight = function () {
            var content = $('#content');
            var sidebar = $('#sidebar');
            var body = $('body');
            var height;

            if (body.hasClass('sidebar-fixed')) {
                height = $(window).height() - $('#header').height() + 1;
            } else {
                height = sidebar.height() + 20;
            }
            if (height >= content.height()) {
                content.attr('style', 'min-height:' + height + 'px !important');
            }
        };
        //菜单效果--end
    });

    //下拉菜单
    $(".navbar-nav").mouseover(function () {
        $(".dropdown").addClass("open");
        var $i = $(".pull-down-icon");
        $i.removeClass("fa-angle-down");
        $i.addClass("fa-angle-up");
    }).mouseout(function () {
        $(".dropdown").removeClass("open");
        var $i = $(".pull-down-icon");
        $i.removeClass("fa-angle-up");
        $i.addClass("fa-angle-down");
    });

    //修改密码页面
    $("#resetPwd").on("click", function () {
        window.location.href = appPath + '/pages/resetPwd.html';
    });

    //退出登录
    $("#logoutBtn").on("click", function () {
        $.post(appPath + '/logout.do', {"占位": 1}, function (res) {
            if (res.code == 0) {
                window.location.href = appPath + '/login.html';
            } else {
                layer.msg(res.msg);
            }
        });
    });

    /**
     * 条件搜索
     */
    $(".conditions").on("change", function () {
        $('#table_id').DataTable().ajax.reload();
    });

    /**
     * 清空查询条件
     */
    $("#clearBtn").on("click", function () {
        $(".conditions").val("");
        $('#table_id').DataTable().ajax.reload();
    });

    /**
     * 返回按钮
     */
    $("#backBtn").on("click", function () {
        window.location.href = document.referrer;//返回上一页并刷新
    })

    /**
     * 整数输入验证
     */
    $("body").on("change", ".int-reg", function () {
        var content = $.trim($(this).val());
        if (content && !intReg.test(content)) {
            layer.msg("请输入正确的数字");
        }
    })
    /**
     * 手机号验证
     */
    $("body").on("change", ".phone-reg", function () {
        var content = $.trim($(this).val());
        if (content && !phoneReg.test(content)) {
            layer.msg("请输入正确的手机号");
        }
    })

    /**
     * 小时输入验证
     */
    $("body").on("change", ".hour-reg", function () {
        var content = $.trim($(this).val());
        if (content && !intReg.test(content)) {
            layer.msg("请输入正确的数字");
        } else if (parseInt(content) > 23) {
            layer.msg("请输入正确的时间");
        }
    })
    /**
     * 分钟输入验证
     */
    $("body").on("change", ".minute-reg", function () {
        var content = $.trim($(this).val());
        if (content && !intReg.test(content)) {
            layer.msg("请输入正确的数字");
        } else if (parseInt(content) > 59) {
            layer.msg("请输入正确的时间");
        }

    })

});
/**
 * 时间格式化(默认)
 * @param date
 * @param type 格式化类型  1：yyyy-MM-dd  2：HH:mm  3：yyyy-MM-dd HH:mm
 * @returns {String}
 */
function formatDate(date, type) {
    if (date == null) {
        return "";
    }
    var time = new Date(date);
    var year = time.getFullYear();
    var month = time.getMonth() + 1;
    if (month < 10) {
        month = "0" + month;
    }
    var date = time.getDate();
    if (date < 10) {
        date = "0" + date;
    }
    var hours = time.getHours();
    if (hours < 10) {
        hours = "0" + hours;
    }
    var min = time.getMinutes();
    if (min < 10) {
        min = "0" + min;
    }
    var sec = time.getSeconds();
    if (sec < 10) {
        sec = "0" + sec;
    }
    var d = '';
    if (type == 1) {
        d = year + "-" + month + "-" + date;
    } else if (type == 2) {
        d = hours + ":" + min;
    } else if (type == 3) {
        d = year + "-" + month + "-" + date + " " + hours + ":" + min;
    } else {
        d = year + "-" + month + "-" + date + " " + hours + ":" + min + ":" + sec;
    }
    return d;
}

//主页面预览图片
function fnPrevImg(ele) {
    var src = $(ele).attr('src');
    $('#img_preview').show();
    $('#img_preview img').attr('src', src);
}
//关闭预览
function fnClosePreview() {
    $('#img_preview').hide();
}
//移除图片
function delImg(item) {
    $(item).parent().remove();
}


/**
 * 图片上传插件初始化
 */
function createUploader(filePicker, imgShow, imgShowDiv, imgUrlValue, imgWidth, imgHeight) {
    var uploader = WebUploader.create({
        // 选完文件后，是否自动上传。
        auto: true,
        // swf文件路径
        swf: appPath + '/statics/plugins/webuploader-0.1.5/Uploader.swf',
        // 文件接收服务端。
        server: appPath + '/fileUpload.do',
        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '' + filePicker,
        //最大上传2M
        fileSingleSizeLimit: 2 * 1024 * 1024,
        // 只允许选择图片文件。
        accept: {
            title: 'Images',
            extensions: 'jpg,jpeg,bmp,png',
            mimeTypes: 'image/jpg,image/jpeg,image/bmp,image/png'
        }
    });
    // 当有文件添加进来的时候
    uploader.on('fileQueued', function (file) {
        //标记用户选择了上传图片
        imgFlag = 1;
        // 创建缩略图
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        var $img = $("" + imgShow);
        uploader.makeThumb(file, function (error, src) {
            if (error) {
                $img.replaceWith('<span>不能预览</span>');
                return;
            }
            $img.prop('src', src);
        }, imgWidth, imgHeight);
        $("" + imgShowDiv).removeClass("hidden");
    });

    // 文件上传过程中创建进度条实时显示。
    uploader.on('uploadProgress', function (file, percentage) {
        var $li = $("" + imgShowDiv),
            $percent = $li.find('.progress .progress-bar');

        // 避免重复创建
        if (!$percent.length) {
            $percent = $('<div class="progress progress-striped active" style="width: 120px;margin:5px 0 0 100px;">' +
                '<div class="progress-bar" role="progressbar" style="width: 100%">' +
                '</div>' +
                '</div>').appendTo($li).find('.progress-bar');
        }

        $li.find('p.state').text('上传中');

        $percent.css('width', percentage * 100 + '%');
    });

    uploader.on('uploadSuccess', function (file, response) {
        if (response.code == 0) {
            $("" + imgUrlValue).val(response.data);
        }
    });
    // 文件上传失败，显示上传出错。
    uploader.on('uploadError', function (file) {
        var $li = $("" + imgShowDiv),
            $error = $li.find('div.error');

        // 避免重复创建
        if (!$error.length) {
            $error = $('<div class="error"></div>').appendTo($li);
        }

        $error.text('上传失败');
    });
    //上传错误
    uploader.on("error", function (type) {
        if (type == "Q_TYPE_DENIED") {
            layer.msg("请上传jpg、jpeg、bmp、png格式文件", {icon: 0});
        } else if (type == "F_EXCEED_SIZE") {
            layer.msg("图片最大不能超过2M", {icon: 0});
        }
    });

    // 完成上传完了，成功或者失败，先删除进度条。
    uploader.on('uploadComplete', function (file) {
        $("" + imgShowDiv).find('.progress').remove();
        imgFlag = 2;
    });
    return uploader;
}
