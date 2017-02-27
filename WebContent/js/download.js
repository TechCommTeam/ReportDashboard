function downloadFile() {
	var element, fileName, table, tr, td, i,elementText,status;
	table = document.getElementById("getDownloadFile");
	tr = table.getElementsByTagName("tr");	
	elementText=document.getElementById("tableHeading").innerHTML;
	fileName=elementText.split("::")[1];
	var result=""
		var a=document.createElement('a');
	var data_type = 'data:application/txt;charset=utf-16';

	for (i = 0; i < tr.length-1; i++) {
		if($(tr[i]).is(':visible')){
			td = tr[i].getElementsByTagName("td")[2];
			if(td.innerHTML.length>10){
				result+=td.innerHTML+"\r\n";
			}else{
				result+=td.innerHTML+"\n";
			}
		}
	}
	result=encodeURIComponent(result);
	a.href = data_type + ', ' + result;
	a.download = fileName+".txt";
	a.click();	
}