#!/usr/bin/env python
import sys
import os
import os.path
import xml.dom.minidom
import subprocess

if "1.8.0" in subprocess.check_output("java -Xmx32m -version", shell=1, stderr=subprocess.STDOUT) :
  subprocess.check_call("mvn -Dmaven.test.skip=true clean source:jar deploy --settings mvn_settings.xml", shell=1, stderr=subprocess.STDOUT)
else :
  print "not java 8"