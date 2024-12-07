BEGIN {
 for (i=4256; i<=4293; i++){
	printf("%s", toHexString(i))
 }
 for (i=4304; i<=4344; i++){
	printf("%s", toHexString(i))
 }
}


function toHexString(number){
	result = "\"\\u"
	result = result toHex(number)
	result = result "\","
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
