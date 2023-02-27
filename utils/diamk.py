# DiaMK (c) 2023 Baltasar MIT License <jbgarcia@uvigo.es>


from string import Template
from xml.dom import minidom as xml


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


ETQ_Q = "q"
ETQ_ID = "id"
ETQ_DATA_ID = "dataId"
ETQ_GOTO = "goto"
ETQ_W = "w"
ETQ_VALUE = "value"
ETQ_OPT = "opt"
ETQ_OPTS = "opts"
ETQ_TEXT = "text"


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


def parse_file(fn: str) -> dict[str, dict]:
    def store_q_attribute(qst: dict, dom_attr: tuple):
        if dom_attr[0] == ETQ_TEXT:
            qst[ETQ_TEXT] = fmt_box_text(dom_attr[1])
        elif dom_attr[0] == ETQ_ID:
            qst[ETQ_ID] = dom_attr[1]
        elif dom_attr[0] == ETQ_DATA_ID:
            qst[ETQ_DATA_ID] = dom_attr[1]
        elif dom_attr[0] == ETQ_VALUE:
            qst[ETQ_VALUE] = dom_attr[1]

    def store_opt_attribute(q: dict, op: dict, dom_attr: tuple):
        op[ETQ_W] = "0"
        op[ETQ_GOTO] = str(int(q[ETQ_ID]) + 1)

        if dom_attr[0] == ETQ_TEXT:
            op[ETQ_TEXT] = fmt_box_text(dom_attr[1])
        elif dom_attr[0] == ETQ_VALUE:
            op[ETQ_VALUE] = dom_attr[1]
        elif dom_attr[0] == ETQ_GOTO:
            op[ETQ_GOTO] = dom_attr[1]
        elif dom_attr[0] == ETQ_W:
            op[ETQ_W] = dom_attr[1]

    qs = {}
    dom = xml.parse(fn)
    dom_qs = dom.getElementsByTagName(ETQ_Q)

    for dom_q in dom_qs:
        q = {}

        if dom_q.hasAttributes():
            for attr in dom_q.attributes.items():
                store_q_attribute(q, attr)

            q[ETQ_OPTS] = []
            for sub_node in dom_q.getElementsByTagName(ETQ_OPT):
                opt = {}
                q[ETQ_OPTS].append(opt)

                for attr in sub_node.attributes.items():
                    store_opt_attribute(q, opt, attr)

        qs[q[ETQ_ID]] = q

    return qs


def create_diagram(qs: dict[str, dict]) -> str:
    diagram_template = Template(str_diagram_template)
    box_template = Template(str_box_template)
    edge_template = Template(str_edge_template)
    toret = ""

    substs = {
        "shape": "box",
        "box_text": "",
        "box_name": "",
        "next_box_name": ""
    }

    # Generate boxes
    for q in qs.values():
        substs["shape"] = "diamond" if len(q[ETQ_OPTS]) > 0 else "box"
        substs["box_text"] = q[ETQ_TEXT]
        substs["box_name"] = q[ETQ_DATA_ID]
        toret += box_template.substitute(substs)

    # Generate edges
    for q in qs.values():
        for opt in q[ETQ_OPTS]:
            next_box = qs.get(opt[ETQ_GOTO])

            if next_box:
                substs["box_name"] = q[ETQ_DATA_ID]
                substs["next_box_name"] = next_box[ETQ_DATA_ID]
                substs["label"] = opt[ETQ_TEXT] + '\n' + opt[ETQ_W]
                toret += edge_template.substitute(substs)

    return diagram_template.substitute(body=toret)


if __name__ == "__main__":
    print(create_diagram(parse_file("data.xml")))
