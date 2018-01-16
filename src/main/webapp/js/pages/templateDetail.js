var urlData = location.href.split('?')[1].split("=");
var templateId = urlData[0];
var page = "";
$(function () {

    /**
     * 编辑按钮
     */
    $("#editBtn").on("click", function () {
        window.location.href = appPath + '/pages/templatePage.html?edit=' + page + "=" + templateId;
    });

    // 初始化数据
    $.post(appPath + '/template/getTemplateDetail.do', {'templateId': templateId}, function (res) {
        if (res.code == 0) {
            if (res.data.courseType == 1) {
                $(".header-one").html("团课模版");
                page = "13_14";
            } else {
                $(".header-one").html("私教模版");
                page = "13_24";
            }
            $(".header-two").html(res.data.templateName);
            $("#templateName").html(res.data.templateName);
            $("#minutes").html(res.data.minutes + "min");
            $("#minNum").html(res.data.minNum + "人");
            $("#maxNum").html(res.data.maxNum + "人");
            $("#intro").html(res.data.intro);
            $("#notes").html(res.data.notes);
            var imgs = res.data.images.split(",");
            var imgHtml = "";
            for (var i = 0; i < imgs.length; i++) {
                imgHtml += '<img src="' + imgViewPath + imgs[i] + '" class="img-normal" style="">&nbsp;';
            }
            $("#images").html(imgHtml);
        } else {
            layer.msg(res.msg);
        }
    });

});
