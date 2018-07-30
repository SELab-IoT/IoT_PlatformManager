$(()=>{
	
	function handle(id){
		$("#"+id).click(()=>{
			$("#wrap").empty();
			jQuery.get("requests/"+id+".txt", (data)=>{
				parseRequests(data);
			});
		});
	}
	
	$("button").each(function(index){
		handle(this.id);
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
		var url_platformManager = "http://localhost:8080";
		var url_pep = "http://pepServer:8080";
		var lines = requests.split(/\r?\n/);
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
			else if(line.startsWith("PM")){ //e.g. PM/longin
				url = url_platformManager + line.substring(2);
				append("URL: "+url);
				br();
			}
			else if(line.startsWith("PEP")){ //e.g. PEP/profile
				url = url_pep + line.substring(3);
				append("URL: "+url);
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
				append("Status: "+status+"("+header.status+")");
				br();
				code("Contents: "+JSON.stringify(data, null, 2));
			}
			
			function failure(data, status){
				append("Status: "+status+"("+data.status+")");
				br();
				console.log(data.responseText);
				//var json = JSON.stringify(JSON.parse(data.responseText), null, 2);
				//console.log(json);
				code("Contents: "+data.responseText);
			}
		}
		
	}
	
});