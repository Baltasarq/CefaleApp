// CefaleApp (c) 2023/24 Baltasar MIT License <baltasarq@uvigo.es>


package com.devbaltasarq.cefaleapp.core.treatment;


import androidx.annotation.NonNull;

import com.devbaltasarq.cefaleapp.core.Language;
import com.devbaltasarq.cefaleapp.core.LocalizedText;
import com.devbaltasarq.cefaleapp.core.XMLToolBox;
import com.devbaltasarq.cefaleapp.core.treatment.advisor.TreatmentMessage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/** XML Loader for all medicines. */
public class TreatmentXMLoader {
    private static final String ETQ_MEDICINE = "medicine";
    private static final String ETQ_MEDICINE_GROUP = "medicineGroup";
    private static final String ETQ_MEDICINE_CLASS = "medicineClass";
    private static final String ETQ_ID = "id";
    private static final String ETQ_CLASS = "classId";
    private static final String ETQ_NAME = "name";
    private static final String ETQ_POS_IN_GROUP = "posInGroup";
    private static final String ETQ_GROUP_ID = "groupId";
    private static final String ETQ_URL = "url";
    private static final String ETQ_ADVERSE_EFFECTS = "adverseEffects";
    private static final String ETQ_POSOLOGY = "posology";
    private static final String ETQ_MORBIDITY = "morbidity";
    private static final String ETQ_MORBIDITIES = "morbidities";
    private static final String ETQ_DESC = "desc";
    private static final String ETQ_MEDICAL_TREATMENT = "medicalTreatment";
    private static final String ETQ_ADVISED_GROUP = "advisedGroup";
    private static final String ETQ_ADVISED = "advised";
    private static final String ETQ_INCOMPATIBLE_GROUP = "incompatibleGroup";
    private static final String ETQ_INCOMPATIBLE = "incompatible";
    private static final String ETQ_TREATMENT_MSG = "treatmentMessage";

    private TreatmentXMLoader(Map<Medicine.Id, Medicine> medicines,
                              Map<MedicineGroup.Id, MedicineGroup> medicineGroups,
                              Map<MedicineClass.Id, MedicineClass> medicineClasses,
                              Map<Morbidity.Id, Morbidity> morbidities,
                              Map<TreatmentMessage.Id, TreatmentMessage> messages)
    {
        this.medicines = medicines;
        this.morbidities = morbidities;
        this.medicineGroups = medicineGroups;
        this.medicineClasses = medicineClasses;
        this.treatmentMessages = messages;
    }

    /** @return all the loaded medicines, as a map of Id, Medicine. */
    public Map<Medicine.Id, Medicine> getMedicines()
    {
        return this.medicines;
    }

    /** @return all the loaded medicine groups, as a map of Id, MedicineGroup. */
    public Map<MedicineGroup.Id, MedicineGroup> getMedicineGroups()
    {
        return this.medicineGroups;
    }

    /** @return all the loaded medicine classes, as a map of Id, MedicineClass. */
    public Map<MedicineClass.Id, MedicineClass> getMedicineClasses()
    {
        return this.medicineClasses;
    }

    /** @return all the loaded morbidities, as a map of Id, Morbidity. */
    public Map<Morbidity.Id, Morbidity> getMorbididties()
    {
        return this.morbidities;
    }

    public Map<TreatmentMessage.Id, TreatmentMessage> getTreatmentMessages()
    {
        return this.treatmentMessages;
    }

    public static TreatmentXMLoader loadFromXML(InputStream in)
            throws IOException
    {
        TreatmentXMLoader toret = null;

        try {
            final DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
            final DocumentBuilder DB = DBF.newDocumentBuilder();
            final Document DOM = DB.parse( in );
            final Element DOC = DOM.getDocumentElement();
            final Map<MedicineClass.Id, MedicineClass> MEDICINE_CLASSES =
                                            loadMedicineClassesFromXML( DOC );
            final Map<MedicineGroup.Id, MedicineGroup> MEDICINE_GROUPS =
                                            loadMedicineGroupsFromXML( DOC );
            final Map<Medicine.Id, Medicine> MEDICINES = loadMedicinesFromXML( DOC );
            final Map<Morbidity.Id, Morbidity> MORBIDITIES = loadMorbiditiesFromXML( DOC );
            final Map<TreatmentMessage.Id, TreatmentMessage> MESSAGES = loadTreatmentMessages( DOC );

            assignMedicineGroupsToClasses( MEDICINE_GROUPS, MEDICINE_CLASSES );
            assignMedicinesToMedicineGroups( MEDICINES, MEDICINE_GROUPS );

            toret = new TreatmentXMLoader(
                                    MEDICINES,
                                    MEDICINE_GROUPS,
                                    MEDICINE_CLASSES,
                                    MORBIDITIES,
                                    MESSAGES );
        } catch(ParserConfigurationException | SAXException exc)
        {
            throw new IOException( exc.getMessage() );
        }

        return toret;
    }

