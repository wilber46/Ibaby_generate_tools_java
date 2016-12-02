package cn.xrrjkj.mixtool;

import jdk.internal.org.xml.sax.InputSource;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by user05 on 11/16/2016.
 */
public class MixDomTool {
    public static final int TYPE_CONCEPT_REFS = 0;
    public static final int TYPE_INTENT = 1;

    public static final String NODE_PROJECT = "project";
    public static final String NODE_ONTOLOGY = "ontology";
    public static final String NODE_INTENTS = "intents";
    public static final String NODE_INTENT = "intent";
    public static final String NODE_LINKS = "links";
    public static final String NODE_LINK = "link";
    public static final String NODE_CONCEPTS = "concepts";
    public static final String NODE_CONCEPT = "concept";
    public static final String NODE_DICTIONARIES = "dictionaries";
    public static final String NODE_DICTIONARY = "dictionary";
    public static final String NODE_ENTRY = "entry";
    public static final String NODE_SAMPLES = "samples";
    public static final String NODE_SAMPLE = "sample";
    public static final String NODE_INTENT_REF = "intentref";
    public static final String NODE_COUNT = "count";
    public static final String NODE_LITERAL = "literal";
    public static final String NODE_VALUE = "value";
    public static final String NODE_ANNOTATION = "annotation";
    public static final String NODE_CONCEPT_REF = "conceptref";
    public static final String NODE_NAME = "name";
    public static final String NODE_BASE = "base";
    public static final String NODE_XMLNS_NUANCE = "xmlns:nuance";
    public static final String NODE_XML_LANG = "xml:lang";
    public static final String NODE_NUANCE_VERSION = "nuance:version";
    public static final String NODE_SETTINGS = "settings";
    public static final String NODE_SETTING = "setting";
    public static final String NODE_RELATIONS = "relations";
    public static final String NODE_RELATION = "relation";
    public static final String NODE_FREE_TEXT = "freetext";
    public static final String NODE_TYPE = "type";

    public static final String TEXT_NUANCE_VERSION = "2.0";
    public static final String TEXT_XMLNS_NUANCE = "https://developer.nuance.com/mix/nlu/trsx";
    public static final String TEXT_XML_LANG = "en-us";
    public static final String TEXT_BASE = "http://developer.nuance.com/mix/nlu/trsx/ontology-1.0";

    public static final List<String> NUANCE_CONCEPTS = new ArrayList<String >(
            Arrays.asList(new String[]{
                    "nuance_AMOUNT",
                    "nuance_BOOLEAN",
                    "nuance_CALENDARX",
                    "nuance_CARDINAL_NUMBER",
                    "nuance_DOUBLE",
                    "nuance_DURATION",
                    "nuance_DURATION_RANGE",
                    "nuance_GENERIC_ORDER",
                    "nuance_GLOBAL",
                    "nuance_NUMBER",
                    "nuance_ORDINAL_NUMBER",
                    "nuance_QUANTITY",
                    "nuance_TEMPERATURE"
            })
    ) ;

    boolean isValidParams(String[] args) {
        if(null == args) {
            System.out.println("params must be not null!");
            return false;
        }

        List<String> params = Arrays.asList(args);
        if(params.contains("-h") || params.contains("-help")) {
            printHelpInfo();
            return false;
        }

        if(!params.contains("-i") || !params.contains("-d") || !params.contains("-s") || !params.contains("-o")) {
            System.out.println("param incorrect!must pass -i -d -s -o 4 params");
            printHelpInfo();
            return false;
        }


        int index = -1;
        index = params.indexOf("-i");
        if(index >= 0 && index + 1 < params.size()) {
            pathIntent = params.get(index + 1);
        } else {
            System.out.println("not found intent file");
            printHelpInfo();
            return false;
        }

        index = params.indexOf("-d");
        if(index >= 0 && index + 1 < params.size()) {
            pathDictionary = params.get(index + 1);
        } else {
            System.out.println("not found dictionary file");
            printHelpInfo();
            return false;
        }

        index = params.indexOf("-s");
        if(index >= 0 && index + 1 < params.size()) {
            pathSample = params.get(index + 1);
        } else {
            System.out.println("not found sample file");
            printHelpInfo();
            return false;
        }

        index = params.indexOf("-o");
        if(index >=0 && index + 1 < params.size()) {
            pathOutput = params.get(index + 1);
        } else {
            System.out.println("no output path param");
            printHelpInfo();
            return false;
        }

        return true;
    }

    private Document document;
    private String pathIntent;
    private String pathDictionary;
    private String pathSample;
    private String pathOutput;

    public void setPathIntent(String pathIntent) {
        this.pathIntent = pathIntent;
    }

    public void setPathDictionary(String pathDictionary) {
        this.pathDictionary = pathDictionary;
    }

