function checkBoxFilter() {
				var showAll = true;
				 $('tr').hide();
				$('input[type=checkbox]').each(
						function() {
							if ($(this)[0].checked) {
								showAll = false;
								var status = $(this).attr('rel');
								var value = $(this).val();
								console.log("Status:"+status+" Value:"+value);
								$('td.' + status + '[rel="' + value + '"]')
										.parent('tr').show();
							}
						});
				if (showAll) {
					$('tr').show();
				}
				console.log("I am here");
				$(document.getElementById('tableHeader')).show();
				$(document.getElementById('filterOption')).show();
}
//Dynamic Search Filter for TestCases
function searchFilter() {
	  var input, filter, table, tr, td1,td2, i;
	  input = document.getElementById("searchInput");
	  filter = input.value.toUpperCase();
	  table = document.getElementById("machineTable");
	  tr = table.getElementsByTagName("tr");
	  for (i = 0; i < tr.length; i++) {
	    td1 = tr[i].getElementsByTagName("td")[2];
	    td2 = tr[i].getElementsByTagName("td")[4];
	    if (td1||td2) {
	      if ((td1.innerHTML.toUpperCase().indexOf(filter) > -1)||(td2.innerHTML.toUpperCase().indexOf(filter) > -1)) {
	        tr[i].style.display = "";
	      } else {
	        tr[i].style.display = "none";
	      }
	    }       
	  }
	}