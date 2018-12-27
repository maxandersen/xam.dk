
import sys
import mf2py
import yaml
from yaml import dump

obj = mf2py.parse(url=sys.argv[1])

print yaml.safe_dump(obj)
