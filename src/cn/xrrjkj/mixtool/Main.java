package cn.xrrjkj.mixtool;

/**
 * Created by user05 on 11/16/2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {
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
