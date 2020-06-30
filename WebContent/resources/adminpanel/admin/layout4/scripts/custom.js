$("#createInstanceButton").click(function(event) {
	event.preventDefault();
	$.ajax({
		url : "/LivoCloud/createInstance",
		type : "POST",
		success : function(response) {
			document.getElementById("instanceIp").innerHTML= response;
			
			
		},
		error : function(response) {
			alert("error : " +response.responseText);

		}

	});

});
