# XML converter (c) 2025 Baltasar MIT License <jbgarcia@uvigo.es>
# This is part of the CefaleApp project.
# Its purpose is to convert XML for medicines
# from version 1 (just Spanish) to version 2 (multilingual).

"""
<medicine
    id="ACIDO_VALPROICO"
    name="Ácido valproico"
    groupId="E"
    posInGroup="1"
    url="https://www.vademecum.es/medicamento-acido+valproico+aurovitas_ficha_49201">
    <posology>
        Dosis mínima 300 mg/día; dosis recomendada 600 mg/día.
    </posology>
    <adverseEffects>
        En mujeres en edad fértil, por sus efectos teratogénicos y
        posibilidad de autismo, debe tomarse obligatoriamente
        con anticonceptivos seguros.
        Somnolencia, cansancio, irritabilidad, inquietud durante el sueño,
        ataxia, temblor distal, trastornos gastrointestinales,
        aumento de peso, pérdida de cabello, elevación de transaminasas,
        trombocitopenia.
    </adverseEffects>
</medicine>

To:

<medicine
    id="ACIDO_VALPROICO"
    groupId="E"
    posInGroup="1"
    url="https://www.vademecum.es/medicamento-acido+valproico+aurovitas_ficha_49201">

    <name>
        <text lang="es">...</text>
    </name>
    <posology>
        <text lang="es">
            Dosis mínima 300 mg/día; dosis recomendada 600 mg/día.
        </text>
    </posology>
    <adverseEffects>
        <text lang="es">
            En mujeres en edad fértil, por sus efectos teratogénicos y
            posibilidad de autismo, debe tomarse obligatoriamente
            con anticonceptivos seguros.
            Somnolencia, cansancio, irritabilidad, inquietud durante el sueño,
            ataxia, temblor distal, trastornos gastrointestinales,
            aumento de peso, pérdida de cabello, elevación de transaminasas,
            trombocitopenia.
        </text>
    </adverseEffects>
</medicine>
"""


import sys
import xml.etree.ElementTree as ET


def _transform_text(q_node, sub_node, tag, get_contents):
    """Converts a given node from Spanish only to multilingual.
        :param q_node: the question "q" node.
        :param sub_node: the new subnode to be filled, e.g. "contents"
        :param tag: the tag to extract from q, e.g. "text"
        :param get_contents: a lambda to extract the "tag" from q_node
    """
    text_es = ET.SubElement(sub_node, "text", attrib={"lang":"es"})
    text_en = ET.SubElement(sub_node, "text", attrib={"lang":"en"})
    text_pt = ET.SubElement(sub_node, "text", attrib={"lang":"pt"})

    contents = get_contents(q_node, tag)

    if contents is not None:
        text_es.text = contents
        text_en.text = "In English!!!"
        text_pt.text = "Em Portugués!!!"
...



def transform_text_from_attrib(q_node, sub_node, tag):
    """Converts a given node from Spanish only to multilingual.
        :param q_node: the question "q" node.
        :param sub_node: the new subnode to be filled, e.g. "contents"
        :param tag: the tag to extract from q's attributes. e.g. "text"
    """
    return _transform_text(
                q_node,
                sub_node,
                tag,
                lambda node, tag: node.attrib[tag])

                
def transform_text_from_subtag(q_node, sub_node, tag):
    """Converts a given node from Spanish only to multilingual.
        :param q_node: the question "q" node.
        :param sub_node: the new subnode to be filled, e.g. "contents"
        :param tag: the tag to extract from q's attributes. e.g. "text"
    """
    return _transform_text(
                q_node,
                sub_node,
                tag,
                lambda node, tag: node.find(tag).text)
...


def xml12xml2(data):
    """Converts the whole XML from Spanish only to multilingual.
        :param data: the data in ElementTree objects.
        :return: the data converted, as ElementTree objects.
    """
    toret = ET.Element("medicines")
    
    for child in data.getroot():
        if child.tag == "medicine":
            q_node = ET.SubElement(toret, "medicine")
            q_node.attrib["id"] = child.attrib["id"]
            q_node.attrib["groupId"] = child.attrib["groupId"]
            q_node.attrib["posInGroup"] = child.attrib["posInGroup"]
            q_node.attrib["url"] = child.attrib["url"]

            # Name
            name_node = ET.SubElement(q_node, "name")
            transform_text_from_attrib(child, name_node, "name")
            
            # Text
            posology_node = ET.SubElement(q_node, "posology")
            transform_text_from_subtag(child, posology_node, "posology")

            # AdverseEffects
            adverse_effects_node = ET.SubElement(q_node, "adverseEffects")
            transform_text_from_subtag(child, adverse_effects_node, "adverseEffects")
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
