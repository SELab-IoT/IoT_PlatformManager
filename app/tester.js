$(()=>{
	$("#pep_register").click(()=>{
		
		$("#wrap").empty();
		jQuery.get("requests/pep_register.txt", (data)=>{
			parseRequests(data);
		});
		
	});


	$("#query").click(()=>{
		
		$("#wrap").empty();
		jQuery.get("requests/query.txt", (data)=>{
			parseRequests(data);
		});
		
	});
	
	function br(){
		$("#wrap").append("<br/>");
	}
	
	function append(line){
		$("#wrap").append(line);
	}
	
	function code(code){
		$("#wrap").append("<pre style=\"font-family:unset\">"+code+"</pre>");
	}
	
	function parseRequests(requests){
		var lines = requests.split(/\r?\n/);
		var server = "http://localhost:8000";
		var url = "";
		var method = "GET";
		var buflag = false;
		var buffer = "";
		lines.forEach(line=>{
			if(line.startsWith("#")){
				append("<hr/>");
				append("<h3>"+line+"</h3>");
				append("<h4>Request</h4>");
			}
			else if(["GET", "POST"].includes(line)){
				method = line;
				append("Method: "+line);
				br();
			}
			else if(line.startsWith("/")){
				url = server + line;
				append("URL: "+server+line);
				br();
			}
			else if(line.startsWith("{")){
				append("Params: ");
				br();
				buflag = true;
			}
			else if(line.endsWith("}")){
				buflag = false;
				buffer += line;
				append(line);
				processRequest(url, method, buffer);
				buffer = "";
			}
			
			if(buflag){ 
				buffer += line;
				append(line);
				br();
			}
			
		});
		
		function processRequest(url, method, data){
			append("<h4>Response</h4>");
			$.ajaxSetup({ async:false });
			if(method === "GET"){
				$.get(url).done(success).fail(failure);
			}
			if(method === "POST"){
				data = JSON.stringify(JSON.parse(data));
				$.ajax({
					url : url,
					type : "POST",
					data : data,
					contentType : "application/json",
					dataType : "json",
					success : success,
					error : failure
				});
			}
			
			function success(data, status, header){
				append("<b>Status:</b> "+status+"("+header.status+")");
				br();
				code("<b>Response:</b> "+JSON.stringify(data, null, 2));
			}
			
			function failure(data, status){
				append("<b>Status:</b> "+status+"("+data.status+")");
				br();
				var json = JSON.stringify(JSON.parse(data.responseText), null, 2);
				console.log(json);
				code("<b>Response:</b> "+json);
			}
		}
		
	}
	
});