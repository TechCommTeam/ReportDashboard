
function getMachineValues(versionBuild) {
	var req = new XMLHttpRequest();
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			// check for HTTP status of OK
			if (req.status == 200) {
				try {
					var job=JSON.parse(req.responseText);
					//Set Result for Version Run
					var versionResult=job["machineData"];
					//Ger version information
					var temp=versionBuild.split(".");
					var build=temp[temp.length-1];
					var table="<thead>" +
					"<th>No</th><th>Machine</th><th>Total</th>" +
					"<th>Pass</th><th>Fail</th><th>Date</th><th>Detail</th>" +
					"</thead>";
					var i=1;
					table+="<tbody>";

					for(key in versionResult){
						var pass=versionResult[key][0]-versionResult[key][1];
						table+="<tr>" +
						"<td>"+i+"</td><td>"+key+"</td><td>"+versionResult[key][0]+"</td>" +
						"<td>"+pass+"</td><td>"+versionResult[key][1]+"</td><td>"+versionResult[key][2]+
						"</td><td><input type='image' src='assets/icons/details.jpg' " +
						"onclick=getTestCaseValues('"+key+"','"+versionBuild+"')></td>" +
						"</tr>";
						i++;
					}
					table+="</tbody>";
					
					//Set Value for respective Field in table
					document.getElementById("tableHeading").innerHTML="FrameMaker "+temp[0]+".0.x."+build;
					document.getElementById("machineTable").innerHTML=table;
					document.getElementById("allRunView").innerHTML="<a href='table.html'>All Run</a>";
					document.getElementById("buildView").innerHTML="Build View <b class='caret'></b>";

				} catch (e) {
					console.log("Exception::-"+e.toString());
				}
			}
		}
	};
	req.open("GET", "GetMachineDataServlet?versionBuild="+versionBuild, true);
	req.send(null);
}