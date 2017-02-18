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
					//Get table body element for machine status
					var tbodyElement=document.getElementById("machineProgressBar");
					var tbodyData="";
					var pClass=null;
					var mStatus="";
					var pBarType={
							running:"progress-bar progress-bar-info",
							complete:"progress-bar progress-bar-success",
							blocked:"progress-bar progress-bar-danger",
							stopped:"progress-bar progress-bar-warning"
					};

					for(key in progressBarResult ){
						var mValues=progressBarResult[key];

						//Finding Bar class Type
						pCompleted=Math.round((mValues[0]+mValues[1])*100/(mValues[2]),2);

						switch(mValues[3]){
						case 0:
							mStatus="complete";
							pClass=pBarType.complete;
							break;
						case 1:
							mStatus="running";
							pClass=pBarType.running;
							break;
						case -1:
							mStatus="blocked";
							pClass=pBarType.blocked;
							break;
						case -2:
							mStatus="stopped";
							pClass=pBarType.stopped;
							break;
						default:
							mStatus="Information Not Available";
							pClass="";
							break;
						}

						var barType="<div class='"+pClass+"' role='progressbar' aria-valuenow='"
						+pCompleted+"'aria-valuemin='0' aria-valuemax='100' style='width: "+pCompleted+"%'>"
						+pCompleted+"% "+mStatus+"</div>";

						//starting table entries with table row
						tbodyData+="<tr>";
						tbodyData+="<td>"+key+"</td>";
						tbodyData+="<td><div class='progress'>"+barType+"</div></td>";
						tbodyData+="<td class='td-actions text-center'>"+mValues[0]+"</td>";
						tbodyData+="<td class='td-actions text-center'>"+mValues[1]+"</td>";
						tbodyData+="</tr>";
					}
					tbodyElement.innerHTML=tbodyData;
				} catch (e) {
					console.log("Exception::-"+e.toString());
				}
			}
		}
	};
	req.open("GET", "ProgressBar", true);
	req.send(null);
}