    private static void assignMedicineGroupsToClasses(
            final Map<MedicineGroup.Id, MedicineGroup> MEDICINE_GROUPS,
            final Map<MedicineClass.Id, MedicineClass> MEDICINE_CLASSES)
    {
        for(final MedicineGroup GROUP: MEDICINE_GROUPS.values()) {
            final MedicineClass.Id CLS_ID = GROUP.getClsId();
            final MedicineClass CLS = MEDICINE_CLASSES.get( CLS_ID );

            if ( CLS_ID != null
              && CLS != null )
            {
                CLS.insert( GROUP );
            } else {
                throw new Error( "Loading groups in classes "
                                + "missing class/class id: " + CLS_ID );
            }
        }

        return;
    }

    private static void
        assignMedicinesToMedicineGroups(
            final Map<Medicine.Id, Medicine> MEDICINES,
            final Map<MedicineGroup.Id, MedicineGroup> MEDICINE_GROUPS)
    {
        for(final Medicine MEDICINE: MEDICINES.values()) {
            final MedicineGroup.Id GROUP_ID = MEDICINE.getGroupId();
            final MedicineGroup MEDICINE_GROUP = MEDICINE_GROUPS.get( GROUP_ID );

            if ( GROUP_ID != null
              && MEDICINE_GROUP != null )
            {
                MEDICINE_GROUP.insert( MEDICINE.getGroupPos(), MEDICINE );
            } else {
                throw new Error(
                        "loading medicines in groups, missing group/groupId: "
                        + GROUP_ID );
            }
        }

        return;
    }

    /** Loads all medicine groups from XML.
      * @param DOC the root element.
      * @return a map in which the keys are MedicineGroup.Id's,
      *         and values are the medicines groups themselves.
      */
    @NonNull
    private static Map<MedicineGroup.Id, MedicineGroup>
        loadMedicineGroupsFromXML(final Element DOC)
        throws IOException
    {
        final Map<MedicineGroup.Id, MedicineGroup> TORET = new HashMap<>( 10 );

        // Load from XML
        final NodeList MEDICINE_GROUP_NODES = DOC.getElementsByTagName( ETQ_MEDICINE_GROUP );

        for(int i = 0; i < MEDICINE_GROUP_NODES.getLength(); ++i) {
            final MedicineGroup MEDICINE_GROUP =
                    loadMedicineGroupFromXML(
                            (Element) MEDICINE_GROUP_NODES.item( i ) );

            TORET.put( MEDICINE_GROUP.getId(), MEDICINE_GROUP );
        }

        return TORET;
    }

    /** Loads all medicine groups from XML.
     * @param DOC the root element.
     * @return a map in which the keys are MedicineGroup.Id's,
     *         and values are the medicines groups themselves.
     */
    @NonNull
    private static Map<MedicineClass.Id, MedicineClass>
        loadMedicineClassesFromXML(final Element DOC)
        throws IOException
    {
        final Map<MedicineClass.Id, MedicineClass> TORET = new HashMap<>( 10 );

        // Load from XML
        final NodeList MEDICINE_CLASS_NODES = DOC.getElementsByTagName( ETQ_MEDICINE_CLASS );

        for(int i = 0; i < MEDICINE_CLASS_NODES.getLength(); ++i) {
            final MedicineClass MEDICINE_CLASS =
                    loadMedicineClassFromXML(
                            (Element) MEDICINE_CLASS_NODES.item( i ) );

            TORET.put( MEDICINE_CLASS.getId(), MEDICINE_CLASS );
        }

        return TORET;
    }

