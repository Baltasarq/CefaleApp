python diamk.py data.xml data > data.gv
python diamk.py data.xml migraine > migraine.gv
python diamk.py data.xml tensional > tensional.gv
dot -Tpng data.gv -odata.png && gwenview data.png&
dot -Tpng migraine.gv -omigraine.png && gwenview migraine.png&
dot -Tpng tensional.gv -otensional.png && gwenview tensional.png&
