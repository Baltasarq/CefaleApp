#! /usr/bin/env python
# xml2html (c) 2024 Baltasar MIT License <baltasarq@gmail.com>


import xml.dom.minidom as xmlp


def htmlFromXml(
            nf: str,
            id_tag: str,
            entity_names: list[str],
            hide_tags: list[str],
            sust_tags: dict[str, str]) -> str:
                
    def sust_tag(tag):
        return sust_tags.get(tag) or tag
    ...
        
    document = xmlp.parse(nf)
    toret = "<!DOCType html><html><body>"
    for entity_name in entity_names:
        elements = document.getElementsByTagName(entity_name)
        toret += f"<div id='dvContents'><details><summary>{sust_tag(entity_name)}</summary><ul>"
    
        for element in elements:
            element_tag = sust_tag(element.tagName)
            toret += f"<details><summary>{element_tag}"
            
            # Attributes
            for attr_key in element.attributes.keys():
                tag = attr_key
                if tag not in hide_tags:
                    tag = sust_tag(tag)
                    value = element.attributes[attr_key].value
                    
                    if tag == id_tag:
                        toret += f"&nbsp;<b><a id='{value}'>{tag}</a></b>: \
                                    <a href='#{value}'>{value}</a>\
                                    </li>"
                    else:
                        toret += f"&nbsp;<b>{tag}</b>: \
                                    <a href='#{value}'>{value}</a>\
                                    </li>"
                    ...
                ...
            ...
            
            toret += "</summary>"
            
            # Subelements
            if element.childNodes:
                toret += "<ul>"
                
                for sub_node in element.childNodes:
                    if isinstance(sub_node, xmlp.Text):
                        continue;
                    ...
                    
                    tag = sub_node.tagName
                    
                    if tag not in hide_tags:
                        tag = sust_tag(tag)
                        toret += f"<li><details><summary>{tag}</summary><p>{sub_node.firstChild.wholeText}</p></li>"
                    ...
                ...
                
                toret += "</ul>"
            ...
            
            toret += "</details>"
        ...
        
        toret += "</details>"
    ...
    
    toret += "</ul></details></div>\n"
    toret += "<script>window.onload = (evt) => {\n\
                document.body.querySelectorAll('details')\
                .forEach((e) => e.setAttribute('open',true));\n\
                };\n</script>\n"
    toret += "\n</body></html>"
    return toret
...


if __name__ == "__main__":
    import sys
    
    if len(sys.argv) < 3:
        print("Missing parameter xml file and entity.")
        print("USE: xml2html car_dealer.xml car")
    else:
        entities = []
        for entity_name in sys.argv[2:]:
            entities.append(entity_name)
        ...
        
        susts = {
            "medicine": "medicina",
            "name": "nombre",
            "groupId": "grpId",
            "medicineGroup": "grupo de medicinas",
            "medicineClass": "clase de medicinas",
            "morbidity": "comorbilidad"
        }
        
        hide = [
            "posInGroup",
            "url"
        ]
        
        print(str(htmlFromXml(sys.argv[1], "id", entities, hide, susts)))
    ...
...