    /** Loads all medicines from XML.
      * @param DOC the root element.
      * @return a map in which the keys are Medicine.Id's, and values are the medicines themselves.
      */
    @NonNull
    private static Map<Medicine.Id, Medicine>
        loadMedicinesFromXML(final Element DOC)
        throws IOException
    {
        final Map<Medicine.Id, Medicine> TORET = new HashMap<>( 10 );

        // Load medicines from XML
        final NodeList MEDICINE_NODES = DOC.getElementsByTagName( ETQ_MEDICINE );

        for(int i = 0; i < MEDICINE_NODES.getLength(); ++i) {
            final Medicine MEDICINE = loadMedicineFromXML( (Element) MEDICINE_NODES.item( i ) );

            TORET.put( MEDICINE.getId(), MEDICINE );
        }

        return TORET;
    }

    /** Loads a single medicine info.
      * @param medicineElement The element to read the medicine info from;
     *                         It should have a tag "medicine"
      * @return a Medicine object.
      * @throws IOException if something goes wrong.
      */
    private static Medicine
        loadMedicineFromXML(Element medicineElement)
        throws IOException
    {
        final String ID = XMLToolBox.getXMLAttributeOrThrow( medicineElement, ETQ_ID );
        final String NAME = XMLToolBox.getXMLAttributeOrThrow( medicineElement, ETQ_NAME );
        final String GROUP_ID = XMLToolBox.getXMLAttributeOrThrow( medicineElement, ETQ_GROUP_ID );
        final String STR_POS_IN_GROUP = XMLToolBox.getXMLAttributeOrThrow( medicineElement, ETQ_POS_IN_GROUP );
        final String URL = XMLToolBox.getXMLAttributeOrThrow( medicineElement, ETQ_URL );
        final Element ELEM_ADVERSE_EFFECTS =
                XMLToolBox.getXMLSubElement( medicineElement, ETQ_ADVERSE_EFFECTS );
        final Element ELEM_POSOLOGY =
                XMLToolBox.getXMLSubElement( medicineElement, ETQ_POSOLOGY );
        int posInGroup = Integer.MIN_VALUE;

        // Adverse effects
        if ( ELEM_ADVERSE_EFFECTS == null
          || ELEM_POSOLOGY == null )
        {
            throw new Error( "loading XML medicine: missing posology or adverse effects info." );
        }

        final String ADVERSE_EFFECTS = formatMultilineValue(
                        ELEM_ADVERSE_EFFECTS.getFirstChild().getNodeValue() );

        final String POSOLOGY = formatMultilineValue(
                ELEM_POSOLOGY.getFirstChild().getNodeValue() );

        if ( ADVERSE_EFFECTS.isEmpty()
          || POSOLOGY.isEmpty() )
        {
            throw new Error( "loading XML medicine: empty posology or adverse effects info." );
        }

        try {
            posInGroup = Integer.parseInt( STR_POS_IN_GROUP );
        } catch(NumberFormatException exc) {
            throw new Error(
                    "loading XML medicine: invalid pos in group: "
                    + STR_POS_IN_GROUP );
        }

        return new Medicine(
                        new Medicine.Id( ID, NAME ),
                        MedicineGroup.Id.get( GROUP_ID ),
                        posInGroup,
                        ADVERSE_EFFECTS,
                        POSOLOGY,
                        URL );
    }

    /** Loads a single medicine class info.
     * @param medicineClassElement The element to read the medicine class info from;
     *                         It should have a tag "medicineClass"
     * @return a MedicineClass object.
     * @throws IOException if something goes wrong.
     */
    private static MedicineClass
        loadMedicineClassFromXML(Element medicineClassElement)
        throws IOException
    {
        final String ID = XMLToolBox.getXMLAttributeOrThrow(
                medicineClassElement, ETQ_ID );
        final String NAME = XMLToolBox.getXMLAttributeOrThrow(
                medicineClassElement, ETQ_NAME );

        return new MedicineClass( new MedicineClass.Id( ID, NAME ) );
    }

