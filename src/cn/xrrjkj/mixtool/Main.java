package cn.xrrjkj.mixtool;

/**
 * Created by user05 on 11/16/2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        String intent = "E:\\ibaby\\data\\intent.txt";
//        String sample = "E:\\ibaby\\data\\sample.txt";
//        String dictionary = "E:\\ibaby\\data\\dictionary.txt";
//        String output = "E:\\ibaby\\data\\output.trax.xml";
//
//        MixDomTool mixDomTool = new MixDomTool();
//        mixDomTool.setPathDictionary(dictionary);
//        mixDomTool.setPathIntent(intent);
//        mixDomTool.setPathSample(sample);
//        mixDomTool.setPathOutput(output);
//        mixDomTool.start();

        MixDomTool mixDomTool = new MixDomTool();
        if(mixDomTool.isValidParams(args)) {
            mixDomTool.start();
        }

//        if(mixDomTool.isValidParams(args)) {
//            mixDomTool.printConceptrefFromIntentFile(MixDomTool.TYPE_CONCEPT_REFS);
//        }

//        if(mixDomTool.isValidParams(args)) {
//            mixDomTool.printFromIntentFile(MixDomTool.TYPE_INTENT);
//        }
    }
}
