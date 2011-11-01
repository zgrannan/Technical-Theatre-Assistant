#Converts multiple lines of '//' comments to a single /* - style comment

from sys import argv #Allow for terminal parameters
from textwrap import wrap #For text formatting

#Associate filenames from terminal
input_filename = argv[1];
output_filename = argv[2];

#Associate file handlers from filenames
input_file = open(input_filename);

lines = input_file.readlines(); #Get lines from input file

input_file.close() #Close the input file

max_comment_chars = 80 #The maximum number of characters a comment can have per line
i = 0 #Initialize the counter
num_lines = len(lines) #Get the number of lines;
output = '' #initialize output

print "Formatting comments for: " + argv[1]

while i < num_lines: 
    if lines[i].find('//') != -1 and i + 1  < num_lines and lines[i+1].lstrip().startswith('//'):

        #Grab the whitespace from the first comment and use it when formatting the comment
	whitespace,nothing,nothing = lines[i+1].rpartition('//')

        #Split the initial line, in case the comment starts on the same line as a command 
	before_comment, double_slash, comment = lines[i].rpartition('//') 
        output = output + before_comment+'\n'
        i = i + 1


	#Start reading more lines, until the comment or file ends
	#This loop removes all the whitespace from the comments, and then 
	#Formats them as a nicely formatted comment that is no more than
	#max_comment_chars wide
	while  i < num_lines and lines[i].lstrip().startswith('//'):
            to_write = lines[i].replace('//','\n').lstrip()
            comment = comment + to_write
            i = i + 1
        else:
	   #The comment has ended, or the file has ended
	   output = output + '\n' + whitespace + '/*\n'
	   comment = comment.replace('\n',' ').ljust(max_comment_chars).lstrip()
	   for line in wrap(comment,max_comment_chars):
		   output = output +  whitespace + '* ' + line +'\n'
	   output = output + whitespace + '*/\n'
    else:
	#The line is not a comment
        output = output + (lines[i])
        i = i + 1

#Reading complete, write the file

output_file = open(output_filename,'w');
output_file.write(output)
output_file.close()
