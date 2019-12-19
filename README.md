# bluetooth_orient_3d
Diplom work

Guide install lib PyBluez and LightBlue on Mac OS X:
1) Start as stated by op until (including) pip install pyobjc
2) Don't install lightblue since pybluez embeds its own version (and it's different)
3) (Make sure you have Xcode installed before the next step, not just the Command Line Tools)
4) Clone https://github.com/pybluez/pybluez.git, open pybluez/macos/LightAquaBlue/LightAquaBlue.xcodeproj, follow this steps in order to fix the "does not contain scheme" error, cd and python setup.py install1)
5) Move LightAcuaBlue.framework from the git library to usr/local.lib/python3.7/site-packages/PyBluez-0.22-py3.7.egg/lightblue