    /** Loads a single medicine group info.
     * @param medicineGroupElement The element to read the medicine group info from;
     *                         It should have a tag "medicineGroup"
     * @return a MedicineGroup object.
     * @throws IOException if something goes wrong.
     */
    private static MedicineGroup
        loadMedicineGroupFromXML(Element medicineGroupElement)
        throws IOException
    {
        final String ID = XMLToolBox.getXMLAttributeOrThrow(
                                    medicineGroupElement, ETQ_ID );
        final String CLS_ID = XMLToolBox.getXMLAttributeOrThrow(
                                    medicineGroupElement, ETQ_CLASS );
        final String NAME = XMLToolBox.getXMLAttributeOrThrow(
                                    medicineGroupElement, ETQ_NAME );

        return new MedicineGroup(
                        new MedicineGroup.Id( ID, NAME ),
                        new MedicineClass.Id( CLS_ID, NAME ));
    }

    private static String formatMultilineValue(String adverseEffects)
    {
        final StringBuilder TORET = new StringBuilder( adverseEffects.length() );
        String[] lines = adverseEffects.split( "\n" );

        for(String line: lines) {
            TORET.append( line.trim() );
            TORET.append( ' ' );
        }

        return TORET.toString().trim();
    }

    @NonNull
    private static Map<Morbidity.Id, Morbidity>
        loadMorbiditiesFromXML(final Element DOC)
        throws IOException
    {
        final Map<Morbidity.Id, Morbidity> TORET = new HashMap<>( 10 );

        // Load medicines from XML
        final NodeList MORBIDITIES_NODE = DOC.getElementsByTagName( ETQ_MORBIDITIES );

        if ( MORBIDITIES_NODE.getLength() < 1 ) {
            throw new Error( "XML: no morbidities!" );
        }

        final Element MORBIDITIES_ELEMENT = (Element) MORBIDITIES_NODE.item( 0 );
        final NodeList MORBIDITY_NODES = MORBIDITIES_ELEMENT.getElementsByTagName( ETQ_MORBIDITY );

        for(int i = 0; i < MORBIDITY_NODES.getLength(); ++i) {
            final Morbidity MORBIDITY = loadMorbidityFromXML( (Element) MORBIDITY_NODES.item( i ) );

            TORET.put( MORBIDITY.getId(), MORBIDITY );
        }

        return TORET;
    }

    @NonNull
    private static Morbidity
        loadMorbidityFromXML(final Element ELEMENT)
        throws IOException
    {
        final String ID = XMLToolBox.getXMLAttributeOrThrow( ELEMENT, ETQ_ID );
        final String NAME = XMLToolBox.getXMLAttributeOrThrow( ELEMENT, ETQ_NAME );
        final String DESC = XMLToolBox.getXMLAttributeOrThrow( ELEMENT, ETQ_DESC );
        final Morbidity MORBIDITY = new Morbidity(
                                        new Morbidity.Id( ID, NAME ),
                                        formatMultilineValue( DESC ) );
        final NodeList NODE_TREATMENT = ELEMENT.getElementsByTagName( ETQ_MEDICAL_TREATMENT );

        if ( NODE_TREATMENT.getLength() > 0 ) {
            loadMedicalTreatmentFromXML( (Element) NODE_TREATMENT.item( 0 ), MORBIDITY );
        }

        return MORBIDITY;
    }