    public void setPathSample(String pathSample) {
        this.pathSample = pathSample;
    }

    public void setPathOutput(String pathOutput) {
        this.pathOutput = pathOutput;
    }

    void start() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM:hh:mm:sss");
        System.out.println("====== start time " + sdf.format(new Date()) + " =============");
        initDocment();
        System.out.println("====== end time " + sdf.format(new Date()) + " ===============");
    }

    void initDocment() throws Exception{
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            this.document = documentBuilder.newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Element project = document.createElement(NODE_PROJECT);
        project.setAttribute(NODE_XMLNS_NUANCE,TEXT_XMLNS_NUANCE);
        project.setAttribute(NODE_XML_LANG,TEXT_XML_LANG);
        project.setAttribute(NODE_NUANCE_VERSION,TEXT_NUANCE_VERSION);

        project.appendChild(appendOntology());

        project.appendChild(appendDictionaries());

        project.appendChild(appendSamples());

        document.appendChild(project);

        write2File();
    }

    void write2File() throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource domSource = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.METHOD,"xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE,"yes");
        transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS,"yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

//        String path = "D:/ibaby.trsx.xml";
        String path = pathOutput;
//        PrintWriter printWriter = new PrintWriter(new FileOutputStream(path));
//        StreamResult streamResult = new StreamResult(printWriter);
////        StreamResult streamResult = new StreamResult(new FileOutputStream("D:/ibaby.trsx.xml"));
//        transformer.transform(domSource,streamResult);

        StreamResult streamResult1 = new StreamResult(new StringWriter());
        transformer.transform(domSource,streamResult1);

        String content = streamResult1.getWriter().toString().replaceAll("]]>","").replaceAll("\\<\\!\\[CDATA\\[","");

        System.out.println("content is \n" + content);

        PrintWriter printWriter = new PrintWriter(new FileOutputStream(path));
        printWriter.append(content);
        printWriter.flush();
        printWriter.close();

        System.out.println("generate dom file ok!location is " + path);
    }

    Element appendOntology() throws Exception {
        Element ontology = document.createElement(NODE_ONTOLOGY);
        ontology.setAttribute(NODE_BASE,TEXT_BASE);

        Element intents = document.createElement(NODE_INTENTS);

        File file = new File(pathIntent);
        if(!file.exists()) {
            throw new FileNotFoundException("file not exist " + pathIntent);
        }

        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> conceptsList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine()) != null) {
            if(line.startsWith("//")) {
                continue;
            } else if(line.isEmpty()) {
                continue;
            } else {
                lines.add(line);
            }
        }
        br.close();

        int i;
        String s;
        List<String> lastSubItems = new ArrayList<>();
        String lastMasterItem = "";
        Element tmpElement;
        for(i = 0;i < lines.size();i ++) {
            s = lines.get(i);
            if(s.startsWith("#")) {
                s = s.replaceFirst("#","").trim();
                if(!conceptsList.contains(s)) {
                    conceptsList.add(s);
                }

                lastSubItems.add(s.split("#")[0]);
            } else {
                if(i == 0) {
                    lastMasterItem = s;
                    continue;
                }

                tmpElement = appendIntent(lastSubItems,lastMasterItem);
                if(null != tmpElement) {
                    intents.appendChild(tmpElement);
                }

                lastSubItems.clear();
                lastMasterItem = s;
            }
        }

        tmpElement = appendIntent(lastSubItems,lastMasterItem);
        if(null != tmpElement) {
            intents.appendChild(tmpElement);
        }


