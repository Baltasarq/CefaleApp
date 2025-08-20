# xmldump (c) 2025 Baltasar MIT License <jbgarcia@uvigo.es>
# This is part of the CefaleApp project.
# Its purpose is to dump data from XML to CSV


"""
<qs>
   <q
       id="q1"
       gotoId="q2"
       type="int"
       summary="age"
       text="How old are you?" />
   <q
       id="q2"
       gotoId="q3"
       type="int"
       summary="height"
       text="How tall are you?" />
 </qs>

With "id" as parameter, it outputs:

id
q1
q2
"""


import sys
import xml.etree.ElementTree as ET


ETQ_ID = "id"
ETQ_SUMMARY = "summary"
ETQ_TEXT = "text"


def extract_from_xml(data):
    """Extracts a given tag from every question node.
        :param data: the XML data from ElementTree.
        :return: a list with all values for that tag.
    """
    toret = []
    
    for child in data.getroot():
        if child.tag == "q":
            q = {}
            q[ETQ_ID] = child.attrib[ETQ_ID]
            q[ETQ_TEXT] = child.attrib[ETQ_TEXT]
            q[ETQ_SUMMARY] = child.attrib[ETQ_SUMMARY]
            toret.append(q)
        ...
    ...
    
    return toret
...


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("[ERR] usage: xmldump <filename>")
        print("    [NFO] e.g.: xmldump workfile.xml")
        sys.exit(-1)
    ...
    
    print("Parsing & generating...")
    fn = sys.argv[1]
    xml_data = ET.parse(fn)
    tags_values = extract_from_xml(xml_data)

    print("Writing...")
    with open(fn + ".csv", "wt") as f:
        f.write("\"" + ETQ_ID
                + ",\"" + ETQ_SUMMARY
                + "\",\"" + ETQ_TEXT
                + "\"\n")
        for value in tags_values:
            f.write(value[ETQ_ID]
                    + ",\"" + value[ETQ_SUMMARY]
                    + "\",\"" + value[ETQ_TEXT].replace("\n", "\\n")
                    + "\"\n")
        ...
    ...
...
