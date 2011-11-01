#Creates code to put a serializable element into a bundle.

from sys import argv

bundle_name = argv[1]
elements = argv[2]

for element in elements.split(','):
	print bundle_name + '.putSerializable("'+element+'", ' + element + ');'

