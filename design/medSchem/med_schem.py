#! /usr/bin/env python
# MedSchem (c) 2024 Baltasar MIT License <jbgarcia@uvigo.gal>


from string import Template
from io import StringIO
from collections import defaultdict
import xml.dom.minidom as xmlp
import sys


ETQ_ID = "id"
ETQ_NAME = "name"
ETQ_GROUP = "groupId"
ETQ_MEDICINE = "medicine"

STR_DIAGRAM_TEMPLATE = """
digraph Meds {
    $body
}
"""

STR_SUBGRAPH_TEMPLATE = """
subgraph $id {
    $body
}
"""

STR_BOX_TEMPLATE = """
    $box_name[shape=$shape, label="$box_text"];
"""

STR_EDGE_TEMPLATE = """
    $box_name -> $next_box_name[label="$label"];
"""


def create_box(id: str, text: str) -> str:
    box_template = Template(STR_BOX_TEMPLATE)
    
    substs = {
        "shape": "box",
        "box_text": text,
        "box_name": id
    }

    return box_template.substitute(substs)
...


def create_edge(id1: str, id2: str) -> str:
    box_template = Template(STR_EDGE_TEMPLATE)
    
    substs = {
        "box_name": id1,
        "next_box_name": id2,
        "label": ""
    }

    return box_template.substitute(substs)
...


def create_subgraph(id: str, body: str) -> str:
    diag_template = Template(STR_SUBGRAPH_TEMPLATE)
    
    substs = {
        "id": id,
        "body": body
    }

    return diag_template.substitute(substs)
...


def create_diagram(body: str) -> str:
    diag_template = Template(STR_DIAGRAM_TEMPLATE)
    
    substs = {
        "body": body
    }

    return diag_template.substitute(substs)
...


def fmt_id(str_id: str) -> str:
    return str_id.lower().replace('-', '_')
...


def get_meds_by_group(nf: str) -> defaultdict[str, str]:
    doc = xmlp.parse(nf)
    meds_by_groups = defaultdict(list)
    
    for elem in doc.getElementsByTagName(ETQ_MEDICINE):
        med_id = elem.attributes[ETQ_ID].value
        med_name = elem.attributes[ETQ_NAME].value
        med_group = elem.attributes[ETQ_GROUP].value
        meds_by_groups[med_group].append((med_id, med_name))
    ...
    
    return meds_by_groups
...


def generate_diagram(nf: str) -> str:
    meds_by_groups = get_meds_by_group(nf)
    toret = ""
    
    keys = sorted(list(meds_by_groups.keys()))
    for key in keys:
        key_id = fmt_id(key)
        sub_graph = create_box(key_id, key)
        
        for (id, med) in meds_by_groups[key]:
            med_id = fmt_id(id)
            sub_graph += create_box(med_id, med)
            sub_graph += create_edge(key_id, med_id)
        ...
        
        toret += create_subgraph(key_id, sub_graph)
    ...

    return create_diagram(toret)
...


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Missing file name.")
    else:
        print(generate_diagram(sys.argv[1]))
    ...
...
