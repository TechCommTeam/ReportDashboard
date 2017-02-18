var pieChartResult={
		passPer:0,
		failPer:0,
		remainsPer:100,
};
var barChartResult=null;
var keys=new Array();
function getLatestValues() {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			// check for HTTP status of OK
			if (req.status == 200) {
				try {
					var job=JSON.parse(req.responseText);
					//Set Result for Pie Chart
					var pieChart=job["pieChart"];
					total=pieChart["total"];
					fail=pieChart["failures"];
					totalRun=pieChart["totalRun"];
					pieChartResult.remainsPer=Math.round((total-totalRun)*100/total,1);
					pieChartResult.failPer=Math.round((fail*100)/total,1);
					pieChartResult.passPer=100-(pieChartResult.failPer+pieChartResult.remainsPer);

					//Set Result For Bar Chart 
					barChartResult=job["barChart"];
					for(key in barChartResult){
						keys.push(key);
					}
					var buildidElement=document.getElementById("buildid");
					buildidElement.innerHTML="Build No:- "+keys[0];
					demo.initChartist();
				} catch (e) {
					console.log("Exception::-"+e.toString());
				}
			}
		}
	};
	req.open("GET", "GetCurrentTestCases", true);
	req.send(null);
}