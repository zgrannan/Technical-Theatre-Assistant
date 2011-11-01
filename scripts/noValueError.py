#Creates java code to check whether or not an object is zero
#Creates an appropriate toast error and returns (nothing) if the object is zero

from sys import argv
from nameConverter import convert_xml
from makeToast import make_toast
def get_error_check (name):
	output = 'if (' + name + ".equals(0)){\n\t"
	output += make_toast(convert_xml(name)+'_error') +'\n\treturn;\n}'
	return output

for name in argv[1].split(','):
	print get_error_check(name)

	
