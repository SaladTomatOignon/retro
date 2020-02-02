function submitform() {
	//initialisaion du fichier reader 
	var reader = new FileReader();
	var file = document.getElementById("inputFile").files[0];
	var fileName =fileName
	//recupère les options
	console.log("inputFile =");
	console.log("type [" + file.type + "]");
	console.log("name [" + file.type + "]");
	console.log("size [" + file.type + "]");
	console.log("lastMofifiedDate ["+file.lastMofifiedDate +"]");
	reader.onload = function(event) {
		console.log(reader.result);
	};
	reader.readAsText(file);
	var javaVersionValue =  document.getElementById("javaVersionValue").value;
	var targetValue = document.getElementById("target_1").checked;
	var forceValue = document.getElementById("force_1").checked;
	var infoValue = document.getElementById("info_1").checked;
	var featureValue = document.getElementById("feature_1").checked;

	var TWR = "";
	var lambda = "";
	var concatenation = "";
	var nestmates ="";
	var record ="";

	if(document.getElementById("TWR").checked){
		var TWR = document.getElementById("TWR").value;
	} 

	if(document.getElementById("lambda").checked){
		var lambda = document.getElementById("lambda").value;
	} 

	if(document.getElementById("concatenation").checked){
		var concatenation = document.getElementById("concatenation").value;
	} 

	if(document.getElementById("nestmates").checked){
		var nestmates = document.getElementById("nestmates").value;
	} 

	if(document.getElementById("record").checked){
		var record = document.getElementById("record").value;
	} 

	console.log("\n\nargumeents :");
	console.log("javaVersionValue = [" + javaVersionValue + "]");
	console.log("targetValue = [" + targetValue+ "]");
	console.log("forceValue = ["  + forceValue+ "]");
	console.log("featureValue = ["  + featureValue + "]");
	console.log("[" + TWR + "]");
	console.log("[" + lambda + "]");
	console.log("[" + concatenation + "]");
	console.log("[" + nestmates + "]");
	console.log("[" + record + "]");

	//creation de formdata pour envoyer les donner à quarkus
	var formData = new formData();
	formData.append('file',file);
	formData.append('file',fileName);
	
	formData.append('javaVersionValue',javaVersionValue); 
	formData.append('infoValue',infoValue);
	formData.append('targetValue',targetValue);
	formData.append('forceValue',forceValue);
	formData.append('featureValue',featureValue);

	formData.append('TWR',TWR);
	formData.append('lambda',lambda);
	formData.append('concatenation',concatenation);
	formData.append('nestmates',nestmates);
	formData.append('record',record);
	
	//envoie des données

	var xhr = new XMLHttpRequest();
	xhr.open('POST', 'index.html');
	xhr.onload() = function(){
		//TODO..
	}
	xhr.onreadystatechange = function() {
		if(xhr.readyState === 4) {
			document.getElementById("outputTextDiv").innerHTML += "<p> processing file ... <p>";
			console.log("xhr.response = " + xhr.response);

			document.getElementById("outputTextDiv").innerHTML += "<p> ____________________</p>";
			var response = xhr.response;
			var parseResponse = response.split("\n");
			parseResponse.forEach((elem) => {
				document.getElementById("outputTextDiv").innerHTML += "<p>"+ elem + "</p>";
			});
		}
	}

	xhr.upload.onprogress = function (event) {
		//TODO
	};
	xhr.send(formData);

}

function init() {
	let formData = new FormData();
	let files = document.getElementById("files").files;
	let count = 0;

	for (file of files) {
		let reader = new FileReader();
		reader.readAsText(file, "UTF-8");
		reader.onload = function (evt) {
			formData.append("file" + count, evt.target.result);
		}
		reader.onerror = function (evt) {
			alert("Error reading file " + file.name);
		}
		reader.onloadend = function (evt) {
			count++;
			if ( count === 2 ) {
				convert(formData);
			}
		}
		
	}
}

function convert(formData) {
	let twr = document.getElementById("TWR").checked;
	let lambda = document.getElementById("Lambda").checked;
	let concatenation = document.getElementById("Concatenation").checked;
	let nestmate = document.getElementById("Nestmate").checked;
	let record = document.getElementById("Record").checked;

	let force = document.getElementById("Force").checked;
	let target = document.getElementById("Target_version").value;

	formData.append("twr", twr);
	formData.append("lambda", lambda);
	formData.append("concatenation", concatenation);
	formData.append("nestmate", nestmate);
	formData.append("record", record);

	formData.append("force", force);
	formData.append("target", target);

	var xhr = new XMLHttpRequest();
	xhr.open('POST', '/conversion/convert');
	xhr.send(formData);
}