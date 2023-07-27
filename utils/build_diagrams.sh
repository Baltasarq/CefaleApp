python diamk.py data.xml data > data.gv
python diamk.py data.xml migraine > migraine.gv
python diamk.py data.xml depression > depression.gv
dot -Tpng data.gv -odata.png && gwenview data.png&
dot -Tpng migraine.gv -omigraine.png && gwenview migraine.png&
dot -Tpng depression.gv -odepression.png && gwenview depression.png&
