python diamk.py data.xml > data.gv
dot -Tpng data.gv -odata.png && gwenview data.png&
