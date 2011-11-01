# Converts xml / python names into java names and vice-versa

from sys import argv
import re

convert_to_java = '-toJava'
convert_to_xml = '-toXML'
associate_xml = '-a'

if len(argv) > 1 :
	mode = argv[1]
if len(argv) > 2:
	input_text = argv[2]

def convert_xml ( java ):
	return re.sub(r'([a-z])([A-Z])', r'\1_\2', java).lower()

def convert_java ( xml ):
	output = ''
	words = xml.split('_') 
	for word in words:
		if ( word != words[0] ):
			output= output + word.capitalize()
		else:
			output= output + word
	return output

def associate ( names ):
	output =''
	for name in names.split(','):
		xml_name = convert_xml(name)
		obj_type = xml_name.split('_').pop().capitalize()
		if ( obj_type == 'Box' ):
			obj_type = 'CheckBox'
		output = output + name + ' = (' + obj_type + ') findViewById(R.id.'
		output = output + xml_name + ');\n'
	return output



#If this is an xml / python variable name
if mode == convert_to_java:
	print convert_java(input_text)
elif mode == convert_to_xml:
	print convert_xml(input_text)
elif mode == associate_xml:
	print associate(input_text)

