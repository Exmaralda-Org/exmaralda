BEGIN {
 for (i=913; i<=929; i++){
	printf("%s","<key>");
	printf("%s","<content>&#x")
	printf("%s", toHexString(i))
	printf("%s", ";</content>")
	printf("%s","<description>")
	printf("%s", toHexString(i))
	printf("%s", "</description>")
	printf("%s", "<font name=\"Times New Roman\" size=\"12\" face=\"plain\"/>")
	printf("%s","</key>");
 }
 for (i=931; i<=937; i++){
	printf("%s","<key>");
	printf("%s","<content>&#x")
	printf("%s", toHexString(i))
	printf("%s", ";</content>")
	printf("%s","<description>")
	printf("%s", toHexString(i))
	printf("%s", "</description>")
	printf("%s", "<font name=\"Times New Roman\" size=\"12\" face=\"plain\"/>")
	printf("%s","</key>");
 }
 for (i=945; i<=969; i++){
	printf("%s","<key>");
	printf("%s","<content>&#x")
	printf("%s", toHexString(i))
	printf("%s", ";</content>")
	printf("%s","<description>")
	printf("%s", toHexString(i))
	printf("%s", "</description>")
	printf("%s", "<font name=\"Times New Roman\" size=\"12\" face=\"plain\"/>")
	printf("%s","</key>");
 }
}


function toHexString(number){
	result = ""
	# result = "\"\\u"
	result = result toHex(number)
	# result = result "\","
	return result
}

function toHex(number){
	d1 = int(number/(16**3))
	d2 = int((number - d1*(16**3))/(16**2))
	d3 = int((number - d1*(16**3) - d2*(16**2))/(16**1))
	d4 = (number - d1*(16**3) - d2*(16**2) - d3*(16**1))
	print number "=" hex(d1) hex(d2) hex(d3) hex(d4) > "con"
	return hex(d1) hex(d2) hex(d3) hex(d4)
}

function hex(number){
	number+=0
	if (number<10){
		return number ""
	}
	else if (number==10){
		return "A"
	}
	else if (number==11){
		return "B"
	}
	else if (number==12){
		return "C"
	}
	else if (number==13){
		return "D"
	}
	else if (number==14){
		return "E"
	}
	else if (number==15){
		return "F"
	}
}
