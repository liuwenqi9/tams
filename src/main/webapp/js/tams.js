/**
 * Created by DELL on 2016-10-16.
 */

function doEditorPreSubmit(editorID,transferID) {

    // alert(UE.getEditor(editorID).getPlainTxt());
    jQuery('#' + transferID).val(UE.getEditor(editorID).getPlainTxt());
    return true;
}



function initNavDialog() {
    jQuery(".navigationDialog .nav>ul>:first-child>a").tab('show');
    //去掉dialog本身的header
    jQuery(".navigationDialog>.modal-content>.modal-header").remove();

    jQuery("#navDialog").removeClass("fade");
    //添加导航栏点击切换事件
    jQuery(".navigationDialog .nav a").click(function(e){
        e.preventDefault();
        jQuery(this).tab('show');
    });


    jQuery(".navigationDialog .close").click(function(a) {
        a.stopPropagation();
        jQuery(".navigationDialog .nav .active").removeClass("active");
        jQuery(".navigationDialog .tab-content .active").removeClass("active");
    })
}



//var jsonObj = {"header":["编辑","待负责人审核","待学院审核","待学校审核","工作"],"data":[[{"checked":false,"disabled":false},{"checked":true,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false}],[{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":true,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false}],[{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":true,"disabled":false},{"checked":false,"disabled":false}],[{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":true,"disabled":false}],[{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false},{"checked":false,"disabled":false}]]}

function drawStatusTransTable(boxid,tableJson){

    var jsonObj = eval('('+tableJson+')');
    var tbl = document.createElement("table");
    var tbody = document.createElement("tbody");
    tbl.className+="table table-striped table-bordered table-hover ";

    var thead = document.createElement("tr");

    var topleft = document.createElement("th");
    topleft.style.width = '130px';
    topleft.innerHTML = "<div class=\"out\"> <b>当前状态</b>  <em>新状态</em> </div>";

    thead.appendChild(topleft);

    for(var i=0;i<jsonObj.header.length;i++){

        var header = document.createElement("th");
        header.innerHTML = jsonObj.header[i];
        thead.appendChild(header);

    }
    tbody.appendChild(thead);

    for(var i=0;i<jsonObj.header.length;i++){

        var row = document.createElement("tr");
        var header = document.createElement("th");
        header.innerHTML = jsonObj.header[i];
        row.appendChild(header);

        for(var j=0;j<jsonObj.header.length;j++){

            var td = document.createElement("td");
            var cb = document.createElement("input");
            cb.type="checkbox";

            cb.checked = jsonObj.data[i][j].checked;
            cb.disabled = jsonObj.data[i][j].disabled;
            td.appendChild(cb);

            row.appendChild(td);

        }
        tbody.appendChild(row);
    }
    tbl.appendChild(tbody);
    var box = document.getElementById(boxid);
    box.appendChild(tbl);
}

function save() {
    var tableTransObj = {
        "header":[],
        "data":[]
    };
    var tbl = document.getElementById('status').getElementsByTagName('tbody')[0];
    var rows = tbl.childNodes;
    var head = rows[0].childNodes;
    for(var i=1;i<head.length;i++)
    {
        tableTransObj.header[i-1] = head[i].innerHTML;
    }

    for(var i=1;i<head.length;i++)
    {
        var row_data =[];
        var row = rows[i].childNodes;
        for(var j=1;j<head.length;j++)
        {
            row_data[j-1]={
                "checked":null,
                "disabled":null
            }
            row_data[j-1].checked = row[j].childNodes[0].checked;
            row_data[j-1].disabled = row[j].childNodes[0].disabled;
        }
        tableTransObj.data[i-1]=row_data;
    }
    document.getElementById('hidden-statusTrans').getElementsByTagName('textarea')[0].innerHTML = JSON.stringify(tableTransObj);
   jQuery("#hidden-save").click();

}


/**
 * 要求传入chart容器id，表title，中转站id
 * 指定隐藏input中转站的dataTransferid，从该中转站提取val作为data输入
 * data格式为：[['高数',1000],['线代',5000]]
 * 或格式2：[{"name":"高数","y":10000},{"name":"线代","y":5000}]
 * @param chartId
 */
function getPieChart(chartId,title,data) {
    data = JSON.parse(data);

    // 尝试过将下面这段setOptions代码提取为initHigicharts()但是没有效果
    Highcharts.setOptions({
        lang: {
            printChart:"打印图表",
            downloadJPEG: "下载JPEG 图片" ,
            downloadPDF: "下载PDF文档"  ,
            downloadPNG: "下载PNG 图片"  ,
            downloadSVG: "下载SVG 矢量图" ,
            exportButtonTitle: "导出图片"
        }
    });

    jQuery('#' + chartId).highcharts({
        credits:{
          enabled:false     // 去除highcharts的水印
        },
        title: {
            text: title
        },
        tooltip: {
            // 除了此项占比，还需要加一个此项具体数值
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true, // 一块饼的提取
                cursor: 'pointer', // 鼠标指针能否点击
                dataLabels: {
                    enabled: false
                },
                showInLegend: true
            }
        },
        series: [{
            type: 'pie',
            name: ' ',
            data: data
        }]
    });
}