//        Element tmpIntent;
//        Element tmpLinks;
////        Element lastIntent = null;
//        Element lastLinks = null;
//        if(lines.size() > 0) {
//            int i;
//            String s;
//            for(i = 0;i < lines.size();i ++) {
//                s = lines.get(i);
//                if(s.startsWith("#")) {
//                    s = s.replaceFirst("#","").trim();
//                    if(!conceptsList.contains(s)) {
//                        conceptsList.add(s);
//                    }
//                    if(null != lastLinks) {
//                        lastLinks.appendChild(appendLink(s.split("#")[0]));
//                    }
//                } else {
//                    tmpLinks = document.createElement(NODE_LINKS);
//
//                    tmpIntent = document.createElement(NODE_INTENT);
//                    tmpIntent.setAttribute(NODE_NAME,s.trim());
//
//                    tmpIntent.appendChild(tmpLinks);
//
//                    intents.appendChild(tmpIntent);
//
//                    lastLinks = tmpLinks;
//                    tmpIntent = null;
//                    tmpLinks = null;
//                }
//            }
//        }

        ontology.appendChild(intents);

        Element concepts = document.createElement(NODE_CONCEPTS);

        Element tmpConcept = null;
        for(String ss : conceptsList) {
            // ignore inner nuance_* concept
            if(null != ss && NUANCE_CONCEPTS.contains(ss)) {
                continue;
            }

            tmpConcept = appendConcept(ss);
            if(null != tmpConcept) {
                concepts.appendChild(tmpConcept);
            }
        }

        ontology.appendChild(concepts);

        return ontology;
    }

    Element appendIntent(List<String> items,String s) {
        Element tmpLinks = null;
        if(items.size() > 0) {
            tmpLinks = document.createElement(NODE_LINKS);
            for(String ss : items) {
                tmpLinks.appendChild(appendLink(ss));
            }
        } else {
            tmpLinks = null;
        }

        Element tmpIntent = document.createElement(NODE_INTENT);
        tmpIntent.setAttribute(NODE_NAME,s.trim());
        if(null != tmpLinks) {
            tmpIntent.appendChild(tmpLinks);
        }

        return tmpIntent;
    }

    Element appendConcept(String s) throws Exception {
        String[] contents = s.split("#");
        Element concept = document.createElement(NODE_CONCEPT);

        if(null != contents) {
            if(contents.length == 1) {
                concept.setAttribute(NODE_NAME,contents[0]);
                return concept;
            }

            List<String> relationNodes = new ArrayList<>();
            List<String> settingNodes = new ArrayList<>();

            if(contents.length > 0) {
                concept.setAttribute(NODE_NAME,contents[0]);

                int i;
                String tmpStr,tmpName,tmpValue;
                int tmpIndex;
                for(i = 1;i < contents.length;i ++) {
                    tmpStr = contents[i];
                    tmpIndex = tmpStr.indexOf("(");
                    if(tmpIndex > 0) {
                        tmpName = tmpStr.substring(0,tmpIndex);
                        tmpValue = tmpStr.substring(tmpIndex + 1,tmpStr.length() - 1);

                        if(tmpName.equals(NODE_SETTING)) {
                            settingNodes.add(tmpValue);
                        } else if(tmpName.equals(NODE_RELATION)) {
                            relationNodes.add(tmpValue);
                        } else if(tmpName.equals(NODE_FREE_TEXT)) {
                            concept.setAttribute(NODE_FREE_TEXT,tmpValue);
                        }
                    }
                }

                Element tmpElement = null;
                if(settingNodes.size() > 0) {
                    tmpElement = document.createElement(NODE_SETTINGS);
                    for(String str : settingNodes) {
//                        tmpElement.appendChild(appendSettingOrRelation(true,str));
                        appendSettingOrRelation(tmpElement,true,str);
                    }
                    concept.appendChild(tmpElement);
                }

                if(relationNodes.size() > 0) {
                    tmpElement = document.createElement(NODE_RELATIONS);
                    for (String str : relationNodes) {
//                        tmpElement.appendChild(appendSettingOrRelation(false,str));
                        appendSettingOrRelation(tmpElement,false,str);
                    }
                    concept.appendChild(tmpElement);
                }
        }
        }

        return concept;
    }

    void appendSettingOrRelation(Element parent,boolean settings,String s) throws Exception{
        Element tmpElement = null;
        String[] tmpStrs = s.split(",");

        String[] tmpSubItems;
        if(null != tmpStrs && tmpStrs.length > 0) {
            int i;

            if(settings) {
                for(i = 0;i < tmpStrs.length;i ++) {
                    tmpElement = document.createElement(NODE_SETTING);
                    tmpSubItems = tmpStrs[i].split("=");
                    if(null != tmpSubItems && tmpSubItems.length == 2) {
                        tmpElement.setAttribute(NODE_NAME,tmpSubItems[0]);
                        tmpElement.setAttribute(NODE_VALUE,tmpSubItems[1]);
                        parent.appendChild(tmpElement);
                    }
                }
            } else {
                for(i = 0;i < tmpStrs.length;i ++) {
                    tmpElement = document.createElement(NODE_RELATION);
                    tmpSubItems = tmpStrs[i].split("=");
                    if(null != tmpSubItems && tmpSubItems.length == 2) {
                        tmpElement.setAttribute(NODE_TYPE,tmpSubItems[0]);
                        tmpElement.setAttribute(NODE_CONCEPT_REF,tmpSubItems[1]);
                        parent.appendChild(tmpElement);
                    }
                }
            }
        }
    }

    Element appendDictionaries() throws Exception{
        Element dictionaries = document.createElement(NODE_DICTIONARIES);

        File file = new File(pathDictionary);
        if(!file.exists()) {
            throw new FileNotFoundException("file not exist " + pathDictionary);
        }

        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> entryList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine()) != null) {
            if(line.startsWith("//")) {
                continue;
            } else if(line.isEmpty()) {
                continue;
            } else {
                lines.add(line);
            }
        }
        br.close();

        Element tmpDict;
        Element lastDict = null;
        if(lines.size() > 0) {
            int i;
            String s;
            for(i = 0;i < lines.size();i ++) {
                s = lines.get(i);
                if(s.startsWith("#")) {
                    s = s.replaceFirst("#","").trim();
                    entryList.add(s);
                    if(null != lastDict) {
                        lastDict.appendChild(appendEntry(s));
                    }
                } else {
                    tmpDict = document.createElement(NODE_DICTIONARY);
                    tmpDict.setAttribute(NODE_CONCEPT_REF,s.trim());
                    dictionaries.appendChild(tmpDict);
                    lastDict = tmpDict;
                    tmpDict = null;
                }
            }
        }

        return dictionaries;
    }

    Element appendSamples() throws  Exception {
        File file = new File(pathSample);
        if(!file.exists()) {
            System.out.println("file not exist " + pathSample);
        }

        ArrayList<String> lines = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine()) != null) {
            if(line.startsWith("//")) {
                continue;
            }
            if(line.isEmpty()) {
                continue;
            }
            lines.add(line.trim().replaceAll("###",NODE_CONCEPT_REF).replaceAll("##",NODE_ANNOTATION));
        }
        br.close();

        Element samples = document.createElement(NODE_SAMPLES);

        String tmpIntentRef = "";
        Element tmpSample = null;
        for(String s : lines) {
            if(s.isEmpty()) {
                continue;
            }

            if(s.startsWith("#")) {
                tmpSample = appendSample(tmpIntentRef,s.replaceFirst("#",""));
                if(null != tmpIntentRef) {
                    samples.appendChild(tmpSample);
                }
            } else {
                tmpIntentRef = s;
            }
        }

        return samples;
    }

    Element appendSample(String intentRef,String text) {
        String count = "";
        String content = "";

        if(text.contains("#") && !intentRef.isEmpty()) {
            String[] tmp = text.split("#");
            count = tmp[0];
            try {
                Integer.valueOf(count);
            } catch (NumberFormatException e) {
                count = "1";
            }
            content = tmp[1];

            Element sample = document.createElement(NODE_SAMPLE);
            sample.setAttribute(NODE_INTENT_REF,intentRef);
            sample.setAttribute(NODE_COUNT,count);
            sample.appendChild(document.createCDATASection(content));
//            sample.appendChild(document.createTextNode(content));
            return sample;
        }
        return null;
    }

    Element appendEntry(String name) {
        String literal = name;
        String value = name;

        if(name.contains("#")) {
            String[] tmp = name.split("#");
            literal = tmp[0].trim();
            value = tmp[1].trim();
        }

        Element entries = document.createElement(NODE_ENTRY);
        entries.setAttribute(NODE_LITERAL,literal);
        entries.setAttribute(NODE_VALUE,value);

        return entries;
    }

    Element appendLink(String name) {
        Element link = document.createElement(NODE_LINK);
        link.setAttribute(NODE_CONCEPT_REF,name);

        return link;
    }

    public void printHelpInfo() {
        System.out.println("useage:");
        System.out.println("-h:get help message");
        System.out.println("-i:select intent text file");
        System.out.println("-d:select dictionary text file");
        System.out.println("-s:select sample text file");
        System.out.println("-o:select generate out directory");
        System.out.println("example:\n-i D:/intent.txt -d D:/dictionary.txt -s D:/sample.txt -o D:/output.xml");
    }

    public void printFromIntentFile(int type) throws Exception {
        File file = new File(pathIntent) ;
        if(!file.exists()) {
            System.out.println("path not exist " + pathIntent);
            return;
        }

        ArrayList<String> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while((line = br.readLine()) != null) {
            if(line.isEmpty()) {
                continue;
            } else {
                list.add(line);
            }
        }
        br.close();

        ArrayList<String> printedList = new ArrayList<>();
        switch (type) {
            case TYPE_CONCEPT_REFS: {
                printedList.clear();
                System.out.println("print all conceptRefs:");
                for(String s : list) {
                    if(s.startsWith("#")) {
                        s = s.replaceFirst("#","").trim();
                        if(!printedList.contains(s)) {
                            System.out.println(s);
                            printedList.add(s);
                        }
                    }
                }
                break;
            }
            case TYPE_INTENT: {
                printedList.clear();
                System.out.println("print all intent:");
                for (String s : list) {
                    if(s.startsWith("#")) {
                        continue;
                    } else if(s.startsWith("//")) {
                        continue;
                    } else {
                        if(!printedList.contains(s)) {
                            System.out.println(s.trim());
                            printedList.add(s);
                        }
                    }
                }
                break;
            }
        }
    }
}
