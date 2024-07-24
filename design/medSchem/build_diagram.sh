python med_schem.py migraine_treatment.xml > data.gv
dot -Tpng data.gv -odata.png && gwenview data.png
