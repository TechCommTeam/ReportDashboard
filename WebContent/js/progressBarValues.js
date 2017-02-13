var progressBarResult=null;
function progressBarValues() {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			// check for HTTP status of OK
			if (req.status == 200) {
				try {
					var job=JSON.parse(req.responseText);
					//Set Result For Bar Chart 
					progressBarResult=job["progressBar"];
					console.log(progressBarResult);
				} catch (e) {
					console.log("Exception::-"+e.toString());
				}
			}
		}
	};
	req.open("GET", "ProgressBar", true);
	req.send(null);
}