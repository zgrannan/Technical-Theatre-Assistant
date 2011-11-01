# Creates code to get objects from a bundle

from sys import argv

object_type = argv[1]
names = argv[2]

for name in names.split(','):
	output = 'if ( savedInstanceState.getSerializable("' + name +'") != null )\n\t'
	output += name + ' = (' + object_type +') savedInstanceState.getSerializable("'
	output += name + '");'
	print output

