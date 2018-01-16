var chartA;//课程预约总收入
var chartB;//会员卡销售总收入
var chartC;//会员卡销售收入
var chartD;//课程数量
var chartE;//会员转化
var chartF;//当前会员卡状态
$(function () {
    //Echarts初始化
    chartA = echarts.init(document.getElementById('courseTotal'));
    chartB = echarts.init(document.getElementById('cardTotal'));
    chartC = echarts.init(document.getElementById('card'));
    chartD = echarts.init(document.getElementById('course'));
    chartE = echarts.init(document.getElementById('user'));
    chartF = echarts.init(document.getElementById('userStatus'));
    queryAB();
    queryC();
    queryD();
    queryE();
    queryF();
    //条件查询
    $("#totalSelect").on("change", function () {
        queryAB();
    });
    $("#cardSelect").on("change", function () {
        queryC();
    });
    $("#courseSelect").on("change", function () {
        queryD();
    });
    $("#userSelect").on("change", function () {
        queryE();
    });
    // 课程总数
    $.post(appPath + '/system/getCourseTotal.do', {'占位': 1}, function (res) {
        if (res.code == 0) {
            $("#courseNum").html(res.data.courseNum);
            $("#orderNum").html(res.data.orderNum);
        } else {
            layer.msg(res.msg);
        }
    });
});

function queryAB() {
    var days = $("#totalSelect").val();
    var tempDate = new Date();
    var endDate = formatDate(tempDate.getTime(), 1);
    tempDate.setTime(tempDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(days));
    var startDate = formatDate(tempDate.getTime(), 1);
    $.post(appPath + '/system/getTotalView.do', {
        "startDate": startDate,
        "endDate": endDate
    }, function (res) {
        if (res.code == 0 ) {
            var courseAmount = 0;
            var cardAmount = 0;
            if(res.data){
                courseAmount = res.data.courseAmount;
                cardAmount = res.data.cardAmount;
                if(!courseAmount){
                    courseAmount = 0;
                }
                if(!cardAmount){
                    cardAmount = 0;
                }
            }
            $("#total").html(parseInt(courseAmount) + parseInt(cardAmount));
            $("#courseAmount").html(courseAmount);
            $("#cardAmount").html(cardAmount);
            // 指定图表的配置项和数据
            var option = {
                legend: {},
                series: [
                    {
                        name: '收入',
                        type: 'pie',
                        radius: ['50%', '90%'],
                        hoverAnimation: false,
                        label: {
                            normal: {
                                show: false,
                                position: 'center',
                                formatter: "{d}%"
                            },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '13',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false
                            }
                        },
                        color: ['#62CEE4', '#E6E6E6'],
                        data: [
                            {value: courseAmount},
                            {value: cardAmount}
                        ]
                    }
                ]
            };
            chartA.setOption(option);
            var optionB = {
                legend: {},
                series: [
                    {
                        name: '收入',
                        type: 'pie',
                        radius: ['50%', '90%'],
                        hoverAnimation: false,
                        label: {
                            normal: {
                                show: false,
                                position: 'center',
                                formatter: "{d}%"
                            },
                            emphasis: {
                                show: true,
                                textStyle: {
                                    fontSize: '13',
                                    fontWeight: 'bold'
                                }
                            }
                        },
                        labelLine: {
                            normal: {
                                show: false
                            }
                        },
                        color: ['#5467D0', '#E6E6E6'],
                        data: [
                            {value: cardAmount},
                            {value: courseAmount}
                        ]
                    }
                ]
            };
            chartB.setOption(optionB);
        } else {
            layer.msg(res.msg);
        }
    })
}

function queryC() {
    var days = $("#cardSelect").val();
    var tempDate = new Date();
    var endDate = formatDate(tempDate.getTime(), 1);
    tempDate.setTime(tempDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(days));
    var startDate = formatDate(tempDate.getTime(), 1);
    $.post(appPath + '/system/getCardView.do', {
        "startDate": startDate,
        "endDate": endDate
    }, function (res) {
        if (res.code == 0) {
            var amountA = 0;//储值卡
            var amountB = 0;//次卡
            for (var i = 0; i < res.data.length; i++) {
                if (res.data[i].cardType == 1) {
                    amountA = res.data[i].amount;
                } else if (res.data[i].cardType == 2) {
                    amountB = res.data[i].amount;
                }
            }
            var optionC = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{b} : {c} ({d}%)"
                },
                legend: {
                    top: 'bottom',
                    data: ['储值卡', '次卡']
                },
                series: [
                    {
                        name: '会员卡销售收入',
                        type: 'pie',
                        radius: ['30%', '50%'],
                        color: ['#5467D0', '#F2B048'],
                        avoidLabelOverlap: false,
                        data: [
                            {value: amountA, name: '储值卡'},
                            {value: amountB, name: '次卡'}
                        ],
                        label: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: '16',
                                    fontWeight: 'bold'
                                },
                                formatter: "{c}元"
                            }
                        },
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
            };
            chartC.setOption(optionC);
        } else {
            layer.msg(res.msg);
        }
    })
}