/**
 * barChart输入的data格式为(待定)
 * @param chartId
 * @param title
 * @param dataTransferId
 */
function getBarChart(chartId,title,data) {
    data = eval(data);

    // 尝试过将下面这段setOptions代码提取为initHigicharts()但是没有效果
    Highcharts.setOptions({
        lang: {
            printChart:"打印图表",
            downloadJPEG: "下载JPEG 图片" ,
            downloadPDF: "下载PDF文档"  ,
            downloadPNG: "下载PNG 图片"  ,
            downloadSVG: "下载SVG 矢量图" ,
            exportButtonTitle: "导出图片"
        }
    });

    jQuery('#' + chartId).highcharts({
        credits:{
            enabled:false   // 去除highcharts的水印
        },
        chart: {
            type: 'column' // 竖柱图
        },
        title: {
            text: title
        },
        xAxis: {
            // objlist.name
            categories: [
                '高等数学',
                '概率论',
                '应用数学',
                '离散数学',
                '统计学',
                '计算数学',
                '运筹学与控制论',
                '数学分析']
        },
        yAxis: [
            {
                allowDecimals: false, // 坐标轴刻度不为小数
                title: {text: '经费'},
                labels: {
                    format: '{y} 元'
                }
            },
            {
                title: {text: '助教优秀率'},
                opposite: true,
                labels: {
                    format: '{y} %'
                }
            }
        ],
        series: [{
            name: '经费',
            color: '#ff4d4d',
            // objlist.y
            data: [1200, 1000, 600, 900, 800, 500, 500, 900],
            tooltip: {ySuffix: '元'},
            pointPadding: 0.15,
            pointPlacement: -0.03
        }, {
            name: '助教优秀率',
            color: '#66a3ff',
            data: [60, 90, 85, 70, 80, 85, 80, 75],
            tooltip: {ySuffix: '%'},
            yAxis: 1, // 双y轴的关键
            pointPadding: 0.15,
            pointPlacement: 0.03
        }],
        tooltip: {shared: true}
    });

}

/**
 * 该函数可统一地将表格外的过滤器移动到表格内，隐藏搜索按钮，
 * 为所有过滤器添加按下搜索按钮事件（select添加onchange，input添加keydown）
 *
 * @param searchbox ----xml中放置所有过滤器的外层框，其中包括搜索按钮，过滤器顺序与表格中一致
 * 若有的列不需要过滤器，则在该位置加入一个messageField或其他控件，
 * 并添加hidden-field的样式类来占位
 *
 * @param tablebox  ----xml中表格bean的id
 */
function refreshTableFilter(searchbox,tablebox) {

    //得到所有过滤器
    var searchFields = jQuery(searchbox).children("div");

    //得到table元素
    var table = jQuery(jQuery(tablebox).find("table")[0]);

    //表头
    var thead = jQuery(table.find('thead')[0]);
    //过滤器所在的表头
    var filter = jQuery("<thead class='search-filter'></thead>");

    var tr = jQuery("<tr></tr>");
    //得到searchbox中的搜索按钮，以便添加事件
    var searchButton =jQuery(searchbox).children("button")[0];

    //遍历过滤器，移入表格中
    for (var i = 0;i < searchFields.size();i++)
    {
        var th = jQuery("<th></th>");
        th.append(searchFields[i]);
        tr.append(th);

        //为输入框添加事件
        if (jQuery(searchFields[i]).children()[0].tagName=='INPUT'){
            jQuery(searchFields[i]).on(
                {keydown: function(e){
                    var key = e.which;
                    if(key == 13 && document.activeElement.id == jQuery(searchFields[i]).attr("id")){
                        e.preventDefault();
                        jQuery(searchButton).click();
                    }
                }
            });
        }
        //为下拉框添加事件
        if (jQuery(searchFields[i]).children()[0].tagName=='SELECT'){
            jQuery(searchFields[i]).on('change', function () {

                jQuery(searchButton).click();
            } );
        }
    }
    filter.append(tr);
    thead.after(filter)
}

/**
 * 为每个page生成edusec中的header样式
 * 简易版方法
 * @param id header-content中额外设置的一个标题容器对应的id
 * @param icon 图标样式
 * @param bigTitle 正标题
 * @param smallTitle 副标题
 */
function initContentHeader(id,icon,bigTitle,smallTitle) {
    jQuery('#'+id).html('<h2> <i class="'+icon+'"></i> '+bigTitle+' |<small> '+smallTitle+'</small></h2>');
}

