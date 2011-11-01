#Creates code that gets a dimension from an intent

from sys import argv
from nameConverter import convert_java

intent_name = argv[2]

if argv[1] == '-case':
	for name in argv[3].split(','):
		output = 'case ' + name +":{\n\t" + convert_java(name) + ".setValue((Dimension)"
		output += intent_name + '.getSerializableExtra("dimension"));\n\tbreak;\n}'
		print output
