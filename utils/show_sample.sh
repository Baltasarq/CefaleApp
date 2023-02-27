python diamk.py data.xml > $1.gv
dot -Tpng $1.gv -o$1.png && gwenview $1.png&