function queryD() {
    var days = $("#courseSelect").val();
    var tempDate = new Date();
    var endDate = formatDate(tempDate.getTime(), 1);
    tempDate.setTime(tempDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(days));
    var startDate = formatDate(tempDate.getTime(), 1);
    $.post(appPath + '/system/getCourseView.do', {
        "startDate": startDate,
        "endDate": endDate
    }, function (res) {
        if (res.code == 0) {
            var courseNumA = 0;//团课节数
            var orderNumA = 0;//团课预约人数
            var courseNumB = 0;//私教节数
            var orderNumB = 0;//私教预约人数
            for (var i = 0; i < res.data.courseNum.length; i++) {
                if (res.data.courseNum[i].courseType == 1) {
                    courseNumA = res.data.courseNum[i].courseNum;
                } else if (res.data.courseNum[i].courseType == 2) {
                    courseNumB = res.data.courseNum[i].courseNum;
                }
            }
            for (var i = 0; i < res.data.courseOrder.length; i++) {
                if (res.data.courseOrder[i].courseType == 1) {
                    orderNumA = res.data.courseOrder[i].orderNum;
                } else if (res.data.courseOrder[i].courseType == 2) {
                    orderNumB = res.data.courseOrder[i].orderNum;
                }
            }
            var optionD = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    }
                },
                legend: {
                    left: "right",
                    data: ['团课', '私教']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: {
                    type: 'value',
                    boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    data: ['课程节数', '服务次数']
                },
                color: ['#66CB94', '#878BB6'],
                series: [
                    {
                        name: '团课',
                        type: 'bar',
                        data: [courseNumA, orderNumA]
                    },
                    {
                        name: '私教',
                        type: 'bar',
                        data: [courseNumB, orderNumB]
                    }
                ]
            };
            chartD.setOption(optionD);
        } else {
            layer.msg(res.msg);
        }
    })
}

function queryE() {
    var days = $("#userSelect").val();
    var tempDate = new Date();
    var endDate = formatDate(tempDate.getTime(), 1);
    tempDate.setTime(tempDate.getTime() - 1000 * 60 * 60 * 24 * parseInt(days));
    var startDate = formatDate(tempDate.getTime(), 1);
    $.post(appPath + '/system/getUserCardView.do', {
        "startDate": startDate,
        "endDate": endDate
    }, function (res) {
        if (res.code == 0) {
            var userNum = res.data.userNum;
            var cardNum = res.data.cardNum;
            var optionE = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{b}: {c} ({d}%)"
                },
                legend: {
                    top: 'bottom',
                    data: ['新注册', '会员']
                },
                series: [
                    {
                        name: '会员转化',
                        type: 'pie',
                        radius: ['30%', '50%'],
                        avoidLabelOverlap: false,
                        color: ['#62CEE4', '#F2B048'],
                        label: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: '16',
                                    fontWeight: 'bold'
                                },
                                formatter: "{c}人"
                            }
                        },
                        labelLine: {
                            normal: {
                                show: true
                            }
                        },
                        data: [
                            {value: userNum, name: '新注册'},
                            {value: cardNum, name: '会员'}
                        ]
                    }
                ]
            };
            chartE.setOption(optionE);
        } else {
            layer.msg(res.msg);
        }
    })
}

function queryF() {
    $.post(appPath + '/system/getCardStatusView.do', { "占位": 1 }, function (res) {
        if (res.code == 0) {
            var cardNum = res.data.cardNum;
            var stopNum = res.data.stopNum;
            var datedNum = res.data.datedNum;
            var normalNum = cardNum - datedNum;
            var optionF = {
                tooltip: {
                    trigger: 'item',
                    formatter: "{b}: {c} ({d}%)"
                },
                legend: {
                    top: 'bottom',
                    data: ['正常', '已过期', '已封禁']
                },
                series: [
                    {
                        name: '状态',
                        type: 'pie',
                        radius: '50%',
                        avoidLabelOverlap: false,
                        color: ['#66CB94', '#62CEE4', '#F2B048'],
                        label: {
                            normal: {
                                show: true,
                                textStyle: {
                                    fontSize: '16',
                                    fontWeight: 'bold'
                                },
                                formatter: "{c}张"
                            }
                        },
                        labelLine: {
                            normal: {
                                show: true
                            }
                        },
                        data: [
                            {value: normalNum, name: '正常'},
                            {value: stopNum, name: '已过期'},
                            {value: datedNum, name: '已封禁'}
                        ]
                    }
                ]
            };
            chartF.setOption(optionF);
        } else {
            layer.msg(res.msg);
        }
    })
}