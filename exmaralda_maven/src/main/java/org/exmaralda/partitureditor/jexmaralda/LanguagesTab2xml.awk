BEGIN {
	print "<languages>"
	FS="\t"
}
{
	print "<language lang=\"" $1 "\" country=\"" $2 "\" status=\"" $3 "\" desc=\"" $4 "\"/>"
}

END {
	print "</languages>"
}