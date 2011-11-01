# Divides a list of numbers by pi

from sys import argv;

output = ""

for number in raw_input().split(','):
	output += str(float(number) * 25) +", "

print output
