# DiaMK (c) 2023 Baltasar MIT License <jbgarcia@uvigo.es>


import sys
from string import Template
from xml.dom import minidom as xml
from html.parser import HTMLParser
from io import StringIO


str_diagram_template = """
digraph Migranna {
    $body
}
"""

str_box_template = """
    $box_name[shape=$shape, label="$box_text"];
"""

str_edge_template = """
    $box_name -> $next_box_name[label="$label"];
"""


ETQ_BRANCH = "branch"
ETQ_Q = "q"
ETQ_ID = "id"
ETQ_GOTO = "goto"
ETQ_VALUE = "value"
ETQ_OPT = "opt"
ETQ_OPTS = "opts"
ETQ_TEXT = "text"
ETQ_SUMMARY = "summary"


class MLStripper(HTMLParser):
    def __init__(self):
        super().__init__()
        self.reset()
        self.strict = False
        self.convert_charrefs= True
        self.text = StringIO()
        
    def handle_data(self, d):
        self.text.write(d)
        
    def get_data(self):
        return self.text.getvalue()


def strip_tags(txt):
    s = MLStripper()
    s.feed(txt)
    return s.get_data()


def fmt_box_text(t: str) -> str:
    l = list(t.strip())
    num_spaces = 0

    for i, ch in enumerate(l):
        if ch == ' ':
            if num_spaces >= 2:
                l[i] = '\n'
                num_spaces = 0

            num_spaces += 1

    return "".join(l)


def parse_file(fn: str) -> dict:
    def store_q_attribute(qst: dict, dom_attr: tuple):
        if dom_attr[0] == ETQ_TEXT:
            qst[ETQ_TEXT] = fmt_box_text(dom_attr[1])
        elif dom_attr[0] == ETQ_ID:
            qst[ETQ_ID] = dom_attr[1]
        elif dom_attr[0] == ETQ_VALUE:
            qst[ETQ_VALUE] = dom_attr[1]
        elif dom_attr[0] == ETQ_GOTO:
            qst[ETQ_GOTO] = dom_attr[1]
        elif dom_attr[0] == ETQ_SUMMARY:
            qst[ETQ_SUMMARY] = dom_attr[1]

    branches = {}
    dom = xml.parse(fn)
    dom_branches = dom.getElementsByTagName(ETQ_BRANCH)

    for dom_branch in dom_branches:
        branch_id = dom_branch.attributes[ETQ_ID].value
        dom_qs = dom_branch.getElementsByTagName(ETQ_Q)
        qs = {}
        head = None

        for dom_q in dom_qs:
            # New question
            q = {}

            # Read attributes
            if dom_q.hasAttributes():
                if not head:
                    head = dom_q.attributes[ETQ_ID].value

                for attr in dom_q.attributes.items():
                    store_q_attribute(q, attr)

            qs[q[ETQ_ID]] = q

        branches[branch_id] = (head, qs)

    return branches


def create_diagram_for(branches: dict, id: str) -> str:
    diagram_template = Template(str_diagram_template)
    box_template = Template(str_box_template)
    edge_template = Template(str_edge_template)
    toret = ""
    head, qs = branches[id]

    substs = {
        "shape": "box",
        "box_text": "",
        "box_name": "",
        "next_box_name": ""
    }

    # Generate boxes
    for q in qs.values():
        text = strip_tags(q[ETQ_TEXT].replace('\n', ' '))
        
        substs["shape"] = "box"
        substs["box_text"] = text
        substs["box_name"] = q[ETQ_ID]
        toret += box_template.substitute(substs)

    # Generate edges
    for q in qs.values():
        next_box = qs.get(q.get(ETQ_GOTO))

        if next_box:
            substs["box_name"] = q[ETQ_ID]
            substs["next_box_name"] = next_box[ETQ_ID]
            substs["label"] = strip_tags(q[ETQ_SUMMARY])
            toret += edge_template.substitute(substs)

    return diagram_template.substitute(body=toret)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("[ERR] I need an XML file name to create a diagram for", file=sys.stderr)
        sys.exit(-1)
    elif len(sys.argv) < 3:
        print("[ERR] I need a branch name to create a diagram for", file=sys.stderr)
        sys.exit(-2)

    branch_id = sys.argv[2]
    branches = parse_file(sys.argv[1])
    branch = branches.get(branch_id)

    if not branch:
        print("[ERR] No branch with id:", branch_id, file=sys.stderr)
        sys.exit(-3)

    print(create_diagram_for(branches, branch_id))
