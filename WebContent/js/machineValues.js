
function getMachineValues(versionBuild) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			// check for HTTP status of OK
			if (req.status == 200) {
				try {
					console.log(req.responseText);

				} catch (e) {
					console.log("Exception::-"+e.toString());
				}
			}
		}
	};
	req.open("GET", "GetMachineDataServlet?versionBuild="+versionBuild, true);
	req.send(null);
}