# XML converter (c) 2025 Baltasar MIT License <jbgarcia@uvigo.es>
# This is part of the CefaleApp project.
# Its purpose is to convert XML for test questions
# from version 1 (just Spanish) to version 2 (multilingual).

"""
<q
   id="q1"
   gotoId="q2"
   type="int"
   summary="age"
   text="How old are you?" />
   
# This is converted to:

 <q
   id="q1"
   gotoId="q2"
   type="int">
   
   <summary>
       <text lang="es">Edad</text>
       <text lang="en">Age</text>
       <text lang="pt">Idade</text>
   </summary>

   <contents>
       <text lang="es">¿Cuántos años tienes?</text>
       <text lang="en">How old are you?</text>
       <text lang="pt">Quantos anos tem</text>
   </contents>
</q>
"""


import sys
import xml.etree.ElementTree as ET


def transform_text(q_node, sub_node, tag):
    """Converts a given node from Spanish only to multilingual.
        :param q_node: the question "q" node.
        :param sub_node: the new subnode to be filled, e.g. "contents"
        :param tag: the tag to extract from q's attributes. e.g. "text"
    """
    text_es = ET.SubElement(sub_node, "text", attrib={"lang":"es"})
    text_en = ET.SubElement(sub_node, "text", attrib={"lang":"en"})
    text_pt = ET.SubElement(sub_node, "text", attrib={"lang":"pt"})

    contents = q_node.attrib[tag]
    text_es.text = contents
    text_en.text = "In English!!!"
    text_pt.text = "Em Portugués!!!"

    if (contents.lower()[:2] == "si"
     or contents.lower()[:2] == "sí"):
        text_en.text = "Yes."
        text_pt.text = "Sím."
    ...

    if contents.lower()[:2] == "no":
        text_en.text = "No."
        text_pt.text = "Non."
    ...
...


def xml12xml2(data):
    """Converts the whole XML from Spanish only to multilingual.
        :param data: the data in ElementTree objects.
        :return: the data converted, as ElementTree objects.
    """
    toret = ET.Element("qs")
    
    for child in data.getroot():
        if child.tag == "q":
            q_node = ET.SubElement(toret, "q")
            q_node.attrib["id"] = child.attrib["id"]
            q_node.attrib["goto"] = child.attrib["goto"]
            q_node.attrib["type"] = child.attrib["type"]

            # Summary
            summary = ET.SubElement(q_node, "summary")
            transform_text(child, summary, "summary")
            
            # Text
            contents = ET.SubElement(q_node, "contents")
            transform_text(child, contents, "text")
            
            for sub_opt in child:
                if sub_opt.tag == "opt":
                    opt = ET.SubElement(q_node, "opt")
                    opt.attrib["value"] = sub_opt.attrib["value"]
                    transform_text(sub_opt, opt, "text")
                ...
            ...
        ...
    ...
    
    return ET.ElementTree(toret)
...


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("[ERR] missing file name.")
        sys.exit(-1)
    ...
    
    print("Parsing & converting...")
    fn = sys.argv[1]
    xml_data = ET.parse(fn)
    xml_data2 = xml12xml2(xml_data)

    print("Writing...")
    ET.indent(xml_data2, space="    ", level=0) 
    with open(fn + ".xml2.xml", "wt") as f:
        xml_data2.write(f, encoding="unicode")
    ...
...
