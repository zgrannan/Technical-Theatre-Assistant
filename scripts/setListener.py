# Adds a listener to a view or list of views
# I will come back to this.
from sys import argv
import re

listener_name = argv[1]
elements = argv[2]

set_listener_function = "setOn" + listener_name
for element in elements.split(','):
	output =  element + '.' + set_listener_function + '('
	output = output + element + "Listener);"
	print output

