var piChartResult={
		passPer:10,
		failPer:20,
		remainsPer:70,
};
function getLatestValues() {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			// check for HTTP status of OK
			if (req.status == 200) {
				try {
					console.log("Response Text New:- " + req.responseText);
					var mObject=JSON.parse(req.responseText);
					console.log(mObject["totalRun"]);
					total=mObject["total"];
					fail=mObject["failures"];
					var totalRun=mObject["totalRun"];
					piChartResult.remainsPer=Math.round((total-totalRun)*100/total,1);
					piChartResult.failPer=Math.round((fail*100)/total,1);
					piChartResult.passPer=100-(piChartResult.failPer+piChartResult.remainsPer);
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