    private static void
        loadMedicalTreatmentFromXML(
            final Element ELEMENT,
            final Morbidity MORBIDITY)
    {
        final NodeList NODES_ADVISED_MEDICINES =
                            ELEMENT.getElementsByTagName( ETQ_ADVISED );
        final NodeList NODES_ADVISED_MEDICINE_GROUPS =
                            ELEMENT.getElementsByTagName( ETQ_ADVISED_GROUP );
        final NodeList NODES_INCOMPATIBLE_MEDICINES =
                            ELEMENT.getElementsByTagName( ETQ_INCOMPATIBLE );
        final NodeList NODES_INCOMPATIBLE_MEDICINE_GROUPS =
                            ELEMENT.getElementsByTagName( ETQ_INCOMPATIBLE_GROUP );
        final ArrayList<Medicine.Id> ADVISED_MEDICINES =
                            new ArrayList<>( NODES_ADVISED_MEDICINES.getLength() );
        final ArrayList<Medicine.Id> INCOMPATIBLE_MEDICINES =
                            new ArrayList<>( NODES_INCOMPATIBLE_MEDICINES.getLength() );
        final ArrayList<MedicineGroup.Id> ADVISED_MEDICINE_GROUPS =
                            new ArrayList<>( NODES_ADVISED_MEDICINE_GROUPS.getLength() );
        final ArrayList<MedicineGroup.Id> INCOMPATIBLE_MEDICINE_GROUPS =
                            new ArrayList<>( NODES_INCOMPATIBLE_MEDICINE_GROUPS.getLength() );

        // Set the advised medicines
        for(int i = 0; i < NODES_ADVISED_MEDICINES.getLength(); ++i) {
            final Element EAM = (Element) NODES_ADVISED_MEDICINES.item( i );
            final String STR_MEDICINE_ID = EAM.getFirstChild().getNodeValue();
            ADVISED_MEDICINES.add( Medicine.Id.get( STR_MEDICINE_ID ) );
        }

        MORBIDITY.setAdvisedMedicines( ADVISED_MEDICINES );

        // Set the advised medicine groups
        for(int i = 0; i < NODES_ADVISED_MEDICINE_GROUPS.getLength(); ++i) {
            final Element EAMG = (Element) NODES_ADVISED_MEDICINE_GROUPS.item( i );
            final String STR_GROUP_ID = EAMG.getFirstChild().getNodeValue();
            ADVISED_MEDICINE_GROUPS.add( MedicineGroup.Id.get( STR_GROUP_ID ) );
        }

        MORBIDITY.setAdvisedMedicineGroups( ADVISED_MEDICINE_GROUPS );

        // Set the incompatible medicines
        for(int i = 0; i < NODES_INCOMPATIBLE_MEDICINES.getLength(); ++i) {
            final Element EIM = (Element) NODES_INCOMPATIBLE_MEDICINES.item( i );
            final String STR_MEDICINE_ID = EIM.getFirstChild().getNodeValue();
            INCOMPATIBLE_MEDICINES.add( Medicine.Id.get( STR_MEDICINE_ID ) );
        }

        MORBIDITY.setIncompatibleMedicines( INCOMPATIBLE_MEDICINES );

        // Set the incompatible medicine groups
        for(int i = 0; i < NODES_INCOMPATIBLE_MEDICINE_GROUPS.getLength(); ++i) {
            final Element IMG = (Element) NODES_INCOMPATIBLE_MEDICINE_GROUPS.item( i );
            final String STR_GROUP_ID = IMG.getFirstChild().getNodeValue();
            INCOMPATIBLE_MEDICINE_GROUPS.add( MedicineGroup.Id.get( STR_GROUP_ID ) );
        }

        MORBIDITY.setIncompatibleMedicineGroups( INCOMPATIBLE_MEDICINE_GROUPS );
    }

    public static Map<TreatmentMessage.Id, TreatmentMessage>
        loadTreatmentMessages(final Element ELEMENT)
        throws IOException
    {
        final Map<TreatmentMessage.Id, TreatmentMessage> TORET = new HashMap<>( 5 );
        final NodeList MESSAGE_NODES = ELEMENT.getElementsByTagName( ETQ_TREATMENT_MSG );

        for(int i = 0; i < MESSAGE_NODES.getLength(); ++i) {
            final TreatmentMessage MSG = loadTreatmentMessage( (Element) MESSAGE_NODES.item( i ) );

            TORET.put( MSG.getId(), MSG );
        }

        return TORET;
    }

    private static TreatmentMessage
        loadTreatmentMessage(final Element MSG_NODE)
        throws IOException
    {
        final String ID = XMLToolBox.getXMLAttributeOrThrow( MSG_NODE, ETQ_ID );
        //final LocalizedText L10N_MESSAGE = XMLToolBox.readXMLL10nText( MSG_NODE );
        String msg = MSG_NODE.getFirstChild().getNodeValue();
        LocalizedText lt = new LocalizedText();
        lt.add( Language.es, msg );

        return new TreatmentMessage( new TreatmentMessage.Id( ID ), lt /*L10N_MESSAGE*/ );
    }

    private final Map<Medicine.Id, Medicine> medicines;
    private final Map<Morbidity.Id, Morbidity> morbidities;
    private final Map<MedicineGroup.Id, MedicineGroup> medicineGroups;
    private final Map<MedicineClass.Id, MedicineClass> medicineClasses;
    private final Map<TreatmentMessage.Id, TreatmentMessage> treatmentMessages;
}
