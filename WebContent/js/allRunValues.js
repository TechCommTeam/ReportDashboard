var userValue={
		version:0
};
function getFmCompleteRun(state) {
	var req = new XMLHttpRequest();
	var versionElement=document.getElementById("currentVersionNumber");
	userValue.version=versionElement.getAttribute("version");
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			// check for HTTP status of OK
			if (req.status == 200) {
				try {
					console.log(req.responseText);
					var job=JSON.parse(req.responseText);
					//Set Result for Version Run
					var versionResult=job["allRun"];
					var tbodyElement=document.getElementById("FMLatestRunResult");

					//Set Button Status for next operation
					var buttonStatus=job["buttonStatus"]["buttonStatus"];
					if(!buttonStatus[0]){

						if(!buttonStatus[1])
							document.getElementById("rightButton").setAttribute("disabled","disabled")
							else
								document.getElementById("rightButton").removeAttribute("disabled");
						document.getElementById("leftButton").setAttribute("disabled","disabled");

					}else{
						if(!buttonStatus[1]){
							document.getElementById("rightButton").setAttribute("disabled","disabled");

						}else{

							document.getElementById("rightButton").removeAttribute("disabled");
						}
						document.getElementById("leftButton").removeAttribute("disabled");
					}

					var setVersion=true;
					var tString="";
					var i=1;
					//Creating entries to insert in page view
					for(key in versionResult){
						var versionInfo=key.split(".");
						var tag="NA";
						if(versionResult[key][2]!=null){
							tag=versionResult[key][2];
						}
						if(setVersion){
							versionElement.setAttribute("version",versionInfo[0]);
							versionElement.innerHTML=" "+versionInfo[0];
							setVersion=false;
						}
						var pass=versionResult[key][0]-versionResult[key][1];
						tString+="<tr><td>"+(i++)+"</td>" +
						"<td>"+versionInfo[2]+"</td>" +
						"<td>"+versionResult[key][0]+"</td>" +
						"<td>"+pass+"</td>" +
						"<td>"+versionResult[key][1]+"</td>" +
						"<td>"+tag+"</td>" +
						"<td>"+versionResult[key][3]+"</td>"+
						"<td><input type='image' src='assets/icons/right_chevron.jpg' " +
						"onclick=getMachineValues('"+key+"')></td></tr>";
					}
					tbodyElement.innerHTML=tString;
				} catch (e) {
					console.log("Exception::-"+e.toString());
				}
			}
		}
	};
	req.open("GET", "GetLastRunResult?version="+userValue.version+"&state="+state, true);
	req.send(null);
}