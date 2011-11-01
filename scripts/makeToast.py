#makes a toast with the given string ID

from sys import argv

def make_toast (string_id):
	return "Toast.makeText(getBaseContext(), getString(R.string." + string_id + "), Toast.LENGTH_SHORT).show();"

if ( argv[0] == "makeToast.py" ):
	print make_toast(argv[1])

