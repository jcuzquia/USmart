
function loadChart(){
	$(function () {
	    $('#daily_series_container').highcharts({
	    	
	    	
	    	
	    	
	    	data: {
	            csv: document.getElementById('csv').innerHTML,
	            y: "Weekend",
	    		color: 'green',
	            //parsed: function () {
	            //    start = +new Date();
	            //}
	        },
	        
	        chart: {
                zoomType: 'x'
            },
	        
	        plotOptions: {
	            line: {
	            	lineWidth:1,
	                marker: {
	                    enabled: false
	                }
	            },
	            row: {
	                colorByPoint: false
	            }
	           
	        },
	    	
	    	title: {
	            text: 'Monthly Average Temperature',
	            x: -20 //center
	        },
	        subtitle: {
	            text: 'Source: WorldClimate.com',
	            x: -20
	        },
	        yAxis: {
	            title: {
	                text: 'Temperature (°C)'
	            },
	            plotLines: [{
	                value: 0,
	                width: 0.1,
	                color: '#808080'
	            }]
	        },
	        tooltip: {
	            valueSuffix: '°C'
	        },
	        legend: {
	        	enabled: false,
	            layout: 'vertical',
	            align: 'right',
	            verticalAlign: 'middle',
	            borderWidth: 0
	        },

	    });
	});
}